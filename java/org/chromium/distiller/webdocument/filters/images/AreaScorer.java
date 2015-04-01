// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import com.google.gwt.dom.client.Element;

/**
 * ImageScorer that uses image area (length*width) as its heuristic.
 */
public class AreaScorer extends BaseImageScorer {
    public final int maxScore;
    public final int minArea;
    public final int maxArea;

    /**
     * Initialize this ImageScorer with appropriate info.
     * @param maximumScore The maximum score that should be given to any image.
     * @param minimumArea The smallest area in px an image can consume and still be scored.
     * @param maximumArea The largest area in px an image can consume and still be scored.
     */
    public AreaScorer(int maximumScore, int minimumArea, int maximumArea) {
        maxScore = maximumScore;
        minArea = minimumArea;
        maxArea = maximumArea;
    }

    @Override
    protected int computeScore(Element e) {
        int area = e.getOffsetWidth() * e.getOffsetHeight();
        if (area < minArea) return 0;

        int score = (int) ((float) (area - minArea) / (maxArea - minArea) * maxScore);
        return Math.min(score, maxScore);
    }

    @Override
    public int getMaxScore() {
        return maxScore;
    }
}
