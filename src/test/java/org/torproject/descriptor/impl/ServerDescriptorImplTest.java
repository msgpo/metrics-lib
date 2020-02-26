/* Copyright 2012--2020 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.torproject.descriptor.BandwidthHistory;
import org.torproject.descriptor.BridgeServerDescriptor;
import org.torproject.descriptor.DescriptorParseException;
import org.torproject.descriptor.ServerDescriptor;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeSet;

/* Test parsing of relay server descriptors. */
public class ServerDescriptorImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /* Helper class to build a descriptor based on default data and
   * modifications requested by test methods. */
  private static class DescriptorBuilder {

    private String routerLine = "router saberrider2008 94.134.192.243 "
        + "9001 0 0";

    private static ServerDescriptor createWithRouterLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.routerLine = line;
      return db.buildDescriptor();
    }

    private String bandwidthLine = "bandwidth 51200 51200 53470";

    private static ServerDescriptor createWithBandwidthLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.bandwidthLine = line;
      return db.buildDescriptor();
    }

    private String platformLine = "platform Tor 0.2.2.35 "
        + "(git-b04388f9e7546a9f) on Linux i686";

    private static ServerDescriptor createWithPlatformLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.platformLine = line;
      return db.buildDescriptor();
    }

    private String publishedLine = "published 2012-01-01 04:03:19";

    private static ServerDescriptor createWithPublishedLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.publishedLine = line;
      return db.buildDescriptor();
    }

    private String fingerprintLine = "opt fingerprint D873 3048 FC8E "
        + "C910 2466 AD8F 3098 622B F1BF 71FD";

    private static ServerDescriptor createWithFingerprintLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.fingerprintLine = line;
      return db.buildDescriptor();
    }

    private String hibernatingLine = null;

    private static ServerDescriptor createWithHibernatingLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.hibernatingLine = line;
      return db.buildDescriptor();
    }

    private String uptimeLine = "uptime 48";

    private static ServerDescriptor createWithUptimeLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.uptimeLine = line;
      return db.buildDescriptor();
    }

    private String onionKeyLines = "onion-key\n"
        + "-----BEGIN RSA PUBLIC KEY-----\n"
        + "MIGJAoGBAKM+iiHhO6eHsvd6Xjws9z9EQB1V/Bpuy5ciGJ1U4V9SeiKooSo5Bp"
        + "PL\no3XT+6PIgzl3R6uycjS3Ejk47vLEJdcVTm/VG6E0ppu3olIynCI4QryfCE"
        + "uC3cTF\n9wE4WXY4nX7w0RTN18UVLxrt1A9PP0cobFNiPs9rzJCbKFfacOkpAg"
        + "MBAAE=\n"
        + "-----END RSA PUBLIC KEY-----";

    private static ServerDescriptor createWithOnionKeyLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.onionKeyLines = lines;
      return db.buildDescriptor();
    }

    private String signingKeyLines = "signing-key\n"
        + "-----BEGIN RSA PUBLIC KEY-----\n"
        + "MIGJAoGBALMm3r3QDh482Ewe6Ub9wvRIfmEkoNX6q5cEAtQRNHSDcNx41gjELb"
        + "cl\nEniVMParBYACKfOxkS+mTTnIRDKVNEJTsDOwryNrc4X9JnPc/nn6ymYPiN"
        + "DhUROG\n8URDIhQoixcUeyyrVB8sxliSstKimulGnB7xpjYOlO8JKaHLNL4TAg"
        + "MBAAE=\n"
        + "-----END RSA PUBLIC KEY-----";

    private static ServerDescriptor createWithSigningKeyLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.signingKeyLines = lines;
      return db.buildDescriptor();
    }

    private String onionKeyCrosscertLines = null;

    private static ServerDescriptor createWithOnionKeyCrosscertLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.onionKeyCrosscertLines = lines;
      return db.buildDescriptor();
    }

    private String ntorOnionKeyCrosscertLines = null;

    private static ServerDescriptor createWithNtorOnionKeyCrosscertLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.ntorOnionKeyCrosscertLines = lines;
      return db.buildDescriptor();
    }

    private String exitPolicyLines = "reject *:*";

    private static ServerDescriptor createWithExitPolicyLines(
        String lines) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.exitPolicyLines = lines;
      return db.buildDescriptor();
    }

    private String contactLine = "contact Random Person <nobody AT "
        + "example dot com>";

    private static ServerDescriptor createWithContactLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.contactLine = line;
      return db.buildDescriptor();
    }

    private String familyLine = null;

    private static ServerDescriptor createWithFamilyLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.familyLine = line;
      return db.buildDescriptor();
    }

    private String readHistoryLine = null;

    private static ServerDescriptor createWithReadHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.readHistoryLine = line;
      return db.buildDescriptor();
    }

    private String writeHistoryLine = null;

    private static ServerDescriptor createWithWriteHistoryLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.writeHistoryLine = line;
      return db.buildDescriptor();
    }

    private String eventdnsLine = null;

    private static ServerDescriptor createWithEventdnsLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.eventdnsLine = line;
      return db.buildDescriptor();
    }

    private String cachesExtraInfoLine = null;

    private static ServerDescriptor createWithCachesExtraInfoLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.cachesExtraInfoLine = line;
      return db.buildDescriptor();
    }

    private String extraInfoDigestLine = "opt extra-info-digest "
        + "1469D1550738A25B1E7B47CDDBCD7B2899F51B74";

    private static ServerDescriptor createWithExtraInfoDigestLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.extraInfoDigestLine = line;
      return db.buildDescriptor();
    }

    private String hiddenServiceDirLine = "opt hidden-service-dir";

    private static ServerDescriptor createWithHiddenServiceDirLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.hiddenServiceDirLine = line;
      return db.buildDescriptor();
    }

    private String protocolsLine = null;

    private static ServerDescriptor createWithProtocolsLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.protocolsLine = line;
      return db.buildDescriptor();
    }

    private String protoLine = "proto Cons=1-2 Desc=1-2 DirCache=1 HSDir=1 "
        + "HSIntro=3 HSRend=1-2 Link=1-4 LinkAuth=1 Microdesc=1-2 Relay=1-2";

    private static ServerDescriptor createWithProtoLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.protoLine = line;
      return db.buildDescriptor();
    }

    private String allowSingleHopExitsLine = null;

    private static ServerDescriptor
        createWithAllowSingleHopExitsLine(String line)
        throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.allowSingleHopExitsLine = line;
      return db.buildDescriptor();
    }

    private String ipv6PolicyLine = null;

    private static ServerDescriptor createWithIpv6PolicyLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.ipv6PolicyLine = line;
      return db.buildDescriptor();
    }

    private String ntorOnionKeyLine = null;

    private static ServerDescriptor createWithNtorOnionKeyLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.ntorOnionKeyLine = line;
      return db.buildDescriptor();
    }

    private String tunnelledDirServerLine = null;

    private static ServerDescriptor createWithTunnelledDirServerLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.tunnelledDirServerLine = line;
      return db.buildDescriptor();
    }

    private String routerSignatureLines = "router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "o4j+kH8UQfjBwepUnr99v0ebN8RpzHJ/lqYsTojXHy9kMr1RNI9IDeSzA7PSqT"
        + "uV\n4PL8QsGtlfwthtIoZpB2srZeyN/mcpA9fa1JXUrt/UN9K/+32Cyaad7h0n"
        + "HE6Xfb\njqpXDpnBpvk4zjmzjjKYnIsUWTnADmu0fo3xTRqXi7g=\n"
        + "-----END SIGNATURE-----";

    private static ServerDescriptor createWithRouterSignatureLines(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.routerSignatureLines = line;
      return db.buildDescriptor();
    }

    private String unrecognizedLine = null;

    private static ServerDescriptor createWithUnrecognizedLine(String line)
        throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.unrecognizedLine = line;
      return db.buildDescriptor();
    }

    private byte[] nonAsciiLineBytes = null;

    private static ServerDescriptor createWithNonAsciiLineBytes(
        byte[] lineBytes) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.nonAsciiLineBytes = lineBytes;
      return db.buildDescriptor();
    }

    private String identityEd25519Lines = null;

    private String masterKeyEd25519Line = null;

    private String routerSigEd25519Line = null;

    private static ServerDescriptor createWithEd25519Lines(
        String identityEd25519Lines, String masterKeyEd25519Line,
        String routerSigEd25519Line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.identityEd25519Lines = identityEd25519Lines;
      db.masterKeyEd25519Line = masterKeyEd25519Line;
      db.routerSigEd25519Line = routerSigEd25519Line;
      return db.buildDescriptor();
    }

    private String bridgeDistributionRequestLine = null;

    private static ServerDescriptor createWithBridgeDistributionRequestLine(
        String line) throws DescriptorParseException {
      DescriptorBuilder db = new DescriptorBuilder();
      db.bridgeDistributionRequestLine = line;
      return db.buildDescriptor();
    }

    private byte[] buildDescriptorBytes() {
      StringBuilder sb = new StringBuilder();
      if (this.routerLine != null) {
        sb.append(this.routerLine).append("\n");
      }
      if (this.identityEd25519Lines != null) {
        sb.append(this.identityEd25519Lines).append("\n");
      }
      if (this.masterKeyEd25519Line != null) {
        sb.append(this.masterKeyEd25519Line).append("\n");
      }
      if (this.bandwidthLine != null) {
        sb.append(this.bandwidthLine).append("\n");
      }
      if (this.platformLine != null) {
        sb.append(this.platformLine).append("\n");
      }
      if (this.publishedLine != null) {
        sb.append(this.publishedLine).append("\n");
      }
      if (this.fingerprintLine != null) {
        sb.append(this.fingerprintLine).append("\n");
      }
      if (this.hibernatingLine != null) {
        sb.append(this.hibernatingLine).append("\n");
      }
      if (this.uptimeLine != null) {
        sb.append(this.uptimeLine).append("\n");
      }
      if (this.onionKeyLines != null) {
        sb.append(this.onionKeyLines).append("\n");
      }
      if (this.signingKeyLines != null) {
        sb.append(this.signingKeyLines).append("\n");
      }
      if (this.onionKeyCrosscertLines != null) {
        sb.append(this.onionKeyCrosscertLines).append("\n");
      }
      if (this.ntorOnionKeyCrosscertLines != null) {
        sb.append(this.ntorOnionKeyCrosscertLines).append("\n");
      }
      if (this.exitPolicyLines != null) {
        sb.append(this.exitPolicyLines).append("\n");
      }
      if (this.contactLine != null) {
        sb.append(this.contactLine).append("\n");
      }
      if (this.bridgeDistributionRequestLine != null) {
        sb.append(this.bridgeDistributionRequestLine).append("\n");
      }
      if (this.familyLine != null) {
        sb.append(this.familyLine).append("\n");
      }
      if (this.readHistoryLine != null) {
        sb.append(this.readHistoryLine).append("\n");
      }
      if (this.writeHistoryLine != null) {
        sb.append(this.writeHistoryLine).append("\n");
      }
      if (this.eventdnsLine != null) {
        sb.append(this.eventdnsLine).append("\n");
      }
      if (this.cachesExtraInfoLine != null) {
        sb.append(this.cachesExtraInfoLine).append("\n");
      }
      if (this.extraInfoDigestLine != null) {
        sb.append(this.extraInfoDigestLine).append("\n");
      }
      if (this.hiddenServiceDirLine != null) {
        sb.append(this.hiddenServiceDirLine).append("\n");
      }
      if (this.protocolsLine != null) {
        sb.append(this.protocolsLine).append("\n");
      }
      if (this.protoLine != null) {
        sb.append(this.protoLine).append("\n");
      }
      if (this.allowSingleHopExitsLine != null) {
        sb.append(this.allowSingleHopExitsLine).append("\n");
      }
      if (this.ipv6PolicyLine != null) {
        sb.append(this.ipv6PolicyLine).append("\n");
      }
      if (this.ntorOnionKeyLine != null) {
        sb.append(this.ntorOnionKeyLine).append("\n");
      }
      if (this.tunnelledDirServerLine != null) {
        sb.append(this.tunnelledDirServerLine).append("\n");
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

    private ServerDescriptorImpl buildDescriptor()
        throws DescriptorParseException {
      byte[] descriptorBytes = this.buildDescriptorBytes();
      if (null == descriptorBytes) {
        descriptorBytes = new byte[0];
      }
      return new RelayServerDescriptorImpl(descriptorBytes,
          new int[] { 0, descriptorBytes.length }, null);
    }
  }

  @Test
  public void testSampleDescriptor() throws DescriptorParseException {
    DescriptorBuilder db = new DescriptorBuilder();
    ServerDescriptor descriptor = db.buildDescriptor();
    assertEquals("saberrider2008", descriptor.getNickname());
    assertEquals("94.134.192.243", descriptor.getAddress());
    assertEquals(9001, descriptor.getOrPort());
    assertEquals(0, descriptor.getSocksPort());
    assertEquals(0, descriptor.getDirPort());
    assertEquals("Tor 0.2.2.35 (git-b04388f9e7546a9f) on Linux i686",
        descriptor.getPlatform());
    assertEquals(new TreeSet<>(Arrays.asList(
        1L, 2L, 3L, 4L)), descriptor.getProtocols().get("Link"));
    assertEquals(new TreeSet<>(Arrays.asList(
        1L)), descriptor.getProtocols().get("LinkAuth"));
    assertEquals(1325390599000L, descriptor.getPublishedMillis());
    assertEquals("D8733048FC8EC9102466AD8F3098622BF1BF71FD",
        descriptor.getFingerprint());
    assertEquals(48, descriptor.getUptime().longValue());
    assertEquals(51200, descriptor.getBandwidthRate());
    assertEquals(51200, descriptor.getBandwidthBurst());
    assertEquals(53470, descriptor.getBandwidthObserved());
    assertEquals("1469D1550738A25B1E7B47CDDBCD7B2899F51B74",
        descriptor.getExtraInfoDigestSha1Hex());
    assertTrue(descriptor.isHiddenServiceDir());
    assertEquals("Random Person <nobody AT example dot com>",
        descriptor.getContact());
    assertEquals(Arrays.asList("reject *:*"),
        descriptor.getExitPolicyLines());
    assertFalse(descriptor.isHibernating());
    assertNull(descriptor.getFamilyEntries());
    assertNull(descriptor.getReadHistory());
    assertNull(descriptor.getWriteHistory());
    assertFalse(descriptor.getUsesEnhancedDnsLogic());
    assertFalse(descriptor.getCachesExtraInfo());
    assertFalse(descriptor.getAllowSingleHopExits());
    assertTrue(descriptor.getUnrecognizedLines().isEmpty());
    assertEquals("a9635dd801ad98dac43aff49baa2dbbaf050222d",
        descriptor.getDigestSha1Hex());
    assertEquals("kvdJKQ6R9i8x1nDqJZ34JFWsu6TquLqQy54nheSWrOY",
        descriptor.getDigestSha256Base64());
  }

  @Test
  public void testRouterLineMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'router' is contained 0 times, but "
        + "must be contained exactly once.");
    DescriptorBuilder.createWithRouterLine(null);
  }

  @Test
  public void testRouterOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithRouterLine("opt router saberrider2008 "
        + "94.134.192.243 9001 0 0");
    assertEquals("saberrider2008", descriptor.getNickname());
    assertEquals("94.134.192.243", descriptor.getAddress());
    assertEquals(9001, descriptor.getOrPort());
    assertEquals(0, descriptor.getSocksPort());
    assertEquals(0, descriptor.getDirPort());
  }

  @Test
  public void testRouterLinePrecedingHibernatingLine()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'router' must be contained in "
        + "the first line.");
    DescriptorBuilder.createWithRouterLine("hibernating 1\nrouter "
        + "saberrider2008 94.134.192.243 9001 0 0");
  }

  @Test
  public void testNicknameMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'router  94.134.192.243 9001 0 0' "
        + "in server descriptor.");
    DescriptorBuilder.createWithRouterLine("router  94.134.192.243 9001 "
        + "0 0");
  }

  @Test
  public void testNicknameInvalidChar() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal nickname in line "
        + "'router $aberrider2008 94.134.192.243 9001 0 0'.");
    DescriptorBuilder.createWithRouterLine("router $aberrider2008 "
        + "94.134.192.243 9001 0 0");
  }

  @Test
  public void testNicknameTooLong() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal nickname in line 'router "
        + "saberrider2008ReallyLongNickname 94.134.192.243 9001 0 0'.");
    DescriptorBuilder.createWithRouterLine("router "
        + "saberrider2008ReallyLongNickname 94.134.192.243 9001 0 0");
  }

  @Test
  public void testNicknameTwoSpaces() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithRouterLine("router saberrider2008  "
        + "94.134.192.243 9001 0 0");
    assertEquals("saberrider2008", descriptor.getNickname());
    assertEquals("94.134.192.243", descriptor.getAddress());
  }

  @Test
  public void testAddress24() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'94.134.192/24' in line 'router saberrider2008 "
        + "94.134.192/24 9001 0 0' is not a valid IPv4 address.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192/24 9001 0 0");
  }

  @Test
  public void testAddress294() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'294.134.192.243' in line 'router "
        + "saberrider2008 294.134.192.243 9001 0 0' is not a valid "
        + "IPv4 address.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "294.134.192.243 9001 0 0");
  }

  @Test
  public void testAddressMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(
        "Illegal line 'router saberrider2008  9001 0 0' in server descriptor.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008  9001 "
        + "0 0");
  }

  @Test
  public void testOrPort99001() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'99001' in line 'router saberrider2008 "
        + "94.134.192.243 99001 0 0' is not a valid port number.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192.243 99001 0 0");
  }

  @Test
  public void testOrPortMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'router saberrider2008 "
        + "94.134.192.243  0 0' in server descriptor.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192.243  0 0");
  }

  @Test
  public void testOrPortOne() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'one' in line 'router saberrider2008 "
        + "94.134.192.243 one 0 0' is not a valid port number.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192.243 one 0 0");
  }

  @Test
  public void testOrPortNewline() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal keyword in line ' 0 0'.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192.243 0\n 0 0");
  }

  @Test
  public void testDirPortMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'router saberrider2008 "
        + "94.134.192.243 9001 0 ' in server descriptor.");
    DescriptorBuilder.createWithRouterLine("router saberrider2008 "
        + "94.134.192.243 9001 0 ");
  }

  @Test
  public void testPlatformMissing() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPlatformLine(null);
    assertNull(descriptor.getPlatform());
  }

  @Test
  public void testPlatformOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPlatformLine("opt platform Tor 0.2.2.35 "
        + "(git-b04388f9e7546a9f) on Linux i686");
    assertEquals("Tor 0.2.2.35 (git-b04388f9e7546a9f) on Linux i686",
        descriptor.getPlatform());
  }

  @Test
  public void testPlatformNoSpace() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPlatformLine("platform");
    assertEquals("", descriptor.getPlatform());
  }

  @Test
  public void testPlatformSpace() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPlatformLine("platform ");
    assertEquals("", descriptor.getPlatform());
  }

  @Test
  public void testProtocolsOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithProtocolsLine("opt protocols Link 1 2 Circuit 1");
    assertEquals(Arrays.asList(1, 2),
        descriptor.getLinkProtocolVersions());
    assertEquals(Arrays.asList(1),
        descriptor.getCircuitProtocolVersions());
  }

  @Test
  public void testProtocolsNoOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithProtocolsLine("protocols Link 1 2 Circuit 1");
    assertEquals(Arrays.asList(1, 2),
        descriptor.getLinkProtocolVersions());
    assertEquals(Arrays.asList(1),
        descriptor.getCircuitProtocolVersions());
  }

  @Test
  public void testProtocolsAb() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown
        .expectMessage("Illegal line 'opt protocols Link A B Circuit 1'.");
    DescriptorBuilder.createWithProtocolsLine("opt protocols Link A B "
        + "Circuit 1");
  }

  @Test
  public void testProtocolsNoCircuitVersions()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'opt protocols Link 1 2'.");
    DescriptorBuilder.createWithProtocolsLine("opt protocols Link 1 2");
  }

  @Test
  public void testProtoGreenPurple() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithProtoLine("proto Green=23 Purple=42");
    assertEquals(new TreeSet<>(Arrays.asList(23L)),
        descriptor.getProtocols().get("Green"));
    assertEquals(new TreeSet<>(Arrays.asList(42L)),
        descriptor.getProtocols().get("Purple"));
  }

  @Test
  public void testProtoInvalid() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid line 'proto Invalid=1+2+3'.");
    DescriptorBuilder.createWithProtoLine("proto Invalid=1+2+3");
  }

  @Test
  public void testPublishedMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'published' is contained 0 times, "
        + "but must be contained exactly once.");
    DescriptorBuilder.createWithPublishedLine(null);
  }

  @Test
  public void testPublishedOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPublishedLine("opt published 2012-01-01 04:03:19");
    assertEquals(1325390599000L, descriptor.getPublishedMillis());
  }

  @Test
  public void testPublished2039() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(
        "Illegal timestamp format in line 'published 2039-01-01 04:03:19'.");
    DescriptorBuilder.createWithPublishedLine("published 2039-01-01 "
        + "04:03:19");
  }

  @Test
  public void testPublished1912() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal timestamp format in line "
        + "'published 1912-01-01 04:03:19'.");
    DescriptorBuilder.createWithPublishedLine("published 1912-01-01 "
        + "04:03:19");
  }

  @Test
  public void testPublishedFeb31() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal timestamp format in line "
        + "'published 2012-02-31 04:03:19'.");
    DescriptorBuilder.createWithPublishedLine("published 2012-02-31 "
        + "04:03:19");
  }

  @Test
  public void testPublishedNoTime() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Line 'published 2012-01-01' does not contain "
        + "a timestamp at the expected position.");
    DescriptorBuilder.createWithPublishedLine("published 2012-01-01");
  }

  @Test
  public void testPublishedMillis() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithPublishedLine("opt published 2012-01-01 04:03:19.123");
    assertEquals(1325390599000L, descriptor.getPublishedMillis());
  }

  @Test
  public void testFingerprintNoOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFingerprintLine("fingerprint D873 3048 FC8E C910 2466 "
            + "AD8F 3098 622B F1BF 71FD");
    assertEquals("D8733048FC8EC9102466AD8F3098622BF1BF71FD",
        descriptor.getFingerprint());
  }

  @Test
  public void testFingerprintG() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal hex string in line 'opt fingerprint "
        + "G873 3048 FC8E C910 2466 AD8F 3098 622B F1BF 71FD'.");
    DescriptorBuilder.createWithFingerprintLine("opt fingerprint G873 "
        + "3048 FC8E C910 2466 AD8F 3098 622B F1BF 71FD");
  }

  @Test
  public void testFingerprintTooShort() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'opt fingerprint D873 3048 FC8E "
        + "C910 2466 AD8F 3098 622B F1BF'.");
    DescriptorBuilder.createWithFingerprintLine("opt fingerprint D873 "
        + "3048 FC8E C910 2466 AD8F 3098 622B F1BF");
  }

  @Test
  public void testFingerprintTooLong() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'opt fingerprint D873 3048 "
        + "FC8E C910 2466 AD8F 3098 622B F1BF 71FD D873'.");
    DescriptorBuilder.createWithFingerprintLine("opt fingerprint D873 "
        + "3048 FC8E C910 2466 AD8F 3098 622B F1BF 71FD D873");
  }

  @Test
  public void testFingerprintNoSpaces() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'opt fingerprint "
        + "D8733048FC8EC9102466AD8F3098622BF1BF71FD'.");
    DescriptorBuilder.createWithFingerprintLine("opt fingerprint "
        + "D8733048FC8EC9102466AD8F3098622BF1BF71FD");
  }

  @Test
  public void testUptimeMissing() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithUptimeLine(null);
    assertNull(descriptor.getUptime());
  }

  @Test
  public void testUptimeOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithUptimeLine("opt uptime 48");
    assertEquals(48, descriptor.getUptime().longValue());
  }

  @Test
  public void testUptimeFourtyEight() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal value in line 'uptime fourty-eight'.");
    DescriptorBuilder.createWithUptimeLine("uptime fourty-eight");
  }

  @Test
  public void testUptimeMinusOne() throws DescriptorParseException {
    DescriptorBuilder.createWithUptimeLine("uptime -1");
  }

  @Test
  public void testUptimeSpace() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Wrong number of values in line 'uptime '.");
    DescriptorBuilder.createWithUptimeLine("uptime ");
  }

  @Test
  public void testUptimeNoSpace() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Wrong number of values in line 'uptime'.");
    DescriptorBuilder.createWithUptimeLine("uptime");
  }

  @Test
  public void testUptimeFourEight() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Wrong number of values in line 'uptime 4 8'.");
    DescriptorBuilder.createWithUptimeLine("uptime 4 8");
  }

  @Test
  public void testBandwidthOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithBandwidthLine("opt bandwidth 51200 51200 53470");
    assertEquals(51200, descriptor.getBandwidthRate());
    assertEquals(51200, descriptor.getBandwidthBurst());
    assertEquals(53470, descriptor.getBandwidthObserved());
  }

  @Test
  public void testBandwidthMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'bandwidth' is contained 0 times, "
        + "but must be contained exactly once.");
    DescriptorBuilder.createWithBandwidthLine(null);
  }

  @Test
  public void testBandwidthOneValue() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown
        .expectMessage("Wrong number of values in line 'bandwidth 51200'.");
    DescriptorBuilder.createWithBandwidthLine("bandwidth 51200");
  }

  @Test
  public void testBandwidthTwoValues() throws DescriptorParseException {
    /* This is allowed, because Tor versions 0.0.8 and older only wrote
     * bandwidth lines with rate and burst values, but no observed
     * value. */
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithBandwidthLine("bandwidth 51200 51200");
    assertEquals(51200, descriptor.getBandwidthRate());
    assertEquals(51200, descriptor.getBandwidthBurst());
    assertEquals(-1, descriptor.getBandwidthObserved());
  }

  @Test
  public void testBandwidthFourValues() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Wrong number of values in line "
        + "'bandwidth 51200 51200 53470 53470'.");
    DescriptorBuilder.createWithBandwidthLine("bandwidth 51200 51200 "
        + "53470 53470");
  }

  @Test
  public void testBandwidthMinusOneTwoThree()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal values in line 'bandwidth -1 -2 -3'.");
    DescriptorBuilder.createWithBandwidthLine("bandwidth -1 -2 -3");
  }

  @Test
  public void testExtraInfoDigestNoOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExtraInfoDigestLine("extra-info-digest "
        + "1469D1550738A25B1E7B47CDDBCD7B2899F51B74");
    assertEquals("1469D1550738A25B1E7B47CDDBCD7B2899F51B74",
        descriptor.getExtraInfoDigestSha1Hex());
  }

  @Test
  public void testExtraInfoDigestNoSpace()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'opt extra-info-digest'.");
    DescriptorBuilder.createWithExtraInfoDigestLine("opt "
        + "extra-info-digest");
  }

  @Test
  public void testExtraInfoDigestTooShort()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal hex string in line 'opt "
        + "extra-info-digest 1469D1550738A25B1E7B47CDDBCD7B2899F5'.");
    DescriptorBuilder.createWithExtraInfoDigestLine("opt "
        + "extra-info-digest 1469D1550738A25B1E7B47CDDBCD7B2899F5");
  }

  @Test
  public void testExtraInfoDigestTooLong()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal hex string in line 'opt "
        + "extra-info-digest 1469D1550738A25B1E7B47CDDBCD7B2899F51B741469'.");
    DescriptorBuilder.createWithExtraInfoDigestLine("opt "
        + "extra-info-digest "
        + "1469D1550738A25B1E7B47CDDBCD7B2899F51B741469");
  }

  @Test
  public void testExtraInfoDigestMissing()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExtraInfoDigestLine(null);
    assertNull(descriptor.getExtraInfoDigestSha1Hex());
  }

  @Test
  public void testExtraInfoDigestAdditionalDigest()
      throws DescriptorParseException {
    String extraInfoDigest = "0879DB7B765218D7B3AE7557669D20307BB21CAA";
    String additionalExtraInfoDigest =
        "V609l+N6ActBveebfNbH5lQ6wHDNstDkFgyqEhBHwtA";
    String extraInfoDigestLine = String.format("extra-info-digest %s %s",
        extraInfoDigest, additionalExtraInfoDigest);
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExtraInfoDigestLine(extraInfoDigestLine);
    assertEquals(extraInfoDigest, descriptor.getExtraInfoDigestSha1Hex());
  }

  @Test
  public void testOnionKeyOpt() throws DescriptorParseException {
    DescriptorBuilder.createWithOnionKeyLines("opt onion-key\n"
        + "-----BEGIN RSA PUBLIC KEY-----\n"
        + "MIGJAoGBAKM+iiHhO6eHsvd6Xjws9z9EQB1V/Bpuy5ciGJ1U4V9SeiKooSo5Bp"
        + "PL\no3XT+6PIgzl3R6uycjS3Ejk47vLEJdcVTm/VG6E0ppu3olIynCI4QryfCE"
        + "uC3cTF\n9wE4WXY4nX7w0RTN18UVLxrt1A9PP0cobFNiPs9rzJCbKFfacOkpAg"
        + "MBAAE=\n"
        + "-----END RSA PUBLIC KEY-----");
  }

  @Test
  public void testSigningKeyOpt() throws DescriptorParseException {
    DescriptorBuilder.createWithSigningKeyLines("opt signing-key\n"
        + "-----BEGIN RSA PUBLIC KEY-----\n"
        + "MIGJAoGBALMm3r3QDh482Ewe6Ub9wvRIfmEkoNX6q5cEAtQRNHSDcNx41gjELb"
        + "cl\nEniVMParBYACKfOxkS+mTTnIRDKVNEJTsDOwryNrc4X9JnPc/nn6ymYPiN"
        + "DhUROG\n8URDIhQoixcUeyyrVB8sxliSstKimulGnB7xpjYOlO8JKaHLNL4TAg"
        + "MBAAE=\n"
        + "-----END RSA PUBLIC KEY-----");
  }

  @Test
  public void testHiddenServiceDirMissing()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHiddenServiceDirLine(null);
    assertFalse(descriptor.isHiddenServiceDir());
  }

  @Test
  public void testHiddenServiceDirNoOpt()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHiddenServiceDirLine("hidden-service-dir");
    assertTrue(descriptor.isHiddenServiceDir());
  }

  @Test
  public void testHiddenServiceDirVersions2And3()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHiddenServiceDirLine("hidden-service-dir 2 3");
    assertTrue(descriptor.isHiddenServiceDir());
  }

  @Test
  public void testContactMissing() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithContactLine(null);
    assertNull(descriptor.getContact());
  }

  @Test
  public void testContactOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithContactLine("opt contact Random Person");
    assertEquals("Random Person", descriptor.getContact());
  }

  @Test
  public void testContactDuplicate() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'contact' is contained 2 times, "
        + "but must be contained at most once.");
    DescriptorBuilder.createWithContactLine("contact Random "
        + "Person\ncontact Random Person");
  }

  @Test
  public void testContactNoSpace() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithContactLine("contact");
    assertEquals("", descriptor.getContact());
  }

  @Test
  public void testContactCarriageReturn()
      throws DescriptorParseException {
    String contactString = "Random "
        + "Person -----BEGIN PGP PUBLIC KEY BLOCK-----\r"
        + "Version: GnuPG v1 dot 4 dot 7 (Darwin)\r\r"
        + "mQGiBEbb0rcRBADqBiUXsmtpJifh74irNnkHbhKMj8O4TqenaZYhdjLWouZsZd"
        + "07\rmTQoP40G4zqOrVEOOcXpdSiRnHWJYfgTnkibNZrOZEZLn3H1ywpovEgESm"
        + "oGEdAX\roid3XuIYRpRnqoafbFg9sg+OofX/mGrO+5ACfagQ9rlfx2oxCWijYw"
        + "pYFRk3NhCY=\r=Xaw3\r-----END PGP PUBLIC KEY BLOCK-----";
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithContactLine("contact " + contactString);
    assertEquals(contactString, descriptor.getContact());
  }

  @Test
  public void testExitPolicyRejectAllAcceptAll()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExitPolicyLines("reject *:*\naccept *:*");
    assertEquals(Arrays.asList("reject *:*", "accept *:*"),
        descriptor.getExitPolicyLines());
  }

  @Test
  public void testExitPolicyOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExitPolicyLines("opt reject *:*");
    assertEquals(Arrays.asList("reject *:*"),
        descriptor.getExitPolicyLines());
  }

  @Test
  public void testExitPolicyNoPort() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'*' in line 'reject *' must contain address "
        + "and port.");
    DescriptorBuilder.createWithExitPolicyLines("reject *");
  }

  @Test
  public void testExitPolicyAccept80RejectAll()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExitPolicyLines("accept *:80\nreject *:*");
    assertEquals(Arrays.asList("accept *:80",
        "reject *:*"), descriptor.getExitPolicyLines());
  }

  @Test
  public void testExitPolicyReject321() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'123.123.123.321' in line 'reject "
        + "123.123.123.321:80' is not a valid IPv4 address.");
    DescriptorBuilder.createWithExitPolicyLines("reject "
        + "123.123.123.321:80");
  }

  @Test
  public void testExitPolicyRejectPort66666()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("'66666' in line 'reject *:66666' "
        + "is not a valid port number.");
    DescriptorBuilder.createWithExitPolicyLines("reject *:66666");
  }

  @Test
  public void testExitPolicyProjectAll() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(
        "Either keyword 'accept' or 'reject' must be contained at least once.");
    DescriptorBuilder.createWithExitPolicyLines("project *:*");
  }

  @Test
  public void testExitPolicyMissing() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Either keyword 'accept' or 'reject' must be "
        + "contained at least once.");
    DescriptorBuilder.createWithExitPolicyLines(null);
  }

  @Test
  public void testExitPolicyMaskTypes() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithExitPolicyLines("reject 192.168.0.0/16:*\n"
        + "reject 94.134.192.243/255.255.255.0:*");
    assertEquals(Arrays.asList("reject 192.168.0.0/16:*",
        "reject 94.134.192.243/255.255.255.0:*"),
        descriptor.getExitPolicyLines());
  }

  @Test(expected = DescriptorParseException.class)
  public void testEndSignatureFourDashes() throws DescriptorParseException {
    DescriptorBuilder.createWithRouterSignatureLines("router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "o4j+kH8UQfjBwepUnr99v0ebN8RpzHJ/lqYsTojXHy9kMr1RNI9IDeSzA7PSqT"
        + "uV\n4PL8QsGtlfwthtIoZpB2srZeyN/mcpA9fa1JXUrt/UN9K/+32Cyaad7h0n"
        + "HE6Xfb\njqpXDpnBpvk4zjmzjjKYnIsUWTnADmu0fo3xTRqXi7g=\n"
        + "-----END SIGNATURE----");
  }

  @Test
  public void testRouterSignatureNotLastLine()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'contact' is contained 2 times, "
        + "but must be contained at most once.");
    DescriptorBuilder.createWithRouterSignatureLines("router-signature\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "o4j+kH8UQfjBwepUnr99v0ebN8RpzHJ/lqYsTojXHy9kMr1RNI9IDeSzA7PSqT"
        + "uV\n4PL8QsGtlfwthtIoZpB2srZeyN/mcpA9fa1JXUrt/UN9K/+32Cyaad7h0n"
        + "HE6Xfb\njqpXDpnBpvk4zjmzjjKYnIsUWTnADmu0fo3xTRqXi7g=\n"
        + "-----END SIGNATURE-----\ncontact me");
  }

  @Test
  public void testHibernatingOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHibernatingLine("opt hibernating 1");
    assertTrue(descriptor.isHibernating());
  }

  @Test
  public void testHibernatingFalse() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHibernatingLine("hibernating 0");
    assertFalse(descriptor.isHibernating());
  }

  @Test
  public void testHibernatingTrue() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithHibernatingLine("hibernating 1");
    assertTrue(descriptor.isHibernating());
  }

  @Test
  public void testHibernatingYep() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'hibernating yep'.");
    DescriptorBuilder.createWithHibernatingLine("hibernating yep");
  }

  @Test
  public void testHibernatingNoSpace() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'hibernating'.");
    DescriptorBuilder.createWithHibernatingLine("hibernating");
  }

  @Test
  public void testFamilyOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFamilyLine("opt family saberrider2008");
    assertEquals(Arrays.asList("saberrider2008"),
        descriptor.getFamilyEntries());
  }

  @Test
  public void testFamilyFingerprint() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFamilyLine("family "
        + "$D8733048FC8EC9102466AD8F3098622BF1BF71FD");
    assertEquals(Arrays.asList("$D8733048FC8EC9102466AD8F3098622BF1BF71FD"),
        descriptor.getFamilyEntries());
  }

  @Test
  public void testFamilyNickname() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFamilyLine("family saberrider2008");
    assertEquals(Arrays.asList("saberrider2008"),
        descriptor.getFamilyEntries());
  }

  @Test
  public void testFamilyDuplicate() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'family' is contained 2 times, "
        + "but must be contained at most once.");
    DescriptorBuilder.createWithFamilyLine("family "
        + "saberrider2008\nfamily saberrider2008");
  }

  @Test
  public void testFamilyNicknamePrefix() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(
        "Illegal hex string in line 'family $saberrider2008'.");
    DescriptorBuilder.createWithFamilyLine("family $saberrider2008");
  }

  @Test
  public void testFamilyFingerprintNoPrefix()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal nickname in line 'family "
        + "D8733048FC8EC9102466AD8F3098622BF1BF71FD'.");
    DescriptorBuilder.createWithFamilyLine("family "
        + "D8733048FC8EC9102466AD8F3098622BF1BF71FD");
  }

  @Test
  public void testFamilyFingerprintNicknameNamed()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFamilyLine("family "
        + "$D8733048FC8EC9102466AD8F3098622BF1BF71FD=saberrider2008");
    assertEquals(Arrays.asList(
        "$D8733048FC8EC9102466AD8F3098622BF1BF71FD=saberrider2008"),
        descriptor.getFamilyEntries());
  }

  @Test
  public void testFamilyFingerprintNicknameUnnamed()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithFamilyLine("family "
        + "$D8733048FC8EC9102466AD8F3098622BF1BF71FD~saberrider2008");
    assertEquals(Arrays.asList(
        "$D8733048FC8EC9102466AD8F3098622BF1BF71FD~saberrider2008"),
        descriptor.getFamilyEntries());
  }

  @Test
  public void testWriteHistory() throws DescriptorParseException {
    String writeHistoryLine = "write-history 2012-01-01 03:51:44 (900 s) "
        + "4345856,261120,7591936,1748992";
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithWriteHistoryLine(writeHistoryLine);
    assertNotNull(descriptor.getWriteHistory());
    BandwidthHistory parsedWriteHistory = descriptor.getWriteHistory();
    assertEquals(writeHistoryLine, parsedWriteHistory.getLine());
    assertEquals(1325389904000L, parsedWriteHistory
        .getHistoryEndMillis());
    assertEquals(900L, parsedWriteHistory.getIntervalLength());
    SortedMap<Long, Long> bandwidthValues = parsedWriteHistory
        .getBandwidthValues();
    assertEquals(4345856L, (long) bandwidthValues.remove(1325387204000L));
    assertEquals(261120L, (long) bandwidthValues.remove(1325388104000L));
    assertEquals(7591936L, (long) bandwidthValues.remove(1325389004000L));
    assertEquals(1748992L, (long) bandwidthValues.remove(1325389904000L));
    assertTrue(bandwidthValues.isEmpty());
  }

  @Test
  public void testWriteHistoryOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithWriteHistoryLine("opt write-history 2012-01-01 "
        + "03:51:44 (900 s) 4345856,261120,7591936,1748992");
    assertNotNull(descriptor.getWriteHistory());
  }

  @Test
  public void testWriteHistory3012() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal timestamp format in line "
        + "'write-history 3012-01-01 03:51:44 (900 s) "
        + "4345856,261120,7591936,1748992'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "3012-01-01 03:51:44 (900 s) 4345856,261120,7591936,1748992");
  }

  @Test
  public void testWriteHistoryNoSeconds()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal timestamp format in line "
        + "'write-history 2012-01-01 03:51 (900 s) 4345856,261120,"
        + "7591936,1748992'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51 (900 s) 4345856,261120,7591936,1748992");
  }

  @Test
  public void testWriteHistoryNoParathenses()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid bandwidth-history line 'write-history "
        + "2012-01-01 03:51:44 900 s 4345856,261120,7591936,1748992'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51:44 900 s 4345856,261120,7591936,1748992");
  }

  @Test
  public void testWriteHistoryNoSpaceSeconds()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid bandwidth-history line "
        + "'write-history 2012-01-01 03:51:44 (900s) "
        + "4345856,261120,7591936,1748992'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51:44 (900s) 4345856,261120,7591936,1748992");
  }

  @Test
  public void testWriteHistoryTrailingComma()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid bandwidth-history line 'write-history "
        + "2012-01-01 03:51:44 (900 s) 4345856,261120,7591936,'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51:44 (900 s) 4345856,261120,7591936,");
  }

  @Test
  public void testWriteHistoryOneTwoThree()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid bandwidth-history line 'write-history "
        + "2012-01-01 03:51:44 (900 s) one,two,three'.");
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51:44 (900 s) one,two,three");
  }

  @Test
  public void testWriteHistoryNoValuesSpace()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithWriteHistoryLine("write-history 2012-01-01 03:51:44 "
        + "(900 s) ");
    assertEquals(900, descriptor.getWriteHistory()
        .getIntervalLength());
    assertTrue(descriptor.getWriteHistory().getBandwidthValues()
        .isEmpty());
  }

  @Test
  public void testWriteHistoryNoValuesNoSpace()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithWriteHistoryLine("write-history 2012-01-01 03:51:44 "
        + "(900 s)");
    assertEquals(900, descriptor.getWriteHistory()
        .getIntervalLength());
    assertTrue(descriptor.getWriteHistory().getBandwidthValues()
        .isEmpty());
  }

  @Test
  public void testWriteHistoryNoS() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Invalid bandwidth-history line "
        + "'write-history 2012-01-01 03:51:44 (900 '.");
    DescriptorBuilder.createWithWriteHistoryLine(
        "write-history 2012-01-01 03:51:44 (900 ");
  }

  @Test
  public void testWriteHistoryExtraArg()
      throws DescriptorParseException {
    DescriptorBuilder.createWithWriteHistoryLine("write-history "
        + "2012-01-01 03:51:44 (900 s) 4345856 bin_size=1024");
  }

  @Test
  public void testWriteHistory1800Seconds()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithWriteHistoryLine("write-history 2012-01-01 03:51:44 "
        + "(1800 s) 4345856");
    assertEquals(1800L, descriptor.getWriteHistory()
        .getIntervalLength());
  }

  @Test
  public void testReadHistory() throws DescriptorParseException {
    String readHistoryLine = "read-history 2012-01-01 03:51:44 (900 s) "
        + "4268032,139264,7797760,1415168";
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithReadHistoryLine(readHistoryLine);
    assertNotNull(descriptor.getReadHistory());
    BandwidthHistory parsedReadHistory = descriptor.getReadHistory();
    assertEquals(readHistoryLine, parsedReadHistory.getLine());
    assertEquals(1325389904000L, parsedReadHistory
        .getHistoryEndMillis());
    assertEquals(900L, parsedReadHistory.getIntervalLength());
    SortedMap<Long, Long> bandwidthValues = parsedReadHistory
        .getBandwidthValues();
    assertEquals(4268032L, (long) bandwidthValues.remove(1325387204000L));
    assertEquals(139264L, (long) bandwidthValues.remove(1325388104000L));
    assertEquals(7797760L, (long) bandwidthValues.remove(1325389004000L));
    assertEquals(1415168L, (long) bandwidthValues.remove(1325389904000L));
    assertTrue(bandwidthValues.isEmpty());
  }

  @Test
  public void testReadHistoryTwoSpaces() throws DescriptorParseException {
    /* There are some server descriptors from older Tor versions that
     * contain "opt read-history  " lines. */
    String readHistoryLine = "opt read-history  2012-01-01 03:51:44 "
        + "(900 s) 4268032,139264,7797760,1415168";
    DescriptorBuilder.createWithReadHistoryLine(readHistoryLine);
  }

  @Test
  public void testEventdnsOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithEventdnsLine("opt eventdns 1");
    assertTrue(descriptor.getUsesEnhancedDnsLogic());
  }

  @Test
  public void testEventdns1() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithEventdnsLine("eventdns 1");
    assertTrue(descriptor.getUsesEnhancedDnsLogic());
  }

  @Test
  public void testEventdns0() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithEventdnsLine("eventdns 0");
    assertFalse(descriptor.getUsesEnhancedDnsLogic());
  }

  @Test
  public void testEventdnsTrue() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'eventdns true'.");
    DescriptorBuilder.createWithEventdnsLine("eventdns true");
  }

  @Test
  public void testEventdnsNo() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'eventdns no'.");
    DescriptorBuilder.createWithEventdnsLine("eventdns no");
  }

  @Test
  public void testCachesExtraInfoOpt() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithCachesExtraInfoLine("opt caches-extra-info");
    assertTrue(descriptor.getCachesExtraInfo());
  }

  @Test
  public void testCachesExtraInfoNoSpace()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithCachesExtraInfoLine("caches-extra-info");
    assertTrue(descriptor.getCachesExtraInfo());
  }

  @Test
  public void testCachesExtraInfoTrue() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'caches-extra-info true'.");
    DescriptorBuilder.createWithCachesExtraInfoLine("caches-extra-info "
        + "true");
  }

  @Test
  public void testAllowSingleHopExitsOpt()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithAllowSingleHopExitsLine("opt allow-single-hop-exits");
    assertTrue(descriptor.getAllowSingleHopExits());
  }

  @Test
  public void testAllowSingleHopExitsNoSpace()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithAllowSingleHopExitsLine("allow-single-hop-exits");
    assertTrue(descriptor.getAllowSingleHopExits());
  }

  @Test
  public void testAllowSingleHopExitsTrue()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'allow-single-hop-exits true'.");
    DescriptorBuilder.createWithAllowSingleHopExitsLine(
        "allow-single-hop-exits true");
  }

  @Test
  public void testAllowSingleHopExitsNonAsciiKeyword()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.allOf(
        Matchers.containsString("Unrecognized character in keyword "),
        Matchers.containsString("allow-single-hop-exits'")));
    DescriptorBuilder.createWithNonAsciiLineBytes(new byte[] {
        0x14, (byte) 0xfe, 0x18,                  // non-ascii chars
        0x61, 0x6c, 0x6c, 0x6f, 0x77, 0x2d,       // "allow-"
        0x73, 0x69, 0x6e, 0x67, 0x6c, 0x65, 0x2d, // "single-"
        0x68, 0x6f, 0x70, 0x2d,                   // "hop-"
        0x65, 0x78, 0x69, 0x74, 0x73 });          // "exits" (no newline)
  }

  @Test
  public void testIpv6PolicyLine() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithIpv6PolicyLine("ipv6-policy accept 80,1194,1220,1293");
    assertEquals("accept", descriptor.getIpv6DefaultPolicy());
    assertEquals("80,1194,1220,1293", descriptor.getIpv6PortList());
  }

  @Test
  public void testIpv6PolicyLineNoPolicy()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ipv6-policy 80'.");
    DescriptorBuilder.createWithIpv6PolicyLine("ipv6-policy 80");
  }

  @Test
  public void testIpv6PolicyLineNoPorts()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ipv6-policy accept'.");
    DescriptorBuilder.createWithIpv6PolicyLine("ipv6-policy accept");
  }

  @Test
  public void testIpv6PolicyLineNoPolicyNoPorts()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ipv6-policy '.");
    DescriptorBuilder.createWithIpv6PolicyLine("ipv6-policy ");
  }

  @Test
  public void testIpv6PolicyLineProject()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ipv6-policy project 80'.");
    DescriptorBuilder.createWithIpv6PolicyLine("ipv6-policy project 80");
  }

  @Test
  public void testTwoIpv6PolicyLines() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'ipv6-policy' is contained 2 times, "
        + "but must be contained at most once.");
    DescriptorBuilder.createWithIpv6PolicyLine(
        "ipv6-policy accept 80,1194,1220,1293\n"
        + "ipv6-policy accept 80,1194,1220,1293");
  }

  @Test
  public void testNtorOnionKeyLine() throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithNtorOnionKeyLine("ntor-onion-key "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY=");
    assertEquals("Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY",
        descriptor.getNtorOnionKey());
  }

  @Test
  public void testNtorOnionKeyLineNoPadding()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithNtorOnionKeyLine("ntor-onion-key "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY");
    assertEquals("Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY",
        descriptor.getNtorOnionKey());
  }

  @Test
  public void testNtorOnionKeyLineNoKey()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ntor-onion-key '.");
    DescriptorBuilder.createWithNtorOnionKeyLine("ntor-onion-key ");
  }

  @Test
  public void testNtorOnionKeyLineTwoKeys()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'ntor-onion-key Y/XgaHcPIJVa"
        + "4D55kir9QLH8rEYAaLXuv3c3sm8jYhY Y/XgaHcPIJVa4D55kir9QLH8rEYAa"
        + "LXuv3c3sm8jYhY'.");
    DescriptorBuilder.createWithNtorOnionKeyLine("ntor-onion-key "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY");
  }

  @Test
  public void testTwoNtorOnionKeyLines() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Blank lines are not allowed.");
    DescriptorBuilder.createWithNtorOnionKeyLine("ntor-onion-key "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY\nntor-onion-key "
        + "Y/XgaHcPIJVa4D55kir9QLH8rEYAaLXuv3c3sm8jYhY\n");
  }

  @Test
  public void testTunnelledDirServerTrue()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithTunnelledDirServerLine("tunnelled-dir-server");
    assertTrue(descriptor.getTunnelledDirServer());
  }

  @Test
  public void testTunnelledDirServerFalse()
      throws DescriptorParseException {
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithTunnelledDirServerLine(null);
    assertFalse(descriptor.getTunnelledDirServer());
  }

  @Test
  public void testTunnelledDirServerTypo()
      throws DescriptorParseException {
    String tunneledDirServerLine = "tunneled-dir-server";
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithTunnelledDirServerLine(tunneledDirServerLine);
    assertEquals(Arrays.asList(tunneledDirServerLine),
        descriptor.getUnrecognizedLines());
  }

  @Test
  public void testTunnelledDirServerTwice()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'tunnelled-dir-server' is contained "
        + "2 times, but must be contained at most once.");
    DescriptorBuilder.createWithTunnelledDirServerLine(
        "tunnelled-dir-server\ntunnelled-dir-server");
  }

  @Test
  public void testTunnelledDirServerArgs()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'tunnelled-dir-server 1'.");
    DescriptorBuilder.createWithTunnelledDirServerLine(
        "tunnelled-dir-server 1");
  }

  @Test
  public void testUnrecognizedLineIgnore()
      throws DescriptorParseException {
    String unrecognizedLine = "unrecognized-line 1";
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithUnrecognizedLine(unrecognizedLine);
    List<String> unrecognizedLines = new ArrayList<>();
    unrecognizedLines.add(unrecognizedLine);
    assertEquals(unrecognizedLines, descriptor.getUnrecognizedLines());
  }

  @Test
  public void testSomeOtherKey() throws DescriptorParseException {
    List<String> unrecognizedLines = new ArrayList<>();
    unrecognizedLines.add("some-other-key");
    unrecognizedLines.add("-----BEGIN RSA PUBLIC KEY-----");
    unrecognizedLines.add("MIGJAoGBAKM+iiHhO6eHsvd6Xjws9z9EQB1V/Bpuy5ciGJ"
        + "1U4V9SeiKooSo5BpPL");
    unrecognizedLines.add("o3XT+6PIgzl3R6uycjS3Ejk47vLEJdcVTm/VG6E0ppu3ol"
        + "IynCI4QryfCEuC3cTF");
    unrecognizedLines.add("9wE4WXY4nX7w0RTN18UVLxrt1A9PP0cobFNiPs9rzJCbKF"
        + "facOkpAgMBAAE=");
    unrecognizedLines.add("-----END RSA PUBLIC KEY-----");
    StringBuilder sb = new StringBuilder();
    for (String line : unrecognizedLines) {
      sb.append("\n").append(line);
    }
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithUnrecognizedLine(sb.toString().substring(1));
    assertEquals(unrecognizedLines, descriptor.getUnrecognizedLines());
  }

  @Test
  public void testUnrecognizedCryptoBlockNoKeyword()
      throws DescriptorParseException {
    List<String> unrecognizedLines = new ArrayList<>();
    unrecognizedLines.add("-----BEGIN RSA PUBLIC KEY-----");
    unrecognizedLines.add("MIGJAoGBAKM+iiHhO6eHsvd6Xjws9z9EQB1V/Bpuy5ciGJ"
        + "1U4V9SeiKooSo5BpPL");
    unrecognizedLines.add("o3XT+6PIgzl3R6uycjS3Ejk47vLEJdcVTm/VG6E0ppu3ol"
        + "IynCI4QryfCEuC3cTF");
    unrecognizedLines.add("9wE4WXY4nX7w0RTN18UVLxrt1A9PP0cobFNiPs9rzJCbKF"
        + "facOkpAgMBAAE=");
    unrecognizedLines.add("-----END RSA PUBLIC KEY-----");
    StringBuilder sb = new StringBuilder();
    for (String line : unrecognizedLines) {
      sb.append("\n").append(line);
    }
    ServerDescriptor descriptor = DescriptorBuilder
        .createWithUnrecognizedLine(sb.toString().substring(1));
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

  @Test
  public void testEd25519() throws DescriptorParseException {
    ServerDescriptor descriptor =
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

  @Test
  public void testEd25519IdentityMasterKeyMismatch()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Mismatch between identity-ed25519 and "
        + "master-key-ed25519.");
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        "master-key-ed25519 AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519IdentityMissing()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(null,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519IdentityDuplicate()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'identity-ed25519' is contained 2 "
        + "times, but must be contained at most once.");
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES + "\n"
        + IDENTITY_ED25519_LINES, MASTER_KEY_ED25519_LINE,
        ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519IdentityEmptyCrypto()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown
        .expectMessage("Invalid length of identity-ed25519 (in bytes): 0");
    DescriptorBuilder.createWithEd25519Lines("identity-ed25519\n"
        + "-----BEGIN ED25519 CERT-----\n-----END ED25519 CERT-----",
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519IdentityInvalidCrypto()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("CRYPTO_END before CRYPTO_BEGIN");
    DescriptorBuilder.createWithEd25519Lines("identity-ed25519\n"
        + "-----END ED25519 CERT-----\n-----BEGIN ED25519 CERT-----",
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519MasterKeyMissing()
      throws DescriptorParseException {
    ServerDescriptor descriptor =
        DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        null, ROUTER_SIG_ED25519_LINE);
    assertEquals(MASTER_KEY_ED25519_LINE.substring(
        MASTER_KEY_ED25519_LINE.indexOf(" ") + 1),
        descriptor.getMasterKeyEd25519());
  }

  @Test
  public void testEd25519MasterKeyDuplicate()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'master-key-ed25519' is contained 2 "
        + "times, but must be contained at most once.");
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE + "\n" + MASTER_KEY_ED25519_LINE,
        ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519RouterSigMissing()
      throws DescriptorParseException {
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, null);
  }

  @Test
  public void testEd25519RouterSigDuplicate()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'router-sig-ed25519' is contained 2 "
        + "times, but must be contained at most once.");
    DescriptorBuilder.createWithEd25519Lines(IDENTITY_ED25519_LINES,
        MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE + "\n"
        + ROUTER_SIG_ED25519_LINE);
  }

  @Test
  public void testEd25519FollowedbyUnrecognizedLine()
      throws DescriptorParseException {
    String unrecognizedLine = "unrecognized-line 1";
    ServerDescriptor serverDecriptor = DescriptorBuilder.createWithEd25519Lines(
        IDENTITY_ED25519_LINES, MASTER_KEY_ED25519_LINE, ROUTER_SIG_ED25519_LINE
        + "\n" + unrecognizedLine);
    assertEquals(Arrays.asList(unrecognizedLine),
        serverDecriptor.getUnrecognizedLines());
  }

  private static final String ONION_KEY_CROSSCERT_LINES =
      "onion-key-crosscert\n"
      + "-----BEGIN CROSSCERT-----\n"
      + "gVWpiNgG2FekW1uonr4KKoqykjr4bqUBKGZfu6s9rvsV1TThnquZNP6ZhX2IPdQA"
      + "\nlfKtzFggGu/4BiJ5oTSDj2sK2DMjY3rjrMQZ3I/wJ25yhc9gxjqYqUYO9MmJwA"
      + "Lp\nfYkqp/t4WchJpyva/4hK8vITsI6eT2BfY/DWMy/suIE=\n"
      + "-----END CROSSCERT-----";

  private static final String NTOR_ONION_KEY_CROSSCERT_LINES =
      "ntor-onion-key-crosscert 1\n"
      + "-----BEGIN ED25519 CERT-----\n"
      + "AQoABiUeAdauu1MxYGMmGLTCPaoes0RvW7udeLc1t8LZ4P3CDo5bAN4nrRfbCfOt"
      + "\nz2Nwqn8tER1a+Ry6Vs+ilMZA55Rag4+f6Zdb1fmHWknCxbQlLHpqHACMtemPda"
      + "Ka\nErPtMuiEqAc=\n"
      + "-----END ED25519 CERT-----";

  @Test
  public void testOnionKeyCrosscert() throws DescriptorParseException {
    ServerDescriptor descriptor =
        DescriptorBuilder.createWithOnionKeyCrosscertLines(
        ONION_KEY_CROSSCERT_LINES);
    assertEquals(ONION_KEY_CROSSCERT_LINES.substring(
        ONION_KEY_CROSSCERT_LINES.indexOf("\n") + 1),
        descriptor.getOnionKeyCrosscert());
  }

  @Test
  public void testOnionKeyCrosscertDuplicate()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'onion-key-crosscert' is contained 2 "
        + "times, but must be contained at most once.");
    DescriptorBuilder.createWithOnionKeyCrosscertLines(
        ONION_KEY_CROSSCERT_LINES + "\n" + ONION_KEY_CROSSCERT_LINES);
  }

  @Test
  public void testNtorOnionKeyCrosscert()
      throws DescriptorParseException {
    ServerDescriptor descriptor =
        DescriptorBuilder.createWithNtorOnionKeyCrosscertLines(
        NTOR_ONION_KEY_CROSSCERT_LINES);
    assertEquals(NTOR_ONION_KEY_CROSSCERT_LINES.substring(
        NTOR_ONION_KEY_CROSSCERT_LINES.indexOf("\n") + 1),
        descriptor.getNtorOnionKeyCrosscert());
    assertEquals(1, descriptor.getNtorOnionKeyCrosscertSign());
  }

  @Test
  public void testNtorOnionKeyCrosscertDuplicate()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Keyword 'ntor-onion-key-crosscert' is "
        + "contained 2 times, but must be contained at most once.");
    DescriptorBuilder.createWithOnionKeyCrosscertLines(
        NTOR_ONION_KEY_CROSSCERT_LINES + "\n"
        + NTOR_ONION_KEY_CROSSCERT_LINES);
  }

  @Test
  public void testBridgeDescriptorDigestsRouterDigestLines()
      throws DescriptorParseException {
    DescriptorBuilder db = new DescriptorBuilder();
    String digestSha1Hex = "A9635DD801AD98DAC43AFF49BAA2DBBAF050222D";
    String digestSha256Base64 = "kvdJKQ6R9i8x1nDqJZ34JFWsu6TquLqQy54nheSWrOY";
    db.routerSignatureLines = "router-digest-sha256 " + digestSha256Base64
        + "\nrouter-digest " + digestSha1Hex;
    byte[] descriptorBytes = db.buildDescriptorBytes();
    BridgeServerDescriptor descriptor = new BridgeServerDescriptorImpl(
        descriptorBytes, new int[] { 0, descriptorBytes.length }, null);
    assertEquals(digestSha1Hex, descriptor.getDigestSha1Hex());
    assertEquals(digestSha256Base64, descriptor.getDigestSha256Base64());
  }

  @Test
  public void testBridgeDescriptorDigestsNoRouterDigestLines()
      throws DescriptorParseException {
    DescriptorBuilder db = new DescriptorBuilder();
    byte[] descriptorBytes = db.buildDescriptorBytes();
    BridgeServerDescriptor descriptor = new BridgeServerDescriptorImpl(
        descriptorBytes, new int[] { 0, descriptorBytes.length }, null);
    assertNull(descriptor.getDigestSha1Hex());
    assertNull(descriptor.getDigestSha256Base64());
  }

  @Test
  public void testBridgeDistributionRequestMoat()
      throws DescriptorParseException {
    ServerDescriptor descriptor =
        DescriptorBuilder.createWithBridgeDistributionRequestLine(
        "bridge-distribution-request moat");
    assertEquals("moat", descriptor.getBridgeDistributionRequest());
  }

  @Test
  public void testBridgeDistributionRequestEmptySpace()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage("Illegal line 'bridge-distribution-request '.");
    DescriptorBuilder.createWithBridgeDistributionRequestLine(
        "bridge-distribution-request ");
  }
}

