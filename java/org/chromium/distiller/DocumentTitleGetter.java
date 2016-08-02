// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/*
 * Parts of this file are adapted from Readability.
 *
 * Readability is Copyright (c) 2010 Src90 Inc
 * and licensed under the Apache License, Version 2.0.
 */

package org.chromium.distiller;

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
    public static String getDocumentTitle(Object objTitle, Element root) {
        String currTitle = "", origTitle = "";

        if (objTitle.getClass() == currTitle.getClass()) {  // If objTitle is of String type.
            currTitle = origTitle = objTitle.toString();
        } else if (root != null) {  // Otherwise, use text of first TITLE element.
            NodeList<Element> titles = root.getElementsByTagName("TITLE");
            if (titles.getLength() > 0) {
              // Use javascript textContent instead of javascript innerText; the latter only returns
              // visible text, but <title> tags are invisible.
              currTitle = origTitle = DomUtil.javascriptTextContent(titles.getItem(0));
            }
        }
        if (currTitle.isEmpty()) return "";

        if (StringUtil.match(currTitle, " [\\|\\-] ")) {  // Title has '|' and/or '-'.
            // Get part before last '|' or '-'.
            currTitle = StringUtil.findAndReplace(origTitle, "(.*)[\\|\\-] .*", "$1");
            if (StringUtil.countWords(currTitle) < 3) {  // Part has < 3 words.
                // Get part after first '|' or '-'.
                currTitle = StringUtil.findAndReplace(origTitle, "[^\\|\\-]*[\\|\\-](.*)", "$1");
            }
        } else if (currTitle.indexOf(": ") != -1) {  // Title has ':'.
            // Get part after last ':'.
            currTitle = StringUtil.findAndReplace(origTitle, ".*:(.*)", "$1");
            if (StringUtil.countWords(currTitle) < 3) {  // Part has < 3 words.
              // Get part after first ':'.
              currTitle = StringUtil.findAndReplace(origTitle, "[^:]*[:](.*)", "$1");
            }
        } else if (root != null && (currTitle.length() > 150 || currTitle.length() < 15)) {
            // Get plain text from the only H1 element.
            // TODO(kuan): this is what readability does, but this block may make more sense as an
            // if rather than else-if, e.g. currently this else-if block is used when original title
            // is "foo" but not when it is "foo |" or "foo:".
            currTitle = findFirstH1(root);
            if (currTitle.isEmpty()) currTitle = origTitle;
        }

        currTitle = StringUtil.jsTrim(currTitle);

        if (StringUtil.countWords(currTitle) <= 4) currTitle = origTitle;

        return currTitle;
    }


    private static String findFirstH1(Element root) {
        NodeList<Element> hOnes = root.getElementsByTagName("H1");
        // Use javascript innerText instead of javascript textContent; the former only returns
        // visible text, and we assume visible H1's are more inclined to being potential titles.
        String h1 = "";
        for (int i = 0; i < hOnes.getLength() && h1.isEmpty(); i++) {
            h1 = DomUtil.getInnerText(hOnes.getItem(i));
        }
        return h1;
    }
}
