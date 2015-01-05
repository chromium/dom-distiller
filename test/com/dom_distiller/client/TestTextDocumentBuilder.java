// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import java.util.LinkedList;

class TestTextDocumentBuilder {
    LinkedList<TextBlock> textBlocks;
    int textBlockIndex;
    TestTextDocumentBuilder() {
        textBlocks = new LinkedList<TextBlock>();
        textBlockIndex = 0;
    }

    private TextBlock addBlock(String text, String... labels) {
        int numWords = text.split(" ").length;
        TextBlock block = new TextBlock(
                text,
                TextBlock.EMPTY_NODE_LIST, TextBlock.EMPTY_NODE_LIST,
                numWords, numWords,
                textBlockIndex++);
        block.setIsContent(false);
        for (String label : labels) {
            block.addLabel(label);
        }
        textBlocks.add(block);
        return block;
    }

    TestTextDocumentBuilder addContentBlock(String text, String... labels) {
        addBlock(text, labels).setIsContent(true);
        return this;
    }

    TestTextDocumentBuilder addNonContentBlock(String text, String... labels) {
        addBlock(text, labels).setIsContent(false);
        return this;
    }

    TextDocument build() {
        return new TextDocument(textBlocks);
    }

    static TextDocument fromPage(Element docElement) {
        BoilerpipeHTMLContentHandler htmlParser = new BoilerpipeHTMLContentHandler();
        htmlParser.startDocument();
        DomToSaxVisitor domToSaxVisitor = new DomToSaxVisitor(htmlParser);
        new DomWalker(domToSaxVisitor).walk(docElement);
        htmlParser.endDocument();
        return htmlParser.toTextDocument();
    }
}
