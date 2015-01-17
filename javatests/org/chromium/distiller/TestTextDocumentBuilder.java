// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.sax.BoilerpipeHTMLContentHandler;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.LinkedList;

public class TestTextDocumentBuilder {
    private LinkedList<TextBlock> textBlocks;
    private int textBlockIndex;
    public TestTextDocumentBuilder() {
        textBlocks = new LinkedList<TextBlock>();
        textBlockIndex = 0;
    }

    private TextBlock addBlock(String text, String... labels) {
        int numWords = text.split(" ").length;
        TextBlock block =
                new TextBlock(text, null, 0, 0, 0, 0, numWords, numWords, textBlockIndex++);
        block.setIsContent(false);
        for (String label : labels) {
            block.addLabel(label);
        }
        textBlocks.add(block);
        return block;
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
        BoilerpipeHTMLContentHandler htmlParser = new BoilerpipeHTMLContentHandler();
        htmlParser.startDocument();
        DomToSaxVisitor domToSaxVisitor = new DomToSaxVisitor(htmlParser);

        Node body = Document.get().getBody();
        if (!JavaScript.contains(body, docElement) && body.equals(docElement)) {
            body.appendChild(docElement);
            new DomWalker(domToSaxVisitor).walk(docElement);
            body.removeChild(docElement);
        } else {
            new DomWalker(domToSaxVisitor).walk(docElement);
        }

        htmlParser.endDocument();
        return htmlParser.toTextDocument();
    }
}
