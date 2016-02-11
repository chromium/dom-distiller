#!/usr/bin/env python
# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import csv
import json
import os
import shutil
import sys

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--out', required=True)
  parser.add_argument('--marked', required=True)
  parser.add_argument('--features', required=True)
  options = parser.parse_args(argv)

  marked = None
  with open(options.marked) as markedin:
    marked = json.load(markedin)

  features = None
  with open(options.features) as features:
    features = json.load(features)

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
    markedMap[m['url']] = m

  merged = []
  for f in features:
    url = f['url']
    if not url in markedMap:
      continue
    merged.append(map(float, [0 if markedMap[url]['good'] == 0 else 1] + f['features'][1::2]))

  header = ['good'] + map(str, features[0]['features'][::2])

  with open(options.out, 'w') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(header)
    for e in merged:
      writer.writerow(e)

  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))

