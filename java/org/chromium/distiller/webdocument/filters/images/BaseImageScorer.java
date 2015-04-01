// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters.images;

import org.chromium.distiller.LogUtil;

import com.google.gwt.dom.client.Element;

/**
 * Base class for an image scorer that handles logging, null checks, and other operations not
 * directly related to score computation.
 */
public abstract class BaseImageScorer implements ImageScorer {
    @Override
    public int getImageScore(Element e) {
        int score = 0;
        if (e != null) {
            score = computeScore(e);
        }
        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole(getClass().getSimpleName() + ": " + score + "/" + getMaxScore());
        }
        return Math.min(score, getMaxScore());
    }

    @Override public abstract int getMaxScore();

    /**
     * Do the actual computation of the score. This method is never called if the Element provided
     * to getImageScore is null.
     * @param e The image element to score.
     * @return An integer score for the provided image element.
     */
    protected abstract int computeScore(Element e);
}
