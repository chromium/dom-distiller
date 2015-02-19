// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import org.chromium.distiller.DomUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * ImageScorer that uses DOM distance as its heuristic.
 */
public class DomDistanceScorer extends BaseImageScorer {
    public final Node firstContentNode;
    public final int maxScore;

    /**
     * Initialize this ImageScorer with appropriate info.
     * @param maximumScore The maximum score that should be given to any image.
     * @param firstContent The first content node as identified by Boilerpipe.
     */
    public DomDistanceScorer(int maximumScore, Node firstContent) {
        maxScore = maximumScore;
        firstContentNode = firstContent;
    }

    @Override
    protected int computeScore(Element e) {
        if (firstContentNode == null) return 0;

        int depthDiff = DomUtil.getNodeDepth(firstContentNode)
                - DomUtil.getNodeDepth(DomUtil.getNearestCommonAncestor(firstContentNode, e));
        float multiplier = 0.0f;
        if (depthDiff < 4) {
            multiplier = 1.0f;
        } else if (depthDiff < 6) {
            multiplier = 0.6f;
        } else if (depthDiff < 8) {
            multiplier = 0.2f;
        }
        return (int) (maxScore * multiplier);
    }

    @Override
    public int getMaxScore() {
        return maxScore;
    }
}
