/* Copyright 2017--2020 The Tor Project
 * See LICENSE for licensing information */

package org.torproject.descriptor.impl;

import org.torproject.descriptor.DescriptorParseException;

public class TestDescriptor extends DescriptorImpl {

  protected TestDescriptor(byte[] rawDescriptorBytes, int[] offsetAndLength,
      boolean blankLinesAllowed)
      throws DescriptorParseException {
    super(rawDescriptorBytes, offsetAndLength, null, blankLinesAllowed);
  }

  protected TestDescriptor(byte[] rawDescriptorBytes, int[] offsetAndLength) {
    super(rawDescriptorBytes, offsetAndLength, null);
  }
}



