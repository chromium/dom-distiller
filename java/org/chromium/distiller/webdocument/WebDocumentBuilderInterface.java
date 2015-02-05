// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Text;

public interface WebDocumentBuilderInterface {
    void skipElement(Element e);
    void startElement(Element element);
    void endElement();
    void textNode(Text textNode);
    void dataTable(Element e);
    void embed(WebEmbed embedNode);
}
