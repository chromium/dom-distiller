// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2010 Christian Kohlschütter
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

import com.google.gwt.dom.client.Element;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.labels.LabelAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines an action that is to be performed whenever a particular tag occurs during HTML parsing.
 *
 * @author Christian Kohlschütter
 */
public abstract class CommonTagActions {

    private CommonTagActions() {
    }

    public static final class Chained implements TagAction {

        private final TagAction t1;
        private final TagAction t2;

        public Chained(final TagAction t1, final TagAction t2) {
            this.t1 = t1;
            this.t2 = t2;
        }

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, Element e) {
            return t1.start(instance, e) | t2.start(instance, e);
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            return t1.end(instance) | t2.end(instance);
        }

        @Override
        public boolean changesTagLevel() {
            return t1.changesTagLevel() || t2.changesTagLevel();
        }
    }

    /**
     * Marks this tag as "ignorable", i.e. all its inner content is silently skipped.
     */
    public static final TagAction TA_IGNORABLE_ELEMENT = new TagAction() {

        @Override
        public boolean start(final BoilerpipeHTMLContentHandler instance, final Element e) {
            instance.inIgnorableElement++;
            return true;
        }

        @Override
        public boolean end(final BoilerpipeHTMLContentHandler instance) {
            instance.inIgnorableElement--;
            return true;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    };

    /**
     * Marks this tag as "anchor" (this should usually only be set for the <code>&lt;A&gt;</code> tag).
     * Anchor tags may not be nested.
     */
    public static final TagAction TA_ANCHOR_TEXT = new TagAction() {
        private boolean lastAnchorHadHref = false;

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, final Element e) {
            lastAnchorHadHref = false;
            if (instance.inIgnorableElement == 0  && e.hasAttribute("href")) {
                instance.addWhitespaceIfNecessary();
                instance.tokenBuffer
                    .append(BoilerpipeHTMLContentHandler.ANCHOR_TEXT_START);
                lastAnchorHadHref = true;
                instance.tokenBuffer.append(' ');
                instance.sbLastWasWhitespace = true;
            }
            return false;
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            if (instance.inIgnorableElement == 0 && lastAnchorHadHref) {
                instance.addWhitespaceIfNecessary();
                instance.tokenBuffer
                        .append(BoilerpipeHTMLContentHandler.ANCHOR_TEXT_END);
                instance.tokenBuffer.append(' ');
                instance.sbLastWasWhitespace = true;
            }
            return false;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    };

    /**
     * Marks this tag the body element (this should usually only be set for the <code>&lt;BODY&gt;</code> tag).
     */
    public static final TagAction TA_BODY = new TagAction() {
        @Override
        public boolean start(final BoilerpipeHTMLContentHandler instance, final Element e) {
            instance.flushBlock();
            instance.inBody++;
            return false;
        }

        @Override
        public boolean end(final BoilerpipeHTMLContentHandler instance) {
            instance.flushBlock();
            instance.inBody--;
            return false;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    };

    /**
     * Marks this tag a simple "inline" element, which neither generates whitespace, nor a new block.
     */
    public static final TagAction TA_INLINE_NO_WHITESPACE = new TagAction() {

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, final Element e) {
            return false;
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            return false;
        }

        @Override
        public boolean changesTagLevel() {
            return false;
        }
    };
    private static final Pattern PAT_FONT_SIZE = Pattern
            .compile("([\\+\\-]?)([0-9])");

    /**
     * Explicitly marks this tag a simple "block-level" element, which always generates whitespace
     */
    public static final TagAction TA_BLOCK_LEVEL = new TagAction() {

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, final Element e) {
            return true;
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            return true;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    };

    /**
     * Explicitly marks this tag an inline-block element, which does not generate whitespace.
     */
    public static final TagAction TA_INLINE_BLOCK_LEVEL = new TagAction() {

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, final Element e) {
            return false;
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            return false;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    };

    /**
     * Special TagAction for the <code>&lt;FONT&gt;</code> tag, which keeps track of the
     * absolute and relative font size.
     */
    public static final TagAction TA_FONT = new TagAction() {

        @Override
        public boolean start(final BoilerpipeHTMLContentHandler instance, final Element e) {
            if (e.hasAttribute("size")) {
                String sizeAttr = e.getAttribute("size");

                Matcher m = PAT_FONT_SIZE.matcher(sizeAttr);
                if (m.matches()) {
                    String rel = m.group(1);
                    final int val = Integer.parseInt(m.group(2));
                    final int size;
                    if (rel.length() == 0) {
                        // absolute
                        size = val;
                    } else {
                        // relative
                        int prevSize;
                        if (instance.fontSizeStack.isEmpty()) {
                            prevSize = 3;
                        } else {
                            prevSize = 3;
                            for (Integer s : instance.fontSizeStack) {
                                if (s != null) {
                                    prevSize = s;
                                    break;
                                }
                            }
                        }
                        if (rel.charAt(0) == '+') {
                            size = prevSize + val;
                        } else {
                            size = prevSize - val;
                        }

                    }
                    instance.fontSizeStack.add(0, size);
                } else {
                    instance.fontSizeStack.add(0, null);
                }
            } else {
                instance.fontSizeStack.add(0, null);
            }
            return false;
        }

        @Override
        public boolean end(final BoilerpipeHTMLContentHandler instance) {
            instance.fontSizeStack.removeFirst();
            return false;
        }

        @Override
        public boolean changesTagLevel() {
            return false;
        }
    };


    /**
     * {@link CommonTagActions} for block-level elements, which triggers some {@link LabelAction} on the generated
     * {@link TextBlock}.
     */
    public static final class BlockTagLabelAction implements TagAction {

        private final LabelAction action;

        public BlockTagLabelAction(final LabelAction action) {
            this.action = action;
        }

        @Override
        public boolean start(BoilerpipeHTMLContentHandler instance, final Element e) {
            instance.addLabelAction(action);
            return true;
        }

        @Override
        public boolean end(BoilerpipeHTMLContentHandler instance) {
            return true;
        }

        @Override
        public boolean changesTagLevel() {
            return true;
        }
    }
}
