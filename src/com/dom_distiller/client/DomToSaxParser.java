// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;

import org.xml.sax.Attributes;
import org.xml.sax.AttributesImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.logging.Logger;

/**
 * Used to generate sax events from the DOM tree.
 */
class DomToSaxParser {
    static Logger logger = Logger.getLogger("DomToSaxParser");

    private static class DomToSaxVisitor implements DomWalker.Visitor {
        private static final String sHtmlNamespace = "http://www.w3.org/1999/xhtml";
        private final ContentHandler handler;

        DomToSaxVisitor(ContentHandler h) {
            handler = h;
        }

        @Override
        public boolean visit(Node n) {
            try {
                switch (n.getNodeType()) {
                    case Node.TEXT_NODE:
                        String text = Text.as(n).getData();
                        handler.characters(text.toCharArray(), 0, text.length());
                        return false;
                    case Node.ELEMENT_NODE:
                        Element e = Element.as(n);
                        Attributes attrs = getSaxAttributes(e);
                        handler.startElement(sHtmlNamespace, e.getTagName(), e.getTagName(), attrs);
                        return true;
                    case Node.DOCUMENT_NODE:  // Don't recurse into sub-documents.
                    default:  // This case is for comment nodes.
                        return false;
                }
            } catch (SAXException e) {
                return false;
            }
        }

        @Override
        public void exit(Node n) {
            Element e = Element.as(n);
            try {
                handler.endElement(sHtmlNamespace, e.getTagName(), e.getTagName());
            } catch (SAXException ex) {
                // Intentionally ignored.
            }
        }
    }

    /**
     * This will generate sax events for the DOM tree rooted at e to the provided ContentHandler.
     */
    static void parse(Element e, ContentHandler handler) {
        new DomWalker(new DomToSaxVisitor(handler)).walk(e);
    }

    /**
     * @Return The element's attribute list converted to org.xml.sax.Attributes.
     */
    public static Attributes getSaxAttributes(Element e) {
        AttributesImpl attrs = new AttributesImpl();

        JsArray<Node> jsAttrs = getAttributes(e);
        for (int i = 0; i < jsAttrs.length(); ++i) {
            final Node jsAttr = jsAttrs.get(i);
            attrs.addAttribute("", jsAttr.getNodeName(), jsAttr.getNodeName(), "CDATA", jsAttr.getNodeValue());
        }

        return attrs;
    }

    /**
     * GWT does not provide a way to get a list of all attributes that have been explicitly set on a
     * DOM element (only a way to query the value of a particular attribute). In javascript, this
     * list is accessible as elem.attributes.
     *
     * @Return The element's attribute list from javascript.
     */
    public static native JsArray<Node> getAttributes(Element elem) /*-{
        return elem.attributes;
    }-*/;
}
