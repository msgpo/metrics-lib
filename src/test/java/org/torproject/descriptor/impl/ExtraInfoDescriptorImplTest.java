/* Copyright 2012--2017 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.torproject.descriptor.BridgeExtraInfoDescriptor;
import org.torproject.descriptor.DescriptorParseException;
import org.torproject.descriptor.ExtraInfoDescriptor;
import org.torproject.descriptor.RelayExtraInfoDescriptor;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/* Test parsing of extra-info descriptors. */
public class ExtraInfoDescriptorImplTest {

  /* Helper class to build a descriptor based on default data and
   * modifications requested by test methods. */
  private static class DescriptorBuilder {

    private String extraInfoLine = "extra-info chaoscomputerclub5 "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26";

    private static ExtraInfoDescriptor createWithExtraInfoLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.extraInfoLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String publishedLine = "published 2012-02-11 09:08:36";

    private static ExtraInfoDescriptor createWithPublishedLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.publishedLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String writeHistoryLine = "write-history 2012-02-11 09:03:39 "
        + "(900 s) 4713350144,4723824640,4710717440,4572675072";

    private static ExtraInfoDescriptor createWithWriteHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.writeHistoryLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String readHistoryLine = "read-history 2012-02-11 09:03:39 "
        + "(900 s) 4707695616,4699666432,4650004480,4489718784";

    private static ExtraInfoDescriptor createWithReadHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.readHistoryLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String dirreqWriteHistoryLine = "dirreq-write-history "
        + "2012-02-11 09:03:39 (900 s) 81281024,64996352,60625920,"
        + "67922944";

    private static ExtraInfoDescriptor createWithDirreqWriteHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.dirreqWriteHistoryLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String dirreqReadHistoryLine = "dirreq-read-history "
        + "2012-02-11 09:03:39 (900 s) 17074176,16235520,16005120,"
        + "16209920";

    private static ExtraInfoDescriptor createWithDirreqReadHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.dirreqReadHistoryLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String geoipDbDigestLine = null;

    private static ExtraInfoDescriptor createWithGeoipDbDigestLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.geoipDbDigestLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String geoip6DbDigestLine = null;

    private static ExtraInfoDescriptor createWithGeoip6DbDigestLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.geoip6DbDigestLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String geoipStatsLines = null;

    private static ExtraInfoDescriptor createWithGeoipStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.geoipStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String dirreqStatsLines = null;

    private static ExtraInfoDescriptor createWithDirreqStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.dirreqStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String entryStatsLines = null;

    private static ExtraInfoDescriptor createWithEntryStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.entryStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String cellStatsLines = null;

    private static ExtraInfoDescriptor createWithCellStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.cellStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String connBiDirectLine = null;

    private static ExtraInfoDescriptor createWithConnBiDirectLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.connBiDirectLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String exitStatsLines = null;

    private static ExtraInfoDescriptor createWithExitStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.exitStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String bridgeStatsLines = null;

    private static ExtraInfoDescriptor createWithBridgeStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.bridgeStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String hidservStatsLines = null;

    private static ExtraInfoDescriptor createWithHidservStatsLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.hidservStatsLines = lines;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String unrecognizedLine = null;

    private static ExtraInfoDescriptor createWithUnrecognizedLine(
        String line, boolean failUnrecognizedDescriptorLines)
        throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.unrecognizedLine = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(),
          failUnrecognizedDescriptorLines);
    }

    private byte[] nonAsciiLineBytes = null;

    private static ExtraInfoDescriptor createWithNonAsciiLineBytes(
        byte[] lineBytes, boolean failUnrecognizedDescriptorLines)
        throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.nonAsciiLineBytes = lineBytes;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(),
          failUnrecognizedDescriptorLines);
    }

    private String routerSignatureLines = "router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "o4j+kH8UQfjBwepUnr99v0ebN8RpzHJ/lqYsTojXHy9kMr1RNI9IDeSzA7PSqT"
        + "uV\n4PL8QsGtlfwthtIoZpB2srZeyN/mcpA9fa1JXUrt/UN9K/+32Cyaad7h0n"
        + "HE6Xfb\njqpXDpnBpvk4zjmzjjKYnIsUWTnADmu0fo3xTRqXi7g=\n"
        + "-----END SIGNATURE-----";

    private static ExtraInfoDescriptor createWithRouterSignatureLines(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.routerSignatureLines = line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private String identityEd25519Lines = null;

    private String masterKeyEd25519Line = null;

    private String routerSigEd25519Line = null;

    private static ExtraInfoDescriptor createWithEd25519Lines(
        String identityEd25519Lines, String masterKeyEd25519Line,
        String routerSigEd25519Line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.identityEd25519Lines = identityEd25519Lines;
      db.masterKeyEd25519Line = masterKeyEd25519Line;
      db.routerSigEd25519Line = routerSigEd25519Line;
      return new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    }

    private byte[] buildDescriptor() {
      StringBuilder sb = new StringBuilder();
      if (this.extraInfoLine != null) {
        sb.append(this.extraInfoLine).append("\n");
      }
      if (this.identityEd25519Lines != null) {
        sb.append(this.identityEd25519Lines).append("\n");
      }
      if (this.masterKeyEd25519Line != null) {
        sb.append(this.masterKeyEd25519Line).append("\n");
      }
      if (this.publishedLine != null) {
        sb.append(this.publishedLine).append("\n");
      }
      if (this.writeHistoryLine != null) {
        sb.append(this.writeHistoryLine).append("\n");
      }
      if (this.readHistoryLine != null) {
        sb.append(this.readHistoryLine).append("\n");
      }
      if (this.dirreqWriteHistoryLine != null) {
        sb.append(this.dirreqWriteHistoryLine).append("\n");
      }
      if (this.dirreqReadHistoryLine != null) {
        sb.append(this.dirreqReadHistoryLine).append("\n");
      }
      if (this.geoipDbDigestLine != null) {
        sb.append(this.geoipDbDigestLine).append("\n");
      }
      if (this.geoip6DbDigestLine != null) {
        sb.append(this.geoip6DbDigestLine).append("\n");
      }
      if (this.geoipStatsLines != null) {
        sb.append(this.geoipStatsLines).append("\n");
      }
      if (this.dirreqStatsLines != null) {
        sb.append(this.dirreqStatsLines).append("\n");
      }
      if (this.entryStatsLines != null) {
        sb.append(this.entryStatsLines).append("\n");
      }
      if (this.cellStatsLines != null) {
        sb.append(this.cellStatsLines).append("\n");
      }
      if (this.connBiDirectLine != null) {
        sb.append(this.connBiDirectLine).append("\n");
      }
      if (this.exitStatsLines != null) {
        sb.append(this.exitStatsLines).append("\n");
      }
      if (this.bridgeStatsLines != null) {
        sb.append(this.bridgeStatsLines).append("\n");
      }
      if (this.hidservStatsLines != null) {
        sb.append(this.hidservStatsLines).append("\n");
      }
      if (this.unrecognizedLine != null) {
        sb.append(this.unrecognizedLine).append("\n");
      }
      if (this.nonAsciiLineBytes != null) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(sb.toString().getBytes());
          baos.write(this.nonAsciiLineBytes);
          baos.write("\n".getBytes());
          if (this.routerSignatureLines != null) {
            baos.write(this.routerSignatureLines.getBytes());
          }
          return baos.toByteArray();
        } catch (IOException e) {
          return null;
        }
      }
      if (this.routerSigEd25519Line != null) {
        sb.append(this.routerSigEd25519Line).append("\n");
      }
      if (this.routerSignatureLines != null) {
        sb.append(this.routerSignatureLines).append("\n");
      }
      return sb.toString().getBytes();
    }
  }

  /* Helper class to build a set of geoip-stats lines based on default
   * data and modifications requested by test methods. */
  private static class GeoipStatsBuilder {

    private String geoipStartTimeLine = "geoip-start-time 2012-02-10 "
        + "18:32:51";

    private static ExtraInfoDescriptor createWithGeoipStartTimeLine(
        String line) throws DescriptorParseException {
      GeoipStatsBuilder gsb = new GeoipStatsBuilder();
      gsb.geoipStartTimeLine = line;
      return DescriptorBuilder.createWithGeoipStatsLines(
          gsb.buildGeoipStatsLines());
    }

    private String geoipClientOriginsLine = "geoip-client-origins "
        + "de=1152,cn=896,us=712,it=504,ru=352,fr=208,gb=208,ir=200";

    private static ExtraInfoDescriptor createWithGeoipClientOriginsLine(
        String line) throws DescriptorParseException {
      GeoipStatsBuilder gsb = new GeoipStatsBuilder();
      gsb.geoipClientOriginsLine = line;
      return DescriptorBuilder.createWithGeoipStatsLines(
          gsb.buildGeoipStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithGeoipStatsLines(
          new GeoipStatsBuilder().buildGeoipStatsLines());
    }

    private String buildGeoipStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.geoipStartTimeLine != null) {
        sb.append(this.geoipStartTimeLine).append("\n");
      }
      if (this.geoipClientOriginsLine != null) {
        sb.append(this.geoipClientOriginsLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of dirreq-stats lines based on default
   * data and modifications requested by test methods. */
  private static class DirreqStatsBuilder {

    private String dirreqStatsEndLine = "dirreq-stats-end 2012-02-11 "
        + "00:59:53 (86400 s)";

    private static ExtraInfoDescriptor createWithDirreqStatsEndLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqStatsEndLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3IpsLine = "dirreq-v3-ips us=1544,de=1056,"
        + "it=1032,fr=784,es=640,ru=440,br=312,gb=272,kr=224,sy=192";

    private static ExtraInfoDescriptor createWithDirreqV3IpsLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3IpsLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2IpsLine = "dirreq-v2-ips ";

    private static ExtraInfoDescriptor createWithDirreqV2IpsLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2IpsLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3ReqsLine = "dirreq-v3-reqs us=1744,de=1224,"
        + "it=1080,fr=832,es=664,ru=536,br=344,gb=296,kr=272,in=216";

    private static ExtraInfoDescriptor createWithDirreqV3ReqsLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3ReqsLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2ReqsLine = "dirreq-v2-reqs ";

    private static ExtraInfoDescriptor createWithDirreqV2ReqsLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2ReqsLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3RespLine = "dirreq-v3-resp ok=10848,"
        + "not-enough-sigs=8,unavailable=0,not-found=0,not-modified=0,"
        + "busy=80";

    private static ExtraInfoDescriptor createWithDirreqV3RespLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3RespLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2RespLine = "dirreq-v2-resp ok=0,unavailable=0,"
        + "not-found=1576,not-modified=0,busy=0";

    private static ExtraInfoDescriptor createWithDirreqV2RespLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2RespLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2ShareLine = "dirreq-v2-share 0.37%";

    private static ExtraInfoDescriptor createWithDirreqV2ShareLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2ShareLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3ShareLine = "dirreq-v3-share 0.37%";

    private static ExtraInfoDescriptor createWithDirreqV3ShareLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3ShareLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3DirectDlLine = "dirreq-v3-direct-dl "
        + "complete=36,timeout=4,running=0,min=7538,d1=20224,d2=28950,"
        + "q1=40969,d3=55786,d4=145813,md=199164,d6=267230,d7=480900,"
        + "q3=481049,d8=531276,d9=778086,max=15079428";

    private static ExtraInfoDescriptor createWithDirreqV3DirectDlLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3DirectDlLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2DirectDlLine = "dirreq-v2-direct-dl "
        + "complete=0,timeout=0,running=0";

    private static ExtraInfoDescriptor createWithDirreqV2DirectDlLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2DirectDlLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV3TunneledDlLine = "dirreq-v3-tunneled-dl "
        + "complete=10608,timeout=204,running=4,min=507,d1=20399,"
        + "d2=27588,q1=29292,d3=30889,d4=40624,md=59967,d6=103333,"
        + "d7=161170,q3=209415,d8=256711,d9=452503,max=23417777";

    private static ExtraInfoDescriptor createWithDirreqV3TunneledDlLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV3TunneledDlLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private String dirreqV2TunneledDlLine = "dirreq-v2-tunneled-dl "
        + "complete=0,timeout=0,running=0";

    private static ExtraInfoDescriptor createWithDirreqV2TunneledDlLine(
        String line) throws DescriptorParseException {
      DirreqStatsBuilder dsb = new DirreqStatsBuilder();
      dsb.dirreqV2TunneledDlLine = line;
      return DescriptorBuilder.createWithDirreqStatsLines(
          dsb.buildDirreqStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithDirreqStatsLines(
          new DirreqStatsBuilder().buildDirreqStatsLines());
    }

    private String buildDirreqStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.dirreqStatsEndLine != null) {
        sb.append(this.dirreqStatsEndLine).append("\n");
      }
      if (this.dirreqV3IpsLine != null) {
        sb.append(this.dirreqV3IpsLine).append("\n");
      }
      if (this.dirreqV2IpsLine != null) {
        sb.append(this.dirreqV2IpsLine).append("\n");
      }
      if (this.dirreqV3ReqsLine != null) {
        sb.append(this.dirreqV3ReqsLine).append("\n");
      }
      if (this.dirreqV2ReqsLine != null) {
        sb.append(this.dirreqV2ReqsLine).append("\n");
      }
      if (this.dirreqV3RespLine != null) {
        sb.append(this.dirreqV3RespLine).append("\n");
      }
      if (this.dirreqV2RespLine != null) {
        sb.append(this.dirreqV2RespLine).append("\n");
      }
      if (this.dirreqV2ShareLine != null) {
        sb.append(this.dirreqV2ShareLine).append("\n");
      }
      if (this.dirreqV3ShareLine != null) {
        sb.append(this.dirreqV3ShareLine).append("\n");
      }
      if (this.dirreqV3DirectDlLine != null) {
        sb.append(this.dirreqV3DirectDlLine).append("\n");
      }
      if (this.dirreqV2DirectDlLine != null) {
        sb.append(this.dirreqV2DirectDlLine).append("\n");
      }
      if (this.dirreqV3TunneledDlLine != null) {
        sb.append(this.dirreqV3TunneledDlLine).append("\n");
      }
      if (this.dirreqV2TunneledDlLine != null) {
        sb.append(this.dirreqV2TunneledDlLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of entry-stats lines based on default
   * data and modifications requested by test methods. */
  private static class EntryStatsBuilder {

    private String entryStatsEndLine = "entry-stats-end 2012-02-11 "
        + "01:59:39 (86400 s)";

    private static ExtraInfoDescriptor createWithEntryStatsEndLine(
        String line) throws DescriptorParseException {
      EntryStatsBuilder esb = new EntryStatsBuilder();
      esb.entryStatsEndLine = line;
      return DescriptorBuilder.createWithEntryStatsLines(
          esb.buildEntryStatsLines());
    }

    private String entryIpsLine = "entry-ips ir=25368,us=15744,it=14816,"
        + "de=13256,es=8280,fr=8120,br=5176,sy=4760,ru=4504,sa=4216,"
        + "gb=3152,pl=2928,nl=2208,kr=1856,ca=1792,ua=1272,in=1192";

    private static ExtraInfoDescriptor createWithEntryIpsLine(
        String line) throws DescriptorParseException {
      EntryStatsBuilder esb = new EntryStatsBuilder();
      esb.entryIpsLine = line;
      return DescriptorBuilder.createWithEntryStatsLines(
          esb.buildEntryStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithEntryStatsLines(
          new EntryStatsBuilder().buildEntryStatsLines());
    }

    private String buildEntryStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.entryStatsEndLine != null) {
        sb.append(this.entryStatsEndLine).append("\n");
      }
      if (this.entryIpsLine != null) {
        sb.append(this.entryIpsLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of cell-stats lines based on default
   * data and modifications requested by test methods. */
  private static class CellStatsBuilder {

    private String cellStatsEndLine = "cell-stats-end 2012-02-11 "
        + "01:59:39 (86400 s)";

    private static ExtraInfoDescriptor createWithCellStatsEndLine(
        String line) throws DescriptorParseException {
      CellStatsBuilder csb = new CellStatsBuilder();
      csb.cellStatsEndLine = line;
      return DescriptorBuilder.createWithCellStatsLines(
          csb.buildCellStatsLines());
    }

    private String cellProcessedCellsLine = "cell-processed-cells "
        + "1441,11,6,4,2,1,1,1,1,1";

    private static ExtraInfoDescriptor createWithCellProcessedCellsLine(
        String line) throws DescriptorParseException {
      CellStatsBuilder csb = new CellStatsBuilder();
      csb.cellProcessedCellsLine = line;
      return DescriptorBuilder.createWithCellStatsLines(
          csb.buildCellStatsLines());
    }

    private String cellQueuedCellsLine = "cell-queued-cells "
        + "3.29,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00";

    private static ExtraInfoDescriptor createWithCellQueuedCellsLine(
        String line) throws DescriptorParseException {
      CellStatsBuilder csb = new CellStatsBuilder();
      csb.cellQueuedCellsLine = line;
      return DescriptorBuilder.createWithCellStatsLines(
          csb.buildCellStatsLines());
    }

    private String cellTimeInQueueLine = "cell-time-in-queue "
        + "524,1,1,0,0,25,0,0,0,0";

    private static ExtraInfoDescriptor createWithCellTimeInQueueLine(
        String line) throws DescriptorParseException {
      CellStatsBuilder csb = new CellStatsBuilder();
      csb.cellTimeInQueueLine = line;
      return DescriptorBuilder.createWithCellStatsLines(
          csb.buildCellStatsLines());
    }

    private String cellCircuitsPerDecileLine = "cell-circuits-per-decile "
        + "866";

    private static ExtraInfoDescriptor
        createWithCellCircuitsPerDecileLine(String line)
        throws DescriptorParseException {
      CellStatsBuilder csb = new CellStatsBuilder();
      csb.cellCircuitsPerDecileLine = line;
      return DescriptorBuilder.createWithCellStatsLines(
          csb.buildCellStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithCellStatsLines(
          new CellStatsBuilder().buildCellStatsLines());
    }

    private String buildCellStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.cellStatsEndLine != null) {
        sb.append(this.cellStatsEndLine).append("\n");
      }
      if (this.cellProcessedCellsLine != null) {
        sb.append(this.cellProcessedCellsLine).append("\n");
      }
      if (this.cellQueuedCellsLine != null) {
        sb.append(this.cellQueuedCellsLine).append("\n");
      }
      if (this.cellTimeInQueueLine != null) {
        sb.append(this.cellTimeInQueueLine).append("\n");
      }
      if (this.cellCircuitsPerDecileLine != null) {
        sb.append(this.cellCircuitsPerDecileLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of exit-stats lines based on default
   * data and modifications requested by test methods. */
  private static class ExitStatsBuilder {

    private String exitStatsEndLine = "exit-stats-end 2012-02-11 "
        + "01:59:39 (86400 s)";

    private static ExtraInfoDescriptor createWithExitStatsEndLine(
        String line) throws DescriptorParseException {
      ExitStatsBuilder esb = new ExitStatsBuilder();
      esb.exitStatsEndLine = line;
      return DescriptorBuilder.createWithExitStatsLines(
          esb.buildExitStatsLines());
    }

    private String exitKibibytesWrittenLine = "exit-kibibytes-written "
        + "25=74647,80=31370,443=20577,49755=23,52563=12,52596=1111,"
        + "57528=4,60912=11,61351=6,64811=3365,other=2592";

    private static ExtraInfoDescriptor createWithExitKibibytesWrittenLine(
        String line) throws DescriptorParseException {
      ExitStatsBuilder esb = new ExitStatsBuilder();
      esb.exitKibibytesWrittenLine = line;
      return DescriptorBuilder.createWithExitStatsLines(
          esb.buildExitStatsLines());
    }

    private String exitKibibytesReadLine = "exit-kibibytes-read "
        + "25=35562,80=1254256,443=110279,49755=9396,52563=1911,"
        + "52596=648,57528=1188,60912=1427,61351=1824,64811=14,"
        + "other=3054";

    private static ExtraInfoDescriptor createWithExitKibibytesReadLine(
        String line) throws DescriptorParseException {
      ExitStatsBuilder esb = new ExitStatsBuilder();
      esb.exitKibibytesReadLine = line;
      return DescriptorBuilder.createWithExitStatsLines(
          esb.buildExitStatsLines());
    }

    private String exitStreamsOpenedLine = "exit-streams-opened "
        + "25=369748,80=64212,443=151660,49755=4,52563=4,52596=4,57528=4,"
        + "60912=4,61351=4,64811=4,other=1212";

    private static ExtraInfoDescriptor createWithExitStreamsOpenedLine(
        String line) throws DescriptorParseException {
      ExitStatsBuilder esb = new ExitStatsBuilder();
      esb.exitStreamsOpenedLine = line;
      return DescriptorBuilder.createWithExitStatsLines(
          esb.buildExitStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithExitStatsLines(
          new ExitStatsBuilder().buildExitStatsLines());
    }

    private String buildExitStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.exitStatsEndLine != null) {
        sb.append(this.exitStatsEndLine).append("\n");
      }
      if (this.exitKibibytesWrittenLine != null) {
        sb.append(this.exitKibibytesWrittenLine).append("\n");
      }
      if (this.exitKibibytesReadLine != null) {
        sb.append(this.exitKibibytesReadLine).append("\n");
      }
      if (this.exitStreamsOpenedLine != null) {
        sb.append(this.exitStreamsOpenedLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of bridge-stats lines based on default
   * data and modifications requested by test methods. */
  private static class BridgeStatsBuilder {

    private String bridgeStatsEndLine = "bridge-stats-end 2012-02-11 "
        + "01:59:39 (86400 s)";

    private static ExtraInfoDescriptor createWithBridgeStatsEndLine(
        String line) throws DescriptorParseException {
      BridgeStatsBuilder bsb = new BridgeStatsBuilder();
      bsb.bridgeStatsEndLine = line;
      return DescriptorBuilder.createWithBridgeStatsLines(
          bsb.buildBridgeStatsLines());
    }

    private String bridgeIpsLine = "bridge-ips ir=24,sy=16,??=8,cn=8,"
        + "de=8,es=8,fr=8,gb=8,in=8,jp=8,kz=8,nl=8,ua=8,us=8,vn=8,za=8";

    private static ExtraInfoDescriptor createWithBridgeIpsLine(
        String line) throws DescriptorParseException {
      BridgeStatsBuilder bsb = new BridgeStatsBuilder();
      bsb.bridgeIpsLine = line;
      return DescriptorBuilder.createWithBridgeStatsLines(
          bsb.buildBridgeStatsLines());
    }

    private String bridgeIpVersionsLine = "bridge-ip-versions v4=8,v6=16";

    private static ExtraInfoDescriptor createWithBridgeIpVersionsLine(
        String line) throws DescriptorParseException {
      BridgeStatsBuilder bsb = new BridgeStatsBuilder();
      bsb.bridgeIpVersionsLine = line;
      return DescriptorBuilder.createWithBridgeStatsLines(
          bsb.buildBridgeStatsLines());
    }

    private String bridgeIpTransportsLine = "bridge-ip-transports "
        + "<OR>=8,obfs2=792,obfs3=1728";

    private static ExtraInfoDescriptor createWithBridgeIpTransportsLine(
        String line) throws DescriptorParseException {
      BridgeStatsBuilder bsb = new BridgeStatsBuilder();
      bsb.bridgeIpTransportsLine = line;
      return DescriptorBuilder.createWithBridgeStatsLines(
          bsb.buildBridgeStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithBridgeStatsLines(
          new BridgeStatsBuilder().buildBridgeStatsLines());
    }

    private String buildBridgeStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.bridgeStatsEndLine != null) {
        sb.append(this.bridgeStatsEndLine).append("\n");
      }
      if (this.bridgeIpsLine != null) {
        sb.append(this.bridgeIpsLine).append("\n");
      }
      if (this.bridgeIpVersionsLine != null) {
        sb.append(this.bridgeIpVersionsLine).append("\n");
      }
      if (this.bridgeIpTransportsLine != null) {
        sb.append(this.bridgeIpTransportsLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  /* Helper class to build a set of hidserv-stats lines based on default
   * data and modifications requested by test methods. */
  private static class HidservStatsBuilder {

    private String hidservStatsEndLine = "hidserv-stats-end 2015-12-03 "
        + "14:26:56 (86400 s)";

    private static ExtraInfoDescriptor createWithHidservStatsEndLine(
        String line) throws DescriptorParseException {
      HidservStatsBuilder hsb = new HidservStatsBuilder();
      hsb.hidservStatsEndLine = line;
      return DescriptorBuilder.createWithHidservStatsLines(
          hsb.buildHidservStatsLines());
    }

    private String hidservRendRelayedCellsLine =
        "hidserv-rend-relayed-cells 36474281 delta_f=2048 epsilon=0.30 "
        + "bin_size=1024";

    private static ExtraInfoDescriptor
        createWithHidservRendRelayedCellsLine(String line)
        throws DescriptorParseException {
      HidservStatsBuilder hsb = new HidservStatsBuilder();
      hsb.hidservRendRelayedCellsLine = line;
      return DescriptorBuilder.createWithHidservStatsLines(
          hsb.buildHidservStatsLines());
    }

    private String hidservDirOnionsSeenLine = "hidserv-dir-onions-seen "
        + "-3 delta_f=8 epsilon=0.30 bin_size=8";

    private static ExtraInfoDescriptor createWithHidservDirOnionsSeenLine(
        String line) throws DescriptorParseException {
      HidservStatsBuilder hsb = new HidservStatsBuilder();
      hsb.hidservDirOnionsSeenLine = line;
      return DescriptorBuilder.createWithHidservStatsLines(
          hsb.buildHidservStatsLines());
    }

    private static ExtraInfoDescriptor createWithDefaultLines()
        throws DescriptorParseException {
      return DescriptorBuilder.createWithHidservStatsLines(
          new HidservStatsBuilder().buildHidservStatsLines());
    }

    private String buildHidservStatsLines() {
      StringBuilder sb = new StringBuilder();
      if (this.hidservStatsEndLine != null) {
        sb.append(this.hidservStatsEndLine).append("\n");
      }
      if (this.hidservRendRelayedCellsLine != null) {
        sb.append(this.hidservRendRelayedCellsLine).append("\n");
      }
      if (this.hidservDirOnionsSeenLine != null) {
        sb.append(this.hidservDirOnionsSeenLine).append("\n");
      }
      String lines = sb.toString();
      if (lines.endsWith("\n")) {
        lines = lines.substring(0, lines.length() - 1);
      }
      return lines;
    }
  }

  @Test()
  public void testSampleDescriptor() throws DescriptorParseException {
    DescriptorBuilder db = new DescriptorBuilder();
    ExtraInfoDescriptor descriptor =
        new RelayExtraInfoDescriptorImpl(db.buildDescriptor(), true);
    assertEquals("chaoscomputerclub5", descriptor.getNickname());
    assertEquals("A9C039A5FD02FCA06303DCFAABE25C5912C63B26",
        descriptor.getFingerprint());
    assertEquals(1328951316000L, descriptor.getPublishedMillis());
    assertNotNull(descriptor.getWriteHistory());
    assertEquals(1328951019000L, descriptor.getWriteHistory()
        .getHistoryEndMillis());
    assertEquals(900L, descriptor.getWriteHistory().getIntervalLength());
    assertEquals(4572675072L, (long) descriptor.getWriteHistory()
        .getBandwidthValues().get(1328951019000L));
    assertNotNull(descriptor.getReadHistory());
    assertNotNull(descriptor.getDirreqWriteHistory());
    assertNotNull(descriptor.getDirreqReadHistory());
  }

  @Test(expected = DescriptorParseException.class)
  public void testExtraInfoLineMissing() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine(null);
  }

  @Test()
  public void testExtraInfoOpt() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithExtraInfoLine("opt extra-info chaoscomputerclub5 "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
    assertEquals("chaoscomputerclub5", descriptor.getNickname());
    assertEquals("A9C039A5FD02FCA06303DCFAABE25C5912C63B26",
        descriptor.getFingerprint());
  }

  @Test()
  public void testExtraInfoNicknameTwoSpaces()
      throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithExtraInfoLine("opt extra-info chaoscomputerclub5  "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
    assertEquals("chaoscomputerclub5", descriptor.getNickname());
    assertEquals("A9C039A5FD02FCA06303DCFAABE25C5912C63B26",
        descriptor.getFingerprint());
  }

  @Test(expected = DescriptorParseException.class)
  public void testExtraInfoLineNotFirst()
      throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("geoip-db-digest "
        + "916A3CA8B7DF61473D5AE5B21711F35F301CE9E8\n"
        + "extra-info chaoscomputerclub5 "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
  }

  @Test(expected = DescriptorParseException.class)
  public void testNicknameMissing() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info  "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
  }

  @Test(expected = DescriptorParseException.class)
  public void testNicknameInvalidChar() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info "
        + "chaoscomputerclub% A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
  }

  @Test(expected = DescriptorParseException.class)
  public void testNicknameTooLong() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info "
        + "chaoscomputerclub5ReallyLongNickname "
        + "A9C039A5FD02FCA06303DCFAABE25C5912C63B26");
  }

  @Test(expected = DescriptorParseException.class)
  public void testFingerprintG() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info "
        + "chaoscomputerclub5 G9C039A5FD02FCA06303DCFAABE25C5912C63B26");
  }

  @Test(expected = DescriptorParseException.class)
  public void testFingerprintTooShort() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info "
        + "chaoscomputerclub5 A9C039A5FD02FCA06303DCFAABE25C5912C6");
  }

  @Test(expected = DescriptorParseException.class)
  public void testFingerprintTooLong() throws DescriptorParseException {
    DescriptorBuilder.createWithExtraInfoLine("extra-info "
        + "chaoscomputerclub5 A9C039A5FD02FCA06303DCFAABE25C5912C63B26"
        + "A9C0");
  }

  @Test(expected = DescriptorParseException.class)
  public void testPublishedMissing() throws DescriptorParseException {
    DescriptorBuilder.createWithPublishedLine(null);
  }

  @Test()
  public void testPublishedOpt() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithPublishedLine("opt published 2012-02-11 09:08:36");
    assertEquals(1328951316000L, descriptor.getPublishedMillis());
  }

  @Test()
  public void testPublishedMillis() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithPublishedLine("opt published 2012-02-11 09:08:36.123");
    assertEquals(1328951316000L, descriptor.getPublishedMillis());
  }

  @Test(expected = DescriptorParseException.class)
  public void testWriteHistoryNegativeBytes()
      throws DescriptorParseException {
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-02-11 09:03:39 (900 s) "
        + "-4713350144,-4723824640,-4710717440,-4572675072");
  }

  @Test()
  public void testReadHistoryTabInterval()
      throws DescriptorParseException {
    DescriptorBuilder.createWithReadHistoryLine("read-history "
        + "2012-02-11 09:03:39 (900\ts) "
        + "4707695616,4699666432,4650004480,4489718784");
  }

  @Test()
  public void testReadHistoryTabIntervalBytes()
      throws DescriptorParseException {
    DescriptorBuilder.createWithReadHistoryLine("read-history "
        + "2012-02-11 09:03:39 (900 s)\t"
        + "4707695616,4699666432,4650004480,4489718784");
  }

  @Test(expected = DescriptorParseException.class)
  public void testReadHistoryNegativeInterval()
      throws DescriptorParseException {
    DescriptorBuilder.createWithReadHistoryLine("read-history "
        + "2012-02-11 09:03:39 (-900 s) "
        + "4707695616,4699666432,4650004480,4489718784");
  }

  @Test()
  public void testReadHistoryNonStandardInterval()
      throws DescriptorParseException {
    DescriptorBuilder.createWithReadHistoryLine("read-history "
        + "2012-02-11 09:03:39 (1800 s) "
        + "4707695616,4699666432,4650004480,4489718784");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqWriteHistoryMissingBytesBegin()
      throws DescriptorParseException {
    DescriptorBuilder.createWithDirreqWriteHistoryLine(
        "dirreq-write-history 2012-02-11 09:03:39 (900 s) "
        + ",64996352,60625920,67922944");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqWriteHistoryMissingBytesMiddle()
      throws DescriptorParseException {
    DescriptorBuilder.createWithDirreqWriteHistoryLine(
        "dirreq-write-history 2012-02-11 09:03:39 (900 s) "
        + "81281024,,60625920,67922944");
  }

  @Test()
  public void testDirreqWriteHistoryExtraArg() throws DescriptorParseException {
    DescriptorBuilder.createWithDirreqWriteHistoryLine(
        "dirreq-write-history "
        + "2012-02-11 09:03:39 (900 s) 81281024,64996352,60625920,"
        + "67922944 bin_size=1024");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqReadHistoryMissingBytesEnd()
      throws DescriptorParseException {
    DescriptorBuilder.createWithDirreqReadHistoryLine(
        "dirreq-read-history 2012-02-11 09:03:39 (900 s) "
        + "17074176,16235520,16005120,");
  }

  @Test()
  public void testGeoipDbDigestValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithGeoipDbDigestLine("geoip-db-digest "
        + "916A3CA8B7DF61473D5AE5B21711F35F301CE9E8");
    assertEquals("916A3CA8B7DF61473D5AE5B21711F35F301CE9E8",
        descriptor.getGeoipDbDigestSha1Hex());
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipDbDigestTooShort()
      throws DescriptorParseException {
    DescriptorBuilder.createWithGeoipDbDigestLine("geoip-db-digest "
        + "916A3CA8B7DF61473D5AE5B21711F35F301C");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipDbDigestIllegalChars()
      throws DescriptorParseException {
    DescriptorBuilder.createWithGeoipDbDigestLine("geoip-db-digest "
        + "&%6A3CA8B7DF61473D5AE5B21711F35F301CE9E8");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipDbDigestMissing()
      throws DescriptorParseException {
    DescriptorBuilder.createWithGeoipDbDigestLine("geoip-db-digest");
  }

  @Test()
  public void testGeoipDbDigestExtraArg() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithGeoip6DbDigestLine("geoip-db-digest "
            + "916A3CA8B7DF61473D5AE5B21711F35F301CE9E8 "
            + "yblgrXtEgF3glaKv5ZvHhRREUI1t1c37SxparXSmYR4Q1yiK5zg4HE8eT9ILPRW9"
            + "3I5W/pZGQxL8Bu42dGjnAQ");
    assertEquals("916A3CA8B7DF61473D5AE5B21711F35F301CE9E8",
        descriptor.getGeoipDbDigest());
  }

  @Test()
  public void testGeoip6DbDigestValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithGeoip6DbDigestLine("geoip6-db-digest "
        + "916A3CA8B7DF61473D5AE5B21711F35F301CE9E8");
    assertEquals("916A3CA8B7DF61473D5AE5B21711F35F301CE9E8",
        descriptor.getGeoip6DbDigestSha1Hex());
  }

  @Test()
  public void testGeoip6DbDigestExtraArg() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithGeoip6DbDigestLine("geoip6-db-digest "
            + "916A3CA8B7DF61473D5AE5B21711F35F301CE9E8 "
            + "yblgrXtEgF3glaKv5ZvHhRREUI1t1c37SxparXSmYR4Q1yiK5zg4HE8eT9ILPRW9"
            + "3I5W/pZGQxL8Bu42dGjnAQ");
    assertEquals("916A3CA8B7DF61473D5AE5B21711F35F301CE9E8",
        descriptor.getGeoip6DbDigest());
  }

  @Test()
  public void testGeoipStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = GeoipStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328898771000L, descriptor.getGeoipStartTimeMillis());
    SortedMap<String, Integer> ips = descriptor.getGeoipClientOrigins();
    assertNotNull(ips);
    assertEquals(1152, ips.get("de").intValue());
    assertEquals(896, ips.get("cn").intValue());
    assertFalse(ips.containsKey("pl"));
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipStartTimeDateOnly()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipStartTimeLine("geoip-start-time "
        + "2012-02-10");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsDash()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de-1152,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,ir=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsZero()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=zero,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,ir=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsNone()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=none,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,ir=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsOther()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,other=200");
  }

  @Test()
  public void testGeoipClientOriginsQuestionMarks()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,??=200");
  }

  @Test()
  public void testGeoipClientOriginsCapital()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins DE=1152,CN=896,US=712,IT=504,RU=352,FR=208,"
        + "GB=208,IR=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsMissingBegin()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins ,cn=896,us=712,it=504,ru=352,fr=208,gb=208,"
        + "ir=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsMissingMiddle()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,,us=712,it=504,ru=352,fr=208,"
        + "gb=208,ir=200");
  }

  @Test(expected = DescriptorParseException.class)
  public void testGeoipClientOriginsMissingEnd()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,cn=896,us=712,it=504,ru=352,fr=208,"
        + "gb=208,");
  }

  @Test()
  public void testGeoipClientOriginsDuplicate()
      throws DescriptorParseException {
    /* dir-spec.txt doesn't say anything about duplicate country codes, so
     * this line is valid, even though it leads to a somewhat undefined
     * parse result. */
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,de=952,cn=896,us=712,it=504,"
        + "ru=352,fr=208,gb=208,ir=200");
  }

  @Test()
  public void testGeoipClientOriginsExtraArg()
      throws DescriptorParseException {
    GeoipStatsBuilder.createWithGeoipClientOriginsLine(
        "geoip-client-origins de=1152,de=952,cn=896,us=712,it=504 "
            + "ru=352 fr=208 gb=208 ir=200");
  }

  @Test()
  public void testDirreqStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DirreqStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328921993000L, descriptor.getDirreqStatsEndMillis());
    assertEquals(86400L, descriptor.getDirreqStatsIntervalLength());
    SortedMap<String, Integer> ips = descriptor.getDirreqV3Ips();
    assertNotNull(ips);
    assertEquals(1544, ips.get("us").intValue());
    assertFalse(ips.containsKey("no"));
    assertTrue(descriptor.getDirreqV2Ips().isEmpty());
    SortedMap<String, Integer> reqs = descriptor.getDirreqV3Reqs();
    assertEquals(832, reqs.get("fr").intValue());
    assertTrue(descriptor.getDirreqV2Reqs().isEmpty());
    SortedMap<String, Integer> resp = descriptor.getDirreqV3Resp();
    assertEquals(10848, resp.get("ok").intValue());
    assertEquals(8, resp.get("not-enough-sigs").intValue());
    resp = descriptor.getDirreqV2Resp();
    assertEquals(1576, resp.get("not-found").intValue());
    assertEquals(0.37, descriptor.getDirreqV2Share(), 0.0001);
    assertEquals(0.37, descriptor.getDirreqV3Share(), 0.0001);
    SortedMap<String, Integer> dl = descriptor.getDirreqV3DirectDl();
    assertEquals(36, dl.get("complete").intValue());
    dl = descriptor.getDirreqV2DirectDl();
    assertEquals(0, dl.get("timeout").intValue());
    dl = descriptor.getDirreqV3TunneledDl();
    assertEquals(10608, dl.get("complete").intValue());
    dl = descriptor.getDirreqV2TunneledDl();
    assertEquals(0, dl.get("complete").intValue());
  }

  @Test()
  public void testDirreqStatsIntervalTwoDays()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqStatsEndLine("dirreq-stats-end "
        + "2012-02-11 00:59:53 (172800 s)");
  }

  @Test()
  public void testDirreqStatsExtraArg()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqStatsEndLine("dirreq-stats-end "
        + "2012-02-11 00:59:53 (172800 s) XXXXXXXXXXXXXXXXXXXXXXXXXXX");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3IpsThreeLetterCountry()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3IpsLine("dirreq-v3-ips "
        + "usa=1544");
  }

  @Test()
  public void testDirreqV3IpsExtraArg()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3IpsLine("dirreq-v3-ips "
        + "ab=12,cd=34 ef=56");
  }

  @Test()
  public void testDirreqV2IpsDigitCountry()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2IpsLine("dirreq-v2-ips 00=8");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3ReqsOneLetterCountry()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3ReqsLine("dirreq-v3-reqs "
        + "u=1744");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV2ReqsNoNumber()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2ReqsLine("dirreq-v2-reqs us=");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3RespTwoEqualSigns()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3RespLine("dirreq-v3-resp "
        + "ok==10848");
  }

  @Test()
  public void testDirreqV3RespExtraArg()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3RespLine("dirreq-v3-resp "
        + "ok=1084 4801=ko");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV2RespNull()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2RespLine("dirreq-v2-resp "
        + "ok=null");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV2ShareComma()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2ShareLine("dirreq-v2-share "
        + "0,37%");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3ShareNoPercent()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3ShareLine("dirreq-v3-share "
        + "0.37");
  }

  @Test()
  public void testDirreqV3ShareExtraArg()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3ShareLine("dirreq-v3-share "
        + "0.37% 123456");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3DirectDlSpace()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3DirectDlLine(
        "dirreq-v3-direct-dl complete 36");
  }

  @Test()
  public void testDirreqV2DirectDlNegative()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2DirectDlLine(
        "dirreq-v2-direct-dl complete=-8");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3TunneledDlTooLarge()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV3TunneledDlLine(
        "dirreq-v3-tunneled-dl complete=2147483648");
  }

  @Test(expected = DescriptorParseException.class)
  public void testDirreqV3TunneledDlDouble()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2TunneledDlLine(
        "dirreq-v2-tunneled-dl complete=0.001");
  }

  @Test()
  public void testDirreqV3TunneledDlExtraArg()
      throws DescriptorParseException {
    DirreqStatsBuilder.createWithDirreqV2TunneledDlLine(
        "dirreq-v2-tunneled-dl complete=-8 incomplete=1/-8");
  }

  @Test()
  public void testEntryStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = EntryStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328925579000L, descriptor.getEntryStatsEndMillis());
    assertEquals(86400L, descriptor.getEntryStatsIntervalLength());
    SortedMap<String, Integer> ips = descriptor.getEntryIps();
    assertNotNull(ips);
    assertEquals(25368, ips.get("ir").intValue());
    assertFalse(ips.containsKey("no"));
  }

  @Test(expected = DescriptorParseException.class)
  public void testEntryStatsEndNoDate() throws DescriptorParseException {
    EntryStatsBuilder.createWithEntryStatsEndLine("entry-stats-end "
        + "01:59:39 (86400 s)");
  }

  @Test(expected = DescriptorParseException.class)
  public void testEntryStatsIpsSemicolon()
      throws DescriptorParseException {
    EntryStatsBuilder.createWithEntryIpsLine("entry-ips "
        + "ir=25368;us=15744");
  }

  @Test()
  public void testCellStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = CellStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328925579000L, descriptor.getCellStatsEndMillis());
    assertEquals(86400L, descriptor.getCellStatsIntervalLength());
    List<Integer> processedCells = descriptor.getCellProcessedCells();
    assertEquals(10, processedCells.size());
    assertEquals(1441, processedCells.get(0).intValue());
    assertEquals(11, processedCells.get(1).intValue());
    List<Double> queuedCells = descriptor.getCellQueuedCells();
    assertEquals(10, queuedCells.size());
    assertEquals(3.29, queuedCells.get(0), 0.001);
    assertEquals(0.00, queuedCells.get(1), 0.001);
    List<Integer> timeInQueue = descriptor.getCellTimeInQueue();
    assertEquals(10, timeInQueue.size());
    assertEquals(524, timeInQueue.get(0).intValue());
    assertEquals(1, timeInQueue.get(1).intValue());
    assertEquals(866, descriptor.getCellCircuitsPerDecile());
  }

  @Test(expected = DescriptorParseException.class)
  public void testCellStatsEndNoSeconds()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellStatsEndLine("cell-stats-end "
        + "2012-02-11 01:59:39 (86400)");
  }

  @Test(expected = DescriptorParseException.class)
  public void testCellProcessedCellsNineComma()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellProcessedCellsLine(
        "cell-processed-cells 1441,11,6,4,2,1,1,1,1,");
  }

  @Test(expected = DescriptorParseException.class)
  public void testCellProcessedCellsEleven()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellQueuedCellsLine("cell-queued-cells "
        + "3.29,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00");
  }

  @Test(expected = DescriptorParseException.class)
  public void testCellTimeInQueueDouble()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellTimeInQueueLine("cell-time-in-queue "
        + "524.0,1.0,1.0,0.0,0.0,25.0,0.0,0.0,0.0,0.0");
  }

  @Test(expected = DescriptorParseException.class)
  public void testCellCircuitsPerDecileNegative()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellCircuitsPerDecileLine(
        "cell-circuits-per-decile -866");
  }

  @Test()
  public void testCellCircuitsPerDecileExtraArg()
      throws DescriptorParseException {
    CellStatsBuilder.createWithCellCircuitsPerDecileLine(
        "cell-circuits-per-decile 866 866 866 866 866");
  }

  @Test()
  public void testConnBiDirectValid()
      throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithConnBiDirectLine("conn-bi-direct 2012-02-11 01:59:39 "
        + "(86400 s) 42173,1591,1310,1744");
    assertEquals(1328925579000L,
        descriptor.getConnBiDirectStatsEndMillis());
    assertEquals(86400L, descriptor.getConnBiDirectStatsIntervalLength());
    assertEquals(42173, descriptor.getConnBiDirectBelow());
    assertEquals(1591, descriptor.getConnBiDirectRead());
    assertEquals(1310, descriptor.getConnBiDirectWrite());
    assertEquals(1744, descriptor.getConnBiDirectBoth());
  }

  @Test(expected = DescriptorParseException.class)
  public void testConnBiDirectStatsFive()
      throws DescriptorParseException {
    DescriptorBuilder.createWithConnBiDirectLine("conn-bi-direct "
        + "2012-02-11 01:59:39 (86400 s) 42173,1591,1310,1744,42");
  }

  @Test()
  public void testConnBiDirectStatsExtraArg()
      throws DescriptorParseException {
    DescriptorBuilder.createWithConnBiDirectLine("conn-bi-direct "
        + "2012-02-11 01:59:39 (86400 s) 42173,1591,1310,1744 +1");
  }

  @Test()
  public void testExitStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = ExitStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328925579000L, descriptor.getExitStatsEndMillis());
    assertEquals(86400L, descriptor.getExitStatsIntervalLength());
    String[] ports = new String[] { "25", "80", "443", "49755",
        "52563", "52596", "57528", "60912", "61351", "64811", "other" };
    int[] writtenValues = new int[] { 74647, 31370, 20577, 23, 12, 1111,
        4, 11, 6, 3365, 2592 };
    int index = 0;
    for (Map.Entry<String, Long> e :
        descriptor.getExitKibibytesWritten().entrySet()) {
      assertEquals(ports[index], e.getKey());
      assertEquals(writtenValues[index++], e.getValue().intValue());
    }
    int[] readValues = new int[] { 35562, 1254256, 110279, 9396, 1911,
        648, 1188, 1427, 1824, 14, 3054 };
    index = 0;
    for (Map.Entry<String, Long> e :
        descriptor.getExitKibibytesRead().entrySet()) {
      assertEquals(ports[index], e.getKey());
      assertEquals(readValues[index++], e.getValue().intValue());
    }
    int[] streamsValues = new int[] { 369748, 64212, 151660, 4, 4, 4, 4,
        4, 4, 4, 1212 };
    index = 0;
    for (Map.Entry<String, Long> e :
        descriptor.getExitStreamsOpened().entrySet()) {
      assertEquals(ports[index], e.getKey());
      assertEquals(streamsValues[index++], e.getValue().intValue());
    }
  }

  @Test(expected = DescriptorParseException.class)
  public void testExitStatsEndNoSeconds()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitStatsEndLine("exit-stats-end "
        + "2012-02-11 01:59 (86400 s)");
  }

  @Test(expected = DescriptorParseException.class)
  public void testExitStatsWrittenNegativePort()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitKibibytesWrittenLine(
        "exit-kibibytes-written -25=74647");
  }

  @Test(expected = DescriptorParseException.class)
  public void testExitStatsWrittenUnknown()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitKibibytesWrittenLine(
        "exit-kibibytes-written unknown=74647");
  }

  @Test(expected = DescriptorParseException.class)
  public void testExitStatsReadNegativeBytes()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitKibibytesReadLine(
        "exit-kibibytes-read 25=-35562");
  }

  @Test()
  public void testExitStatsReadTooLarge()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitKibibytesReadLine(
        "exit-kibibytes-read other=2282907805");
  }

  @Test()
  public void testExitStatsStreamsTooLarge()
      throws DescriptorParseException {
    ExitStatsBuilder.createWithExitStreamsOpenedLine(
        "exit-streams-opened 25=2147483648");
  }

  @Test()
  public void testExitStatsStreamsExtraArg() throws DescriptorParseException {
    ExitStatsBuilder.createWithExitStreamsOpenedLine(
        "exit-streams-opened 25=21474 3648");
  }

  @Test()
  public void testBridgeStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = BridgeStatsBuilder
        .createWithDefaultLines();
    assertEquals(1328925579000L, descriptor.getBridgeStatsEndMillis());
    assertEquals(86400L, descriptor.getBridgeStatsIntervalLength());
    SortedMap<String, Integer> ips = descriptor.getBridgeIps();
    assertNotNull(ips);
    assertEquals(24, ips.get("ir").intValue());
    assertEquals(16, ips.get("sy").intValue());
    assertFalse(ips.containsKey("no"));
    SortedMap<String, Integer> ver = descriptor.getBridgeIpVersions();
    assertNotNull(ver);
    assertEquals(8, ver.get("v4").intValue());
    assertEquals(16, ver.get("v6").intValue());
    assertFalse(ver.containsKey("v8"));
    SortedMap<String, Integer> trans = descriptor.getBridgeIpTransports();
    assertNotNull(trans);
    assertEquals(8, trans.get("<OR>").intValue());
    assertEquals(792, trans.get("obfs2").intValue());
    assertEquals(1728, trans.get("obfs3").intValue());
  }

  @Test(expected = DescriptorParseException.class)
  public void testBridgeStatsEndIntervalZero()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeStatsEndLine("bridge-stats-end "
        + "2012-02-11 01:59:39 (0 s)");
  }

  @Test()
  public void testBridgeStatsEndExtraArg()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeStatsEndLine("bridge-stats-end "
        + "2012-02-11 01:59:39 (86400 s) 99999999999999999999999999999999");
  }

  @Test(expected = DescriptorParseException.class)
  public void testBridgeIpsDouble()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpsLine("bridge-ips ir=24.5");
  }

  @Test(expected = DescriptorParseException.class)
  public void testBridgeIpsNonAsciiKeyword()
      throws DescriptorParseException {
    DescriptorBuilder.createWithNonAsciiLineBytes(new byte[] {
        0x14, (byte) 0xfe, 0x18,                  // non-ascii chars
        0x62, 0x72, 0x69, 0x64, 0x67, 0x65, 0x2d, // "bridge-"
        0x69, 0x70, 0x73 }, false);               // "ips" (no newline)
  }

  @Test()
  public void testBridgeIpsExtraArg()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpsLine("bridge-ips ir=24 5");
  }

  @Test(expected = DescriptorParseException.class)
  public void testBridgeIpVersionsDouble()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpVersionsLine(
        "bridge-ip-versions v4=24.5");
  }

  @Test()
  public void testBridgeIpVersionsExtraArg()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpVersionsLine(
        "bridge-ip-versions v4=24 5");
  }

  @Test(expected = DescriptorParseException.class)
  public void testBridgeIpTransportsDouble()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpTransportsLine(
        "bridge-ip-transports obfs2=24.5");
  }

  @Test()
  public void testBridgeIpTransportsUnderscore()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpTransportsLine(
        "bridge-ip-transports meek=32,obfs3_websocket=8,websocket=64");
  }

  @Test()
  public void testBridgeIpTransportsExtraArg()
      throws DescriptorParseException {
    BridgeStatsBuilder.createWithBridgeIpTransportsLine(
        "bridge-ip-transports obfs2=24 5");
  }

  @Test()
  public void testHidservStatsValid() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor = HidservStatsBuilder
        .createWithDefaultLines();
    assertEquals(1449152816000L, descriptor.getHidservStatsEndMillis());
    assertEquals(86400L, descriptor.getHidservStatsIntervalLength());
    assertEquals(36474281.0, descriptor.getHidservRendRelayedCells(),
        0.0001);
    Map<String, Double> params =
        descriptor.getHidservRendRelayedCellsParameters();
    assertTrue(params.containsKey("delta_f"));
    assertEquals(2048.0, params.remove("delta_f"), 0.0001);
    assertTrue(params.containsKey("epsilon"));
    assertEquals(0.3, params.remove("epsilon"), 0.0001);
    assertTrue(params.containsKey("bin_size"));
    assertEquals(1024.0, params.remove("bin_size"), 0.0001);
    assertTrue(params.isEmpty());
    assertEquals(-3.0, descriptor.getHidservDirOnionsSeen(), 0.0001);
    params = descriptor.getHidservDirOnionsSeenParameters();
    assertTrue(params.containsKey("delta_f"));
    assertEquals(8.0, params.remove("delta_f"), 0.0001);
    assertTrue(params.containsKey("epsilon"));
    assertEquals(0.3, params.remove("epsilon"), 0.0001);
    assertTrue(params.containsKey("bin_size"));
    assertEquals(8.0, params.remove("bin_size"), 0.0001);
    assertTrue(params.isEmpty());
  }

  @Test()
  public void testHidservStatsEndLineMissing()
      throws DescriptorParseException {
    ExtraInfoDescriptor descriptor =
        HidservStatsBuilder.createWithHidservStatsEndLine(null);
    assertEquals(-1L, descriptor.getHidservStatsEndMillis());
    assertEquals(-1L, descriptor.getHidservStatsIntervalLength());
  }

  @Test()
  public void testHidservRendRelayedCellsNoParams()
        throws DescriptorParseException {
    ExtraInfoDescriptor descriptor =
        HidservStatsBuilder.createWithHidservRendRelayedCellsLine(
        "hidserv-rend-relayed-cells 36474281");
    assertEquals(36474281.0, descriptor.getHidservRendRelayedCells(),
        0.0001);
    assertTrue(
        descriptor.getHidservRendRelayedCellsParameters().isEmpty());
  }

  @Test(expected = DescriptorParseException.class)
  public void testHidservDirOnionsSeenCommaSeparatedParams()
      throws DescriptorParseException {
    HidservStatsBuilder.createWithHidservDirOnionsSeenLine(
        "hidserv-dir-onions-seen -3 delta_f=8,epsilon=0.30,bin_size=8");
  }

  @Test(expected = DescriptorParseException.class)
  public void testHidservDirOnionsSeenNoDoubleParams()
      throws DescriptorParseException {
    HidservStatsBuilder.createWithHidservDirOnionsSeenLine(
        "hidserv-dir-onions-seen -3 delta_f=A epsilon=B bin_size=C");
  }

  @Test()
  public void testHidservDirOnionsSeenExtraArg()
      throws DescriptorParseException {
    HidservStatsBuilder.createWithHidservDirOnionsSeenLine(
        "hidserv-dir-onions-seen -3 delta_f=8 epsilon=0.30 bin_size=8 pi=3");
  }

  @Test()
  public void testRouterSignatureOpt()
      throws DescriptorParseException {
    DescriptorBuilder.createWithRouterSignatureLines("opt "
        + "router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "crypto lines are ignored anyway\n"
        + "-----END SIGNATURE-----");
  }

  @Test(expected = DescriptorParseException.class)
  public void testRouterSignatureNotLastLine()
      throws DescriptorParseException {
    DescriptorBuilder.createWithRouterSignatureLines("router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "o4j+kH8UQfjBwepUnr99v0ebN8RpzHJ/lqYsTojXHy9kMr1RNI9IDeSzA7PSqT"
        + "uV\n4PL8QsGtlfwthtIoZpB2srZeyN/mcpA9fa1JXUrt/UN9K/+32Cyaad7h0n"
        + "HE6Xfb\njqpXDpnBpvk4zjmzjjKYnIsUWTnADmu0fo3xTRqXi7g=\n"
        + "-----END SIGNATURE-----\npublished 2012-02-11 09:08:36");
  }

  @Test(expected = DescriptorParseException.class)
  public void testUnrecognizedLineFail()
      throws DescriptorParseException {
    String unrecognizedLine = "unrecognized-line 1";
    DescriptorBuilder.createWithUnrecognizedLine(unrecognizedLine, true);
  }

  @Test()
  public void testUnrecognizedLineIgnore()
      throws DescriptorParseException {
    String unrecognizedLine = "unrecognized-line 1";
    ExtraInfoDescriptor descriptor = DescriptorBuilder
        .createWithUnrecognizedLine(unrecognizedLine, false);
    List<String> unrecognizedLines = new ArrayList<>();
    unrecognizedLines.add(unrecognizedLine);
    assertEquals(unrecognizedLines, descriptor.getUnrecognizedLines());
  }

  private static final String IDENTITY_ED25519_LINES =
      "identity-ed25519\n"
      + "-----BEGIN ED25519 CERT-----\n"
      + "AQQABiX1AVGv5BuzJroQXbOh6vv1nbwc5rh2S13PyRFuLhTiifK4AQAgBACBCMwr"
      + "\n4qgIlFDIzoC9ieJOtSkwrK+yXJPKlP8ojvgkx8cGKvhokOwA1eYDombzfwHcJ1"
      + "EV\nbhEn/6g8i7wzO3LoqefIUrSAeEExOAOmm5mNmUIzL8EtnT6JHCr/sqUTUgA="
      + "\n"
      + "-----END ED25519 CERT-----";

  private static final String MASTER_KEY_ED25519_LINE =
      "master-key-ed25519 gQjMK+KoCJRQyM6AvYniTrUpMKyvslyTypT/KI74JMc";

  private static final String ROUTER_SIG_ED25519_LINE =
      "router-sig-ed25519 y7WF9T2GFwkSDPZEhB55HgquIFOl5uXUFMYJPq3CXXUTKeJ"
      + "kSrtaZUB5s34fWdHQNtl84mH4dVaFMunHnwgYAw";

  @Test()
  public void testEd25519() throws DescriptorParseException {
    ExtraInfoDescriptor descriptor =
        DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
    assertEquals(IDENTITY_ED25519_LINES.substring(
        IDENTITY_ED25519_LINES.indexOf("\n") + 1),
        descriptor.getIdentityEd25519());
    assertEquals(MASTER_KEY_ED25519_LINE.substring(
        MASTER_KEY_ED25519_LINE.indexOf(" ") + 1),
        descriptor.getMasterKeyEd25519());
    assertEquals(ROUTER_SIG_ED25519_LINE.substring(
        ROUTER_SIG_ED25519_LINE.indexOf(" ") + 1),
        descriptor.getRouterSignatureEd25519());
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519IdentityMasterKeyMismatch()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        "master-key-ed25519 AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        ROUTER_SIG_ED25519_LINE);
  }

  @Test()
  public void testEd25519IdentityMissing()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(null,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519IdentityDuplicate()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES + "\n"
        + IDENTITY_ED25519_LINES, MASTER_KEY_ED25519_LINE,
        ROUTER_SIG_ED25519_LINE);
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519IdentityEmptyCrypto()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines("identity-ed25519\n"
        + "-----BEGIN ED25519 CERT-----\n-----END ED25519 CERT-----",
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
  }

  @Test()
  public void testEd25519MasterKeyMissing()
      throws DescriptorParseException {
    ExtraInfoDescriptor descriptor =
        DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        null, ROUTER_SIG_ED25519_LINE);
    assertEquals(MASTER_KEY_ED25519_LINE.substring(
        MASTER_KEY_ED25519_LINE.indexOf(" ") + 1),
        descriptor.getMasterKeyEd25519());
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519MasterKeyDuplicate()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE + "\n" + MASTER_KEY_ED25519_LINE,
        ROUTER_SIG_ED25519_LINE);
  }

  @Test()
  public void testEd25519RouterSigMissing()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, null);
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519RouterSigDuplicate()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE + "\n"
        + ROUTER_SIG_ED25519_LINE);
  }

  @Test(expected = DescriptorParseException.class)
  public void testEd25519FollowedbyUnrecognizedLine()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE
        + "\nunrecognized-line 1");
  }

  @Test()
  public void testExtraInfoDigestSha256Relay()
      throws DescriptorParseException {
    byte[] descriptorBytes = ("extra-info Unnamed "
        + "EA5B335055D2F03013FF030381F02B1C631EC723\n"
        + "identity-ed25519\n"
        + "-----BEGIN ED25519 CERT-----\n"
        + "AQQABiZRAenzZorGtx6xapoEeaqcLLOk3uWwJXTvOVLluSXXbRSZAQAgBADLN5"
        + "wp\nCEOrRbshSbj1NDAUgc6cxU65M/Vx1x+b5+EXbkQZ5uiyB4pphVF5kPPT1P"
        + "SleYqM\n8j+tlKh2i6+Xr0xScSPpmtG00/D0MoRlT7ZdaaaT5iw1DWDQCZ8BHG"
        + "lAZwU=\n"
        + "-----END ED25519 CERT-----\n"
        + "published 2015-12-01 04:38:12\n"
        + "write-history 2015-12-01 01:40:37 (14400 s) 88704000,60825600,"
        + "61747200,76953600,61516800,59443200\n"
        + "read-history 2015-12-01 01:40:37 (14400 s) 87321600,59443200,"
        + "59904000,74880000,60364800,58060800\n"
        + "router-sig-ed25519 c6eUeJs/SVjun3JhmjByEeWdRDyunSMAnGVhx71JiRj"
        + "YzR8x5IcPebylG7m10wiolFxinvw78UhrrGo9Sq5ZBw\n"
        + "router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "oC2qFHCDOKSRoIPR86jdRxEYia390Z4d8fT0yr/1mg4RQ7lHmxlzFT6QxCswdX"
        + "Ry\nvGNGR0wARySgyE+YKKWYn/Hp547JhhWd9Oc7BuFMY0XMvl/HOo+B9VjW+l"
        + "nv6UBE\niqxx3C3Iw0ymohvOenyCUa/7TmsT7eVotDO57uIoGEc=\n"
        + "-----END SIGNATURE-----\n"
        + "").getBytes();
    RelayExtraInfoDescriptor descriptor =
        new RelayExtraInfoDescriptorImpl(descriptorBytes, true);
    assertEquals("Pt1BtzfRwhYqGCDo8jjchS8nJP3ovrDyHGn+dqPbMgw",
        descriptor.getDigestSha256Base64());
  }

  @Test()
  public void testExtraInfoDigestSha256Bridge()
      throws DescriptorParseException {
    byte[] descriptorBytes = ("extra-info idideditheconfig "
        + "DC28749EC9E26E61DE492E46CD830379E9931B09\n"
        + "master-key-ed25519 "
        + "38FzmOIE6Mm85Ytx0MhFM6X9EuxWRUgb6HjyMGuO2AU\n"
        + "published 2015-12-03 13:23:19\n"
        + "write-history 2015-12-03 09:59:32 (14400 s) 53913600,52992000,"
        + "53222400,53222400,53452800,53222400\n"
        + "read-history 2015-12-03 09:59:32 (14400 s) 61056000,60364800,"
        + "60364800,60134400,60595200,60364800\n"
        + "geoip-db-digest 5BF366AD4A0572D82A1A0F6628AF8EF7725E3AB9\n"
        + "geoip6-db-digest 212DE17D5A368DCAFA19B95F168BFFA101145A93\n"
        + "router-digest-sha256 "
        + "TvrqpjI7OmCtwGwair/NHUxg5ROVVQYz6/EDyXsDHR4\n"
        + "router-digest 00B98F076B586272C3172B7F3DA29ADEE75F2ED8\n")
        .getBytes();
    BridgeExtraInfoDescriptor descriptor =
        new BridgeExtraInfoDescriptorImpl(descriptorBytes, true);
    assertEquals("TvrqpjI7OmCtwGwair/NHUxg5ROVVQYz6/EDyXsDHR4",
        descriptor.getDigestSha256Base64());
  }
}

