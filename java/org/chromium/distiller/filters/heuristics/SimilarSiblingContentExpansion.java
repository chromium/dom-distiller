// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.filters.heuristics;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.JavaScript;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Marks "siblings" of content as content if they are "similar" enough.
 *
 * This calculates "siblings" by finding a "canonical" DOM node for each TextBlock. This node is the
 * highest ancestor of the TextBlock's first contained node that does not contain (in its subtree)
 * the last node of the previous TextBlock or the first node of the next TextBlock.
 *
 * If a content block and a non-content block are siblings and are "similar" enough, then the
 * non-content block is marked as content. The "similarity" test is configurable in various ways.
 */
public final class SimilarSiblingContentExpansion implements BoilerpipeFilter {

    public static class Builder {
        private boolean mAllowCrossTitles;
        private boolean mAllowCrossHeadings;
        private boolean mAllowMixedTags;
        private double mMaxLinkDensity;
        private int mMaxBlockDistance;

        public Builder() {
            mAllowCrossTitles = false;
            mAllowCrossHeadings = false;
            mAllowMixedTags = false;
            mMaxLinkDensity = 0.0;
            mMaxBlockDistance = 0;
        }

        public Builder allowCrossTitles() {
            mAllowCrossTitles = true;
            return this;
        }

        public Builder allowCrossHeadings() {
            mAllowCrossHeadings = true;
            return this;
        }

        public Builder allowMixedTags() {
            mAllowMixedTags = true;
            return this;
        }

        public Builder maxLinkDensity(double density) {
            mMaxLinkDensity = density;
            return this;
        }

        public Builder maxBlockDistance(int distance) {
            mMaxBlockDistance = distance;
            return this;
        }

        public SimilarSiblingContentExpansion build() {
            return new SimilarSiblingContentExpansion(
                    mAllowCrossTitles,
                    mAllowCrossHeadings,
                    mAllowMixedTags,
                    mMaxLinkDensity,
                    mMaxBlockDistance);
        }
    }

    private final boolean allowCrossTitles;
    private final boolean allowCrossHeadings;
    private final boolean allowMixedTags;
    private final double maxLinkDensity;
    private final int maxBlockDistance;

    private List<Node> canonicalReps;
    private List<TextBlock> textBlocks;

    private SimilarSiblingContentExpansion(
            boolean allowCrossTitles,
            boolean allowCrossHeadings,
            boolean allowMixedTags,
            double maxLinkDensity,
            int maxBlockDistance) {
        this.allowCrossTitles = allowCrossTitles;
        this.allowCrossHeadings = allowCrossHeadings;
        this.allowMixedTags = allowMixedTags;
        this.maxLinkDensity = maxLinkDensity;
        this.maxBlockDistance = maxBlockDistance;
    }

    @Override
    public boolean process(TextDocument doc) {
        textBlocks = doc.getTextBlocks();
        if (textBlocks.size() < 2) {
            return false;
        }

        Node docNode = Document.get().getDocumentElement();
        canonicalReps = findCanonicalReps(textBlocks, docNode);

        // After processing a block, it will be added to either the list of good or the list of bad
        // blocks. The good list contains blocks that are content, and bad contains non-content.
        // The range [goodBegin, goodEnd) is a set of blocks that could be potential siblings (and
        // similar for bad).
        int[] good = new int[textBlocks.size()];
        int goodBegin = 0, goodEnd = 0;
        int[] bad = new int[textBlocks.size()];
        int badBegin = 0, badEnd = 0;

        boolean changes = false;
        for (int i = 0; i < textBlocks.size(); i++) {
            if ((!allowCrossTitles && textBlocks.get(i).hasLabel(DefaultLabels.TITLE)) ||
                    (!allowCrossHeadings && textBlocks.get(i).hasLabel(DefaultLabels.HEADING))) {
                // Clear the sets of potential siblings (since expansion is not allowed to cross
                // this block).
                goodBegin = goodEnd;
                badBegin = badEnd;
                continue;
            }

            if (allowExpandFrom(i)) {
                good[goodEnd++] = i;
                // Check the potential bad siblings and set any matches to content.
                for (int j = badBegin; j < badEnd; j++) {
                    int b = bad[j];
                    if (i - b > maxBlockDistance) {
                        if (j == badBegin) {
                            // "i - bad[badBegin] > maxBlockDistance" for all the following blocks.
                            badBegin++;
                        }
                        continue;
                    }
                    if (isSimilarIndex(i, b)) {
                        changes = true;
                        textBlocks.get(b).setIsContent(true);
                        // Remove bad[j] from the "bad" potential set. There is no need to add it to
                        // the good set since any further sibling of it will also be a sibling of
                        // the current block which has already been added to the list.
                        bad[j] = bad[badBegin++];
                    }
                }
            } else if (allowExpandTo(i)) {
                int j;
                // Check the potential good siblings. If there's a match, this block becomes
                // content.
                for (j = goodBegin; j < goodEnd; j++) {
                    int g = good[j];
                    if (i - g > maxBlockDistance) {
                        if (j == goodBegin) {
                            // "i - good[goodBegin] > maxBlockDistance" for all the following
                            // blocks.
                            goodBegin++;
                        }
                        continue;
                    }
                    if (isSimilarIndex(i, g)) {
                        changes = true;
                        textBlocks.get(i).setIsContent(true);
                        // Remove good[j] from the potential set. This can be done because any
                        // sibling of it will also be a sibling of the current block, and the
                        // current block will be added to the potential set below.
                        good[j] = good[goodBegin++];
                        break;
                    }
                }
                if (j == goodEnd) {
                    bad[badEnd++] = i;
                } else {
                    good[goodEnd++] = i;
                }
            }
        }

        return changes;
    }

    private boolean allowExpandFrom(int i) {
        return textBlocks.get(i).isContent()
                && !textBlocks.get(i).hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)
                && !textBlocks.get(i).hasLabel(DefaultLabels.TITLE);
    }

    private boolean allowExpandTo(int i) {
        return textBlocks.get(i).getLinkDensity() <= maxLinkDensity
                && !textBlocks.get(i).isContent()
                && !textBlocks.get(i).hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)
                && !textBlocks.get(i).hasLabel(DefaultLabels.TITLE);
    }

    private boolean isSimilarIndex(int i, int j) {
        Node leftNode = canonicalReps.get(i), rightNode = canonicalReps.get(j);
        if (!allowMixedTags && !areSameTag(leftNode, rightNode)) return false;
        return leftNode.getParentNode().equals(rightNode.getParentNode());
    }

    /**
     * Finds the "canonical" nodes for each TextBlock.
     *
     * A canonical node is the highest ancestor of the TextBlock's first non-whitespace text
     * element, that is not also an ancestor of the previous TextBlock's last text element or the
     * next TextBlock's first text element.
     */
    private static List<Node> findCanonicalReps(List<TextBlock> textBlocks, Node docNode) {
        List<Node> reps = new ArrayList<Node>();
        for (int i = 0; i < textBlocks.size(); ++i) {
            Node nextNode = i + 1 == textBlocks.size()
                    ? docNode
                    : textBlocks.get(i + 1).getFirstNonWhitespaceTextNode();
            Node prevNode = i == 0 ? docNode : textBlocks.get(i - 1).getLastNonWhitespaceTextNode();
            Node currNode = textBlocks.get(i).getFirstNonWhitespaceTextNode();

            // Find the highest ancestor of currNode that is not also an ancestor of one of prevNode
            // or nextNode;
            Node currParent = currNode.getParentNode();
            while (!JavaScript.contains(currParent, prevNode)
                    && !JavaScript.contains(currParent, nextNode)) {
                currNode = currParent;
                currParent = currNode.getParentNode();
            }
            reps.add(currNode);
        }
        return reps;
    }

    private static boolean areSameTag(Node left, Node right) {
        if (left.getNodeType() != right.getNodeType()) return false;
        if (left.getNodeType() != Node.ELEMENT_NODE) return true;
        return left.getNodeName().equals(right.getNodeName());
    }

}
