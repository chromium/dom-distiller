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
package org.chromium.distiller.document;

import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Describes a block of text.
 *
 * A block can be an "atomic" text node (i.e., a sequence of text that is not
 * interrupted by any HTML markup) or a compound of such atomic elements.
 *
 * @author Christian Kohlschütter
 */
public class TextBlock implements Cloneable {
    private final List<WebElement> webElements;
    private List<Integer> textIndexes;

    private boolean isContent;

    private String text;
    private Set<String> labels;

    private int numWords;
    private int numWordsInAnchorText;
    private float linkDensity;

    private int tagLevel;

    public TextBlock(List<WebElement> elements, int index) {
        webElements = elements;
        textIndexes = new ArrayList<Integer>();
        textIndexes.add(index);

        WebText wt = (WebText) webElements.get(index);

        // The labels are just used for text processing done on the TextDocument. So, the WebText
        // doesn't actually have any need for them. Instead of having to copy the set, this just
        // takes ownership of it from the underlying WebText.
        labels = wt.takeLabels();

        numWords = wt.getNumWords();
        numWordsInAnchorText = wt.getNumLinkedWords();
        tagLevel = wt.getTagLevel();
        text = wt.getText();

        initDensities();
    }

    public boolean isContent() {
        return isContent;
    }

    /**
     * Sets isContent.
     *
     * @return true if the isContent value changed.
     */
    public boolean setIsContent(boolean isContent) {
        if (isContent == this.isContent) {
            return false;
        }
        this.isContent = isContent;
        return true;
    }

    public String getText() {
        return text;
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumWordsInAnchorText() {
        return numWordsInAnchorText;
    }

    public float getLinkDensity() {
        return linkDensity;
    }

    public void mergeNext(final TextBlock other) {
        text += '\n';
        text += other.text;

        numWords += other.numWords;
        numWordsInAnchorText += other.numWordsInAnchorText;
        initDensities();

        isContent |= other.isContent;

        textIndexes.addAll(other.textIndexes);

        labels.addAll(other.labels);
        tagLevel = Math.min(tagLevel, other.tagLevel);
    }

    public int getOffsetBlocksStart() {
        return getFirstText().getOffsetBlock();
    }

    public int getOffsetBlocksEnd() {
        return getLastText().getOffsetBlock();
    }

    private String labelsDebugString() {
        return new TreeSet<String>(labels).toString();
    }

    @Override
    public String toString() {
        String s = "[";
        s += getOffsetBlocksStart() + "-" + getOffsetBlocksEnd() + ";";
        s += "tl=" + tagLevel + ";";
        s += "nw=" + numWords + ";";
        s += "ld=" + linkDensity + ";";
        s += "]\t";
        s += (isContent ? LogUtil.kGreen + "CONTENT" : LogUtil.kPurple + "boilerplate")
                + LogUtil.kReset + ",";
        s += LogUtil.kDarkGray + labelsDebugString() + LogUtil.kReset;
        s += "\n" + getText();
        return s;
    }

    public String debugString() {
        return toString();
    }

    /**
     * Adds an label to this {@link TextBlock}.
     */
    public void addLabel(final String label) {
        labels.add(label);
    }

    /**
     * Returns whether this TextBlock has the given label.
     */
    public boolean hasLabel(final String label) {
        return labels.contains(label);
    }

    /**
     * Removes a label if this TextBlock has the label.
     *
     * @return True if a label was removed.
     */
    public boolean removeLabel(final String label) {
        if (!labels.contains(label)) return false;
        labels.remove(label);
        return true;
    }

    /**
     * Returns the labels associated to this TextBlock, or <code>null</code> if no such labels
     * exist.
     *
     * NOTE: The returned instance is the one used directly in TextBlock. You have full access
     * to the data structure. However it is recommended to use the label-specific methods in {@link
     *TextBlock}
     * whenever possible.
     *
     * @return Returns the set of labels.
     */
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * Adds a set of labels to this {@link TextBlock}.
     *
     * @param l The labels to be added.
     */
    public void addLabels(final String... l) {
        for (String label : l) {
            labels.add(label);
        }
    }

    /**
     * @return the first non-whitespace Text node.
     */
    public Node getFirstNonWhitespaceTextNode() {
        return getFirstText().getFirstNonWhitespaceTextNode();
    }

    /**
     * @return the first non-whitespace Text node.
     */
    public Node getLastNonWhitespaceTextNode() {
        return getLastText().getLastNonWhitespaceTextNode();
    }

    public int getTagLevel() {
        return tagLevel;
    }

    public void setTagLevel(int tagLevel) {
        this.tagLevel = tagLevel;
    }

    public void applyToModel() {
        if (!isContent) return;
        for (Integer i : textIndexes) {
            WebText wt = (WebText) webElements.get(i);
            wt.setIsContent(true);
            if (hasLabel(DefaultLabels.TITLE)) {
                wt.addLabel(DefaultLabels.TITLE);
            }
        }
    }

    private void initDensities() {
        linkDensity = numWords == 0 ? 0 : numWordsInAnchorText / (float) numWords;
    }

    private WebText getFirstText() {
        return (WebText) webElements.get(textIndexes.get(0));
    }

    private WebText getLastText() {
        return (WebText) webElements.get(textIndexes.get(textIndexes.size() - 1));
    }
}
