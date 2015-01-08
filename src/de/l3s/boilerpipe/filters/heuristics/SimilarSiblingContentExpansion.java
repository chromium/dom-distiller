// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package de.l3s.boilerpipe.filters.heuristics;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;

import com.dom_distiller.client.JavaScript;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Document;

import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;

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

        boolean changes = false;
        for (int i = 0; i < textBlocks.size(); i++) {
            for (int j = i + 1; j < Math.min(textBlocks.size(), i + maxBlockDistance + 1); j++) {
                if (!allowCrossTitles && textBlocks.get(j).hasLabel(DefaultLabels.TITLE)) break;
                if (!allowCrossHeadings && textBlocks.get(j).hasLabel(DefaultLabels.HEADING)) break;
                if (shouldExpandForward(i, j)) {
                    changes = true;
                    textBlocks.get(j).setIsContent(true);
                }
                if (shouldExpandBackward(i, j)) {
                    changes = true;
                    textBlocks.get(i).setIsContent(true);
                }
            }
        }
        return changes;
    }

    private boolean shouldExpandForward(int left, int right) {
        return allowExpandBetween(left, right)
                && allowExpandFrom(left)
                && allowExpandTo(right);
    }

    private boolean shouldExpandBackward(int left, int right) {
        return allowExpandBetween(left, right)
                && allowExpandFrom(right)
                && allowExpandTo(left);
    }

    private boolean allowExpandBetween(int left, int right) {
        return validIndex(left) && validIndex(right)
                && (Math.abs(left - right) <= maxBlockDistance)
                && isSimilarIndex(left, right);
    }

    private boolean allowExpandFrom(int i) {
        return textBlocks.get(i).isContent()
                && !textBlocks.get(i).hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)
                && !textBlocks.get(i).hasLabel(DefaultLabels.TITLE);
    }

    private boolean validIndex(int i) {
        return 0 <= i && i < textBlocks.size();
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
        ArrayList<Node> reps = new ArrayList<Node>(textBlocks.size());
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
