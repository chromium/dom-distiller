# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import sys

import plugin_protos
import types


def Debug(data):
  sys.stderr.write(str(data))
  sys.stderr.write('\n')
  sys.stderr.flush()


def TitleCase(s):
  return ''.join((p[0].upper() + p[1:] for p in s.split('_')))


def Indented(s, indent=2):
  return '\n'.join((' ' * indent) + p for p in s.rstrip('\n').split('\n'))


proto_path_to_file_map = {}


def RegisterProtoFile(proto_file):
  proto_path_to_file_map[proto_file.Filename()] = proto_file
  types.RegisterTypesForFile(proto_file)


def GetProtoFileForFilename(filename):
  proto_file = proto_path_to_file_map[filename]
  assert proto_file
  return proto_file


def ReadRequestFromStdin():
  data = sys.stdin.read()
  return plugin_protos.PluginRequestFromString(data)
