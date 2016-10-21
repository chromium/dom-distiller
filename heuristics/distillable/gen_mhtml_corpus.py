#!/usr/bin/env python

import json
import sys
import os

"""Converter from scrawled MHTML to DOM Distiller eval corpora.

Usage:
- Scrawl with get_screenshots.py with MHTML output.
- cd to the output directory.
- Pick the entries, and put the IDs in a file.
  $ cat list
  9
  876
  5432
- Run the converter like this:
  $ cat list | /path/to/gen_mhtml_corpus.py > mhtml.txt

Known issues:
- Only support utf-8 encoding.
"""

def gen_corpus(id):
  info = '%s.info' % id
  feature = '%s.feature' % id
  if not os.path.exists(feature) or not os.path.exists(info):
    print >>sys.stderr, "\nERROR ID %s doesn't exist" % id
    return
  features = json.load(open(feature, 'r'))
  url = features['url']
  title = features['features']['title']
  try:
    mhtml = open('%s.mhtml' % id, 'rb').read()
    if not mhtml.startswith('From:'):
      print >>sys.stderr, "\nSKIPPED ID %s" % id
      return
    res = []
    res.append(url)
    res.append('url: ' + json.dumps(url))
    res.append('title: ' + json.dumps(title))
    res.append('html: ' + json.dumps(mhtml))
    res.append('content: ""')
    res.append('')
    print '\n'.join(res)
    sys.stderr.write('.')
  except UnicodeDecodeError, e:
    print >>sys.stderr, "\nERROR handling ID %s" % id
    print >>sys.stderr, e
    pass

def main(argv):
  list = sys.stdin.read().split()
  for i in list:
    gen_corpus(i)
  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))
