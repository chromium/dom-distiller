// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.util;

import org.chromium.distiller.StringUtil;
import org.chromium.distiller.document.TextBlock;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.ArrayList;
import java.util.List;

public class TextBlockBuilder {
    private final StringBuilder textBuffer = new StringBuilder();
    private int numWords = 0;
    private int numAnchorWords = 0;

    private int blockTagLevel = -1;
    private boolean inAnchor = false;

    private final List<Node> allTextNodes = new ArrayList<Node>();
    private int firstNode = 0;
    private int firstNonWhitespaceNode = -1;
    private int lastNonWhitespaceNode = 0;

    public void textNode(Text textNode, int tagLevel) {
        String text = textNode.getData();

        if (text.isEmpty()) {
            return;
        }

        textBuffer.append(text);
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

    public void reset() {
        textBuffer.setLength(0);
        numWords = 0;
        numAnchorWords = 0;
        firstNode = allTextNodes.size();
        blockTagLevel = -1;
    }

    public TextBlock build(int offsetBlocks) {
        if (firstNode == allTextNodes.size()) {
            return null;
        }

        if (firstNonWhitespaceNode < firstNode) {
            reset();
            return null;
        }

        TextBlock tb = new TextBlock(textBuffer.toString(), allTextNodes, firstNonWhitespaceNode,
                lastNonWhitespaceNode, firstNode, allTextNodes.size(), numWords, numAnchorWords,
                offsetBlocks);
        tb.setTagLevel(blockTagLevel);
        reset();
        return tb;
    }

    public void enterAnchor() {
        inAnchor = true;
        textBuffer.append(' ');
    }

    public void exitAnchor() {
        inAnchor = false;
        textBuffer.append(' ');
    }
}
