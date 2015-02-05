# Copyright 2015 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Wraps the standalone JavaScript inside a templated outer JavaScript.

Chrome needs to wrap the standalone JavaScript so it does not access the real
window object, which is done in the wrapper JS. The output of this script is
the file which is included in the Chrome builds.
"""

import optparse
import sys

def main(argv):
  parser = optparse.OptionParser()
  parser.add_option('-t', '--templatefile',
      help='The path to the output JavaScript template.')
  parser.add_option('-i', '--infile',
      help='The path to the standalone JavaScript to inject into the template.')
  parser.add_option('-o', '--outfile',
      help='The path to the output JavaScript.')
  options, _ = parser.parse_args(argv)

  templatepath = options.templatefile
  inpath = options.infile
  outpath = options.outfile

  if templatepath:
    templatefile = open(templatepath, 'r')
  else:
    print 'Please provide path to the template file'
    return 1

  if inpath:
    infile = open(inpath, 'r')
  else:
    print 'Reading input from stdin'
    infile = sys.stdin

  if outpath:
    outfile = open(outpath, 'w')
  else:
    outfile = sys.stdout

  standalone_js = infile.read()
  template_js = templatefile.read()
  output_js = template_js.replace('$$DISTILLER_JAVASCRIPT', standalone_js)
  outfile.write(output_js)
  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv))
