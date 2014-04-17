// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import com.google.gwt.junit.client.GWTTestCase;

public class MarkupParserTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testNullOpenGraphProtocolParser() {
        // To have a null OpenGraphProtocolParser, don't create its required
        // meta tags.  Instead, create tags that IEReadingViewParser will
        // recognize and legitimize as title.
        String expectedTitle = "Testing null OpenGraphProtocolParser.";
        mHead.appendChild(TestUtil.createTitle(expectedTitle));
        createMeta("title", expectedTitle);
        mBody.appendChild(TestUtil.createHeading(1, expectedTitle));

        MarkupParser parser = new MarkupParser(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    // TODO(kuan): write more tests if or when we determine:
    // - which parser takes precedence
    // - how we merge the different values retrieved from the different parsers.

    @Override
    protected void gwtSetUp() throws Exception {
        // Get root element.
        mRoot = Document.get().getDocumentElement();

        // Get <head> element.
        NodeList<Element> heads = mRoot.getElementsByTagName("HEAD");
        if (heads.getLength() != 1)
            throw new Exception("There shouldn't be more than 1 <head> tag");
        mHead = heads.getItem(0);

        // Get <body> element.
        NodeList<Element> bodies = mRoot.getElementsByTagName("BODY");
        if (bodies.getLength() != 1)
            throw new Exception("There shouldn't be more than 1 <body> tag");
        mBody = bodies.getItem(0);

        // Remove all meta tags, otherwise a testcase may run with the meta tags
        // set up in a previous testcase, resulting in unexpected results.
        NodeList<Element> allMeta = mRoot.getElementsByTagName("META");
        for (int i = allMeta.getLength() - 1; i >= 0; i--) {
            allMeta.getItem(i).removeFromParent();
        }
    }

    private void createMeta(String name, String content) {
        mHead.appendChild(TestUtil.createMetaName(name, content));
    }

    private Element mRoot;
    private Element mHead;
    private Element mBody;
}
