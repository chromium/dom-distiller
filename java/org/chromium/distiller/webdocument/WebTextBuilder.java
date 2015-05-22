// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.StringUtil;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.ArrayList;
import java.util.List;

public class WebTextBuilder {
    private String textBuffer = "";
    private int numWords;
    private int numAnchorWords;

    private int blockTagLevel = -1;
    private boolean inAnchor;

    private final List<Node> allTextNodes = new ArrayList<Node>();
    private int firstNode;
    private int firstNonWhitespaceNode = -1;
    private int lastNonWhitespaceNode;

    public void textNode(Text textNode, int tagLevel) {
        String text = textNode.getData();

        if (text.isEmpty()) {
            return;
        }

        textBuffer += text;
        allTextNodes.add(textNode);

        if (StringUtil.isStringAllWhitespace(text)) {
            return;
        }

        int thisWords = StringUtil.countWords(text);
        numWords += thisWords;
        if (inAnchor) {
            numAnchorWords += thisWords;
        }

        lastNonWhitespaceNode = allTextNodes.size() - 1;
        if (firstNonWhitespaceNode < firstNode) {
            firstNonWhitespaceNode = lastNonWhitespaceNode;
        }

        if (blockTagLevel == -1) {
            blockTagLevel = tagLevel;
        }
    }

    public void lineBreak(Node node) {
        textBuffer += "\n";
        allTextNodes.add(node);
    }

    public void reset() {
        textBuffer = "";
        numWords = 0;
        numAnchorWords = 0;
        firstNode = allTextNodes.size();
        blockTagLevel = -1;
    }

    public WebText build(int offsetBlock) {
        if (firstNode == allTextNodes.size()) {
            return null;
        }

        if (firstNonWhitespaceNode < firstNode) {
            reset();
            return null;
        }

        WebText tb = new WebText(textBuffer, allTextNodes, firstNode, allTextNodes.size(),
                firstNonWhitespaceNode, lastNonWhitespaceNode, numWords, numAnchorWords,
                blockTagLevel, offsetBlock);
        reset();
        return tb;
    }

    public void enterAnchor() {
        inAnchor = true;
        textBuffer += ' ';
    }

    public void exitAnchor() {
        inAnchor = false;
        textBuffer += ' ';
    }
}
