// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.webdocument.filters.images.AreaScorer;
import org.chromium.distiller.webdocument.filters.images.DimensionsRatioScorer;
import org.chromium.distiller.webdocument.filters.images.DomDistanceScorer;
import org.chromium.distiller.webdocument.filters.images.HasFigureScorer;
import org.chromium.distiller.webdocument.filters.images.ImageScorer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;

public class ImageHeuristicsTest extends DomDistillerJsTestCase {

    public void testAreaScorer() {
        Element goodImage = TestUtil.createImage();
        goodImage.getStyle().setProperty("width", "600px");
        goodImage.getStyle().setProperty("height", "350px");
        goodImage.getStyle().setProperty("display", "block");

        Element badImage = TestUtil.createImage();
        badImage.getStyle().setProperty("width", "100px");
        badImage.getStyle().setProperty("height", "100px");
        badImage.getStyle().setProperty("display", "block");

        mBody.appendChild(goodImage);
        mBody.appendChild(badImage);

        ImageScorer scorer = new AreaScorer(50, 75000, 200000);

        assertEquals(true, scorer.getImageScore(goodImage) > 0);
        assertEquals(0, scorer.getImageScore(badImage));
        assertEquals(0, scorer.getImageScore(null));
    }

    public void testDimensionRatioScorer() {
        Element goodImage = TestUtil.createImage();
        goodImage.getStyle().setProperty("width", "600px");
        goodImage.getStyle().setProperty("height", "350px");
        goodImage.getStyle().setProperty("display", "block");

        Element badImage = TestUtil.createImage();
        badImage.getStyle().setProperty("width", "200px");
        badImage.getStyle().setProperty("height", "700px");
        badImage.getStyle().setProperty("display", "block");

        mBody.appendChild(goodImage);
        mBody.appendChild(badImage);

        ImageScorer scorer = new DimensionsRatioScorer(50);

        assertEquals(true, scorer.getImageScore(goodImage) > 0);
        assertEquals(0, scorer.getImageScore(badImage));
        assertEquals(0, scorer.getImageScore(null));
    }

    public void testDomDistanceScorer() {
        Node root = TestUtil.createDiv(0);
        Node content = TestUtil.createDiv(1);
        Element image = TestUtil.createImage();
        image.getStyle().setProperty("width", "100px");
        image.getStyle().setProperty("height", "100px");
        image.getStyle().setProperty("display", "block");
        content.appendChild(image);
        root.appendChild(content);

        mBody.appendChild(root);

        // Build long chain of divs to separate image from content.
        Node currentDiv = TestUtil.createDiv(3);
        root.appendChild(currentDiv);
        for (int i = 0; i < 7; i++) {
            currentDiv.appendChild(TestUtil.createDiv(i+4));
            currentDiv = currentDiv.getChild(0);
        }

        ImageScorer scorer = new DomDistanceScorer(50, content);
        ImageScorer scorerFarContent = new DomDistanceScorer(50, currentDiv);

        assertEquals(true, scorer.getImageScore(image) > 0);
        assertEquals(0, scorerFarContent.getImageScore(image));
        assertEquals(0, scorer.getImageScore(null));
    }

    public void testHasFigureScorer() {
        Node root = TestUtil.createDiv(0);
        Node fig = DOM.createElement("figure");
        Element goodImage = TestUtil.createImage();
        goodImage.getStyle().setProperty("width", "100px");
        goodImage.getStyle().setProperty("height", "100px");
        goodImage.getStyle().setProperty("display", "block");
        fig.appendChild(goodImage);
        root.appendChild(fig);

        mBody.appendChild(root);

        Element badImage = TestUtil.createImage();
        badImage.getStyle().setProperty("width", "100px");
        badImage.getStyle().setProperty("height", "100px");
        badImage.getStyle().setProperty("display", "block");
        root.appendChild(badImage);

        ImageScorer scorer = new HasFigureScorer(50);

        assertEquals(true, scorer.getImageScore(goodImage) > 0);
        assertEquals(0, scorer.getImageScore(badImage));
        assertEquals(0, scorer.getImageScore(null));
    }
}
