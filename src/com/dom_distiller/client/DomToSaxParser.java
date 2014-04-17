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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Used to generate sax events from the DOM tree.
 */
public class DomToSaxParser {
    static Logger logger = Logger.getLogger("DomToSaxParser");

    private static class DomToSaxVisitor implements DomWalker.Visitor {
        private static final String sHtmlNamespace = "http://www.w3.org/1999/xhtml";
        private final ContentHandler handler;
        private List<Node> textNodes;

        DomToSaxVisitor(ContentHandler h) {
            handler = h;
            textNodes = new ArrayList<Node>();
        }

        @Override
        public boolean visit(Node n) {
            try {
                switch (n.getNodeType()) {
                    case Node.TEXT_NODE:
                        textNodes.add(n);
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
     *
     * @Return A list of the text nodes (in order).
     */
    static List<Node> parse(Element e, ContentHandler handler) {
        DomToSaxVisitor visitor = new DomToSaxVisitor(handler);
        new DomWalker(visitor).walk(e);
        return visitor.textNodes;
    }

    /**
     * @Return The element's attribute list converted to org.xml.sax.Attributes.
     */
    public static Attributes getSaxAttributes(Element e) {
        AttributesImpl attrs = new AttributesImpl();

        JsArray<Node> jsAttrs = DomUtil.getAttributes(e);
        for (int i = 0; i < jsAttrs.length(); ++i) {
            final Node jsAttr = jsAttrs.get(i);
            attrs.addAttribute("", jsAttr.getNodeName(), jsAttr.getNodeName(), "CDATA", jsAttr.getNodeValue());
        }

        return attrs;
    }
}
