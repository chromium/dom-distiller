// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/*
 * Parts of this file are adapted from Readability.
 *
 * Readability is Copyright (c) 2010 Src90 Inc
 * and licenced under the Apache License, Version 2.0.
 */

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

/**
 * This class gets the title for the distilled document, whose functionality is migrated from
 * readability.getArticleTitle() in chromium codebase's third_party/readability/js/readability.js.
 * It starts with the document's TITLE element and extracts parts of the text based on delimiters
 * '|', '-' or ':'.  If resulting title is too short or long, it uses the document's first H1
 * element.  If the resulting trimmed title is still too short, it reverts back to the original
 * title in the document's TITLE element.
 * We're not using boilerpipe's functionality because boilerpipe simply uses the document's TITLE
 * element as is, without massaging it at all.  First, BoilerpipeHTMLContentHandler gets the title
 * from the document's TITLE element.  DocumentTitleMatchClassifier checks for substrings of the
 * title in its TextBlock's, and marks a TextBlock with DefaultLabels.TITLE label if its text is
 * identical to one of the substrings.  ExpandTitleToContentFilter then uses these marked
 * TextBlock's to further mark more TextBlocks as content.  Lastly, BoilerplateBlockFilter makes
 * sure to block filtering of these TITLE-marked TextBlock's.
 */
public class DocumentTitleGetter {
    /**
     * @return The title of the distilled document.
     */
    public static String getDocumentTitle(String currTitle, Element root) {

        if (currTitle.isEmpty() && root != null) {  // Otherwise, use text of first TITLE element.
            NodeList<Element> titles = root.getElementsByTagName("TITLE");
            if (titles.getLength() > 0) {
              currTitle = titles.getItem(0).getInnerText();
            }
        }
        if (currTitle.isEmpty()) return "";

        String origTitle = currTitle;
        if (StringUtil.match(currTitle, " [\\|\\-] ")) {  // Title has '|' and/or '-'.
            // Get part before last '|' or '-'.
            currTitle = StringUtil.findAndReplace(origTitle, "(.*)[\\|\\-] .*", "$1");
            if (StringUtil.splitLength(currTitle, "\\s+") < 3) {  // Part has < 3 words.
                // Get part after first '|' or '-'.
                currTitle = StringUtil.findAndReplace(origTitle, "[^\\|\\-]*[\\|\\-](.*)", "$1");
            }
        } else if (currTitle.indexOf(": ") != -1) {  // Title has ':'.
            // Get part after last ':'.
            currTitle = StringUtil.findAndReplace(origTitle, ".*:(.*)", "$1");
            if (StringUtil.splitLength(currTitle, "\\s+") < 3) {  // Part has < 3 words.
              // Get part after first ':'.
              currTitle = StringUtil.findAndReplace(origTitle, "[^:]*[:](.*)", "$1");
            }
        } else if (root != null && (currTitle.length() > 150 || currTitle.length() < 15)) {
            // Get plain text from the only H1 element.
            // TODO(kuan): this is what readability does, but this block may make more sense as an
            // if rather than else-if, e.g. currently this else-if block is used when original title
            // is "foo" but not when it is "foo |" or "foo:".
            currTitle = findTheOnlyH1(root);
            if (currTitle == null) currTitle = origTitle;
        }

        currTitle = StringUtil.trim(currTitle);

        if (StringUtil.splitLength(currTitle, "\\s+") <= 4) currTitle = origTitle;

        return currTitle;
    }


    private static String findTheOnlyH1(Element root) {
        NodeList<Element> hOnes = root.getElementsByTagName("H1");
        return hOnes.getLength() == 1 ? hOnes.getItem(0).getInnerText() : null;
    }
}
