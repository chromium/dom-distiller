// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.sax;

import com.google.gwt.dom.client.Element;

public interface ContentHandler {
    public void endDocument();
    public void ignorableWhitespace(char[] ch, int start, int length);
    public void startDocument();
    public void startElement(Element element, Attributes atts);
    public void endElement(Element element);
    public void characters(char[] ch, int start, int length);
}
