#!/usr/bin/env python
# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import csv
import json
import marshal
import os
import shutil
import math
import sys
import re
import urlparse

def CountMatches(s, p):
  return len(re.findall(p, s))

def WordCount(s):
  return CountMatches(s, r'\w+')

def GetLastSegment(path):
  return re.search('[^/]*\/?$', path).group(0)

def CalcDerivedFeatures(index, opengraph, url, title, numElements, numAnchors, numForms, numPPRE, visibleElements, visiblePPRE,
   innerText, textContent, innerHTML, numText, numPassword, mozScores):
  path = urlparse.urlparse(url).path

  path = path.encode('utf-8')
  innerText = innerText.encode('utf-8')
  textContent = textContent.encode('utf-8')
  innerHTML = innerHTML.encode('utf-8')

  innerTextWords = WordCount(innerText)
  textContentWords = WordCount(textContent)
  innerHTMLWords = WordCount(innerHTML)
  return [
    'openGraph', opengraph,

    'forum', 'forum' in path,
    'index', 'index' in path,
    'search', 'search' in path,
    'view', 'view' in path,
    'archive', 'archive' in path,
    'asp', '.asp' in path,
    'phpbb', 'phpbb' in path,
    'php', path.endswith('.php'),
    'pathLength', len(path),
    'domain', len(path) < 2,
    'pathComponents', CountMatches(path, r'\/.'),
    'slugDetector', CountMatches(path, r'[^\w/]'),
    'pathNumbers', CountMatches(path, r'\d+'),
    'lastSegmentLength', len(GetLastSegment(path)),

    'formCount', numForms,
    'anchorCount', numAnchors,
    'elementCount', numElements,
    'anchorRatio', float(numAnchors) / max(1, numElements),

    'mozScore', min(mozScores[3], 6 * math.sqrt(1000-140)),
    'mozScoreAllSqrt', min(mozScores[4], 6 * math.sqrt(1000)),
    'mozScoreAllLinear', min(mozScores[5], 6000),
  ]

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--out', required=True)
  parser.add_argument('--core', required=True)
  options = parser.parse_args(argv)

  if os.path.exists(options.out):
    raise Exception('exists: ' + options.out)

  core = None
  with open(options.core) as core_file:
    core = marshal.load(core_file)

  for entry in core:
    features = entry['features']
    print 'processing %d' % (entry['index'])
    entry['features'] = CalcDerivedFeatures(
      entry['index'],
      features['opengraph'],
      features['url'],
      features['title'],
      features['numElements'],
      features['numAnchors'],
      features['numForms'],
      features['numPPRE'],
      features['visibleElements'],
      features['visiblePPRE'],
      features['innerText'],
      features['textContent'],
      features['innerHTML'],
      features['numTextInput'],
      features['numPasswordInput'],
      [features['mozScore'], features['mozScoreAllSqrt'], features['mozScoreAllLinear'],
       features['mozScoreFast'], features['mozScoreFastAllSqrt'], features['mozScoreFastAllLinear']]
      )

  with open(options.out, 'w') as outfile:
    json.dump(core, outfile, indent=1)

  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))

