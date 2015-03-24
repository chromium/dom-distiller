// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.document.TextBlock;

import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * The WebDocument is a simplified view of the underlying webpage. It contains the logical elements
 * (blocks of text, image + caption, video, etc).
 */
public class WebDocument {
    private final ArrayList<WebElement> elements;

    public WebDocument() {
        elements = new ArrayList<>();
    }

    public void addText(WebText text) {
        elements.add(text);
    }

    public void addTable(WebTable table) {
        elements.add(table);
    }

    public void addEmbed(WebEmbed embed) {
        elements.add(embed);
    }

    public List<WebElement> getElements() {
        return elements;
    }

    public List<Node> getContentNodes(boolean includeTitle) {
        List<Node> nodes = new ArrayList<Node>();
        for (WebElement e : elements) {
            if (e.getIsContent()) {
                e.addOutputNodes(nodes, includeTitle);
            }
        }
        return nodes;
    }

    public TextDocument createTextDocumentView() {
        ArrayList<TextBlock> textBlocks = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            WebElement e = elements.get(i);
            if (e instanceof WebText) {
                textBlocks.add(new TextBlock(elements, i));
            }
        }
        return new TextDocument(textBlocks);
    }
}
