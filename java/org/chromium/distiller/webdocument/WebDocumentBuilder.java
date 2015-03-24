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
package org.chromium.distiller.webdocument;

import org.chromium.distiller.ContentExtractor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Text;

import java.util.Stack;

/**
 * A simple WebDocument builder.
 */
public class WebDocumentBuilder implements WebDocumentBuilderInterface {
    private int tagLevel;
    private int nextWebTextIndex;

    private WebDocument document = new WebDocument();
    private boolean flush;
    private Stack<ElementAction> actionStack = new Stack<ElementAction>();
    private WebTextBuilder webTextBuilder = new WebTextBuilder();

    public WebDocumentBuilder() {
    }

    @Override
    public void skipElement(Element e) {
        flush = true;
    }

    @Override
    public void startElement(Element element) {
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
    public void endElement() {
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

        webTextBuilder.textNode(textNode, tagLevel);
    }

    @Override
    public void dataTable(Element e) {
        document.addTable(new WebTable(e));
    }

    @Override
    public void embed(WebEmbed embedNode) {
        document.addEmbed(embedNode);
    }

    private void enterAnchor() {
        webTextBuilder.enterAnchor();
    }

    private void exitAnchor() {
        webTextBuilder.exitAnchor();
    }

    public void flushBlock() {
        WebText tb = webTextBuilder.build(nextWebTextIndex);
        if (tb != null) {
            nextWebTextIndex++;
            addWebText(tb);
        }
    }

    protected void addWebText(final WebText tb) {
        for (ElementAction a : actionStack) {
            for (int i = 0; i < a.labels.length(); i++) {
                tb.addLabel(a.labels.get(i));
            }
        }
        document.addText(tb);
    }

    /**
     * Returns a {@link WebDocument} containing the extracted {@link WebText}
     * s. NOTE: Only call this after parsing.
     */
    public WebDocument toWebDocument() {
        // Just to be sure.
        flushBlock();
        return document;
    }

}
