# DOM Distiller

[DOM Distiller](https://chromium.googlesource.com/chromium/dom-distiller)
provides a better reading experience for articles and article-like web pages by
extracting the core text and stripping non-essential from the page.

Projects and features powered by DOM Distiller:

-   Reader Mode, a distraction-free viewing mode for Chrome on Android and
    desktop
-   Reading list on Chrome iOS

DOM Distiller is loosely based on
["Boilerpipe" by Christian Kohlschütter, Peter Fankhauser and Wolfgang Nejdl](http://www.l3s.de/~kohlschuetter/boilerplate/).

## Report a bug

Bugs and feature requests are tracked in Chromium's issue tracker,
[crbug](http://crbug.com). DOM Distiller bugs are filed under
[`component:UI>Browser>ReaderMode`](https://bugs.chromium.org/p/chromium/issues/list?q=component%3AUI%3EBrowser%3EReaderMode).

Examples of bugs that should be reported:

-   Crashes, error pages, and other similar technical issues.
-   Poor extraction quality, e.g. non-essential images or missing text.
-   Reader Mode being offered on pages where it should not be (e.g. login
    pages), or not being offered where it should be (e.g. news articles).

## How to use Chrome's Reader Mode

### Android

Reader Mode has launched on Android and should be available on any up-to-date
version of Chrome. Simply visit a non-mobile-friendly article and tap on the
"Show simplified view" infobar when it appears at the bottom of the screen. You
may need to first enable the feature via accessibility settings.

### Desktop

Reader Mode for Chrome on desktop is still in development. As of M80, an
experimental preview of the feature can be activated by following these steps:

1.  Open Chrome on your desktop computer.
1.  Navigate to [chrome://flags](chrome://flags) and search for
    "enable-reader-mode" with the in-page search box. Alternatively, you may go
    directly to the setting by visiting
    [chrome://flags#enable-reader-mode](chrome://flags#enable-reader-mode).
1.  Click the dropdown box and select "Enabled".
1.  Click "Relaunch Now" at the bottom of the page when prompted.
1.  Visit an article or article-like page, and a Reader Mode icon should appear
    in the omnibox. Click the icon to enter Reader Mode.

## Continuous integration

![Build Status](https://github.com/chromium/dom-distiller/actions/workflows/ant.yml/badge.svg)

[CI waterfall](https://github.com/chromium/dom-distiller/actions)

## Environment setup

You must install the build dependencies before building for the first time. The
following are required on all platforms:

-   Download and install
    [Google Chrome](https://www.google.com/chrome/browser/desktop/).
    -   ChromeDriver requires Google Chrome to be installed at the default
        location for the platform. See the
        [ChromeDriver documentation](https://code.google.com/p/selenium/wiki/ChromeDriver)
        for details.
-   Install the git hooks:

    ```bash
    ./create-hook-symlinks
    ```

### Get the code

1.  Change to the directory where you want the code.

    -   Do not put it inside your main Chromium checkout, i.e. chromium/src.

2.  Clone this git repo:

    ```bash
    git clone https://chromium.googlesource.com/chromium/dom-distiller
    ```

The code will be located inside the newly created `dom-distiller` folder.

### Developing on Ubuntu/Debian

Install the dependencies by entering the `dom-distiller` folder and running:

```bash
sudo ./install-build-deps.sh
```

### Developing on Mac OS X

1.  Install JDK 7 with your organization's software management tool, or download
    it from
    [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
1.  Install [Homebrew](http://brew.sh/).
1.  Install `ant` and `python` using Homebrew:

    ```bash
    brew install ant python
    ```

1.  Install the protocol buffer compiler with Python bindings:

    ```bash
    brew install protobuf --with-python
    ```

1.  Create a folder named `buildtools` inside your DOM Distiller checkout.

1.  Download
    [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads).

1.  Unzip the `chromedriver_mac32.zip` and ensure the binary ends up in your
    `buildtools` folder.

1.  Install the PyPI package management tool `pip`:

    ```bash
    sudo easy_install pip
    ```

1.  Install `selenium` using `pip`:

    ```bash
    pip install --user selenium
    ```

This guide sometimes references a tool called `xvfb`, specifically when running
shell commands with `xvfb-run`. You can remove that part of the command when
developing on Mac OS X. For example, `xvfb-run echo` becomes `echo`.

### Developing with Vagrant

Development is supported only on the above operating systems. We recommend using
Vagrant for development on other systems, such as Windows or Red Hat Linux.

1.  [Install Vagrant](http://www.vagrantup.com/downloads.html) on your system.
    Version 1.7.2 or higher is recommended.
1.  Launch the Vagrant VM instance

    ```bash
    vagrant up
    ```

1.  SSH to the VM

    ```bash
    vagrant ssh
    ```

1.  [Follow the steps for developing on Ubuntu/Debian](#developing-on-ubuntu_debian).

### Tools for contributing

DOM Distiller uses Chromium's collaboration tools. Code reviews are hosted on
[Chromium Gerrit](https://chromium-review.googlesource.com/), and you must
install `depot_tools` by following the guide at
[Chrome infrastructure documentation for depot_tools](http://commondatastorage.googleapis.com/chrome-infra-docs/flat/depot_tools/docs/html/depot_tools_tutorial.html#_setting_up).

### Formatting code

You can run `git cl format` to update your code to follow DOM Distiller's code
formatting guidelines. You must add the following symbolic links to the
`buildtools` folder in your checkout for the command to work correctly:

-   `clang_format` &rarr; `/path/to/chromium/src/buildtools/clang_format/`
-   (64-bit Linux only) `linux64` &rarr;
    `/path/to/chromium/src/buildtools/linux64/`
-   (Mac only) `mac` &rarr; `/path/to/chromium/mac/buildtools/linux64/`

## Building

### Using ant

`ant` is the tool we use to build. All available targets can be listed using
`ant -p`.

Some important targets that you are likely to use while working on the project:

-   `ant test`: Run all tests.
-   `ant test -Dtest.filter=$FILTER_PATTERN`: Run a subset of tests. For
    example, `*.FilterTest.*:*Foo*-*Bar*` would run all tests containing
    `.FilterTest.` and `Foo`, but not those with `Bar`.
-   `ant gwtc`: Compile .class + .java files to JavaScript. Standalone
    JavaScript is available at `war/domdistiller/domdistiller.nocache.js`.
-   `ant gwtc.jstests`: Create a standalone JavaScript for the tests.
-   `ant extractjs`: Create standalone JavaScript from output of ant gwtc. The
    compiled JavaScript file is available at `out/domdistiller.js`.
-   `ant extractjs.jstests`: Create a standalone JavaScript for the tests.
-   `ant package`: Copy the main build artifacts into the `out/package` folder,
    typically the extracted JS and protocol buffer files.

## Contributing

You can use most regular `git` commands during development and `git cl` for
collaboration.

### Preparing changes for review

Create a new local branch and commit the changes you want to make. When you are
done, please run `git cl format` to standardize the code format before
uploading.

### Uploading changes to Gerrit

Checkout your local branch with the changes you want to have reviewed and run
`git cl upload` to create a change list (CL) at
[Chromium Gerrit](https://chromium-review).

The first time you do this, you will have to provide a username and password.

-   For username, use your @chromium.org account.
-   For password, get it from
    [GoogleCode.com settings page](https://code.google.com/hosting/settings)
    when logged into your @chromium.org account, and add the full `machine
    code.google.com login` line to your `~/.netrc` file.

### Landing your changes

Once your reviewer approves your changes, you can click "Submit to CQ" to land
your changes.

## Run in Chrome for desktop

1.  Verify that the following environment variables are set:

    ```bash
    export CHROME_SRC=/path/to/chromium/src
    export DOM_DISTILLER_DIR=/path/to/dom-distiller
    ```

2.  Run `ant package` and copy the generated files into Chrome. You can use this
    bash function to automate the process:

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

3.  From `$CHROME_SRC` run GN to setup ninja build files using

    ```bash
    gn args out/Debug
    ```

### Running the Chrome browser with distiller support

Build Chrome with the `chrome` target and run it with DOM Distiller enabled:

```bash
autoninja -C out/Debug chrome && out/Debug/chrome --enable-dom-distiller
```

You can distill web pages in any of the following ways:

-   Selecting the menu item `Toggle distilled page contents`.
-   Activating the Reader Mode icon when it appears in the omnibox.

To have a unique user profile every time you run Chrome, you can add
`--user-data-dir=/tmp/$(mktemp -d)` as a command line parameter. On Mac OS X,
you can instead write `--user-data-dir=$(mktemp -d 2>/dev/null || mktemp -d -t
'chromeprofile')`.

### Running the automated tests in Chromium

1.  Build the `components_browsertests` target:

    ```bash
    autoninja -C out/Debug components_browsertests
    ```

2.  Run the `components_browsertests` binary to execute the tests:

    ```bash
    out/Debug/components_browsertests
    ```

Some additional tips for running tests:

-   Prefix the command with `xvfb-run` to avoid pop-up windows:

    ```bash
    xvfb-run out/Debug/components_browsertests
    ```

-   Select which tests to run using `--gtest_filter=<pattern>`:

    ```bash
    out/Debug/components_browsertests --gtest_filter=\*Distiller\*
    ```

-   Run tests as isolates by building `components_browsertests_run` and
    executing them with the swarming tool:

    ```bash
    autoninja -C out/Debug components_browsertests_run
    python tools/swarming_client/isolate.py run -s out/Debug/components_browsertests.isolated
    ```

Additional documentation about testing in Chromium can be found on
[Google Test's GitHub page](https://github.com/google/googletest/).

### Running the content extractor

To extract the content from a web page directly, you can run

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

-   Chrome browser: `--vmodule=*distiller*=2`
-   Content extractor: `--debug-level=99`

If this is something you often do, you can put the following function in a bash
file you include (for example `~/.bashrc`) and use it for iterative development:

```bash
distill() {
  (
    roll-distiller && \
    autoninja -C out/Debug components_browsertests &&
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

## Debugging

### Interactive debugging

You can use the Chrome Developer Tools to debug DOM Distiller:

-   Update the test JavaScript by running `ant extractjs.jstests` or `ant test`.
-   Open `war/test.html` in Chrome desktop
-   Open the `Console` panel in Developer Tools (**Ctrl-Shift-J**). On Mac OS X
    you can use **&#x2325;-&#x2318;-I** (uppercase `I`) as the shortcut.
-   Run all tests by calling:

    ```javascript
    org.chromium.distiller.JsTestEntry.run()
    ```

-   To run only a subset of tests, you can use a regular expression that matches
    a single test or multiple tests:

    ```javascript
    org.chromium.distiller.JsTestEntry.runWithFilter('MyTestClass.testSomething')
    ```

The `Sources` panel contains both the extracted JavaScript and the Java source
files, as long as you haven't disabled JavaScript source maps in Developer
Tools. You can set breakpoints in the Java source files to stop the code
execution and examine a variety of useful information, such as variable values.

When a test fails, you will see several stack traces. One of these contains
clickable links to the corresponding Java source files for the stack frames.

### Developer extension

`ant package` generates an unpacked Chrome extension under `out/extension`,
which you can add to the browser with the following steps:

1.  Go to `chrome://extensions`
2.  Enable developer mode
3.  Select to load an unpacked extension and point to the `out/extension`
    folder.

The extension currently supports profiling the extraction code.

It also adds a panel to the Developer Tools which you can use to trigger
extraction on the inspected page. This can be used to trigger and profile
extraction on a mobile device which you are currently inspecting using
`chrome://inspect`.

### Logging

Use `LogUtil.logToConsole()` to log information for debugging. Where the log
output is stored varies with how DOM Distiller is run:

-   `ant test`: Terminal. To get more verbose output, use `ant test
    -Dtest.debug_level=99`.
-   Chrome browser: the Chrome log file, as set by shell variable
    `$CHROME_LOG_FILE`. A release mode build of Chrome will log all JavaScript
    `INFO` there if you start Chrome with `--enable-logging`. You can add
    `--enable-logging=stderr` to have the log go to stderr instead of a file.
-   Content extractor: See
    [documentation about `extract.log` above](#running-the-content-extractor).

For an example, see
`$DOM_DISTILLER_DIR/java/org/chromium/distiller/PagingLinksFinder.java`.

Use `ant package '-Dgwt.custom.args=-style PRETTY'` for easier JavaScript
debugging.

## Mobile distillation from desktop

1.  In the tab with the interesting URL, bring up the Developer Tools emulation
    panel (the mobile device icon).
1.  Select the desired `Device` and reload the page. Verify that you get what
    you expect. For example a Nexus 4 might get a mobile site, whereas Nexus 7
    might get the desktop site.
1.  Copy the User-Agent from the `UA` field to the clipboard. This field does
    require reload after changing device, but it is good practice to verify that
    you get what you expect.
1.  Re-start chrome with `--user-agent="$USER_AGENT_FROM_CLIPBOARD"`. Remember
    to also add `--enable-dom-distiller`.
1.  Select `Toggle distilled page contents` from the menu to display the
    distilled page.

If you want you can copy some of these User-Agent aliases into normal bash
aliases for easy access later. For example, Nexus 4 would be:

```
--user-agent="Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"
```
