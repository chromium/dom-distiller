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

import org.chromium.distiller.LogUtil;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Describes a block of text.
 *
 * A block can be an "atomic" text element (i.e., a sequence of text that is not
 * interrupted by any HTML markup) or a compound of such atomic elements.
 *
 * @author Christian Kohlschütter
 */
public class TextBlock implements Cloneable {
    private boolean isContent = false;

    private StringBuilder text;
    private Set<String> labels;

    private int numWords;
    private int numWordsInAnchorText;
    private float linkDensity;

    private List<Node> allTextNodes;
    private int firstNonWhitespaceNode;
    private int lastNonWhitespaceNode;
    private ArrayList<Integer> nodeRanges;

    private int offsetBlocksStart;
    private int offsetBlocksEnd;

    private int tagLevel;

    public static final TextBlock EMPTY_START;
    public static final TextBlock EMPTY_END;
    static {
        EMPTY_START = new TextBlock("");
        EMPTY_END = new TextBlock("");
        EMPTY_START.offsetBlocksStart = EMPTY_START.offsetBlocksEnd = -1;
        EMPTY_END.offsetBlocksStart = EMPTY_END.offsetBlocksEnd = Integer.MAX_VALUE;
    }

    public TextBlock(final String text) {
        this(text, null, 0, 0, 0, 0, 0, 0, 0);
    }

    public TextBlock(final String text, final List<Node> allTextNodes, int firstWordNode,
            int lastWordNode, int firstNode, int lastNode, final int numWords,
            final int numWordsInAnchorText, final int offsetBlocks) {
        this.text = new StringBuilder(text);

        labels = new HashSet<>();

        this.numWords = numWords;
        this.numWordsInAnchorText = numWordsInAnchorText;

        this.allTextNodes = allTextNodes;
        firstNonWhitespaceNode = firstWordNode;
        lastNonWhitespaceNode = lastWordNode;

        nodeRanges = new ArrayList<>();
        nodeRanges.add(firstNode);
        nodeRanges.add(lastNode);

        this.offsetBlocksStart = offsetBlocks;
        this.offsetBlocksEnd = offsetBlocks;
        initDensities();
    }

    public boolean isContent() {
        return isContent;
    }

    public boolean setIsContent(boolean isContent) {
        if (isContent != this.isContent) {
            this.isContent = isContent;
            return true;
        } else {
            return false;
        }
    }

    public String getText() {
        return text.toString();
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
        text.append('\n');
        text.append(other.text);

        numWords += other.numWords;
        numWordsInAnchorText += other.numWordsInAnchorText;

        offsetBlocksEnd = other.offsetBlocksEnd;

        initDensities();

        this.isContent |= other.isContent;

        nodeRanges.addAll(other.nodeRanges);

        lastNonWhitespaceNode = other.lastNonWhitespaceNode;

        labels.addAll(other.labels);

        tagLevel = Math.min(tagLevel, other.tagLevel);
    }

    private void initDensities() {
        linkDensity = numWords == 0 ? 0 : numWordsInAnchorText / (float) numWords;
    }

    public int getOffsetBlocksStart() {
        return offsetBlocksStart;
    }
    public int getOffsetBlocksEnd() {
        return offsetBlocksEnd;
    }

    // Do the formatting directly so that it is the same in prod/dev.
    private String linkDensityDebugString() {
        if (Math.abs(Math.round(linkDensity) - linkDensity) < 0.0001) {
            return Long.toString(Math.round(linkDensity)) + ".0";
        }
        double rounded = Math.round(linkDensity * 1000000) / 1000000.0;
        return Double.toString(rounded);
    }

    // The labels are in sorted order so that the order is the same in prod/dev.
    private String labelsDebugString() {
        return new TreeSet<String>(labels).toString();
    }

    @Override
    public String toString() {
        return "[" + offsetBlocksStart + "-" + offsetBlocksEnd + ";tl="+tagLevel+"; nw="+numWords+";ld="+linkDensityDebugString()+"]\t" +
            (isContent ? LogUtil.kGreen + "CONTENT" : LogUtil.kPurple + "boilerplate") + LogUtil.kReset +
            "," + LogUtil.kDarkGray + labelsDebugString() + LogUtil.kReset + "\n" + getText();
    }


    /**
     * Adds an arbitrary String label to this {@link TextBlock}.
     *
     * @param label The label
     * @see DefaultLabels
     */
    public void addLabel(final String label) {
        labels.add(label);
    }

    /**
     * Checks whether this TextBlock has the given label.
     *
     * @param label The label
     * @return <code>true</code> if this block is marked by the given label.
     */
    public boolean hasLabel(final String label) {
        return labels.contains(label);
    }

    public boolean removeLabel(final String label) {
        return labels.remove(label);
    }

    /**
     * Returns the labels associated to this TextBlock, or <code>null</code> if no such labels
     * exist.
     *
     * NOTE: The returned instance is the one used directly in TextBlock. You have full access
     * to the data structure. However it is recommended to use the label-specific methods in {@link TextBlock}
     * whenever possible.
     *
     * @return Returns the set of labels, or <code>null</code> if no labels was added yet.
     */
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * Adds a set of labels to this {@link TextBlock}.
     * <code>null</code>-references are silently ignored.
     *
     * @param l The labels to be added.
     */
    public void addLabels(final Set<String> l) {
        if(l == null) {
            return;
        }
        this.labels.addAll(l);
    }

    /**
     * Adds a set of labels to this {@link TextBlock}.
     * <code>null</code>-references are silently ignored.
     *
     * @param l The labels to be added.
     */
    public void addLabels(final String... l) {
        if(l == null) {
            return;
        }
        for(final String label : l) {
            this.labels.add(label);
        }
    }

    /**
     * @return a list of all Text nodes (including whitespace-only ones), or <code>null</code>.
     */
    public List<Node> getAllTextNodes() {
        List<Node> res = new ArrayList<Node>();
        for (int i = 0; i < nodeRanges.size(); i += 2) {
            res.addAll(allTextNodes.subList(nodeRanges.get(i), nodeRanges.get(i + 1)));
        }
        return res;
    }

    /**
     * @return the first non-whitespace Text node, or <code>null</code>.
     */
    public Node getFirstNonWhitespaceTextNode() {
        return allTextNodes.get(firstNonWhitespaceNode);
    }

    /**
     * @return the first non-whitespace Text node, or <code>null</code>.
     */
    public Node getLastNonWhitespaceTextNode() {
        return allTextNodes.get(lastNonWhitespaceNode);
    }

    public int getTagLevel() {
        return tagLevel;
    }

    public void setTagLevel(int tagLevel) {
        this.tagLevel = tagLevel;
    }
}
