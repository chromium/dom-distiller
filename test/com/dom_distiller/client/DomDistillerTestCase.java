// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Base test case for all DomDistiller tests. Ensures that each test starts
 * with a fresh document.
 */
public class DomDistillerTestCase extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    protected Element mRoot;
    protected Element mHead;
    protected Element mBody;

    @Override
    protected void gwtSetUp() throws Exception {
        mRoot = Document.get().getDocumentElement();
        NodeList<Node> children = mRoot.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            children.getItem(i).removeFromParent();
        }
        mHead = Document.get().createElement("head");
        mRoot.appendChild(mHead);
        mBody = Document.get().createElement("body");
        mRoot.appendChild(mBody);
    }
}
