#!/bin/bash

git log --oneline -n 5
sudo ./install-build-deps.sh
sudo start xvfb
sudo ant test -Dtest.filter='*-*.ParsedUrlTest.*:*PagePatternTest.testIsPagePatternValid:*PagePatternTest.testIsPagingUrl'
sudo ant package
