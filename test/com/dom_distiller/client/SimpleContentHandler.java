// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

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
    public void endDocument() throws SAXException {}

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {}

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

    @Override
    public void processingInstruction(String target, String data) throws SAXException {}

    @Override
    public void setDocumentLocator(Locator locator) {}

    @Override
    public void skippedEntity(String name) throws SAXException {}

    @Override
    public void startDocument() throws SAXException {}

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
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
    public void endElement(String uri, String localName, String qName) throws SAXException {
        documentStringBuilder.append("</" + localName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        documentStringBuilder.append(ch, start, length);
    }

    private StringBuilder documentStringBuilder;
}
