#!/usr/bin/env python
# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Runs DomDistillers jstests.

This uses ChromeDriver (https://sites.google.com/a/chromium.org/chromedriver/) to run the jstests.
This requires that the ChromeDriver executable is on the PATH and that Selenium WebDriver is
installed.

In addition, ChromeDriver assumes that Chrome is available at /usr/bin/google-chrome.
"""

import argparse
import os
import sys
import time
import urllib

try:
  from selenium import webdriver
except:
  print 'ERROR:'
  print 'Couldn\'t import webdriver. Please run `sudo ./install-build-deps.sh`.'
  sys.exit(1)

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--filter', help='See gtest_filter syntax.')
  parser.add_argument('--repeat', type=int, default=1, help='Number of times to repeat the tests.')
  parser.add_argument('--debug_level', help='Verbosity level of debug messages.')
  parser.add_argument('--no_console_log',
      action='store_true', help='Disable the console log output.')
  parser.add_argument('--shuffle', help='Run test cases in random order.')
  options = parser.parse_args(argv)

  params = {}
  if options.filter:
    params['filter'] = options.filter

  if options.debug_level:
    params['debug_level'] = int(options.debug_level)

  if options.no_console_log:
    params['console_log'] = '0'

  if options.shuffle:
    params['shuffle'] = options.shuffle

  test_runner = "return org.chromium.distiller.JsTestEntry.run()";
  test_html = os.path.abspath(os.path.join(os.path.dirname(__file__), "war", "test.html"))
  test_html += "?" + urllib.urlencode(params)

  driver = webdriver.Chrome()
  driver.get("file://" + test_html)
  for i in range(options.repeat):
    start = time.time()
    result = driver.execute_script(test_runner)

    end = time.time()
    if not result['success'] or options.repeat == i+1:
      print result['log'].encode('utf-8')
    print 'Tests run: %d, Failures: %d, Skipped: %d, Time elapsed: %0.3f sec' % (result['numTests'],
        result['failed'], result['skipped'], end - start)
    if not result['success']:
      driver.quit()
      if options.repeat > 1:
        print 'Failed at run #%d/%d' % (i+1, options.repeat)
      return 1
  driver.quit()
  if options.repeat > 1:
    print 'Passed %d runs' % (options.repeat)
  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))

