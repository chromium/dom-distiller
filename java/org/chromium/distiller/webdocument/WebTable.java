// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;
import org.chromium.distiller.DomWalker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.List;

public class WebTable extends WebElement {
    private Element tableElement;
    public WebTable(Element tableRoot) {
        tableElement = tableRoot;
    }

    public void addOutputNodes(final List<Node> nodes, boolean includeTitle) {
        new DomWalker(new DomWalker.Visitor() {
            @Override
            public boolean visit(Node n) {
                switch (n.getNodeType()) {
                    case Node.TEXT_NODE:
                        nodes.add(n);
                        return false;
                    case Node.ELEMENT_NODE:
                        if (!DomUtil.isVisible(Element.as(n))) return false;
                        nodes.add(n);
                        return true;
                    case Node.DOCUMENT_NODE:
                    default:
                        return false;
                }
            }

            @Override
            public void exit(Node n) {
            }

            @Override
            public void skip(Element e) {
            }
        }).walk(tableElement);
    }
}
