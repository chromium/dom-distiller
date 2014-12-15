// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.List;

public class HeaderImageFinderTest extends DomDistillerTestCase {

    public void testScoreWithNullNodes() {
        HeaderImageFinder finder = new HeaderImageFinder(null);
        HeaderImageFinder finder2 = new HeaderImageFinder(TestUtil.createDiv(0));
        assertEquals(0, finder.scoreNonContentImage(null));
        assertEquals(0, finder.scoreNonContentImage(TestUtil.createImage()));
        assertEquals(0, finder2.scoreNonContentImage(null));
    }

    public void testNonZeroImageScore() {
        Node root = TestUtil.createDiv(0);
        Element image = TestUtil.createImage();
        image.getStyle().setProperty("width", "600px");
        image.getStyle().setProperty("height", "350px");
        image.getStyle().setProperty("display", "block");

        Node contentText = TestUtil.createText("content");
        root.appendChild(image);
        root.appendChild(contentText);

        HeaderImageFinder finder = new HeaderImageFinder(root);
        int score = finder.scoreNonContentImage(image);

        assertEquals(true, score>0);
    }
}
