// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.sax;

public interface ContentHandler {
    public void endDocument();
    public void ignorableWhitespace(char[] ch, int start, int length);
    public void startDocument();
    public void startElement(String uri, String localName, String qName, Attributes atts);
    public void endElement(String uri, String localName, String qName);
    public void characters(char[] ch, int start, int length);
}
