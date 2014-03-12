// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

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
@Export()
public class DocumentTitleGetter implements Exportable {
    /**
     * @return The title of the distilled document.
     */
    public static String getDocumentTitle(Object objTitle, Element root) {
        String currTitle  = "", origTitle = "";

        if (objTitle.getClass() == currTitle.getClass()) {  // If objTitle is of String type.
            currTitle = origTitle = objTitle.toString();
        } else if (root != null) {  // Otherwise, use text of first TITLE element.
            NodeList<Element> titles = root.getElementsByTagName("TITLE");
            if (titles.getLength() > 0)
              currTitle = origTitle = titles.getItem(0).getInnerText();
        }
        if (currTitle == "")
          return "";
        
        Pattern pattern = Pattern.compile(" [\\|\\-] ");
        Matcher matcher = pattern.matcher(currTitle);
        if (matcher.find()) {  // Title has '|' and/or '-'.
            // Get part before last '|' or '-'.
            currTitle = findAndReplace(origTitle, "(.*)[\\|\\-] .*", "$1");
            if (splitLength(currTitle, "\\s+") < 3) {  // Part has < 3 words.
                // Get part after first '|' or '-'.
                currTitle = findAndReplace(origTitle, "[^\\|\\-]*[\\|\\-](.*)", "$1");
            }
        } else if (currTitle.indexOf(": ") != -1) {  // Title has ':'.
            // Get part after last ':'.
            currTitle = findAndReplace(origTitle, ".*:(.*)", "$1");
            if (splitLength(currTitle, "\\s+") < 3) {  // Part has < 3 words.
              // Get part after first ':'.
              currTitle = findAndReplace(origTitle, "[^:]*[:](.*)", "$1");
            }
        } else if (root != null && (currTitle.length() > 150 || currTitle.length() < 15)) {
            // Get plain text from the only H1 element.
            // TODO(kuan): this is what readability does, but this block may make more sense as an
            // if rather than else-if, e.g. currently this else-if block is used when original title
            // is "foo" but not when it is "foo |" or "foo:".
            currTitle = findTheOnlyH1(root);
            if (currTitle == null)
                currTitle = origTitle;
        }

        currTitle = StringUtil.trim(currTitle);

        if (splitLength(currTitle, "\\s+") <= 4)
            currTitle = origTitle;

        return currTitle;
    }

    private static String findAndReplace(String input, String regex, String replace) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replace);
    }

    private static int splitLength(String input, String regex) {
        // Either use String.split(), which is rumored to be very slow
        // (see http://turbomanage.wordpress.com/2011/07/12/gwt-performance-tip-watch-out-for-string-split/),
        return input.split(regex).length;

/*
        // OR RegEx.split() via Pattern.split(),
        // TODO(kuan): add test for Pattern.split() if using this.
        Pattern pattern = Pattern.compile(regex);
        return pattern.split(input).length;
*/
    }

    // OR JSNI to call native Javascript regexp (as suggested by the website above).
    // Currently, "ant test.prod" which is closest to the "real world scenario" but still not very
    // accurate, has RegEx.split as the slowest, while GWT String.split and JSNI String.split as
    // almost the same.
    //private static final native int splitLength(String input, String regex) /*-{
        //return input.split(/regex/).length;
    //}-*/;

    private static String findTheOnlyH1(Element root) {
        NodeList<Element> hOnes = root.getElementsByTagName("H1");
        return hOnes.getLength() == 1 ? hOnes.getItem(0).getInnerText() : null;
    }
}
