#!/usr/bin/env python
# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import json
import os
import shutil
import sys
import time
import urllib
import random
from lockfile import FileLock

repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '../..'))

try:
  from selenium import webdriver
except:
  print 'ERROR:'
  print 'Couldn\'t import webdriver. Please run `sudo %s/install-build-deps.sh`.' % repo_root
  sys.exit(1)

def addBuildtoolsToPath():
  envPath = os.environ['PATH']
  buildtoolsPath = repo_root + '/buildtools'
  if not buildtoolsPath in envPath:
    os.environ['PATH'] = buildtoolsPath + ':' + envPath

def getDistillerUrl(u):
  params = { 'url': u}
  return "chrome-distiller://blah/?" + urllib.urlencode(params)

def newDriver():
  chromeOptions = webdriver.ChromeOptions()
  chromeOptions.binary_location = "/usr/bin/google-chrome-unstable";
  chromeOptions.add_argument('--enable-dom-distiller')
  chromeOptions.add_argument('--save-page-as-mhtml')
  driver = webdriver.Chrome(chrome_options=chromeOptions)
  driver.set_page_load_timeout(60)
  driver.set_script_timeout(60)
  print "created a new chrome driver"
  return driver

def writeAggregated(outdir, ext, out, in_marshal=False):
  prevfiles = [os.path.join(outdir, f) for f in os.listdir(outdir)]
  prevfiles = [f for f in prevfiles if os.path.isfile(f) and os.path.splitext(f)[1] == '.' + ext]
  output = []
  print 'reading %s files' % (ext)
  for f in prevfiles:
    with open(f) as infofile:
      info = json.load(infofile)
      output.append(info)
  print 'done reading %s files' % (ext)

  output = sorted(output, key=lambda k: k['index'])
  print 'writing %s files' % (ext)
  with open('%s/%s' % (outdir, out), 'w') as outf:
    if in_marshal:
      import marshal
      marshal.dump(output, outf)
    else:
      json.dump(output, outf, indent=2)
  print 'done writing %s files' % (ext)

def writeIndex(outdir):
  writeAggregated(outdir, "info", "index")

def writeFeature(outdir):
  writeAggregated(outdir, "feature", "feature", in_marshal=True)

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--out', required=True)
  parser.add_argument('urls', nargs='*')
  parser.add_argument('--force', action='store_true')
  parser.add_argument('--urls-file')
  parser.add_argument('--resume', action='store_true')
  parser.add_argument('--write-index', action='store_true')
  parser.add_argument('--save-mhtml', action='store_true')
  options = parser.parse_args(argv)

  outdir = options.out
  if not options.resume:
    if os.path.exists(outdir):
      if not options.force:
        print outdir + ' exists'
        return 1
      shutil.rmtree(outdir, ignore_errors=True)
    os.makedirs(outdir)
  else:
    if not os.path.exists(outdir):
      print outdir + ' doesn\'t exist'
      return 1

  addBuildtoolsToPath()

  if options.urls:
    files = options.urls
  elif options.urls_file:
    with open(options.urls_file) as u:
      files = u.read().splitlines()
  else:
    print 'oh no'
    return 1

  if options.write_index:
    writeIndex(outdir)
    writeFeature(outdir)
    print 'index is written'
    return 0

  driver = newDriver()

  feature_extractor = open('extract_features.js').read()

  try:
    jobs = list(enumerate(files))
    random.shuffle(jobs)
    for i, f in jobs:
      prefix = '%s/%d' % (outdir, i)
      info = '%s.info' % prefix

      if os.path.exists(info):
        print "skip %d" % (i)
        continue;

      with FileLock('%s.lock' % (prefix)):
        if os.path.exists(info):
          print "SKIP %d" % (i)
          continue;
        try:
          ss = '%s.png' % prefix
          dss = '%s-distilled.png' % prefix
          fea = '%s.feature' % prefix

          driver.set_window_size(1280, 5000)
          driver.get(f)
          time.sleep(3) # wait for some async scripts
          driver.save_screenshot(ss)
          print "saved %s" % ss

          features = driver.execute_script(feature_extractor)
          data = {
            'index': i,
            'url': f,
            'features': features
          }
          with open(fea, 'w') as outf:
            json.dump(data, outf, indent=2)
          print "saved %s" % fea

          if options.save_mhtml:
            mhtml = '%s.mhtml' % prefix
            cmd = (
              'xdotool key --clearmodifiers "ctrl+s" && ' +
              'sleep 1 && ' +
              'xdotool key --delay 20 --clearmodifier "Alt+n" && ' +
              'xdotool key --delay 20 --clearmodifiers "ctrl+a" "BackSpace" && ' +
              'xdotool type --delay 10 --clearmodifiers "%s" && ' +
              'xdotool key --delay 20 --clearmodifiers Return'
              ) % (os.getcwd() + '/' + mhtml)
            os.system(cmd)
            time.sleep(3) # wait for file saving
            if not os.path.exists(mhtml):
              # If the file is not saved, the focus point might be lost.
              # Restart the whole xvfb environment to be safe.
              print "[ERROR] Snapshot of [%d] %s (%s) is missing." % (i, f, mhtml)
              break

          driver.set_window_size(640, 5000)
          driver.get(getDistillerUrl(f))
          time.sleep(20) # wait for multi-page, etc
          driver.save_screenshot(dss)
          print "saved %s" % dss

          data = {
            'index': i,
            'url': f,
            'screenshot': ss,
            'distilled': dss,
          }
          with open(info, 'w') as info:
            json.dump(data, info)

        except Exception as e:
          print e
          print "Index=%d URL=%s" % (i, f)
          driver.quit()
          driver = newDriver()
          pass

  finally:
    driver.quit()

  return 0

if __name__ == '__main__':
  sys.exit(main(sys.argv[1:]))

