// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Arrays;
import java.util.List;

public class RelevantImageFinderTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testNoImage() {
        Node root = TestUtil.createDiv(0);
        Node contentText = TestUtil.createText("content");
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(contentNodes, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageAfterContent() {
        Node root = TestUtil.createDiv(0);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        root.appendChild(contentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(contentNodes, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
        assertEquals(image, contentAndImages.get(1));
    }

    public void testInvisibleImageAfterContentIsHidden() {
        Node root = TestUtil.createDiv(0);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        Element.as(image).getStyle().setDisplay(Display.NONE);
        root.appendChild(contentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(contentNodes, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageAfterNonContent() {
        Node root = TestUtil.createDiv(0);
        Node contentText = TestUtil.createText("content");
        Node nonContentText = TestUtil.createText("not content");
        Node image = TestUtil.createImage();
        root.appendChild(contentText);
        root.appendChild(nonContentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(contentNodes, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageWithDifferentParent() {
        Node root = TestUtil.createDiv(0);
        Node leftChild = TestUtil.createDiv(1);
        Node rightChild = TestUtil.createDiv(2);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        leftChild.appendChild(contentText);
        rightChild.appendChild(image);
        root.appendChild(leftChild);
        root.appendChild(rightChild);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(contentNodes, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
        assertEquals(image, contentAndImages.get(1));
    }

}
