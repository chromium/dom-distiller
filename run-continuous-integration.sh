#!/bin/bash

set -e
set -x

git log --oneline -n 5
sudo ./install-build-deps.sh
sudo start xvfb || true
sudo ant test -Dtest.filter='*-*.ParsedUrlTest.*:*PagePatternTest.testIsPagePatternValid:*PagePatternTest.testIsPagingUrl'
sudo ant package
