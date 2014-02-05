// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xml.sax;

public interface Attributes {

  public String getValue(String key);
  public String getURI(int index);
  public String getLocalName(int index);
  public String getQName(int index);
  public String getType(int index);
  public String getValue(int index);
  public int getLength();
}
