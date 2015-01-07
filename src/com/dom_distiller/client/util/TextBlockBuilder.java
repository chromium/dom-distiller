// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.util;

import com.dom_distiller.client.StringUtil;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextBlockBuilder {
    private final StringBuilder textBuffer = new StringBuilder();
    private int numWords = 0;
    private int numAnchorWords = 0;

    private int blockTagLevel = -1;
    private boolean inAnchor = false;

    private final List<Node> nonWhitespaceTextElements = new LinkedList<Node>();
    private final List<Node> allTextElements = new LinkedList<Node>();

    public void textNode(Text textNode, int tagLevel) {
        String text = textNode.getData();

        if (text.isEmpty()) {
            return;
        }

        textBuffer.append(text);
        allTextElements.add(textNode);

        if (StringUtil.isStringAllWhitespace(text)) {
            return;
        }

        int thisWords = StringUtil.countWords(text);
        numWords += thisWords;
        if (inAnchor) {
            numAnchorWords += thisWords;
        }

        if (blockTagLevel == -1) {
            blockTagLevel = tagLevel;
        }

        nonWhitespaceTextElements.add(textNode);
    }

    public void reset() {
        textBuffer.setLength(0);
        numWords = 0;
        numAnchorWords = 0;
        nonWhitespaceTextElements.clear();
        allTextElements.clear();
        blockTagLevel = -1;
    }

    public TextBlock build(int offsetBlocks) {
        String text = textBuffer.toString();
        if (text.length() == 0) {
            return null;
        }

        if (nonWhitespaceTextElements.size() == 0) {
            reset();
            return null;
        }

        TextBlock tb = new TextBlock(text,
                nonWhitespaceTextElements, allTextElements,
                numWords, numAnchorWords,
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
