#!/usr/bin/env python
# Copyright 2016 The Chromium Authors
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

def CalcDerivedFeatures(index, raw):
  return _CalcDerivedFeatures(
    index,
    raw,
    raw['opengraph'],
    raw['url'],
    raw['title'],
    raw['numElements'],
    raw['numAnchors'],
    raw['numForms'],
    raw['numPPRE'],
    raw['visibleElements'],
    raw['visiblePPRE'],
    raw['innerText'],
    raw['textContent'],
    raw['innerHTML'],
    raw['numTextInput'],
    raw['numPasswordInput']
    )

def _CalcDerivedFeatures(index, raw, opengraph, url, title, numElements, numAnchors, numForms, numPPRE, visibleElements, visiblePPRE,
   innerText, textContent, innerHTML, numText, numPassword):
  path = urlparse.urlparse(url).path

  path = path.encode('utf-8')
  innerText = innerText.encode('utf-8')
  textContent = textContent.encode('utf-8')
  innerHTML = innerHTML.encode('utf-8')

  innerTextWords = WordCount(innerText)
  textContentWords = WordCount(textContent)
  innerHTMLWords = WordCount(innerHTML)
  features = [
    'id', index,
    'sin', math.sin(index),
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

    'visibleRatio', float(visibleElements) / max(1, numElements),
    'visiblePPRERatio', float(visiblePPRE) / max(1, numPPRE),
    'PPRERatio', float(numPPRE) / max(1, numElements),
    'anchorPPRERatio', float(numAnchors) / max(1, numPPRE),

    'innerTextLength', len(innerText),
    'textContentLength', len(textContent),
    'innerHtmlLength', len(innerHTML),
    'innerTextLengthRatio', float(len(innerText)) / max(1, len(innerHTML)),
    'textContentLengthRatio', float(len(textContent)) / max(1, len(innerHTML)),
    'innerTexttextContentLengthRatio',float(len(innerText)) / max(1, len(textContent)),

    'innerTextWordCount', innerTextWords,
    'textContentWordCount', textContentWords,
    'innerhtmlWordCount', innerHTMLWords,
    'innerTextWordCountRatio', float(innerTextWords) / max(1, innerHTMLWords),
    'textContentWordCountRatio', float(textContentWords) / max(1, innerHTMLWords),
    'innerTexttextContentWordCountRatio', float(innerTextWords) / max(1, textContentWords),

    'textCount', numText,
    'passwordCount', numPassword,
    'formCount', numForms,
    'anchorCount', numAnchors,
    'elementCount', numElements,
    'anchorRatio', float(numAnchors) / max(1, numElements),
  ]

  for k in sorted(raw):
    if 'mozScore' in k or 'num' in k:
      features += [k, raw[k]]

  return features

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

    entry['features'] = CalcDerivedFeatures(entry['index'], features)

  with open(options.out, 'w') as outfile:
    json.dump(core, outfile, indent=1)

  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))

