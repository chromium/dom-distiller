// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.imageheuristics.AreaScorer;
import org.chromium.distiller.imageheuristics.DimensionsRatioScorer;
import org.chromium.distiller.imageheuristics.DomDistanceScorer;
import org.chromium.distiller.imageheuristics.HasFigureScorer;
import org.chromium.distiller.imageheuristics.ImageScorer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to identify a header image for an article (sometimes known as a mast).
 * Each candidate image is scored based on several heuristics including:
 *    - Dom distance from first content node.
 *    - If an ancestor node of the image is the "figure" tag.
 *    - The ratio of width/height.
 *    - The area of the image (width * height) relative to its container.
 */
public class HeaderImageFinder {

    // The minimum score to be accepted when ranking candidate images.
    public static final int MINIMUM_ACCEPTED_SCORE = 25;

    private final List<ImageScorer> heuristics;

    /**
     * Default constructor creates a list of image heuristics to use.
     * @param firstContent The first node that boilerpipe marked as content.
     */
    public HeaderImageFinder(Node firstContent) {
        heuristics = new ArrayList<ImageScorer>();
        heuristics.add(new AreaScorer(25, 75000, 200000));
        heuristics.add(new DimensionsRatioScorer(25));
        heuristics.add(new DomDistanceScorer(25, firstContent));
        heuristics.add(new HasFigureScorer(15));
    }

    /**
     * Give a non-content image a relevancy score based on several heuristics; higher is better.
     * @param e The element to score.
     * @return An integer score for the image.
     */
    public int scoreNonContentImage(Element e) {
        int score = 0;
        if (e != null && "IMG".equals(e.getTagName())) {
            for (ImageScorer ir : heuristics) {
                score += ir.getImageScore(e);
            }
        }
        logFinalScore(score, e);
        return score;
    }

    private static void logFinalScore(int score, Node n) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        if (n != null) {
            LogUtil.logToConsole("FINAL SCORE: " + score + " : " + Element.as(n).getAttribute("src"));
        } else {
            LogUtil.logToConsole("Null image attempting to be scored!");
        }
    }
}
