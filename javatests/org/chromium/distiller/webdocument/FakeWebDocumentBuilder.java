// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.Stack;

/**
 * A simple "WebDocumentBuilder" that just creates an html-like string from the calls.
 */
public class FakeWebDocumentBuilder implements WebDocumentBuilderInterface {

    private final StringBuilder documentStringBuilder;
    private final Stack<Element> elements;

    FakeWebDocumentBuilder() {
        documentStringBuilder = new StringBuilder();
        elements = new Stack<Element>();
    }

    String getDocumentString() {
        return documentStringBuilder.toString();
    }

    @Override
    public void dataTable(Element element) {
        documentStringBuilder.append("<datatable/>");
    }

    @Override
    public void skipElement(Element element) {}

    @Override
    public void startElement(Element element) {
        elements.push(element);
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
    public void endElement() {
        Element el = elements.pop();
        documentStringBuilder.append("</" + el.getTagName() + ">");
    }

    @Override
    public void textNode(Text textNode) {
        documentStringBuilder.append(textNode.getData());
    }

    @Override
    public void embed(WebEmbed embed) {
    }
}
