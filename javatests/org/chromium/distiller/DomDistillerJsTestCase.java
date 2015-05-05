// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Base test case for all DomDistiller js tests. Ensures that each test starts
 * with a fresh document.
 */
public class DomDistillerJsTestCase extends JsTestCase {
    protected Element mRoot;
    protected Element mHead;
    protected Element mBody;

    @Override
    public void setUp() throws Exception {
        gwtSetUp();
    }

    protected void gwtSetUp() throws Exception {
        mRoot = Document.get().getDocumentElement();
        JsArray<Node> attrs = DomUtil.getAttributes(mRoot);
        for (int i = 0; i < attrs.length(); i++) {
            mRoot.removeAttribute(attrs.get(i).getNodeName());
        }
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
