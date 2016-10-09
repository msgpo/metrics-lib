/* Copyright 2011--2016 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import org.torproject.descriptor.Descriptor;
import org.torproject.descriptor.DescriptorFile;
import org.torproject.descriptor.DescriptorParseException;
import org.torproject.descriptor.DescriptorParser;
import org.torproject.descriptor.DescriptorReader;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

public class DescriptorReaderImpl implements DescriptorReader {

  private static Logger log = LoggerFactory.getLogger(DescriptorReaderImpl.class);
  private boolean hasStartedReading = false;

  private List<File> directories = new ArrayList<>();

  @Override
  public void addDirectory(File directory) {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.directories.add(directory);
  }

  private List<File> tarballs = new ArrayList<>();

  @Override
  public void addTarball(File tarball) {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.tarballs.add(tarball);
  }

  private File historyFile;

  @Override
  public void setExcludeFiles(File historyFile) {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.historyFile = historyFile;
  }

  private SortedMap<String, Long> excludedFiles;

  @Override
  public void setExcludedFiles(SortedMap<String, Long> excludedFiles) {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.excludedFiles = excludedFiles;
  }

  @Override
  public SortedMap<String, Long> getExcludedFiles() {
    if (this.reader == null || !this.reader.hasFinishedReading) {
      throw new IllegalStateException("Operation is not permitted before "
          + "finishing to read.");
    }
    return new TreeMap<>(this.reader.excludedFilesAfter);
  }

  @Override
  public SortedMap<String, Long> getParsedFiles() {
    if (this.reader == null || !this.reader.hasFinishedReading) {
      throw new IllegalStateException("Operation is not permitted before "
          + "finishing to read.");
    }
    return new TreeMap<>(this.reader.parsedFilesAfter);
  }

  private boolean failUnrecognizedDescriptorLines = false;

  @Override
  public void setFailUnrecognizedDescriptorLines() {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.failUnrecognizedDescriptorLines = true;
  }

  private Integer maxDescriptorFilesInQueue = null;

  @Override
  public void setMaxDescriptorFilesInQueue(int max) {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Reconfiguration is not permitted "
          + "after starting to read.");
    }
    this.maxDescriptorFilesInQueue = max;
  }

  private DescriptorReaderRunnable reader;

  @Override
  public Iterator<DescriptorFile> readDescriptors() {
    if (this.hasStartedReading) {
      throw new IllegalStateException("Initiating reading is only "
          + "permitted once.");
    }
    this.hasStartedReading = true;
    BlockingIteratorImpl<DescriptorFile> descriptorQueue =
        this.maxDescriptorFilesInQueue == null
        ? new BlockingIteratorImpl<DescriptorFile>()
        : new BlockingIteratorImpl<DescriptorFile>(
        this.maxDescriptorFilesInQueue);
    this.reader = new DescriptorReaderRunnable(this.directories,
        this.tarballs, descriptorQueue, this.historyFile,
        this.excludedFiles, this.failUnrecognizedDescriptorLines);
    new Thread(this.reader).start();
    return descriptorQueue;
  }

  private static class DescriptorReaderRunnable implements Runnable {

    private List<File> directories;

    private List<File> tarballs;

    private BlockingIteratorImpl<DescriptorFile> descriptorQueue;

    private File historyFile;

    private SortedMap<String, Long> excludedFilesBefore = new TreeMap<>();

    private SortedMap<String, Long> excludedFilesAfter = new TreeMap<>();

    private SortedMap<String, Long> parsedFilesAfter = new TreeMap<>();

    private DescriptorParser descriptorParser;

    private boolean hasFinishedReading = false;

    private DescriptorReaderRunnable(List<File> directories,
        List<File> tarballs,
        BlockingIteratorImpl<DescriptorFile> descriptorQueue,
        File historyFile, SortedMap<String, Long> excludedFiles,
        boolean failUnrecognizedDescriptorLines) {
      this.directories = directories;
      this.tarballs = tarballs;
      this.descriptorQueue = descriptorQueue;
      this.historyFile = historyFile;
      if (excludedFiles != null) {
        this.excludedFilesBefore = excludedFiles;
      }
      this.descriptorParser = new DescriptorParserImpl();
      this.descriptorParser.setFailUnrecognizedDescriptorLines(
          failUnrecognizedDescriptorLines);
    }

    public void run() {
      try {
        this.readOldHistory();
        this.readDescriptors();
        this.readTarballs();
        this.hasFinishedReading = true;
      } catch (Throwable t) {
        log.error("Bug: uncaught exception or error while "
            + "reading descriptors: " + t.getMessage(), t);
      } finally {
        this.descriptorQueue.setOutOfDescriptors();
      }
      if (this.hasFinishedReading) {
        this.writeNewHistory();
      }
    }

    private void readOldHistory() {
      if (this.historyFile == null || !this.historyFile.exists()) {
        return;
      }
      try {
        BufferedReader br = new BufferedReader(new FileReader(
            this.historyFile));
        String line;
        while ((line = br.readLine()) != null) {
          if (!line.contains(" ")) {
            log.warn("Unexpected line structure in old history: " + line);
            continue;
          }
          long lastModifiedMillis = Long.parseLong(line.substring(0,
              line.indexOf(" ")));
          String absolutePath = line.substring(line.indexOf(" ") + 1);
          this.excludedFilesBefore.put(absolutePath, lastModifiedMillis);
        }
        br.close();
      } catch (IOException | NumberFormatException e) {
        log.warn("Trouble reading old history.", e);
      }
    }

    private void writeNewHistory() {
      if (this.historyFile == null) {
        return;
      }
      try {
        if (this.historyFile.getParentFile() != null) {
          this.historyFile.getParentFile().mkdirs();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(
            this.historyFile));
        SortedMap<String, Long> newHistory = new TreeMap<>();
        newHistory.putAll(this.excludedFilesAfter);
        newHistory.putAll(this.parsedFilesAfter);
        for (Map.Entry<String, Long> e : newHistory.entrySet()) {
          String absolutePath = e.getKey();
          long lastModifiedMillis = e.getValue();
          bw.write(String.valueOf(lastModifiedMillis) + " " + absolutePath
              + "\n");
        }
        bw.close();
      } catch (IOException e) {
        log.warn("Trouble writing new history.", e);
      }
    }

    private void readDescriptors() {
      for (File directory : this.directories) {
        if (!directory.exists() || !directory.isDirectory()) {
          continue;
        }
        Stack<File> files = new Stack<>();
        files.add(directory);
        boolean abortReading = false;
        while (!abortReading && !files.isEmpty()) {
          File file = files.pop();
          if (file.isDirectory()) {
            files.addAll(Arrays.asList(file.listFiles()));
          } else if (file.getName().endsWith(".tar")
              || file.getName().endsWith(".tar.bz2")
              || file.getName().endsWith(".tar.xz")) {
            this.tarballs.add(file);
          } else {
            String absolutePath = file.getAbsolutePath();
            long lastModifiedMillis = file.lastModified();
            if (this.excludedFilesBefore.containsKey(absolutePath)
                && this.excludedFilesBefore.get(absolutePath)
                == lastModifiedMillis) {
              this.excludedFilesAfter.put(absolutePath,
                  lastModifiedMillis);
              continue;
            }
            this.parsedFilesAfter.put(absolutePath, lastModifiedMillis);
            DescriptorFileImpl descriptorFile = new DescriptorFileImpl();
            try {
              descriptorFile.setDirectory(directory);
              descriptorFile.setFile(file);
              descriptorFile.setFileName(file.getAbsolutePath());
              descriptorFile.setLastModified(lastModifiedMillis);
              descriptorFile.setDescriptors(this.readFile(file));
            } catch (DescriptorParseException e) {
              descriptorFile.setException(e);
            } catch (IOException e) {
              descriptorFile.setException(e);
              abortReading = true;
            }
            this.descriptorQueue.add(descriptorFile);
          }
        }
      }
    }

    private void readTarballs() {
      List<File> files = new ArrayList<>(this.tarballs);
      boolean abortReading = false;
      while (!abortReading && !files.isEmpty()) {
        File tarball = files.remove(0);
        if (!tarball.getName().endsWith(".tar")
            && !tarball.getName().endsWith(".tar.bz2")
            && !tarball.getName().endsWith(".tar.xz")) {
          continue;
        }
        String absolutePath = tarball.getAbsolutePath();
        long lastModifiedMillis = tarball.lastModified();
        if (this.excludedFilesBefore.containsKey(absolutePath)
            && this.excludedFilesBefore.get(absolutePath)
            == lastModifiedMillis) {
          this.excludedFilesAfter.put(absolutePath, lastModifiedMillis);
          continue;
        }
        this.parsedFilesAfter.put(absolutePath, lastModifiedMillis);
        try {
          FileInputStream in = new FileInputStream(tarball);
          if (in.available() > 0) {
            TarArchiveInputStream tais = null;
            if (tarball.getName().endsWith(".tar.bz2")) {
              tais = new TarArchiveInputStream(
                  new BZip2CompressorInputStream(in));
            } else if (tarball.getName().endsWith(".tar.xz")) {
              tais = new TarArchiveInputStream(
                  new XZCompressorInputStream(in));
            } else if (tarball.getName().endsWith(".tar")) {
              tais = new TarArchiveInputStream(in);
            }
            BufferedInputStream bis = new BufferedInputStream(tais);
            TarArchiveEntry tae = null;
            while ((tae = tais.getNextTarEntry()) != null) {
              if (tae.isDirectory()) {
                continue;
              }
              DescriptorFileImpl descriptorFile =
                  new DescriptorFileImpl();
              descriptorFile.setTarball(tarball);
              descriptorFile.setFileName(tae.getName());
              descriptorFile.setLastModified(tae.getLastModifiedDate()
                  .getTime());
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              int len;
              byte[] data = new byte[1024];
              while ((len = bis.read(data, 0, 1024)) >= 0) {
                baos.write(data, 0, len);
              }
              byte[] rawDescriptorBytes = baos.toByteArray();
              if (rawDescriptorBytes.length < 1) {
                continue;
              }
              try {
                String fileName = tae.getName().substring(
                    tae.getName().lastIndexOf("/") + 1);
                List<Descriptor> parsedDescriptors =
                    this.descriptorParser.parseDescriptors(
                    rawDescriptorBytes, fileName);
                descriptorFile.setDescriptors(parsedDescriptors);
              } catch (DescriptorParseException e) {
                descriptorFile.setException(e);
              }
              this.descriptorQueue.add(descriptorFile);
            }
          }
        } catch (IOException e) {
          abortReading = true;
        }
      }
    }

    private List<Descriptor> readFile(File file) throws IOException,
        DescriptorParseException {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int len;
      byte[] data = new byte[1024];
      while ((len = bis.read(data, 0, 1024)) >= 0) {
        baos.write(data, 0, len);
      }
      bis.close();
      byte[] rawDescriptorBytes = baos.toByteArray();
      return this.descriptorParser.parseDescriptors(rawDescriptorBytes,
          file.getName());
    }
  }
}

