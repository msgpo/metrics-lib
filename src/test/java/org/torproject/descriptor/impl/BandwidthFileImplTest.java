/* Copyright 2019 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.torproject.descriptor.BandwidthFile;
import org.torproject.descriptor.DescriptorParseException;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class BandwidthFileImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Example from bandwidth-file-spec.txt: Version 1.0.0, generated by Torflow.
   */
  private static final String[] specExample100 = new String[] {
      "1523911758",
      "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80 bw=760 nick=Test "
          + "measured_at=1523911725 updated_at=1523911725 "
          + "pid_error=4.11374090719 pid_error_sum=4.11374090719 "
          + "pid_bw=57136645 pid_delta=2.12168374577 circ_fail=0.2 "
          + "scanner=/filepath",
      "node_id=$96C15995F30895689291F455587BD94CA427B6FC bw=189 nick=Test2 "
          + "measured_at=1523911623 updated_at=1523911623 "
          + "pid_error=3.96703337994 pid_error_sum=3.96703337994 "
          + "pid_bw=47422125 pid_delta=2.65469736988 circ_fail=0.0 "
          + "scanner=/filepath" };

  @Test
  public void testSpecExample100() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample100).build(), null);
    assertEquals(LocalDateTime.of(2018, 4, 16, 20, 49, 18),
        bandwidthFile.timestamp());
    assertEquals("1.0.0", bandwidthFile.version());
    assertEquals("torflow", bandwidthFile.software());
    assertFalse(bandwidthFile.softwareVersion().isPresent());
    assertFalse(bandwidthFile.fileCreated().isPresent());
    assertFalse(bandwidthFile.generatorStarted().isPresent());
    assertFalse(bandwidthFile.earliestBandwidth().isPresent());
    assertFalse(bandwidthFile.latestBandwidth().isPresent());
    assertFalse(bandwidthFile.numberEligibleRelays().isPresent());
    assertFalse(bandwidthFile.minimumPercentEligibleRelays().isPresent());
    assertFalse(bandwidthFile.numberConsensusRelays().isPresent());
    assertFalse(bandwidthFile.percentEligibleRelays().isPresent());
    assertFalse(bandwidthFile.minimumNumberEligibleRelays().isPresent());
    assertFalse(bandwidthFile.scannerCountry().isPresent());
    assertFalse(bandwidthFile.destinationsCountries().isPresent());
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityListCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityRelayCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementAttemptCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementFailureCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedErrorCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedNearCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedOldCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedFewCount().isPresent());
    assertFalse(bandwidthFile.timeToReportHalfNetwork().isPresent());
    assertEquals(2, bandwidthFile.relayLines().size());
    BandwidthFile.RelayLine firstRelayLine = bandwidthFile.relayLines().get(0);
    assertEquals(Optional.of("$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80"),
        firstRelayLine.nodeId());
    assertFalse(firstRelayLine.masterKeyEd25519().isPresent());
    assertEquals(760, firstRelayLine.bw());
    Map<String, String> expectedFirstAdditionalKeyValues
        = new LinkedHashMap<>();
    expectedFirstAdditionalKeyValues.put("nick", "Test");
    expectedFirstAdditionalKeyValues.put("measured_at", "1523911725");
    expectedFirstAdditionalKeyValues.put("updated_at", "1523911725");
    expectedFirstAdditionalKeyValues.put("pid_error", "4.11374090719");
    expectedFirstAdditionalKeyValues.put("pid_error_sum", "4.11374090719");
    expectedFirstAdditionalKeyValues.put("pid_bw", "57136645");
    expectedFirstAdditionalKeyValues.put("pid_delta", "2.12168374577");
    expectedFirstAdditionalKeyValues.put("circ_fail", "0.2");
    expectedFirstAdditionalKeyValues.put("scanner", "/filepath");
    assertEquals(expectedFirstAdditionalKeyValues,
        firstRelayLine.additionalKeyValues());
  }

  @Test
  public void testTimestampAsKeyValue() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Unable to parse timestamp in first line"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .replaceLineStartingWith("1523911758", "timestamp=1523911758")
        .build(), null);
  }

  @Test
  public void testEmptyLine() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Blank lines are not allowed."));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .appendLines("")
        .build(), null);
  }

  @Test
  public void testHeaderLineAtEnd() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Expected relay line, but line contains neither node_id nor "
        + "master_key_ed25519"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .appendLines("version=1.0.0")
        .build(), null);
  }

  @Test
  public void testRelayLineWithoutRelayId() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Either additional header line must not use keywords specified in "
        + "relay lines, or relay line is missing required keys"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .replaceLineStartingWith(
        "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80",
        "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80")
        .build(), null);
  }

  @Test
  public void testRelayLineWithoutBw() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Expected relay line, but line contains neither node_id nor "
        + "master_key_ed25519"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .replaceLineStartingWith(
        "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80", "bw=760")
        .build(), null);
  }

  @Test
  public void testBwNotANumber() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Unable to parse bw 'slow' in line"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample100)
        .replaceLineStartingWith(
            "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80",
            "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80 bw=slow")
        .build(), null);
  }

  @Test
  public void testRelayLineTrailingSpace() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample100)
        .replaceLineStartingWith(
            "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80",
            "node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80 bw=760 ")
        .build(), null);
    /* It's okay that this line ends with a space, we're parsing it anyway. */
    assertEquals(2, bandwidthFile.relayLines().size());
    BandwidthFile.RelayLine firstRelayLine = bandwidthFile.relayLines().get(0);
    assertEquals(Optional.of("$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80"),
        firstRelayLine.nodeId());
    assertEquals(760, firstRelayLine.bw());
  }

  /**
   * Example from bandwidth-file-spec.txt: Version 1.1.0, generated by sbws
   * version 0.1.0.
   */
  private static final String[] specExample110 = new String[] {
      "1523911758",
      "version=1.1.0",
      "software=sbws",
      "software_version=0.1.0",
      "latest_bandwidth=2018-04-16T20:49:18",
      "file_created=2018-04-16T21:49:18",
      "generator_started=2018-04-16T15:13:25",
      "earliest_bandwidth=2018-04-16T15:13:26",
      "====",
      "bw=380 error_circ=0 error_misc=0 error_stream=1 "
          + "master_key_ed25519=YaqV4vbvPYKucElk297eVdNArDz9HtIwUoIeo0+cVIpQ "
          + "nick=Test node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80 "
          + "rtt=380 success=1 time=2018-05-08T16:13:26",
      "bw=189 error_circ=0 error_misc=0 error_stream=0 "
          + "master_key_ed25519=a6a+dZadrQBtfSbmQkP7j2ardCmLnm5NJ4ZzkvDxbo0I "
          + "nick=Test2 node_id=$96C15995F30895689291F455587BD94CA427B6FC "
          + "rtt=378 success=1 time=2018-05-08T16:13:36" };

  @Test
  public void testSpecExample110() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample110).build(), null);
    assertEquals(LocalDateTime.of(2018, 4, 16, 20, 49, 18),
        bandwidthFile.timestamp());
    assertEquals("1.1.0", bandwidthFile.version());
    assertEquals("sbws", bandwidthFile.software());
    assertEquals(Optional.of("0.1.0"), bandwidthFile.softwareVersion());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 21, 49, 18)),
        bandwidthFile.fileCreated());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 25)),
        bandwidthFile.generatorStarted());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 26)),
        bandwidthFile.earliestBandwidth());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 20, 49, 18)),
        bandwidthFile.latestBandwidth());
    assertFalse(bandwidthFile.numberEligibleRelays().isPresent());
    assertFalse(bandwidthFile.minimumPercentEligibleRelays().isPresent());
    assertFalse(bandwidthFile.numberConsensusRelays().isPresent());
    assertFalse(bandwidthFile.percentEligibleRelays().isPresent());
    assertFalse(bandwidthFile.minimumNumberEligibleRelays().isPresent());
    assertFalse(bandwidthFile.scannerCountry().isPresent());
    assertFalse(bandwidthFile.destinationsCountries().isPresent());
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityListCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityRelayCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementAttemptCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementFailureCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedErrorCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedNearCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedOldCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedFewCount().isPresent());
    assertFalse(bandwidthFile.timeToReportHalfNetwork().isPresent());
  }

  @Test
  public void testTerminatorLineTooShort() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Unrecognized line '===' starting with '=' character"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample110)
        .replaceLineStartingWith("====", "===").build(), null);
  }

  @Test
  public void testDateTimeContainingSpace() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Unable to parse date-time string"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample110)
        .replaceLineStartingWith("earliest_bandwidth",
        "earliest_bandwidth=2018-04-16 15:13:26")
        .build(), null);
  }

  /**
   * Example from bandwidth-file-spec.txt: Version 1.2.0, generated by sbws
   * version 1.0.3.
   */
  private static final String[] specExample120 = new String[] {
      "1523911758",
      "version=1.2.0",
      "latest_bandwidth=2018-04-16T20:49:18",
      "file_created=2018-04-16T21:49:18",
      "generator_started=2018-04-16T15:13:25",
      "earliest_bandwidth=2018-04-16T15:13:26",
      "minimum_number_eligible_relays=3862",
      "minimum_percent_eligible_relays=60",
      "number_consensus_relays=6436",
      "number_eligible_relays=6000",
      "percent_eligible_relays=93",
      "software=sbws",
      "software_version=1.0.3",
      "=====",
      "bw=38000 bw_mean=1127824 bw_median=1180062 desc_avg_bw=1073741824 "
          + "desc_obs_bw_last=17230879 desc_obs_bw_mean=14732306 error_circ=0 "
          + "error_misc=0 error_stream=1 "
          + "master_key_ed25519=YaqV4vbvPYKucElk297eVdNArDz9HtIwUoIeo0+cVIpQ "
          + "nick=Test node_id=$68A483E05A2ABDCA6DA5A3EF8DB5177638A27F80 "
          + "rtt=380 success=1 time=2018-05-08T16:13:26",
      "bw=1 bw_mean=199162 bw_median=185675 desc_avg_bw=409600 "
          + "desc_obs_bw_last=836165 desc_obs_bw_mean=858030 error_circ=0 "
          + "error_misc=0 error_stream=0 "
          + "master_key_ed25519=a6a+dZadrQBtfSbmQkP7j2ardCmLnm5NJ4ZzkvDxbo0I "
          + "nick=Test2 node_id=$96C15995F30895689291F455587BD94CA427B6FC "
          + "rtt=378 success=1 time=2018-05-08T16:13:36" };

  @Test
  public void testSpecExample120() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample120).build(), null);
    assertEquals(LocalDateTime.of(2018, 4, 16, 20, 49, 18),
        bandwidthFile.timestamp());
    assertEquals("1.2.0", bandwidthFile.version());
    assertEquals("sbws", bandwidthFile.software());
    assertEquals(Optional.of("1.0.3"), bandwidthFile.softwareVersion());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 21, 49, 18)),
        bandwidthFile.fileCreated());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 25)),
        bandwidthFile.generatorStarted());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 26)),
        bandwidthFile.earliestBandwidth());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 20, 49, 18)),
        bandwidthFile.latestBandwidth());
    assertEquals(Optional.of(6000), bandwidthFile.numberEligibleRelays());
    assertEquals(Optional.of(60), bandwidthFile.minimumPercentEligibleRelays());
    assertEquals(Optional.of(6436), bandwidthFile.numberConsensusRelays());
    assertEquals(Optional.of(93), bandwidthFile.percentEligibleRelays());
    assertEquals(Optional.of(3862),
        bandwidthFile.minimumNumberEligibleRelays());
    assertFalse(bandwidthFile.scannerCountry().isPresent());
    assertFalse(bandwidthFile.destinationsCountries().isPresent());
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityListCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityRelayCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementAttemptCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementFailureCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedErrorCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedNearCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedOldCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedFewCount().isPresent());
    assertFalse(bandwidthFile.timeToReportHalfNetwork().isPresent());
  }

  @Test
  public void testNumberEligibleRelaysNotAnInt()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Unable to parse int"));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample120)
        .replaceLineStartingWith("number_eligible_relays=6000",
        "number_eligible_relays=sixthousand").build(), null);
  }

  /**
   * Example from bandwidth-file-spec.txt: Version 1.2.0, generated by sbws
   * version 1.0.3 when there are not enough eligible measured relays.
   */
  private static final String[] specExample120NotEnough = new String[] {
      "1540496079",
      "version=1.2.0",
      "earliest_bandwidth=2018-10-20T19:35:52",
      "file_created=2018-10-25T19:35:03",
      "generator_started=2018-10-25T11:42:56",
      "latest_bandwidth=2018-10-25T19:34:39",
      "minimum_number_eligible_relays=3862",
      "minimum_percent_eligible_relays=60",
      "number_consensus_relays=6436",
      "number_eligible_relays=2960",
      "percent_eligible_relays=46",
      "software=sbws",
      "software_version=1.0.3",
      "=====" };

  @Test
  public void testSpecExample120NotEnough() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample120NotEnough).build(), null);
    assertEquals(LocalDateTime.of(2018, 10, 25, 19, 34, 39),
        bandwidthFile.timestamp());
    assertEquals("1.2.0", bandwidthFile.version());
    assertEquals("sbws", bandwidthFile.software());
    assertEquals(Optional.of("1.0.3"), bandwidthFile.softwareVersion());
    assertEquals(Optional.of(LocalDateTime.of(2018, 10, 25, 19, 35, 3)),
        bandwidthFile.fileCreated());
    assertEquals(Optional.of(LocalDateTime.of(2018, 10, 25, 11, 42, 56)),
        bandwidthFile.generatorStarted());
    assertEquals(Optional.of(LocalDateTime.of(2018, 10, 20, 19, 35, 52)),
        bandwidthFile.earliestBandwidth());
    assertEquals(Optional.of(LocalDateTime.of(2018, 10, 25, 19, 34, 39)),
        bandwidthFile.latestBandwidth());
    assertEquals(Optional.of(2960), bandwidthFile.numberEligibleRelays());
    assertEquals(Optional.of(60), bandwidthFile.minimumPercentEligibleRelays());
    assertEquals(Optional.of(6436), bandwidthFile.numberConsensusRelays());
    assertEquals(Optional.of(46), bandwidthFile.percentEligibleRelays());
    assertEquals(Optional.of(3862),
        bandwidthFile.minimumNumberEligibleRelays());
    assertFalse(bandwidthFile.scannerCountry().isPresent());
    assertFalse(bandwidthFile.destinationsCountries().isPresent());
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityListCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityRelayCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementAttemptCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementFailureCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedErrorCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedNearCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedOldCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedFewCount().isPresent());
    assertFalse(bandwidthFile.timeToReportHalfNetwork().isPresent());
  }

  /**
   * Example from bandwidth-file-spec.txt: Version 1.3.0 headers generated by
   * sbws version 1.0.4.
   */
  private static final String[] specExample130Headers = new String[] {
      "1523911758",
      "version=1.3.0",
      "latest_bandwidth=2018-04-16T20:49:18",
      "destinations_countries=TH,ZZ",
      "file_created=2018-04-16T21:49:18",
      "generator_started=2018-04-16T15:13:25",
      "earliest_bandwidth=2018-04-16T15:13:26",
      "minimum_number_eligible_relays=3862",
      "minimum_percent_eligible_relays=60",
      "number_consensus_relays=6436",
      "number_eligible_relays=6000",
      "percent_eligible_relays=93",
      "scanner_country=SN",
      "software=sbws",
      "software_version=1.0.4",
      "=====" };

  @Test
  public void testSpecExample130Headers() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample130Headers).build(), null);
    assertEquals(LocalDateTime.of(2018, 4, 16, 20, 49, 18),
        bandwidthFile.timestamp());
    assertEquals("1.3.0", bandwidthFile.version());
    assertEquals("sbws", bandwidthFile.software());
    assertEquals(Optional.of("1.0.4"), bandwidthFile.softwareVersion());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 21, 49, 18)),
        bandwidthFile.fileCreated());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 25)),
        bandwidthFile.generatorStarted());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 26)),
        bandwidthFile.earliestBandwidth());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 20, 49, 18)),
        bandwidthFile.latestBandwidth());
    assertEquals(Optional.of(6000), bandwidthFile.numberEligibleRelays());
    assertEquals(Optional.of(60), bandwidthFile.minimumPercentEligibleRelays());
    assertEquals(Optional.of(6436), bandwidthFile.numberConsensusRelays());
    assertEquals(Optional.of(93), bandwidthFile.percentEligibleRelays());
    assertEquals(Optional.of(3862),
        bandwidthFile.minimumNumberEligibleRelays());
    assertEquals(Optional.of("SN"), bandwidthFile.scannerCountry());
    assertArrayEquals(new String[] { "TH", "ZZ" },
        bandwidthFile.destinationsCountries().orElse(null));
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityListCount().isPresent());
    assertFalse(bandwidthFile.recentPriorityRelayCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementAttemptCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementFailureCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedErrorCount().isPresent());
    assertFalse(
        bandwidthFile.recentMeasurementsExcludedNearCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedOldCount().isPresent());
    assertFalse(bandwidthFile.recentMeasurementsExcludedFewCount().isPresent());
    assertFalse(bandwidthFile.timeToReportHalfNetwork().isPresent());
  }

  @Test
  public void testScannerCountryLowerCase() throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Invalid country code 'sn'."));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample130Headers)
        .replaceLineStartingWith("scanner_country", "scanner_country=sn")
        .build(), null);
  }

  @Test
  public void testDestinationsCountriesLowerCase()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Invalid country code list 'th,zz'."));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample130Headers)
        .replaceLineStartingWith("destinations_countries",
            "destinations_countries=th,zz")
        .build(), null);
  }

  @Test
  public void testDestinationsCountriesEndingWithComma()
      throws DescriptorParseException {
    this.thrown.expect(DescriptorParseException.class);
    this.thrown.expectMessage(Matchers.containsString(
        "Invalid country code list 'TH,'."));
    new BandwidthFileImpl(new TestDescriptorBuilder(specExample130Headers)
        .replaceLineStartingWith("destinations_countries",
            "destinations_countries=TH,")
        .build(), null);
  }

  /**
   * Example from bandwidth-file-spec.txt: Version 1.4.0 generated by sbws
   * version 1.1.0.
   */
  private static final String[] specExample140 = new String[] {
      "1523911758",
      "version=1.4.0",
      "latest_bandwidth=2018-04-16T20:49:18",
      "destinations_countries=TH,ZZ",
      "file_created=2018-04-16T21:49:18",
      "generator_started=2018-04-16T15:13:25",
      "earliest_bandwidth=2018-04-16T15:13:26",
      "minimum_number_eligible_relays=3862",
      "minimum_percent_eligible_relays=60",
      "number_consensus_relays=6436",
      "number_eligible_relays=6000",
      "percent_eligible_relays=93",
      "recent_measurement_attempt_count=6243",
      "recent_measurement_failure_count=732",
      "recent_measurements_excluded_error_count=969",
      "recent_measurements_excluded_few_count=3946",
      "recent_measurements_excluded_near_count=90",
      "recent_measurements_excluded_old_count=0",
      "recent_priority_list_count=20",
      "recent_priority_relay_count=6243",
      "scanner_country=SN",
      "software=sbws",
      "software_version=1.1.0",
      "time_to_report_half_network=57273",
      "=====",
      "bw=1 error_circ=1 error_destination=0 error_misc=0 error_second_relay=0 "
        + "error_stream=0 "
        + "master_key_ed25519=J3HQ24kOQWac3L1xlFLp7gY91qkb5NuKxjj1BhDi+m8 "
        + "nick=snap269 node_id=$DC4D609F95A52614D1E69C752168AF1FCAE0B05F "
        + "relay_recent_measurement_attempt_count=3 "
        + "relay_recent_measurements_excluded_error_count=1 "
        + "relay_recent_measurements_excluded_near_count=3 "
        + "relay_recent_consensus_count=3 relay_recent_priority_list_count=3 "
        + "success=3 time=2019-03-16T18:20:57 unmeasured=1 vote=0",
      "bw=1 error_circ=0 error_destination=0 error_misc=0 error_second_relay=0 "
        + "error_stream=2 "
        + "master_key_ed25519=h6ZB1E1yBFWIMloUm9IWwjgaPXEpL5cUbuoQDgdSDKg "
        + "nick=relay node_id=$C4544F9E209A9A9B99591D548B3E2822236C0503 "
        + "relay_recent_measurement_attempt_count=3 "
        + "relay_recent_measurements_excluded_error_count=2 "
        + "relay_recent_measurements_excluded_few_count=1 "
        + "relay_recent_consensus_count=3 relay_recent_priority_list_count=3 "
        + "success=1 time=2019-03-17T06:50:58 unmeasured=1 vote=0" };

  @Test
  public void testSpecExample140() throws DescriptorParseException {
    BandwidthFile bandwidthFile = new BandwidthFileImpl(
        new TestDescriptorBuilder(specExample140).build(), null);
    assertEquals(LocalDateTime.of(2018, 4, 16, 20, 49, 18),
        bandwidthFile.timestamp());
    assertEquals("1.4.0", bandwidthFile.version());
    assertEquals("sbws", bandwidthFile.software());
    assertEquals(Optional.of("1.1.0"), bandwidthFile.softwareVersion());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 21, 49, 18)),
        bandwidthFile.fileCreated());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 25)),
        bandwidthFile.generatorStarted());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 15, 13, 26)),
        bandwidthFile.earliestBandwidth());
    assertEquals(Optional.of(LocalDateTime.of(2018, 4, 16, 20, 49, 18)),
        bandwidthFile.latestBandwidth());
    assertEquals(Optional.of(6000), bandwidthFile.numberEligibleRelays());
    assertEquals(Optional.of(60), bandwidthFile.minimumPercentEligibleRelays());
    assertEquals(Optional.of(6436), bandwidthFile.numberConsensusRelays());
    assertEquals(Optional.of(93), bandwidthFile.percentEligibleRelays());
    assertEquals(Optional.of(3862),
        bandwidthFile.minimumNumberEligibleRelays());
    assertEquals(Optional.of("SN"), bandwidthFile.scannerCountry());
    assertArrayEquals(new String[] { "TH", "ZZ" },
        bandwidthFile.destinationsCountries().orElse(null));
    assertFalse(bandwidthFile.recentConsensusCount().isPresent());
    assertEquals(Optional.of(20),
        bandwidthFile.recentPriorityListCount());
    assertEquals(Optional.of(6243),
        bandwidthFile.recentPriorityRelayCount());
    assertEquals(Optional.of(6243),
        bandwidthFile.recentMeasurementAttemptCount());
    assertEquals(Optional.of(732),
        bandwidthFile.recentMeasurementFailureCount());
    assertEquals(Optional.of(969),
        bandwidthFile.recentMeasurementsExcludedErrorCount());
    assertEquals(Optional.of(90),
        bandwidthFile.recentMeasurementsExcludedNearCount());
    assertEquals(Optional.of(0),
        bandwidthFile.recentMeasurementsExcludedOldCount());
    assertEquals(Optional.of(3946),
        bandwidthFile.recentMeasurementsExcludedFewCount());
    assertEquals(Optional.of(Duration.ofSeconds(57273L)),
        bandwidthFile.timeToReportHalfNetwork());
  }
}
