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
package de.l3s.boilerpipe.document;

import java.util.LinkedList;
import java.util.List;

/**
 * A text document, consisting of one or more {@link TextBlock}s.
 *
 * @author Christian Kohlschütter
 */
public class TextDocument implements Cloneable {
    final List<TextBlock> textBlocks;
    List<String> candidateTitles;

    /**
     * Creates a new {@link TextDocument} with given {@link TextBlock}s, and no
     * title.
     *
     * @param textBlocks
     *            The text blocks of this document.
     */
    public TextDocument(final List<TextBlock> textBlocks) {
        this(null, textBlocks);
    }

    /**
     * Creates a new {@link TextDocument} with given {@link TextBlock}s and
     * given title.
     *
     * @param titles
     *            The possible titles for this text document.
     * @param textBlocks
     *            The text blocks of this document.
     */
    public TextDocument(final List<String> titles, final List<TextBlock> textBlocks) {
        this.candidateTitles = titles;
        this.textBlocks = textBlocks;
    }

    /**
     * Returns the {@link TextBlock}s of this document.
     *
     * @return A list of {@link TextBlock}s, in sequential order of appearance.
     */
    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    /**
     * Returns a list of candidate titles for this document, or <code>null</code> if no
     * such title has been identified. There can be multiple competing titles specified
     * in the document (e.g. document.title vs opengraph info)
     *
     * @return The list of possible titles.
     */
    public List<String> getCandidateTitles() {
        return candidateTitles;
    }

    /**
     * Sets the list of candidate titles.
     * @param candidateTitles
     */
    public void setCanddiateTitles(List<String> candidateTitles) {
        this.candidateTitles = new LinkedList<String>(candidateTitles);
    }
    /**
     * Returns the {@link TextDocument}'s content.
     *
     * @return The content text.
     */
    public String getContent() {
        return getText(true, false);
    }

    /**
     * Returns the {@link TextDocument}'s content, non-content or both
     *
     * @param includeContent Whether to include TextBlocks marked as "content".
     * @param includeNonContent Whether to include TextBlocks marked as "non-content".
     * @return The text.
     */
    public String getText(boolean includeContent, boolean includeNonContent) {
        StringBuilder sb = new StringBuilder();
        LOOP: for (TextBlock block : getTextBlocks()) {
            if(block.isContent()) {
                if(!includeContent) {
                    continue LOOP;
                }
            } else {
                if(!includeNonContent) {
                    continue LOOP;
                }
            }
            sb.append(block.getText());
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Returns detailed debugging information about the contained {@link TextBlock}s.
     *
     * @return Debug information.
     */
    public String debugString() {
        StringBuilder sb = new StringBuilder();
        for(TextBlock tb : getTextBlocks()) {
            sb.append(tb.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
