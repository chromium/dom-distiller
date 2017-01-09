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
import shutil
import sys

def ExtractJavascript(content, module):
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
  marker = module + '();'
  for i, l in enumerate(lines):
    if l.startswith(marker):
      return '\n'.join(
        [l[len(marker):]] +
        lines[i + 1:-1] +
        [lines[-1].replace(
          'if ({0}) {0}.onScriptLoad(gwtOnLoad);'.format(module),
          'gwtOnLoad(undefined, "{0}", "", 0);'.format(module))
        ])
  raise Exception('Failed to find marker line')

def main(argv):
  parser = optparse.OptionParser()
  parser.add_option('-i', '--infile')
  parser.add_option('-o', '--outfile')
  parser.add_option('--module', help='Name of generated javascript module.')
  parser.add_option('--auto', action='store_true',
      help='Calculate input/output paths based on module name.')
  parser.add_option('--sourcemaps', action='store_true',
      help='Also copy sourcemaps.')
  options, _ = parser.parse_args(argv)

  inpath = options.infile
  outpath = options.outfile
  if options.auto:
    inpath = 'war/{0}/{0}.nocache.js'.format(options.module)
    outpath = 'out/{0}.js'.format(options.module)

  if inpath:
    infile = open(inpath, 'r')
  else:
    print 'Reading input from stdin'
    infile = sys.stdin

  if outpath:
    outfile = open(outpath, 'w')
  else:
    outfile = sys.stdout

  compiledJs = infile.read()
  # The compiled js expects to be running in its own iframe. This won't be the
  # case for the standalone js.
  compiledJs = compiledJs.replace('var $wnd = parent', 'var $wnd = window')
  outfile.write(ExtractJavascript(compiledJs, options.module))
  if options.sourcemaps:
    sourcemap = 'debug/{0}/src/{0}.sourcemap'.format(options.module)
    outfile.write('\n')
    outfile.write('//@ sourceMappingURL=%s' % sourcemap)

    insourcemap = glob.glob(
        'war/WEB-INF/deploy/%s/symbolMaps/*sourceMap*' % options.module)[0]
    outsourcemap = 'out/%s' % sourcemap
    shutil.copy(insourcemap, outsourcemap)

  return 0


if __name__ == '__main__':
  sys.exit(main(sys.argv))
