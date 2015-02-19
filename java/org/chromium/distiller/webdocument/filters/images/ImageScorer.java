// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import com.google.gwt.dom.client.Element;

/**
 * This interface is used to represent a single heuristic used in image extraction. The
 * provided image will be given a score based on the heuristic and a max score.
 */
public interface ImageScorer {
    /**
     * Give a particular image a score based on the heuristic implemented in this ImageScorer and
     * what the max score is set to.
     * @param e The element to score.
     * @return An integer score for the image.
     */
    public int getImageScore(Element e);

    /**
     * Get the maximum possible score that this ImageScorer can return.
     * @return The max score for this ImageScorer.
     */
    public int getMaxScore();
}
