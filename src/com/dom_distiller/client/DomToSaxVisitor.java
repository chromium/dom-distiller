// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.client.sax.Attributes;
import com.dom_distiller.client.sax.AttributesImpl;
import com.dom_distiller.client.sax.ContentHandler;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.logging.Logger;

/**
 * Used to generate sax events from the DOM tree.
 */
public class DomToSaxVisitor implements DomWalker.Visitor {
    private static Logger logger = Logger.getLogger("DomToSaxParser");
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
                Attributes attrs = getAttributes(e);
                handler.startElement(e, attrs);
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

    /**
     * @Return The element's attribute list converted to {@link Attributes}.
     */
    public static Attributes getAttributes(Element e) {
        AttributesImpl attrs = new AttributesImpl();

        JsArray<Node> jsAttrs = DomUtil.getAttributes(e);
        for (int i = 0; i < jsAttrs.length(); ++i) {
            final Node jsAttr = jsAttrs.get(i);
            attrs.addAttribute(jsAttr.getNodeName(), jsAttr.getNodeValue());
        }

        return attrs;
    }
}
