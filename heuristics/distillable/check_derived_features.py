#!/usr/bin/env python
# Copyright 2016 The Chromium Authors
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import csv
import json
import os
import shutil
import sys
import unittest

from write_features_csv import filter_fields, getGroups

def isAlmostEqual(a, b, header, eps=0.001):
  assert len(a) == len(b)
  for i in range(len(a)):
    if abs(a[i] - b[i]) > eps:
      print '%s mismatch: a[%d] = %f, b[%d] = %f' % (header[i], i, a[i], i, b[i])
      return False
  return True

def compareDerivedFeatures(features, from_mhtml):
  """Compare the derived features from the JS vs. native impl

  Args:
    features: the JSON dump of features
    from_mhtml (bool): whether the features are collected from mhtml archive
  """
  header = map(str, features[0]['features'][::2])
  err = 0
  skipped = 0
  for f in features:
    if not 'native' in f:
      print 'Skipped %s' % (f['url'])
      skipped += 1
      continue
    data = [map(float, f['features'][1::2])]
    (h, data) = filter_fields(header, data, getGroups(header)['v1'])
    js = data[0]
    # js is now the derived features from JS aligned with native impl.
    if not from_mhtml and js[17] != f['native']['features']['elementCount']:
      # elementCount is simple enough so assume it's correct.
      # If elementCount doesn't match, the DOM might've changed between JS and
      # native runs.
      # For mhtml, this should not be possible since DOM is static.
      print 'Skipped %s' % (f['url'])
      skipped += 1
      continue

    native = map(float, f['native']['derived_features'])
    data = [js, native]
    # Filter out the features derived from path if it is from mhtml, because
    # the url from native impl would be the file:// one.
    if from_mhtml:
      (h, data) = filter_fields(h, data, getGroups(header)['v1NoPath'])
    if not isAlmostEqual(data[0], data[1], h):
      err += 1
      print f['url']
      if from_mhtml:
        print '%s.mhtml' % f['index']
      print data[0]
      print data[1]
      print
  print '%d/%d have mismatching derived features, %d were skipped.' % (err, len(features), skipped)

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--features', required=True, help="filename of aggregated derived features")
  parser.add_argument('--from-mhtml', action='store_true', help="whether the features are from mhtml")
  options = parser.parse_args(argv)

  with open(options.features) as features:
    features = json.load(features)
    compareDerivedFeatures(features, options.from_mhtml)

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))
