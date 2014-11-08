// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.List;

public class HeaderImageFinderTest extends DomDistillerTestCase {

    public void testScoreWithNullNodes() {
        assertEquals(0, HeaderImageFinder.scoreNonContentImage(null, null));
        assertEquals(0, HeaderImageFinder.scoreNonContentImage(TestUtil.createImage(), null));
        assertEquals(0, HeaderImageFinder.scoreNonContentImage(null, TestUtil.createDiv(0)));
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

        int score = HeaderImageFinder.scoreNonContentImage(image,root);

        assertEquals(true, score>0);
    }
}
