// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import com.google.gwt.dom.client.Element;

/**
 * ImageScorer that uses dimension ratio (width/length) as its heuristic.
 */
public class DimensionsRatioScorer extends BaseImageScorer {
    public final int maxScore;

    /**
     * Initialize this ImageScorer with appropriate info.
     * @param maximumScore The maximum score that should be given to any image.
     */
    public DimensionsRatioScorer(int maximumScore) {
        maxScore = maximumScore;
    }

    @Override
    protected int computeScore(Element e) {
        int height = e.getOffsetHeight();
        // For divide by 0 errors.
        if (height <= 0) return 0;

        int width = e.getOffsetWidth();
        float multiplier = 0.0f;
        // We are mainly interested in wide images.
        float ratio = (float) width / height;
        if (ratio > 1.45f && ratio < 1.8f) {
            multiplier = 1.0f;
        } else if (ratio > 1.3f && ratio < 2.2f) {
            multiplier = 0.4f;
        }
        return (int) (maxScore * multiplier);
    }

    @Override
    public int getMaxScore() {
        return maxScore;
    }
}
