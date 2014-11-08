// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;

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

    /**
     * Give a non-content image a relevancy score based on several heuristics; higher is better.
     * @param e The element to score.
     * @param firstContent The first node that boilerpipe marked as content.
     * @return An integer score for the image.
     */
    public static int scoreNonContentImage(Element e, Node firstContent) {
        int score = 0;
        if (e != null && "IMG".equals(e.getTagName())) {
            score += rankHasFigureAncestor(e);
            score += rankImageArea(e);
            score += rankWidthHeightRatio(e);
            score += rankDistanceToContent(e, firstContent);
        }
        logFinalScore(score,e);
        return score;
    }

    /**
     * @return Points if the image has a "figure" tag as an ancestor.
     */
    private static int rankHasFigureAncestor(final Element e) {
        List<Node> parents = DomUtil.getParentNodes(e);
        int score = 0;
        for (Node n : parents) {
            if (n.getNodeType() == Node.ELEMENT_NODE &&
                    "FIGURE".equals(Element.as(n).getTagName())) {
                score = 15;
                break;
            }
        }
        logScoreComponent("Figure ancestor", score);
        return score;
    }

    /**
     * @return Points based on the area/resolution of the image; larger = more points.
     */
    private static int rankImageArea(final Element e) {
        int maxScore = 25;
        int minArea = 75000;
        int maxArea = 200000;

        int area = e.getOffsetWidth() * e.getOffsetHeight();
        int score = 0;

        if (area >= minArea) {
            score = (int)((float)(area-minArea)/(maxArea-minArea) * maxScore);
        }

        if (score > maxScore) {
            score = maxScore;
        }

        logScoreComponent("Image area", score);
        return score;

    }

    /**
     * @return Points based on the width/height of the image.
     */
    private static int rankWidthHeightRatio(final Element e) {
        int width = e.getOffsetWidth();
        int height = e.getOffsetHeight();
        int score = 0;
        // For divide by 0 errors.
        if (height > 0) {
            // We are mainly interested in wide images.
            float ratio = (float)width/height;
            if (ratio > 1.45f && ratio < 1.8f) {
                score = 25;
            } else if (ratio > 1.3f && ratio < 2.2f) {
                score = 10;
            }
        }
        logScoreComponent("Image ratio", score);
        return score;
    }

    /**
     * @return Points based on how far away from the main content the image is.
     */
    private static int rankDistanceToContent(final Node e, final Node firstContentNode) {
        int score = 0;
        if (firstContentNode != null && e != null) {
            int depthDiff = DomUtil.getNodeDepth(firstContentNode) -
                    DomUtil.getNodeDepth(DomUtil.getNearestCommonAncestor(firstContentNode,e));
            if (depthDiff < 4) {
                score = 25;
            } else if (depthDiff < 6) {
                score = 15;
            } else if (depthDiff < 8) {
                score = 5;
            }
        }
        logScoreComponent("DOM distance", score);
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

    private static void logScoreComponent(String heuristicName, int score) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        LogUtil.logToConsole(heuristicName + ": \t" + score);
    }
}
