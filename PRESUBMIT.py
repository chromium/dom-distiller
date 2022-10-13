# Copyright 2014 The Chromium Authors
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Top-level presubmit script for DOM Distiller.

See http://dev.chromium.org/developers/how-tos/depottools/presubmit-scripts
for more details about the presubmit API built into git cl.
"""

import subprocess
import sys

def _Git(args):
  """Runs the requested git command and returns the first line of output."""
  output = subprocess.check_output(['git'] + args)
  return output.split('\n')[0]


def _CheckUpstream(input_api, output_api):
  """Checks that the upstream branch is remote.

  git cl push will push the issue's change to the branch's upstream branch. This
  should be origin/master (or maybe origin/some_branch) to work as expected.
  Otherwise, git cl push will push the change to some local branch and close the
  issue.
  """
  branch = _Git(['symbolic-ref', 'HEAD'])
  shortbranch = branch.replace('refs/heads/', '')
  remote = _Git(['config', '--local', 'branch.%s.remote' % shortbranch])
  if remote != 'origin':
    upstream = _Git(['config', '--local', 'branch.%s.merge' % shortbranch])
    shortupstream = upstream.replace('refs/heads/', '')
    return [output_api.PresubmitError(
        'Changes should be pushed to origin/master.\n'
        'Try this:\n'
        '    git branch -u origin/master\n'
        '    git cl push\n'
        '    git branch -u %s' % shortupstream)]

  return []

def CheckChangeOnCommit(input_api, output_api):
  results = []
  results.extend(input_api.canned_checks.CheckOwners(input_api, output_api))
  results.extend(_CheckUpstream(input_api, output_api))
  return results
