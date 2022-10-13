// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;

import java.util.List;

public class TestUtilTest extends DomDistillerJsTestCase {
    public void testCreateDivTree() {
        List<Element> divs = TestUtil.createDivTree();
        assertEquals(
                TestUtil.expectedDivTreeHtml,
                TestUtil.getElementAsString(divs.get(0)));

    }
}
