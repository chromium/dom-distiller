# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Converts gwt-compiled javascript to standalone javascript

gwt-compiled javascript is in the form of an html file that is expected to be
loaded into its own iframe (with some extra work done in the embedding page).
This reads such a compiled file and converts it to standalone javascript that
can be loaded in the main frame of a page.
"""

import glob
import optparse
import os
import re
import sys

def ExtractJavascript(content):
  """ Extracts javascript from within <script> tags in content. """
  scriptre = re.compile('<script>(<!--)?(?P<inner>.*?)(-->)?</script>',
                        re.MULTILINE | re.DOTALL)
  result = ''
  for match in scriptre.finditer(content):
    result += match.group('inner')
  return result

def FindInputPath(indir):
  """ Finds the path to a file of the form
  in/dir/DC2C3039DDCBB4AD9B63A9D3E25A0BDF.cache.html

  There should only be one such file in indir.
  """
  files = glob.glob(os.path.join(indir, '*.cache.html'))
  if len(files) != 1:
    print 'Unable to find input path: ', files
    return None
  return files[0]

def main(argv):
  parser = optparse.OptionParser()
  parser.add_option('-i', '--indir')
  parser.add_option('-o', '--outfile')
  options, _ = parser.parse_args(argv)

  if options.indir:
    inpath = FindInputPath(options.indir)
    if not inpath:
      return 1
    infile = open(inpath, 'r')
  else:
    print 'Reading input from stdin'
    infile = sys.stdin

  if options.outfile:
    outfile = open(options.outfile, 'w')
  else:
    outfile = sys.stdout

  compiledJs = infile.read()
  # The compiled js expects to be running in its own iframe. This won't be the
  # case for the standalone js.
  compiledJs = compiledJs.replace('var $wnd = parent', 'var $wnd = window')
  outfile.write(ExtractJavascript(compiledJs))
  outfile.write('gwtOnLoad(undefined,"domdistiller","",0);\n')

  return 0


if __name__ == '__main__':
  sys.exit(main(sys.argv))
