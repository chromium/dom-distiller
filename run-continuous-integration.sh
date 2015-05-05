#!/bin/bash

set -e
set -x

git log --oneline -n 5
sudo ./install-build-deps.sh
sudo start xvfb || true
sudo ant test -Dtest.shuffle=1 -Dtest.repeat=10 -Dtest.filter='-*.ParsedUrlTest.*:*PagePatternTest.testIsPagePatternValid:*PagePatternTest.testIsPagingUrl'
sudo ant package
