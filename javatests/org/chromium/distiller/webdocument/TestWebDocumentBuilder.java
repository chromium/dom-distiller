// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import org.chromium.distiller.TestUtil;

public class TestWebDocumentBuilder {
    private WebDocument document = new WebDocument();
    private TestWebTextBuilder webTextBuilder = new TestWebTextBuilder();

    public WebText addText(String text) {
        WebText wt = webTextBuilder.createForText(text);
        document.addText(wt);
        return wt;
    }

    public WebText addNestedText(String text) {
        WebText wt = webTextBuilder.createNestedText(text, 5);
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

    public WebImage addImage() {
        Element image = TestUtil.createImage();
        WebImage wi = new WebImage(image, 100, 100, "http://www.example.com/foo.jpg");
        document.addEmbed(wi);
        return wi;
    }

    public WebImage addLeadImage() {
        Element image = TestUtil.createImage();
        image.setAttribute("width", "600");
        image.setAttribute("height", "400");
        Document.get().getBody().appendChild(image);
        WebImage wi = new WebImage(image, 600, 400, "http://www.example.com/lead.bmp");
        document.addEmbed(wi);
        return wi;
    }

    public WebDocument build() {
        return document;
    }
}
