// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.client.sax.Attributes;
import com.dom_distiller.client.sax.ContentHandler;

/**
 * This is a simple SAX content handler that converts sax events to an xml document. It only handles
 * a small subset of those events.
 */
class SimpleContentHandler implements ContentHandler {

    SimpleContentHandler() {
        documentStringBuilder = new StringBuilder();
    }

    String getDocumentString() {
        return documentStringBuilder.toString();
    }

    @Override
    public void endDocument() {}

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {}

    @Override
    public void startDocument() {}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        documentStringBuilder.append("<" + localName);
        for (int i = 0; i < atts.getLength(); i++) {
            documentStringBuilder.append(" ");
            documentStringBuilder.append(atts.getLocalName(i));
            documentStringBuilder.append("=\"");
            documentStringBuilder.append(atts.getValue(i));
            documentStringBuilder.append("\"");

        }
        documentStringBuilder.append(">");
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        documentStringBuilder.append("</" + localName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        documentStringBuilder.append(ch, start, length);
    }

    private StringBuilder documentStringBuilder;
}
