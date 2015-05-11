# DOM Distiller

DOM Distiller aims to provide a better reading experience by distilling the
content of the page. This distilled content can then be used in a variety of
ways.

The current efforts that will be powered by DOM Distiller:
- Reader mode: a mobile-friendly viewing mode for Chrome mobile

## How to use Reader mode on mobile Chrome

- Open Chrome on your mobile device
- Navigate to [chrome://flags](chrome://flags) and search for "Reader mode"
  (Menu -> Find in page -> Enable Reader Mode Toolbar Icon), or directly go to
  [chrome://flags#enable-reader-mode-toolbar-icon](chrome://flags#enable-reader-mode-toolbar-icon)
- Click Enable to turn on Reader mode
- Click "Relaunch Now" at the bottom of the page
- Next time you're trying to read a page, tap on the "Reader mode" icon in
  the toolbar to try it out!

# Continuous integration

- [![Build Status](https://drone.io/github.com/chromium/dom-distiller/status.png)](https://drone.io/github.com/chromium/dom-distiller/latest)
- [drone.io waterfall](https://drone.io/github.com/chromium/dom-distiller)

# Get the code

In a folder where you want the code (outside of the chromium checkout):

```bash
git clone https://github.com/chromium/dom-distiller.git
```
A `dom-distiller` folder will be created in the folder you run that command.

# Environment setup

Before you build for the first time, you need to install the build dependencies.

For all platforms, it is require to download and install
[Google Chrome browser](https://www.google.com/chrome/browser/desktop/).

ChromeDriver requires Google Chrome to be installed at a specific location
(the default location for the platform). See
[ChromeDriver documentation](https://code.google.com/p/selenium/wiki/ChromeDriver)
for details.

## Developing on Linux

Install the dependencies by entering the `dom-distiller` folder and running:
```bash
sudo ./install-build-deps.sh
```

## Developing on Mac OS X

- Install JDK 7 using either your organizations software management tool,
or download it from
[Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
- Install [Homebrew](http://brew.sh/).
- Install `ant` and `python` using Homebrew:
```bash
brew install ant python
```
- Since both the protocol buffer compiler and Python bindings are needed,
install the `protobuf` package with the `--with-python` command line parameter:
```bash
brew install protobuf --with-python
```
- Create a folder named `buildtools` inside your DOM Distiller checkout
- Download ChromeDriver (chromedriver_mac32.zip) from the
[Download page](https://sites.google.com/a/chromium.org/chromedriver/downloads)
- Unzip the `chromedriver_mac32.zip` and ensure the binary ends up in your
`buildtools` folder.
- Install the PyPI package management tool `pip` by running:
```
sudo easy_install pip
```
- Install `selenium` using `pip`:
```
pip install --user selenium
```

For the rest of this guide, there are sometimes references to a tool called
`xvfb` and specifically when running shell commands using `xvfb-run`. When you
develop using a Mac OS X, you can remove that part of the command. For example
`xvfb-run echo` would just become `echo`.

## Tools for contributing

The DOM Distiller project uses the Chromium tools for collaboration. For code
reviews, the
[Chromium Rietveld code review tool](https://codereview.chromium.org/) is used
and the set of tools found in `depot_tools` is also required.

To get `depot_tools`, follow the guide at
[Chrome infrastructure documentation for depot_tools](http://commondatastorage.googleapis.com/chrome-infra-docs/flat/depot_tools/docs/html/depot_tools_tutorial.html#_setting_up).

The TL;DR of that is to run this from a folder where you install developer
tools, for example in your `$HOME` folder:
```bash
git clone https://chromium.googlesource.com/chromium/tools/depot_tools
export PATH="/path/to/depot_tools:$PATH"
```

You must also setup your local checkout needs to point to the Chromium Rietveld
server. This is a one-time setup for your checkout, so from your `dom-distiller`
checkout folder, run:
```bash
git cl config
```
- `Rietveld server`: `https://codereview.chromium.org`
- You can leave the rest of the fields blank.

# Building

## Using ant

`ant` is the tool we use to build, and the available targets can be listed using
`ant -p`, but the typical targets you might use when you work on this project
is:

- `ant test` Runs all tests.
- `ant test -Dtest.filter=$FILTER_PATTERN` where `$FILTER_PATTERN` is a [gtest\_filter
pattern]
(https://code.google.com/p/googletest/wiki/AdvancedGuide#Running_a_Subset_of_the_Tests).
For example `*.FilterTest.*:*Foo*-*Bar*` would run all tests containing `.FilterTest.`
and `Foo`, but not those with `Bar`.
- `ant gwtc` compiles .class+.java files to JavaScript. Standalone JavaScript
  is available at `war/domdistiller/domdistiller.nocache.js`.
- `ant gwtc.jstests` creates a standalone JavaScript for the tests.
- `ant package` Copies the main build artifacts into the `out/package` folder,
typically the extracted JS and protocol buffer files.

# Contributing

You can use regular `git` command when developing in this project and use
`git cl` for collaboration.

## Uploading a CL for review

On your branch, run: `git cl upload`. The first time you do this, you will have
to provide a username and password.
- For username, use your @chromium.org. account.
- For password, get it from
[GoogleCode.com settings page](https://code.google.com/hosting/settings) when
logged into your @chromium.org account, and add the full
`machine code.google.com login` line to your `~/.netrc` file.

## Committing a CL

- Change upstream to remote master, push cl, then revert upstream to local:
```bash
git branch -u origin/master
git cl land
git branch -u master
```
- For username, use your GitHub account name (the username, not the full
  e-mail).
- For password, use your GitHub password.
  - If you have two-factor authentication enabled, create a personal access
  token at your
  [application settings page](https://github.com/settings/applications) and use
  that as your password.

## Code formatting

Before uploading a CL it is recommended to run `git cl format`. However, this
requires adding symbolic links to your chromium checkout.

Inside the `buildtools` folder of your checkout, add the following symbolic
links:
- `clang_format`	&rarr; `/path/to/chromium/src/buildtools/clang_format/`
- `linux64` ->	&rarr; `/path/to/chromium/src/buildtools/linux64/` (only for Linux 64-bit platform)
- `mac` &rarr; `/path/to/chromium/mac/buildtools/linux64/` (only for Mac platform)

Doing this enables you to run the command `git cl format` to fix the formatting
of your code.

# Run in Chrome for desktop

In this section, the following shell variables and are assumed correctly set:
```bash
export CHROME_SRC=/path/to/chromium/src
export DOM_DISTILLER_DIR=/path/to/dom-distiller
```

- Pull generated package (from ant package) into Chrome. You can use this handy
bash-function to help with that:
```bash
roll-distiller () {
  (
    (cd $DOM_DISTILLER_DIR && ant package) && \
    rm -rf $CHROME_SRC/third_party/dom_distiller_js/dist/* && \
    cp -rf $DOM_DISTILLER_DIR/out/package/* $CHROME_SRC/third_party/dom_distiller_js/dist/ && \
    touch $CHROME_SRC/components/resources/dom_distiller_resources.grdp
  )
}
```
- From `$CHROME_SRC` run GYP to setup ninja build files using
```bash
build/gyp_chromium
```

## Running the Chrome browser with distiller support

- For running Chrome, you need to build the `chrome` target:
```bash
ninja -C out/Debug chrome
```
- Run chrome with DOM Distiller enabled:
```bash
out/Debug/chrome --enable-dom-distiller
```
- This adds a menu item `Distill page` that you can use to distill web pages.
- You can also go to `chrome://dom-distiller` to access the debug page.
- To have a unique user profile every time you run Chrome, you can also add
`--user-data-dir=/tmp/$(mktemp -d)` as a command line parameter.
On Mac OS X, you can instead write
`--user-data-dir=$(mktemp -d 2>/dev/null || mktemp -d -t 'chromeprofile')`.

## Running the automated tests in Chromium

- For running the tests, you need to build the `components_browsertests` target:
```bash
ninja -C out/Debug components_browsertests
```
- Run the `components_browsertests` binary to execute the tests. You can prefix
the command with `xvfb-run` to avoid pop-up windows:
```bash
xvfb-run out/Debug/components_browsertests
```

- To only run tests related to DOM Distiller, run:
```bash
xvfb-run out/Debug/components_browsertests --gtest_filter=\*Distiller\*
```

- For running tests as isolates, you need to build `components_browsertests_run`
and execute them using the swarming tool:
```bash
ninja -C out/Debug components_browsertests_run
python tools/swarming_client/isolate.py run -s out/Debug/components_browsertests.isolated
```

## Running the content extractor

To extract the content from a web page directly, you can run:
```bash
xvfb-run out/Debug/components_browsertests \
  --gtest_filter='*MANUAL_ExtractUrl' \
  --run-manual \
  --test-tiny-timeout=600000 \
  --output-file=./extract.out \
  --url=http://www.example.com \
  > ./extract.log 2>&1
```
`extract.out` has the extracted HTML, `extract.log` has the console logging.

If you need more logging, you can add the following arguments to the command:
- Chrome browser: `--vmodule=*distiller*=2`
- Content extractor: `--debug-level=2`

If this is something you often do, you can put the following function in a bash
file you include (for example `~/.bashrc`) and use it for iterative development:

```bash
distill() {
  (
    roll-distiller && \
    ninja -C out/Debug components_browsertests &&
    xvfb-run out/Debug/components_browsertests \
      --gtest_filter='*MANUAL_ExtractUrl' \
      --run-manual \
      --test-tiny-timeout=600000 \
      --output-file=./extract.out \
      --url=$1 \
      > ./extract.log 2>&1
  )
}
```

Usage when running from `$CHROME_SRC`:

```bash
distill http://example.com/article.html
```

# Debug Code

## Interactive debugging

You can use the Chrome Developer Tools to debug DOM Distiller:
- Update the test JavaScript by running `ant gwtc.jstests` or `ant test`.
- Open `war/test.html` in Chrome desktop
- Open the `Console` panel in Developer Tools (**Ctrl-Shift-J**).
On Mac OS X you can use **&#x2325;-&#x2318;-I** (uppercase `I`) as the shortcut.
- Run all tests by calling:
```javascript
org.chromium.distiller.JsTestEntry.run()
```
- To run only a subset of tests, you can use a regular expression that matches a
single test or multiple tests:
```javascript
org.chromium.distiller.JsTestEntry.runWithFilter('MyTestClass.testSomething')
```

The `Sources` panel contains both the extracted JavaScript and all the Java
source files as long as you haven't disabled JavaScript source maps in
Developer Tools. You can set breakpoints in the Java source files and then
inspect all kinds of different interesting things when that breakpoint is hit.

When a test fails, you will see several stack traces. One of these contains
clickable links to the corresponding Java source files for the stack frames.

## Developer extension

After running `ant package`, the `out/extension` folder contains an unpacked
Chrome extension. This can be added to Chrome and used for development.
- Go to `chrome://extensions`
- Enable developer mode
- Select to load an unpacked extension and point to the `out/extension` folder.

### Features

The extension currently supports profiling the extraction code.

It also adds a panel to the Developer Tools which you can use to trigger
extraction on the inspected page. This can be used to trigger and profile
extraction on a mobile device which you are currently inspecting using
`chrome://inspect`.

## Logging

To add logging, you can use the LogUtil. You can use the Java function
`LogUtil.logToConsole()`. Destination of logs:
- `ant test`: Terminal
- Chrome browser: the Chrome log file, as set by shell variable
`$CHROME_LOG_FILE`. A release mode build of Chrome will log all JavaScript
`INFO` there if you start Chrome with `--enable-logging`. You can add
`--enable-logging=stderr` to have the log go to stderr instead of a file.
- Content extractor: See
[documentation about `extract.log` above](#running-the-content-extractor).

For an example, see
`$DOM_DISTILLER_DIR/java/org/chromium/distiller/PagingLinksFinder.java`.

Use `ant package '-Dgwt.custom.args=-style PRETTY'` for easier JavaScript
debugging.

# Mobile distillation from desktop

1. In the tab with the interesting URL, bring up the Developer Tools emulation
panel (the mobile device icon).
2. Select the desired `Device` and reload the page. Verify that you get what you
expect. For example a Nexus 4 might get a mobile site, whereas Nexus 7 might get
the desktop site.
3. The User-Agent can be copied directly out from the `UA` field. This field
does not even require reload after changing device, but it is good practice to
verify that you get what you expect. Copy this to the clipboard.
4. (Re)start chrome with `--user-agent="$USER_AGENT_FROM_CLIPBOARD"`. Remember
to also add `--enable-dom-distiller`.
5. Distill the same URL in viewer by either using the menu `Distill page` or by
going to `chrome://dom-distiller` and using the input field there.
6. Have fun scrutinizing the Chrome log file.

If you want you can copy some of these User-Agent aliases into normal bash
aliases for easy access later. For example, Nexus 4 would be:
```bash
--user-agent="Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"
```

Steps 1-3 in the guide above can typically be done in a stable version of
Chrome, whereas the rest of the steps is typically done in your own build of
Chrome (hence the "(Re)" in step 4). Besides speed, this also facilitates
side-by-side comparison.
