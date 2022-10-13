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

def filter_fields(header, data, fields):
  """Filter (header, data) with selected header fields.

  Args:
    header ([str]): The header.
    data ([[float]]): The data, with the same number of columns as header.
    fields ([str]): The fields that need to be written.

  Returns:
    (header ([str]), data ([[float]]))

  Examples:
    >>> filter_fields(['a','b','c'], [[0,1,2], [3,4,5]], ['d','b','a'])
    (['b', 'a'], [[1, 0], [4, 3]])
  """

  picked = []
  for f in fields:
    try:
      picked.append(header.index(f))
    except ValueError:
      # OK to have missing values
      pass

  h = [header[i] for i in picked]
  d = []
  for e in data:
    d.append([e[i] for i in picked])
  return (h, d)

def write_features(filename, header, data, fields):
  """Write (header, data) to filename in CSV format, with selected fields.

  Args:
    filename (str): The output filename.
    header ([str]): The header.
    data ([[float]]): The data, with the same number of columns as header.
    fields ([str]): The fields that need to be written.

  Examples:
    >>> write_features(None, ['a','b','c'], [[0,1,2], [3,4,5]], ['d','b','a'])
    b,a
    1,0
    4,3
  """

  (header, data) = filter_fields(header, data, fields)

  if filename:
    writer = csv.writer(open(filename, 'w'))
  else:
    writer = csv.writer(sys.stdout, lineterminator="\n")

  writer.writerow(header)
  writer.writerows(data)

def getGroups(header):
  """Return groups of header fields

  Returns:
    dict of name (str): fields ([str])
  """
  groups = {}
  groupPath = [
    'forum',
    'index',
    'search',
    'view',
    'archive',
    'asp',
    'phpbb',
    'php',
    'pathLength',
    'domain',
    'pathComponents',
    'slugDetector',
    'pathNumbers',
    'lastSegmentLength',
  ]
  groups['path'] = groupPath

  groupNumElement = [
    'numElements',
    'numAnchors',
    'anchorRatio',
    'numForms',
    'numTextInput',
    'numPasswordInput',
    'numPPRE',
  ]
  groups['numElement'] = groupNumElement

  groupVisibleElement = [
    'visibleElements',
    'visibleAnchors',
    'visiblePPRE',
    'visibleAnchorOverPPre',
  ]
  groups['visibleElement'] = groupVisibleElement

  groupEntries = [
    'numSection',
    'numSection2',
    'numSection3',
    'numArticle',
    'numArticle2',
    'numArticle3',
    'numEntries',
    'numEntries2',
    'numEntries3',
    'numH1',
    'numH2',
    'numH3',
    'numH4',
    'headCountSum',
    'headCountMax',
    'entryCountSum',
    'entryCountMax',
  ]
  groups['entries'] = groupEntries

  groupV1 = [
    'openGraph',

    'forum',
    'index',
    'search',
    'view',
    'archive',
    'asp',
    'phpbb',
    'php',
    'pathLength',
    'domain',
    'pathComponents',
    'slugDetector',
    'pathNumbers',
    'lastSegmentLength',

    'formCount',
    'anchorCount',
    'elementCount',
    'anchorRatio',

    'mozScore',
    'mozScoreAllSqrt',
    'mozScoreAllLinear',
  ]
  groups['v1'] = groupV1

  groupV1NoPath = [
    'openGraph',

    'formCount',
    'anchorCount',
    'elementCount',
    'anchorRatio',

    'mozScore',
    'mozScoreAllSqrt',
    'mozScoreAllLinear',
  ]
  groups['v1NoPath'] = groupV1NoPath

  groups['allElement'] = groupNumElement + groupVisibleElement + groupEntries
  groups['mozScores'] = [f for f in header if 'moz' in f]
  groups['noText'] = [f for f in header if not ('inner' in f or 'Content' in f or 'WordCount' in f)]

  return groups

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--out', required=True, help="filename of output")
  parser.add_argument('--marked', help="filename of marked output")
  parser.add_argument('--distilled', help="filename of derived features of distilled content")
  parser.add_argument('--features', required=True, help="filename of derived features")
  options = parser.parse_args(argv)

  if (options.marked is None) + (options.distilled is None) != 1:
    print 'Use exactly one of --marked or --distilled.'
    os.exit(1)

  with open(options.features) as features:
    features = json.load(features)

  if options.marked:
    with open(options.marked) as markedin:
      marked = json.load(markedin)

    markedMap = dict()
    # good:
    # -1 error
    # 0 bad
    # 1 good
    # 2 good w/error
    for m in marked:
      if not 'good' in m:
        continue
      if m['good'] < 0:
        continue
      markedMap[m['url'].strip()] = m

    print "Loaded %d labeled entries" % (len(markedMap))

    merged = []
    for f in features:
      url = f['url']
      if not url in markedMap:
        continue
      merged.append(map(float, [0 if markedMap[url]['good'] == 0 else 1] + f['features'][1::2]))
    print "Merged %d entries" % (len(merged))

  if options.distilled:
    with open(options.distilled) as markedin:
      marked = json.load(markedin)

    markedMap = dict()
    for m in marked:
      feature = m['features']
      feature = dict(zip(feature[::2], feature[1::2]))
      if feature['innerTextLength'] == 0:
        continue
      m['features'] = feature
      markedMap[m['url'].strip()] = m

    print "Loaded %d distilled entries" % (len(markedMap))

    merged = []
    for f in features:
      url = f['url']
      if not url in markedMap:
        continue
      if f['native']['features']['isMobileFriendly'] == 1:
        continue
      if f['native']['distillable'] != 1:
        continue
      feature = markedMap[url]['features']
      merged.append(map(float, [0 if feature['innerTextLength'] < 1000 else 1] + f['features'][1::2]))
    print "Merged %d entries" % (len(merged))

  feature_headers = map(str, features[0]['features'][::2])
  header = ['good'] + feature_headers

  write_features(options.out, header, merged, header)

  # write datasets with a single feature
  outbase = os.path.splitext(options.out)[0]
  for s in feature_headers:
    print 'Single feature: %s' % s
    write_features('%s-feature-%s.csv' % (outbase, s), header, merged, ['good', s])

  # write datasets with feature groups
  for (name, g) in getGroups(feature_headers).iteritems():
    print 'Feature group: %s' % name
    write_features('%s-group-%s.csv' % (outbase, name), header, merged, ['good'] + g)

  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))
