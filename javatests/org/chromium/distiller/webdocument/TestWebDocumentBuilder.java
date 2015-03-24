// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class TestWebDocumentBuilder {
    private WebDocument document = new WebDocument();
    private TestWebTextBuilder webTextBuilder = new TestWebTextBuilder();

    public WebText addText(String text) {
        WebText wt = webTextBuilder.createForText(text);
        document.addText(wt);
        return wt;
    }

    public WebText addAnchorText(String text) {
        WebText wt = webTextBuilder.createForAnchorText(text);
        document.addText(wt);
        return wt;
    }

    public WebTable addTable(String innerHtml) {
        Element table = Document.get().createTableElement();
        table.setInnerHTML(innerHtml);
        Document.get().getBody().appendChild(table);
        WebTable wt = new WebTable(table);
        document.addTable(wt);
        return wt;
    }

    public WebDocument build() {
        return document;
    }
}
