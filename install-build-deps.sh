#!/bin/bash
# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

# Installs required build dependencies (to buildtools/ and the local system).

# If Chrome is older than CHROME_MIN_VERSION, force update.
[ -z "$CHROME_MIN_VERSION" ] && CHROME_MIN_VERSION=49

(
  set -e
  if [ "$(id -u)" != "0" ]; then
    echo "Please run this as root."
    exit 1
  fi

  apt-get update
  apt-get install -y \
    ant \
    openjdk-8-jdk \
    protobuf-compiler \
    python \
    python-setuptools \
    python-protobuf \
    unzip \
    wget \
    xdotool \
    xvfb

  # Specify JDK version in case there are other versions installed.
  update-alternatives --set java $(sudo update-alternatives --list java | grep java-8)

  if ! command -v google-chrome >/dev/null 2>&1; then
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list
    apt-get update
    apt-get install google-chrome-stable
  fi

  # Update chrome if it is too old, and keep the default channel.
  CHROME_VERSION=$(google-chrome --version | tr " " "\n" | awk '/[0-9.]/{print ($1+0)}')
  if (( $CHROME_VERSION < $CHROME_MIN_VERSION )); then
    echo "Current Chrome version is $CHROME_VERSION. Updating to latest."
    case "$(google-chrome --version)" in
      *dev*)
        apt-get install google-chrome-unstable
        ;;
      *beta*)
        apt-get install google-chrome-beta
        ;;
      *)
        apt-get install google-chrome-stable
        ;;
    esac
  fi

  user=$SUDO_USER
  bit=$(getconf LONG_BIT)
  domdistiller=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
  pkg=selenium-3.141.0
  tar=${pkg}.tar.gz
  zip=chromedriver_linux${bit}.zip
  tmp=/tmp/domdistiller-$$
  tools=$domdistiller/buildtools

  set -e
  mkdir $tmp
  cd $tmp

  # The version scheme changed after 2.46. See http://chromedriver.chromium.org/downloads/version-selection
  CHROME_VERSION=$(google-chrome --version | tr " " "\n" | awk '/[0-9.]/{print ($1+0)}')
  VERSION=$(wget -q --output-document - https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION} | cat)
  [ -z "${VERSION}" ] && VERSION=2.24
  wget https://chromedriver.storage.googleapis.com/${VERSION}/$zip
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
