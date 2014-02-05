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

    /**
     * This will generate sax events for the DOM tree rooted at e to the provided ContentHandler.
     */
    static void parse(Element e, ContentHandler handler) throws SAXException {
        Attributes attrs = getSaxAttributes(e);
        handler.startElement("http://www.w3.org/1999/xhtml", e.getTagName(), e.getTagName(), attrs);

        NodeList<Node> children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node n = children.getItem(i);
            switch (n.getNodeType()) {
                case Node.TEXT_NODE:
                    String text = Text.as(n).getData();
                    handler.characters(text.toCharArray(), 0, text.length());
                    break;
                case Node.ELEMENT_NODE:
                    parse(Element.as(n), handler);
                    break;
                case Node.DOCUMENT_NODE:
                    break;
                default: // comments
                    break;
            }
        }
        handler.endElement("http://www.w3.org/1999/xhtml", e.getTagName(), e.getTagName());
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
