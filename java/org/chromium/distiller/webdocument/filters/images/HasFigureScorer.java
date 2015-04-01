// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import org.chromium.distiller.DomUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.List;

/**
 * ImageScorer that scores based on if the image has a "figure" node as an ancestor.
 */
public class HasFigureScorer extends BaseImageScorer {
    public final int maxScore;

    /**
     * Initialize this ImageScorer with appropriate info.
     * @param maximumScore The maximum score that should be given to any image.
     */
    public HasFigureScorer(int maximumScore) {
        maxScore = maximumScore;
    }

    @Override
    protected int computeScore(Element e) {
        List<Node> parents = DomUtil.getParentNodes(e);
        for (Node n : parents) {
            if (n.getNodeType() == Node.ELEMENT_NODE
                    && "FIGURE".equals(Element.as(n).getTagName())) {
                return maxScore;
            }
        }
        return 0;
    }

    @Override
    public int getMaxScore() {
        return maxScore;
    }
}
