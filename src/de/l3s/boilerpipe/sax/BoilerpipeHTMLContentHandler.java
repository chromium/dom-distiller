// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.sax;

import com.dom_distiller.client.DomUtil;
import com.dom_distiller.client.StringUtil;
import com.dom_distiller.client.sax.ContentHandler;
import com.dom_distiller.client.util.TextBlockBuilder;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * A simple SAX {@link ContentHandler}, used by {@link com.dom_distiller.client.ContentExtractor}.
 * Can be used by different parser implementations, e.g. NekoHTML and TagSoup.
 *
 * @author Christian Kohlschütter
 */
public class BoilerpipeHTMLContentHandler implements ContentHandler {
    int tagLevel = 0;

    private final List<TextBlock> textBlocks = new ArrayList<TextBlock>();
    private boolean flush = false;
    Stack<ElementAction> actionStack = new Stack<ElementAction>();
    private TextBlockBuilder textBlockBuilder = new TextBlockBuilder();

    /**
     */
    public BoilerpipeHTMLContentHandler() {
    }

    @Override
    public void endDocument() {
        flushBlock();
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void skipElement(Element e) {
        flush = true;
    }

    @Override
    public void startElement(Element element) {
        String tagName = element.getTagName().toUpperCase();

        ElementAction a = ElementAction.getForElement(element);
        actionStack.push(a);

        if (a.changesTagLevel) {
            tagLevel++;
        }

        if (a.isAnchor) {
            enterAnchor();
        }

        flush |= a.flush;
    }

    @Override
    public void endElement(Element element) {
        ElementAction a = actionStack.peek();

        if (a.changesTagLevel) {
            tagLevel--;
        }

        if (flush || a.flush) {
            flushBlock();
        }

        if (a.isAnchor) {
            exitAnchor();
        }

        // Must be done after flushBlock() because the labels for the block come from the
        // actionStack.
        actionStack.pop();
    }

    @Override
    public void textNode(Text textNode) {
        if (flush) {
            flushBlock();
            flush = false;
        }

        textBlockBuilder.textNode(textNode, tagLevel);
    }

    private void enterAnchor() {
        textBlockBuilder.enterAnchor();
    }

    private void exitAnchor() {
        textBlockBuilder.exitAnchor();
    }

    List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void flushBlock() {
        TextBlock tb = textBlockBuilder.build(textBlocks.size());
        if (tb != null) {
            addTextBlock(tb);
        }
    }

    protected void addTextBlock(final TextBlock tb) {
        for (ElementAction a : actionStack) {
            for (int i = 0; i < a.labels.length(); i++) {
                tb.addLabel(a.labels.get(i));
            }
        }
        textBlocks.add(tb);
    }

    /**
     * Returns a {@link TextDocument} containing the extracted {@link TextBlock}
     * s. NOTE: Only call this after parsing.
     *
     * @return The {@link TextDocument}
     */
    public TextDocument toTextDocument() {
        // just to be sure
        flushBlock();
        // TODO(yfriedman): When BoilerpipeHTMLContentHandler is finished being moved to
        // DomToSaxVisitor, we should be able to set Title directly.
        return new TextDocument(null, getTextBlocks());
    }

}
