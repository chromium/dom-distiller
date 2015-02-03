// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.webdocument.DomConverter;
import org.chromium.distiller.webdocument.TestWebTextBuilder;
import org.chromium.distiller.webdocument.WebDocumentBuilder;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;

public class TestTextDocumentBuilder {
    private ArrayList<TextBlock> textBlocks;
    private ArrayList<WebElement> elements;
    private TestWebTextBuilder webTextBuilder;
    private int textBlockIndex;

    public TestTextDocumentBuilder() {
        textBlocks = new ArrayList<>();
        elements = new ArrayList<>();
        webTextBuilder = new TestWebTextBuilder();
        textBlockIndex = 0;
    }

    private TextBlock addBlock(String text, String... labels) {
        WebText wt = webTextBuilder.createForText(text);
        for (String label : labels) {
            wt.addLabel(label);
        }
        elements.add(wt);
        textBlocks.add(new TextBlock(elements, elements.size() - 1));
        return textBlocks.get(textBlocks.size() - 1);
    }

    public TestTextDocumentBuilder addContentBlock(String text, String... labels) {
        addBlock(text, labels).setIsContent(true);
        return this;
    }

    public TestTextDocumentBuilder addNonContentBlock(String text, String... labels) {
        addBlock(text, labels).setIsContent(false);
        return this;
    }

    public TextDocument build() {
        return new TextDocument(textBlocks);
    }

    public static TextDocument fromPage(Element docElement) {
        WebDocumentBuilder builder = new WebDocumentBuilder();
        DomConverter domConverter = new DomConverter(builder);

        Node body = Document.get().getBody();
        if (!JavaScript.contains(body, docElement) && body.equals(docElement)) {
            body.appendChild(docElement);
            new DomWalker(domConverter).walk(docElement);
            body.removeChild(docElement);
        } else {
            new DomWalker(domConverter).walk(docElement);
        }

        return builder.toWebDocument().createTextDocumentView();
    }
}
