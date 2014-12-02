# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Converts gwt-compiled javascript to standalone javascript

gwt-compiled javascript is in the form of an js file that is expected to be
loaded into its own script tag. This reads such a compiled file and converts it
to standalone javascript that can be loaded as Chrome does.
"""

# TODO(cjhopman): The proper way to do this is to write a gwt Linker
# (gwt.core.ext.Linker) and use that for compilation. See
# http://crbug.com/437113

import glob
import optparse
import os
import re
import sys

def ExtractJavascript(content):
  """ Extracts javascript from within <script> tags in content. """
  lines = content.split('\n');
  # The generated javascript looks something like:
  #
  # function domdistiller() {
  #   ...
  # }
  # domdistiller();<useful code here>
  # <more useful code>
  # <last useful code>;if (domdistiller) domdistiller.onScriptLoad(gwtOnLoad);
  #
  # And so we extract the useful parts and append the correct gwtOnLoad call.
  marker = 'domdistiller();'
  for i, l in enumerate(lines):
    if l.startswith(marker):
      return '\n'.join(
        [l[len(marker):]] +
        lines[i + 1:-1] +
        [lines[-1].replace(
          'if (domdistiller) domdistiller.onScriptLoad(gwtOnLoad);',
          'gwtOnLoad(undefined, \'domdistiller\', \'\', 0);')
        ])
  raise Exception('Failed to find marker line')

def main(argv):
  parser = optparse.OptionParser()
  parser.add_option('-i', '--infile')
  parser.add_option('-o', '--outfile')
  options, _ = parser.parse_args(argv)

  if options.infile:
    infile = open(options.infile, 'r')
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

  return 0


if __name__ == '__main__':
  sys.exit(main(sys.argv))
