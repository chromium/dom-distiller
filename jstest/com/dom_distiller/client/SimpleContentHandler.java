// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.client.sax.ContentHandler;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * This is a simple SAX content handler that converts sax events to an xml document. It only handles
 * a small subset of those events.
 */
class SimpleContentHandler implements ContentHandler {

    private final StringBuilder documentStringBuilder;

    SimpleContentHandler() {
        documentStringBuilder = new StringBuilder();
    }

    String getDocumentString() {
        return documentStringBuilder.toString();
    }

    @Override
    public void endDocument() {}

    @Override
    public void startDocument() {}

    @Override
    public void skipElement(Element element) {}

    @Override
    public void startElement(Element element) {
        documentStringBuilder.append("<");
        documentStringBuilder.append(element.getTagName());
        JsArray<Node> attributes = DomUtil.getAttributes(element);
        for (int i = 0; i < attributes.length(); i++) {
            Node node = attributes.get(i);
            documentStringBuilder.append(" ");
            documentStringBuilder.append(node.getNodeName());
            documentStringBuilder.append("=\"");
            documentStringBuilder.append(node.getNodeValue());
            documentStringBuilder.append("\"");
        }
        documentStringBuilder.append(">");
    }

    @Override
    public void endElement(Element element) {
        documentStringBuilder.append("</" + element.getTagName() + ">");
    }

    @Override
    public void textNode(Text textNode) {
        documentStringBuilder.append(textNode.getData());
    }
}
