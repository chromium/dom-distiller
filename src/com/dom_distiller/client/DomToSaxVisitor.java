// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.client.sax.ContentHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Used to generate sax events from the DOM tree.
 */
public class DomToSaxVisitor implements DomWalker.Visitor {
    private final ContentHandler handler;

    DomToSaxVisitor(ContentHandler h) {
        handler = h;
    }

    @Override
    public boolean visit(Node n) {
        switch (n.getNodeType()) {
            case Node.TEXT_NODE:
                handler.textNode(Text.as(n));
                return false;
            case Node.ELEMENT_NODE:
                Element e = Element.as(n);
                handler.startElement(e);
                return true;
            case Node.DOCUMENT_NODE:  // Don't recurse into sub-documents.
            default:  // This case is for comment nodes.
                return false;
        }
    }

    @Override
    public void exit(Node n) {
        Element e = Element.as(n);
        handler.endElement(e);
    }
}
