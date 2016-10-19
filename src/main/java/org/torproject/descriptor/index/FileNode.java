/* Copyright 2016 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.index;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A FileNode provides the file's name, size, and modified time.
 *
 * @since 1.4.0
 */
public class FileNode implements Comparable<FileNode> {

  private static Logger log = LoggerFactory.getLogger(FileNode.class);

  /** Path (i.e. file name) is exposed in JSON. */
  @Expose
  public final String path;

  /** The file size is exposed in JSON. */
  @Expose
  public final long size;

  /** The last modified date-time string is exposed in JSON. */
  @Expose
  @SerializedName("last_modified")
  public final String lastModified;

  private long lastModifiedMillis;

  /**
   * A FileNode needs a path, i.e. the file name, the file size, and
   * the last modified date-time string.
   */
  public FileNode(String path, long size, String lastModified) {
    this.path = path;
    this.size = size;
    this.lastModified = lastModified;
  }

  /**
   * This compareTo is not compatible with equals or hash!
   * It simply ensures a path-sorted Gson output.
   */
  @Override
  public int compareTo(FileNode other) {
    return this.path.compareTo(other.path);
  }

  /** Lazily returns the last modified time in millis. */
  public long lastModifiedMillis() {
    if (this.lastModifiedMillis == 0) {
      DateFormat dateTimeFormat =
          new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
      dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      try {
        lastModifiedMillis = dateTimeFormat.parse(this.lastModified).getTime();
      } catch (ParseException ex) {
        log.warn("Cannot parse date-time. Setting lastModifiedMillis to -1L.",
            ex);
        this.lastModifiedMillis = -1L;
      }
    }
    return this.lastModifiedMillis;
  }

  /** For debugging purposes. */
  @Override
  public String toString() {
    return "Fn: " + path;
  }
}
