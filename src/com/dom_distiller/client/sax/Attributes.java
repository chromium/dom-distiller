// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.sax;

public interface Attributes {
    String getValue(String key);
    int getIndex(String key);
    String getName(int index);
    String getValue(int index);
    int getLength();
}
