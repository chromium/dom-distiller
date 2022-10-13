#!/usr/bin/env python
# Copyright 2016 The Chromium Authors
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

from calculate_derived_features import CalcDerivedFeatures

repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '../..'))

try:
  from selenium import webdriver
  from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
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

def newDriver(mobile=False):
  chromeOptions = webdriver.ChromeOptions()
  # If you want to use a different version of chrome, specify the full path here.
  #chromeOptions.binary_location = "/usr/bin/google-chrome-unstable";
  # For native distillability features.
  chromeOptions.add_argument('--enable-distillability-service')
  chromeOptions.add_argument('--enable-dom-distiller')
  chromeOptions.add_argument('--save-page-as-mhtml')
  chromeOptions.add_argument('--reader-mode-heuristics=adaboost')
  chromeOptions.add_argument('--distillability-dev')
  if mobile:
    mobile_emulation = { "deviceName": "Google Nexus 5" }
    chromeOptions.add_experimental_option("mobileEmulation", mobile_emulation)

  d = DesiredCapabilities.CHROME
  # This is to enable accessing devtools console log from here, for nativeFeatures().
  d['goog:loggingPrefs'] = {'browser': 'ALL'}
  driver = webdriver.Chrome(chrome_options=chromeOptions, desired_capabilities=d)
  driver.set_page_load_timeout(60)
  driver.set_script_timeout(60)
  print "created a new chrome driver"
  return driver

def nativeFeatures(logs):
  return _parseNative(logs, 'distillability_features = ')

def nativeClassification(logs):
  return _parseNative(logs, 'adaboost_classification = ')

def _parseNative(logs, needle):
  """Parse console logs from Chrome and get decoded JSON.

  Args:
    logs: Chrome log object
    needle (str): the string leading the actual JSON.

  Example:
    >>> _parseNative([{'message':'a=b'},{'message':'ac={"a":[1,2]}'}],'c=')
    {u'a': [1, 2]}
  """
  ret = None
  for log in logs:
    message = log['message']
    loc = message.find(needle)
    if loc >= 0:
      ret = json.loads(message[loc+len(needle):])
  return ret

def saveFeatures(driver, feature_extractor, data, url_override, filename):
  data = dict.copy(data)
  features = driver.execute_script(feature_extractor)
  if url_override:
    features['url'] = url_override
  data['features'] = features

  logs = driver.get_log('browser')
  native = nativeClassification(logs)
  if native:
    native['features'] = nativeFeatures(logs)
    data['native'] = native

  with open(filename, 'w') as outf:
    json.dump(data, outf, indent=2, sort_keys=True)
  print "saved %s" % filename

  derived = dict.copy(data)
  derived['features'] = CalcDerivedFeatures(data['index'], features)

  derived_name = filename + '-derived'
  with open(derived_name, 'w') as outf:
    json.dump(derived, outf, indent=2, sort_keys=True)
  print "saved %s" % derived_name
  return data, derived

def saveInfoFile(data, ss, dss, filename):
  data = dict.copy(data)
  data['screenshot'] = ss
  data['distilled'] = dss
  with open(filename, 'w') as info:
    json.dump(data, info)

def saveMHTML(filename):
  """Save current page as an MHTML file

  This is done by issuing xdotool commands.
  Dependencies:
  - Command line argument "--save-page-as-mhtml" to Chrome.
  - xdotool
  """

  cmd = (
    'xdotool key --clearmodifiers "ctrl+s" && ' +
    'sleep 1 && ' +
    'xdotool key --delay 20 --clearmodifier "Alt+n" && ' +
    'xdotool key --delay 20 --clearmodifiers "ctrl+a" "BackSpace" && ' +
    'xdotool type --delay 10 --clearmodifiers "%s" && ' +
    'xdotool key --delay 20 --clearmodifiers Return'
    ) % (os.path.abspath(filename))
  os.system(cmd)
  time.sleep(3) # wait for file saving
  if not os.path.exists(filename):
    return False
  print "saved %s" % filename
  return True

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
  for n in ["feature-derived", "dfeature-derived", "mfeature-derived", "mdfeature-derived"]:
    writeAggregated(outdir, n, n)
  # Use the following when needing aggregated raw features:
  #writeAggregated(outdir, "feature", "feature", in_marshal=True)
  #writeAggregated(outdir, "dfeature", "dfeature", in_marshal=True)
  #writeAggregated(outdir, "mfeature", "mfeature", in_marshal=True)
  #writeAggregated(outdir, "mdfeature", "mdfeature", in_marshal=True)

def shouldProcess(load_mhtml, no_distill, prefix):
  info = prefix + '.info'
  mhtml = prefix + '.mhtml'
  mfeature = prefix + '.mfeature'
  mdfeature = prefix + '.mdfeature'
  if not load_mhtml:
    return not os.path.exists(info)
  else:
    if no_distill:
      return os.path.exists(mhtml) and not os.path.exists(mfeature)
    else:
      return os.path.exists(mhtml) and not os.path.exists(mdfeature)

def main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument('--out', required=True)
  parser.add_argument('urls', nargs='*')
  parser.add_argument('--force', action='store_true')
  parser.add_argument('--urls-file')
  parser.add_argument('--emulate-mobile', action='store_true')
  parser.add_argument('--resume', action='store_true')
  parser.add_argument('--write-index', action='store_true')
  parser.add_argument('--save-mhtml', action='store_true')
  parser.add_argument('--load-mhtml', action='store_true')
  parser.add_argument('--skip-distillation', action='store_true')
  parser.add_argument('--desktop-distillable-only', action='store_true')
  options = parser.parse_args(argv)

  if options.load_mhtml:
    if options.save_mhtml:
      print '--load-mhtml is not compatible with --save-mhtml'
      return 1
    if options.resume:
      print '--load-mhtml is not compatible with --resume'
      return 1

  outdir = options.out
  if not options.resume and not options.load_mhtml and not options.write_index:
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

  driver = newDriver(options.emulate_mobile)

  feature_extractor = open('extract_features.js').read()

  try:
    jobs = list(enumerate(files))
    random.shuffle(jobs)
    for i, f in jobs:
      prefix = '%s/%d' % (outdir, i)
      info = '%s.info' % prefix
      basedata = {'index': i, 'url': f}

      if not shouldProcess(options.load_mhtml, options.skip_distillation, prefix):
        print "skip %d" % (i)
        continue;

      with FileLock('%s.lock' % (prefix)):
        if not shouldProcess(options.load_mhtml, options.skip_distillation, prefix):
          print "skip %d" % (i)
          continue;
        try:
          ss = '%s.png' % prefix
          dss = '%s-distilled.png' % prefix
          fea = '%s.feature' % prefix
          dfea = '%s.dfeature' % prefix
          mhtml = '%s.mhtml' % prefix
          mhtml_url = 'file://%s' % os.path.abspath(mhtml)

          if options.emulate_mobile:
            driver.set_window_size(400, 800)
          else:
            driver.set_window_size(1280, 5000)
          if options.load_mhtml:
            if not os.path.exists(mhtml):
              print "SKIP %d, no mhtml" % (i)
              continue
            driver.get(mhtml_url)
            time.sleep(1) # wait a bit for things to stablize
          else:
            driver.get(f)
            time.sleep(3) # wait for some async scripts
            driver.save_screenshot(ss)
            print "saved %s" % ss

          url_override = None
          if options.load_mhtml:
            with open(fea) as infile:
              # otherwise it would be file:// of mhtml
              url_override = json.load(infile)['features']['url']
            fea = '%s.mfeature' % prefix
          _, derived = saveFeatures(driver, feature_extractor, basedata, url_override, fea)

          if options.desktop_distillable_only:
            if derived['native']['features']['isMobileFriendly'] or not derived['native']['distillable']:
              os.system('rm %s.feature %s.png' % (prefix, prefix))
              saveInfoFile(basedata, ss, dss, info)
              continue

          if options.save_mhtml:
            if not saveMHTML(mhtml):
              # If the file is not saved, the focus point might be lost.
              # Restart the whole xvfb environment to be safe.
              print "[ERROR] Snapshot of [%d] %s (%s) is missing." % (i, f, mhtml)
              break

          if options.skip_distillation:
            continue

          if options.emulate_mobile:
            driver.set_window_size(400, 800)
          else:
            driver.set_window_size(640, 5000)

          if options.load_mhtml:
            driver.get(getDistillerUrl(mhtml_url))
            time.sleep(10)
            dss = '%s-mdistilled.png' % prefix
            driver.save_screenshot(dss)
            print "saved %s" % dss
            dfea = '%s.mdfeature' % prefix
            saveFeatures(driver, feature_extractor, basedata, None, dfea)
            continue

          driver.get(getDistillerUrl(f))
          for i in range(3):
            time.sleep(20) # wait for multi-page, etc
            driver.save_screenshot(dss)
            print "saved %s" % dss
            feature, _ = saveFeatures(driver, feature_extractor, basedata, None, dfea)
            if feature['features']['innerText'] != "":
              break

          saveInfoFile(basedata, ss, dss, info)

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

