// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;
import org.chromium.distiller.webdocument.filters.images.AreaScorer;
import org.chromium.distiller.webdocument.filters.images.DimensionsRatioScorer;
import org.chromium.distiller.webdocument.filters.images.DomDistanceScorer;
import org.chromium.distiller.webdocument.filters.images.HasFigureScorer;
import org.chromium.distiller.webdocument.filters.images.ImageScorer;
import org.chromium.distiller.webdocument.WebImage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to identify a lead image for an article (sometimes known as a mast).
 * Each candidate image is scored based on several heuristics including:
 *    - If an ancestor node of the image is the "figure" tag.
 *    - The ratio of width/height.
 *    - The area of the image (width * height) relative to its container.
 */
public class LeadImageFinder {
    // The minimum score to be accepted when ranking candidate images.
    public static final int MINIMUM_ACCEPTED_SCORE = 26;

    public static boolean process(WebDocument w) {
        List<WebImage> candidates = new ArrayList<>();
        WebText firstContent = null;

        // TODO(mdjones): WebDocument should have a separate list that point to all images in the
        // document.
        for (WebElement e : w.getElements()) {
            // If the element is an image and not already considered content:
            if (e instanceof WebImage) {
                if (!e.getIsContent()) {
                    candidates.add((WebImage) e);
                } else {
                    // If we hit an image that is content, we are no longer searching for a "lead"
                    // image.
                    break;
                }
            } else if (firstContent == null && e instanceof WebText && e.getIsContent()) {
                firstContent = (WebText) e;
            }
        }

        return findLeadImage(candidates, firstContent);
    }

    /**
     * Detect if an image is relevant to the article.
     * @param wi The WebImage to check.
     * @return Image score.
     */
    private static int getImageScore(WebImage wi, List<ImageScorer> heuristics) {
        if (wi == null || heuristics == null) return 0;
        int score = 0;
        Element e = wi.getImageNode();
        for (ImageScorer ir : heuristics) {
            score += ir.getImageScore(e);
        }
        logFinalScore(score, e);
        return score;
    }

    private static void logFinalScore(int score, Node n) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        if (n != null) {
            LogUtil.logToConsole(
                    "FINAL SCORE: " + score + " : " + Element.as(n).getAttribute("src"));
        } else {
            LogUtil.logToConsole("Null image attempting to be scored!");
        }
    }

    /**
     * Get a list of heuristics (ImageScorers) that will be used to score WebImages.
     * The returned list includes heuristics based on:
     *    - Dom distance from first content node.
     *    - If an ancestor node of the image is the "figure" tag.
     *    - The ratio of width/height.
     *    - The area of the image (width * height) relative to its container.
     * @param firstContent The first Element decided to be content.
     * @return List of ImageScorer objects.
     */
    private static List<ImageScorer> getLeadHeuristics(Element firstContent) {
        List<ImageScorer> heuristics = new ArrayList<>();
        heuristics.add(new AreaScorer(25, 75000, 200000));
        heuristics.add(new DimensionsRatioScorer(25));
        heuristics.add(new DomDistanceScorer(25, firstContent));
        heuristics.add(new HasFigureScorer(15));
        return heuristics;
    }

    /**
     * Find and mark a lead image as content if one exists.
     * @param candidates A list of non-content image candidates.
     * @param firstContent The first WebText marked as content.
     * @return True if an image was marked as content (a lead image was detected).
     */
    private static boolean findLeadImage(List<WebImage> candidates, WebText firstContent) {
        if (candidates.size() == 0) return false;

        Element contentElement = null;
        if (firstContent != null) {
            contentElement = Element.as(firstContent.getFirstNonWhitespaceTextNode());
        }
        List<ImageScorer> heuristics = getLeadHeuristics(contentElement);
        WebImage bestImage = null;
        int bestScore = 0;
        for (WebImage i : candidates) {
            int curScore = getImageScore(i, heuristics);
            if (MINIMUM_ACCEPTED_SCORE <= curScore) {
                if (bestImage == null || bestScore < curScore) {
                    bestImage = i;
                    bestScore = curScore;
                }
            }
        }
        if (bestImage == null) return false;

        bestImage.setIsContent(true);
        return true;
    }
}
