#!/bin/bash
# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

# Installs required build dependencies (to buildtools/ and the local system).

(
  set -e
  if [ "$(id -u)" != "0" ]; then
    echo "Please run this as root."
    exit 1
  fi

  apt-get install \
    ant \
    openjdk-7-jdk \
    protobuf-compiler \
    python \
    python-setuptools \
    python-protobuf \
    unzip \
    wget \
    xvfb

  user=$SUDO_USER
  bit=$(getconf LONG_BIT)
  domdistiller=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
  pkg=selenium-2.44.0
  tar=${pkg}.tar.gz
  zip=chromedriver_linux${bit}.zip
  tmp=/tmp/domdistiller-$$
  tools=$domdistiller/buildtools

  set -e
  mkdir $tmp
  cd $tmp

  wget https://chromedriver.storage.googleapis.com/2.12/$zip
  chmod a+r $zip
  sudo -u $user mkdir -p $tools
  sudo -u $user unzip -o -d $tools $zip
  chmod u+x $tools/chromedriver

  wget https://pypi.python.org/packages/source/s/selenium/$tar
  tar -xf $tar
  cd $pkg

  python setup.py install

  rm -rf $tmp
)
