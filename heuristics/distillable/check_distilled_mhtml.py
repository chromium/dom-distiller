#!/usr/bin/env python
# Copyright 2016 The Chromium Authors
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import json
import os
import sys

def compare_innerText(dfeature, mdfeature):
  """Compare the distilled content from the original page with the one from the mhtml archive

  Args:
    dfeature (str): filename of the distilled feature from the original page
    mdfeature (str): filename of the distilled feature from mhtml archive

  Returns:
    True if the content is the same.
  """

  with open(dfeature) as f:
    d = json.load(f)
  with open(mdfeature) as f:
    md = json.load(f)
  mhtml = os.path.splitext(mdfeature)[0] + '.mhtml'
  if d['features']['innerText'] != md['features']['innerText']:
    if md['features']['innerText'] in d['features']['innerText']:
      # The one from the original might have next page stitched.
      return True
    if md['features']['innerText'] == 'No data found.':
      print '%s failed to distill, but %s can' % (mhtml, d['url'])
    else:
      print '\n[ERROR] Different distilled content.\nFrom original (%s):\n"%s"\n\n\nFrom mhtml (%s):\n"%s"\n' % (
        d['url'], d['features']['innerText'],
        mhtml, md['features']['innerText']
      )
    return False
  return True

def compare_distilled(dir):
  """Compare all the distilled contents from the original pages with those from the mhtml archives

  Args:
    dir (str): directory containing all the extracted features
  """

  files = [os.path.join(dir, f) for f in os.listdir(dir)]
  mdfeatures = [f for f in files if os.path.isfile(f) and os.path.splitext(f)[1] == '.mdfeature']
  err = 0
  for mdfeature in mdfeatures:
    dfeature = os.path.splitext(mdfeature)[0] + '.dfeature'
    if not compare_innerText(dfeature, mdfeature):
      err += 1
  print '%d/%d have different distilled content from mhtml' % (err, len(mdfeatures))

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--dir', required=True, help="data directory")
  options = parser.parse_args(argv)

  compare_distilled(options.dir)

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))
