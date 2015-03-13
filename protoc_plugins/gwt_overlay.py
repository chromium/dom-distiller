#!/usr/bin/env python
# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""protoc plugin to generate gwt overlay types

The generated types have approximately the same interface as protobufs standard
java builder types.
"""

from util import plugin, plugin_protos, writer


class GwtOverlayWriter(writer.CodeWriter):
  def WriteFile(self, proto_file):
    err = proto_file.CheckSupported()
    if err:
      self.AddError(err)
      return

    self.WriteCStyleHeader()

    self.Output('package {java_package};',
                java_package=proto_file.JavaPackage())
    self.Output('')
    self.Output('import java.util.ArrayList;')
    self.Output('import java.util.List;')
    self.Output('import com.google.gwt.core.client.JavaScriptObject;')

    self.Output('')
    self.Output('public class {outer_class} {{',
                  outer_class=proto_file.JavaOuterClass())

    with self.AddIndent():
      for message in proto_file.GetMessages():
        self.WriteMessage(message)

      for enum in proto_file.GetEnums():
        self.WriteEnum(enum)

    self.Output('}}')

  def WriteMessage(self, message):
    self.Output(
        'public static final class {class_name} extends JavaScriptObject {{',
        class_name=message.JavaClassName())
    with self.AddIndent():
      self.WriteConstructorAndInitializer(message)
      for field in message.GetFields():
        self.WriteField(field)

      for nested_type in message.GetMessages():
        self.WriteMessage(nested_type)

      for enum in message.GetEnums():
        self.WriteEnum(enum)
    self.Output('}}')
    self.Output('')

  def WriteConstructorAndInitializer(self, message):
    self.Output(
        'protected {class_name}() {{}}\n'
        '\n'
        'public static {class_name} create() {{\n'
        '  {class_name} obj = createObject().<{class_name}>cast();\n'
        '  obj.initializeDefaults();\n'
        '  return obj;\n'
        '}}\n'
        '\n'
        'private native void initializeDefaults() /*-{{\n'
        '',
        class_name=message.JavaClassName())
    with self.AddIndent():
      for field in message.GetFields():
        if field.IsRepeated():
          self.Output('this[{js_index}] = new Array();',
                      js_index=field.JavascriptIndex())
    self.Output('}}-*/;')
    self.Output('')

  def WriteField(self, field):
    if field.IsRepeated():
      self.RepeatedMemberField(field)
    else:
      self.OptionalMemberField(field)
    self.Output('')

  def WriteEnum(self, enum):
    self.Output('public static class {classname} {{', classname=enum.JavaName())
    with self.AddIndent():
      for val in enum.Values():
        self.Output('public static final int {valname} = {value};',
                    valname=val.GetName(), value=val.GetValue())
    self.Output('}}')
    self.Output('')

  def RepeatedMemberField(self, field):
    if field.IsClassType():
      addFunction = (
          'public final {java_type} add{java_name}() {{\n'
          '  {java_type} res = {java_type}.create();\n'
          '  add{java_name}Internal(res);\n'
          '  return res;\n'
          '}}\n'
          '\n'
          'private final native void add{java_name}Internal({java_type} val) /*-{{\n'
          '  this[{js_index}].push(val);\n'
          '}}-*/;\n'
          '\n'
          )
    else:
      addFunction = (
          'public final native void add{java_name}({java_type} val) /*-{{\n'
          '  this[{js_index}].push(val);\n'
          '}}-*/;\n'
          '\n'
          )

    self.Output(
        'public final native int get{java_name}Count() /*-{{\n'
        '  return this[{js_index}].length;\n'
        '}}-*/;\n'
        '\n'
        'public final native {java_type} get{java_name}(int idx) /*-{{\n'
        '  if (idx >= this[{js_index}].length) throw new RangeError();\n'
        '  return this[{js_index}][idx];\n'
        '}}-*/;\n'
        '\n'
        'public final List<{java_list_type}> get{java_name}List() {{\n'
        '  int count = get{java_name}Count();\n'
        '  List<{java_list_type}> res = new ArrayList<{java_list_type}>(count);\n'
        '  for (int i = 0; i < count; ++i) {{\n'
        '    res.add(get{java_name}(i));\n'
        '  }}\n'
        '  return res;\n'
        '}}\n'
        '\n'
        'public final native void set{java_name}(int idx, {java_type} val) /*-{{\n'
        '  if (idx >= this[{js_index}].length) throw new RangeError();\n'
        '  this[{js_index}][idx] = val;\n'
        '}}-*/;\n'
        '\n'
        'public final native void clear{java_name}() /*-{{\n'
        '  this[{js_index}].length = 0;\n'
        '}}-*/;\n'
        '\n'
        + addFunction,
        java_type=field.JavaType(),
        java_list_type=field.JavaListType(),
        java_name=field.JavaName(),
        js_index=field.JavascriptIndex())

  def OptionalMemberField(self, field):
    self.Output(
        'public final native boolean has{java_name}() /*-{{\n'
        '  return this[{js_index}] != undefined;\n'
        '}}-*/;\n'
        '\n'
        'public final native void clear{java_name}() /*-{{\n'
        '  delete this[{js_index}];\n'
        '}}-*/;\n'
        '\n'
        'public final native {java_type} get{java_name}() /*-{{\n'
        '  if (this[{js_index}] === undefined) throw new TypeError();\n'
        '  return this[{js_index}];\n'
        '}}-*/;\n'
        '\n'
        'public final native void set{java_name}({java_type} val) /*-{{\n'
        '  if (val == undefined) throw new TypeError();\n'
        '  this[{js_index}] = val;\n'
        '}}-*/;\n'
        '',
        java_type=field.JavaType(),
        java_name=field.JavaName(),
        js_index=field.JavascriptIndex())


def main():
  request = plugin.ReadRequestFromStdin()
  response = plugin_protos.PluginResponse()

  # TODO(cjhopman): This should only be generating files for files listed in
  # request.file_to_generate. Since we don't actually support dependencies,
  # only files in file_to_generate should be here, anyway.
  for proto_file in request.GetAllFiles():
    plugin.RegisterProtoFile(proto_file)

    writer = GwtOverlayWriter()
    writer.WriteFile(proto_file)
    response.AddFileWithContent(proto_file.JavaFilename(), writer.GetValue())

    if writer.GetErrors():
      response.AddError('\n'.join(writer.GetErrors()))

  response.WriteToStdout()


if __name__ == '__main__':
  main()
