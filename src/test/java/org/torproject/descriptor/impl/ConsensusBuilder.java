/* Copyright 2012--2020 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import org.torproject.descriptor.DescriptorParseException;
import org.torproject.descriptor.RelayNetworkStatusConsensus;

import java.util.ArrayList;
import java.util.List;

/* Helper class to build a consensus based on default data and
 * modifications requested by test methods. */
public class ConsensusBuilder {

  String networkStatusVersionLine = "network-status-version 3";

  protected static RelayNetworkStatusConsensus
      createWithNetworkStatusVersionLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.networkStatusVersionLine = line;
    return cb.buildConsensus();
  }

  private String voteStatusLine = "vote-status consensus";

  protected static RelayNetworkStatusConsensus
      createWithVoteStatusLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.voteStatusLine = line;
    return cb.buildConsensus();
  }

  private String consensusMethodLine = "consensus-method 11";

  protected static RelayNetworkStatusConsensus
      createWithConsensusMethodLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.consensusMethodLine = line;
    return cb.buildConsensus();
  }

  private String validAfterLine = "valid-after 2011-11-30 09:00:00";

  protected static RelayNetworkStatusConsensus
      createWithValidAfterLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.validAfterLine = line;
    return cb.buildConsensus();
  }

  private String freshUntilLine = "fresh-until 2011-11-30 10:00:00";

  protected static RelayNetworkStatusConsensus
      createWithFreshUntilLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.freshUntilLine = line;
    return cb.buildConsensus();
  }

  private String validUntilLine = "valid-until 2011-11-30 12:00:00";

  protected static RelayNetworkStatusConsensus
      createWithValidUntilLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.validUntilLine = line;
    return cb.buildConsensus();
  }

  private String votingDelayLine = "voting-delay 300 300";

  protected static RelayNetworkStatusConsensus
      createWithVotingDelayLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.votingDelayLine = line;
    return cb.buildConsensus();
  }

  String clientVersionsLine = "client-versions 0.2.1.31,"
      + "0.2.2.34,0.2.3.6-alpha,0.2.3.7-alpha,0.2.3.8-alpha";

  protected static RelayNetworkStatusConsensus
      createWithClientVersionsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.clientVersionsLine = line;
    return cb.buildConsensus();
  }

  String serverVersionsLine = "server-versions 0.2.1.31,"
      + "0.2.2.34,0.2.3.6-alpha,0.2.3.7-alpha,0.2.3.8-alpha";

  protected static RelayNetworkStatusConsensus
      createWithServerVersionsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.serverVersionsLine = line;
    return cb.buildConsensus();
  }

  private String packageLines = null;

  protected static RelayNetworkStatusConsensus
      createWithPackageLines(String lines)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.packageLines = lines;
    return cb.buildConsensus();
  }

  private String knownFlagsLine = "known-flags Authority BadExit Exit "
      + "Fast Guard HSDir Named Running Stable Unnamed V2Dir Valid";

  protected static RelayNetworkStatusConsensus
      createWithKnownFlagsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.knownFlagsLine = line;
    return cb.buildConsensus();
  }

  private String recommendedClientProtocolsLine =
      "recommended-client-protocols Cons=1-2 Desc=1-2 DirCache=1 HSDir=1 "
      + "HSIntro=3 HSRend=1 Link=4 LinkAuth=1 Microdesc=1-2 Relay=2";

  protected static RelayNetworkStatusConsensus
      createWithRecommendedClientProtocolsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.recommendedClientProtocolsLine = line;
    return cb.buildConsensus();
  }

  private String recommendedRelayProtocolsLine =
      "recommended-relay-protocols Cons=1-2 Desc=1-2 DirCache=1 HSDir=1 "
      + "HSIntro=3 HSRend=1 Link=4 LinkAuth=1 Microdesc=1-2 Relay=2";

  protected static RelayNetworkStatusConsensus
      createWithRecommendedRelayProtocolsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.recommendedRelayProtocolsLine = line;
    return cb.buildConsensus();
  }

  private String requiredClientProtocolsLine =
      "required-client-protocols Cons=1-2 Desc=1-2 DirCache=1 HSDir=1 "
      + "HSIntro=3 HSRend=1 Link=4 LinkAuth=1 Microdesc=1-2 Relay=2";

  protected static RelayNetworkStatusConsensus
      createWithRequiredClientProtocolsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.requiredClientProtocolsLine = line;
    return cb.buildConsensus();
  }

  private String requiredRelayProtocolsLine =
      "required-relay-protocols Cons=1 Desc=1 DirCache=1 HSDir=1 HSIntro=3 "
      + "HSRend=1 Link=3-4 LinkAuth=1 Microdesc=1 Relay=1-2";

  protected static RelayNetworkStatusConsensus
      createWithRequiredRelayProtocolsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.requiredRelayProtocolsLine = line;
    return cb.buildConsensus();
  }

  private String paramsLine = "params "
      + "CircuitPriorityHalflifeMsec=30000 bwauthbestratio=1 "
      + "bwauthcircs=1 bwauthdescbw=0 bwauthkp=10000 bwauthpid=1 "
      + "bwauthtd=5000 bwauthti=50000 bwauthtidecay=5000 cbtnummodes=3 "
      + "cbtquantile=80 circwindow=1000 refuseunknownexits=1";

  protected static RelayNetworkStatusConsensus
      createWithParamsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.paramsLine = line;
    return cb.buildConsensus();
  }

  private String sharedRandPreviousValueLine =
      "shared-rand-previous-value 8 "
      + "grwbnD6I40odtsdtWYxqs0DvPweCur6qG2Fo5p5ivS4=";

  protected static RelayNetworkStatusConsensus
      createWithSharedRandPreviousValueLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.sharedRandPreviousValueLine = line;
    return cb.buildConsensus();
  }

  private String sharedRandCurrentValueLine =
      "shared-rand-current-value 8 "
      + "D88plxd8YeLfCIVAR9gjiFlWB1WqpC53kWr350o1pzw=";

  protected static RelayNetworkStatusConsensus
      createWithSharedRandCurrentValueLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.sharedRandCurrentValueLine = line;
    return cb.buildConsensus();
  }

  List<String> dirSources = new ArrayList<>();

  List<String> statusEntries = new ArrayList<>();

  private String directoryFooterLine = "directory-footer";

  protected void setDirectoryFooterLine(String line) {
    this.directoryFooterLine = line;
  }

  protected static RelayNetworkStatusConsensus
      createWithDirectoryFooterLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.directoryFooterLine = line;
    return cb.buildConsensus();
  }

  private String bandwidthWeightsLine = "bandwidth-weights Wbd=285 "
      + "Wbe=0 Wbg=0 Wbm=10000 Wdb=10000 Web=10000 Wed=1021 Wee=10000 "
      + "Weg=1021 Wem=10000 Wgb=10000 Wgd=8694 Wgg=10000 Wgm=10000 "
      + "Wmb=10000 Wmd=285 Wme=0 Wmg=0 Wmm=10000";

  protected void setBandwidthWeightsLine(String line) {
    this.bandwidthWeightsLine = line;
  }

  protected static RelayNetworkStatusConsensus
      createWithBandwidthWeightsLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.bandwidthWeightsLine = line;
    return cb.buildConsensus();
  }

  private List<String> directorySignatures = new ArrayList<>();

  protected void addDirectorySignature(String directorySignatureString) {
    this.directorySignatures.add(directorySignatureString);
  }

  private String unrecognizedHeaderLine = null;

  protected static RelayNetworkStatusConsensus
      createWithUnrecognizedHeaderLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.unrecognizedHeaderLine = line;
    return cb.buildConsensus();
  }

  private String unrecognizedDirSourceLine = null;

  protected static RelayNetworkStatusConsensus
      createWithUnrecognizedDirSourceLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.unrecognizedDirSourceLine = line;
    return cb.buildConsensus();
  }

  private String unrecognizedStatusEntryLine = null;

  protected static RelayNetworkStatusConsensus
      createWithUnrecognizedStatusEntryLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.unrecognizedStatusEntryLine = line;
    return cb.buildConsensus();
  }

  private String unrecognizedFooterLine = null;

  protected static RelayNetworkStatusConsensus
      createWithUnrecognizedFooterLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.unrecognizedFooterLine = line;
    return cb.buildConsensus();
  }

  private String unrecognizedDirectorySignatureLine = null;

  protected static RelayNetworkStatusConsensus
      createWithUnrecognizedDirectorySignatureLine(String line)
      throws DescriptorParseException {
    ConsensusBuilder cb = new ConsensusBuilder();
    cb.unrecognizedDirectorySignatureLine = line;
    return cb.buildConsensus();
  }

  protected ConsensusBuilder() {
    this.dirSources.add("dir-source tor26 "
        + "14C131DFC5C6F93646BE72FA1401C02A8DF2E8B4 86.59.21.38 "
        + "86.59.21.38 80 443\ncontact Peter Palfrader\nvote-digest "
        + "0333880AA67ED7E07C11108656D0C8D6DD1C7E5D");
    this.dirSources.add("dir-source ides "
        + "27B6B5996C426270A5C95488AA5BCEB6BCC86956 216.224.124.114 "
        + "216.224.124.114 9030 9090\ncontact Mike Perry "
        + "<mikeperryTAfsckedTODorg>\nvote-digest "
        + "1A8827ECD53184F7A771EFA9B3D30DC473FE8670");
    this.statusEntries.add("r ANONIONROUTER "
        + "AHhuQ8zFQJdT8l42Axxc6m6kNwI yEMZ5B/JQixNZgC1+2rLe0pR9rU "
        + "2011-11-30 02:52:58 93.128.66.111 24051 24052\ns Exit Fast "
        + "Named Running V2Dir Valid\nv Tor 0.2.2.34\nw "
        + "Bandwidth=1100\np reject 25,119,135-139,6881-6999");
    this.statusEntries.add("r Magellan AHlabo2RwnD8I7MPOIpJVVPgGJQ "
        + "rB/7uzI4mU38bZ9cSXEy+Z/4Cuk 2011-11-30 05:37:35 "
        + "188.177.149.216 9001 9030\ns Fast Named Running V2Dir "
        + "Valid\nv Tor 0.2.2.34\nw Bandwidth=367\np reject 1-65535");
    this.directorySignatures.add("directory-signature "
        + "14C131DFC5C6F93646BE72FA1401C02A8DF2E8B4 "
        + "3509BA5A624403A905C74DA5C8A0CEC9E0D3AF86\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "NYRcTWAMRiYYiGW0hIbzeZKU6sefg98AwwXrQUCudO8wfA1cfgttTDoscB9I"
        + "TbOY\nr+c30jV/qQCMamTAEDGgJTw8KghI32vytupKallI1EjCOF8UvL1UnA"
        + "LgpaR7sZ3W\n7WQZVVrWDtnYaULOEKfwnGnRC7WwE+YRSysbzwwCVs0=\n"
        + "-----END SIGNATURE-----");
    this.directorySignatures.add("directory-signature "
        + "27B6B5996C426270A5C95488AA5BCEB6BCC86956 "
        + "D5C30C15BB3F1DA27669C2D88439939E8F418FCF\n"
        + "-----BEGIN SIGNATURE-----\n"
        + "DzFPj3vyYrCv0W3r8qDPJPlmeLnadY+drjWkdOqO66Ih/hAWBb9KcBJAX1sX"
        + "aDA7\n/iSaDhduBXuJdcu8lbmMP8d6uYBdRjHXqWDXySUZAkSfPB4JJPNGvf"
        + "oQA/qeby7E\n5374pPPL6WwCLJHkKtk21S9oHDmFBdlZq7JWQelWlVM=\n"
        + "-----END SIGNATURE-----");
  }

  protected byte[] buildConsensusBytes() {
    StringBuilder sb = new StringBuilder();
    this.appendHeader(sb);
    this.appendDirSources(sb);
    this.appendStatusEntries(sb);
    this.appendFooter(sb);
    this.appendDirectorySignatures(sb);
    return sb.toString().getBytes();
  }

  protected RelayNetworkStatusConsensus buildConsensus()
      throws DescriptorParseException {
    byte[] consensusBytes = this.buildConsensusBytes();
    return new RelayNetworkStatusConsensusImpl(consensusBytes,
        new int[] { 0, consensusBytes.length }, null);
  }

  private void appendHeader(StringBuilder sb) {
    if (this.networkStatusVersionLine != null) {
      sb.append(this.networkStatusVersionLine).append("\n");
    }
    if (this.voteStatusLine != null) {
      sb.append(this.voteStatusLine).append("\n");
    }
    if (this.consensusMethodLine != null) {
      sb.append(this.consensusMethodLine).append("\n");
    }
    if (this.validAfterLine != null) {
      sb.append(this.validAfterLine).append("\n");
    }
    if (this.freshUntilLine != null) {
      sb.append(this.freshUntilLine).append("\n");
    }
    if (this.validUntilLine != null) {
      sb.append(this.validUntilLine).append("\n");
    }
    if (this.votingDelayLine != null) {
      sb.append(this.votingDelayLine).append("\n");
    }
    if (this.clientVersionsLine != null) {
      sb.append(this.clientVersionsLine).append("\n");
    }
    if (this.serverVersionsLine != null) {
      sb.append(this.serverVersionsLine).append("\n");
    }
    if (this.packageLines != null) {
      sb.append(this.packageLines).append("\n");
    }
    if (this.knownFlagsLine != null) {
      sb.append(this.knownFlagsLine).append("\n");
    }
    if (this.recommendedClientProtocolsLine != null) {
      sb.append(this.recommendedClientProtocolsLine).append("\n");
    }
    if (this.recommendedRelayProtocolsLine != null) {
      sb.append(this.recommendedRelayProtocolsLine).append("\n");
    }
    if (this.requiredClientProtocolsLine != null) {
      sb.append(this.requiredClientProtocolsLine).append("\n");
    }
    if (this.requiredRelayProtocolsLine != null) {
      sb.append(this.requiredRelayProtocolsLine).append("\n");
    }
    if (this.paramsLine != null) {
      sb.append(this.paramsLine).append("\n");
    }
    if (this.sharedRandPreviousValueLine != null) {
      sb.append(this.sharedRandPreviousValueLine).append("\n");
    }
    if (this.sharedRandCurrentValueLine != null) {
      sb.append(this.sharedRandCurrentValueLine).append("\n");
    }
    if (this.unrecognizedHeaderLine != null) {
      sb.append(this.unrecognizedHeaderLine).append("\n");
    }
  }

  private void appendDirSources(StringBuilder sb) {
    for (String dirSource : this.dirSources) {
      sb.append(dirSource).append("\n");
    }
    if (this.unrecognizedDirSourceLine != null) {
      sb.append(this.unrecognizedDirSourceLine).append("\n");
    }
  }

  private void appendStatusEntries(StringBuilder sb) {
    for (String statusEntry : this.statusEntries) {
      sb.append(statusEntry).append("\n");
    }
    if (this.unrecognizedStatusEntryLine != null) {
      sb.append(this.unrecognizedStatusEntryLine).append("\n");
    }
  }

  private void appendFooter(StringBuilder sb) {
    if (this.directoryFooterLine != null) {
      sb.append(this.directoryFooterLine).append("\n");
    }
    if (this.bandwidthWeightsLine != null) {
      sb.append(this.bandwidthWeightsLine).append("\n");
    }
    if (this.unrecognizedFooterLine != null) {
      sb.append(this.unrecognizedFooterLine).append("\n");
    }
  }

  private void appendDirectorySignatures(StringBuilder sb) {
    for (String directorySignature : this.directorySignatures) {
      sb.append(directorySignature).append("\n");
    }
    if (this.unrecognizedDirectorySignatureLine != null) {
      sb.append(this.unrecognizedDirectorySignatureLine).append("\n");
    }
  }
}

