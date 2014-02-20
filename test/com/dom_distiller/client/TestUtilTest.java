// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.List;

import com.google.gwt.dom.client.Element;

import com.google.gwt.junit.client.GWTTestCase;

public class TestUtilTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testCreateDivTree() {
        List<Element> divs = TestUtil.createDivTree();
        assertEquals(
                TestUtil.expectedDivTreeHtml,
                TestUtil.getElementAsString(divs.get(0)));

    }
}
