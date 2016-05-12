// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Background:
 *   The long article/news/forum thread/blog document may be partitioned into several partial pages
 *   by webmaster.  Each partial page has outlinks pointing to the adjacent partial pages.  The
 *   anchor text of those outlinks is numeric.
 *
 * This class parses the document to collect groups of adjacent plain text numbers and outlinks with
 * digital anchor text.  These are then passed to PageParameterParser which would spit out the
 * pagination URLs if available.
 */
public class PageParameterParser {
    // If the numeric value of a link's anchor text is greater than this number, we don't think it
    // represents the page number of the link.
    private static final int MAX_NUM_FOR_PAGE_PARAM = 100;

    /**
     * Stores PageParamInfo.PageInfo and the anchor's text, specifically returned by
     * getPageInfoAndText().
     */
    private static class PageInfoAndText {
        private final PageParamInfo.PageInfo mPageInfo;
        private final String mText;

        PageInfoAndText(int number, String url, String text) {
            mPageInfo = new PageParamInfo.PageInfo(number, url);
            mText = text;
        }
    }

    /**
     * Entry point for PageParameterParser.
     * Parses the document to collect outlinks with numeric anchor text and numeric text around
     * them.  These are then passed to PageParameterParser to detect pagination URLs.
     *
     * @return PageParamInfo (see PageParamInfo.java), always.  If no page parameter is detected or
     * determined to be best, its mType is PageParamInfo.Type.UNSET.
     *
     * @param originalUrl the original URL of the document to be parsed.
     * @param timingInfo for tracking performance.
     */
    public static PageParamInfo parse(String originalUrl, TimingInfo timingInfo) {
        PageParameterParser parser = new PageParameterParser(timingInfo);
        return parser.parseDocument(Document.get().getDocumentElement(), originalUrl);
    }

    private final TimingInfo mTimingInfo;
    private String mDocUrl = "";
    private ParsedUrl mParsedUrl = null;
    private final MonotonicPageInfosGroups mAdjacentNumbersGroups = new MonotonicPageInfosGroups();
    private int mNumForwardLinksProcessed = 0;

    private static RegExp sHrefCleaner = RegExp.compile("\\/$");
    private static RegExp sInvalidParentWrapper = null;

    private PageParameterParser(TimingInfo timingInfo) {
        mTimingInfo = timingInfo;
    }

    /**
     * Actually implements PageParameterParser.parse(), see above description for parse().
     */
    private PageParamInfo parseDocument(Element root, String originalUrl) {
        double startTime = DomUtil.getTime();

        mDocUrl = sHrefCleaner.replace(originalUrl, "");
        mParsedUrl = ParsedUrl.create(mDocUrl);
        if (mParsedUrl == null) return new PageParamInfo();  // Invalid document URL.

        AnchorElement baseAnchor = PagingLinksFinder.createAnchorWithBase(
                PagingLinksFinder.getBaseUrlForRelative(root, originalUrl));

        NodeList<Element> allLinks = root.getElementsByTagName("A");
        int idx = 0;
        while (idx < allLinks.getLength()) {
            final AnchorElement link = AnchorElement.as(allLinks.getItem(idx));
            PageInfoAndText pageInfoAndText = getPageInfoAndText(link, baseAnchor);
            if (pageInfoAndText == null) {
                idx++;
                continue;
            }

            // This link is a good candidate for pagination.

            // Close current group of adjacent numbers, add a new group if necessary.
            mAdjacentNumbersGroups.addGroup();

            // Before we append the link to the new group of adjacent numbers, check if it's
            // preceded by a text node with numeric text; if so, add it before the link.
            findAndAddClosestValidLeafNodes(link, false, true, null);

            // Add the link to the current group of adjacent numbers.
            mAdjacentNumbersGroups.addPageInfo(pageInfoAndText.mPageInfo);

            // Add all following text nodes and links with numeric text.
            mNumForwardLinksProcessed = 0;
            findAndAddClosestValidLeafNodes(link, false, false, baseAnchor);

            // Skip the current link and links already processed in the forward
            // findandAddClosestValidLeafNodes().
            idx += 1 + mNumForwardLinksProcessed;
        }  // while there're links.

        mAdjacentNumbersGroups.cleanup();

        LogUtil.addTimingInfo(startTime, mTimingInfo, "PageParameterParser");

        startTime = DomUtil.getTime();
        PageParamInfo info = PageParameterDetector.detect(mAdjacentNumbersGroups, mDocUrl);
        LogUtil.addTimingInfo(startTime, mTimingInfo, "PageParameterDetector");
        return info;
    }

    /**
     * @return a populated PageInfoAndText if given link is to be added to mAdjacentNumbersGroups.
     * Otherwise, returns null if link is to be ignored.
     * "javascript:" links with numeric text are considered valid links to be added.
     *
     * @param link to process.
     * @param baseAnchor created for the current document.
     */
    private PageInfoAndText getPageInfoAndText(AnchorElement link, AnchorElement baseAnchor) {
        // Ignore invisible links.
        if (!DomUtil.isVisible(link)) return null;

        // Use javascript innerText (instead of javascript textContent) to only get visible text.
        String linkText = StringUtil.jsTrim(DomUtil.getInnerText(link));
        int number = linkTextToNumber(linkText);
        if (!isPlainPageNumber(number)) return null;

        String linkHref = resolveLinkHref(link, baseAnchor);
        final boolean isEmptyHref = linkHref.isEmpty();
        boolean isJavascriptLink = false;
        ParsedUrl url = null;
        if (!isEmptyHref) {
            isJavascriptLink = isJavascriptHref(linkHref);
            url = ParsedUrl.create(linkHref);
            if (url == null ||
                    (!isJavascriptLink && !url.getHost().equalsIgnoreCase(mParsedUrl.getHost()))) {
                return null;
            }
            url.setHash("");
        }

        if (isEmptyHref || isJavascriptLink || isDisabledLink(link)) {
            return new PageInfoAndText(number, "", linkText);
        }

        return new PageInfoAndText(number, sHrefCleaner.replace(url.toString(), ""), linkText);
    }

    /**
     * Finds and adds the leaf node(s) closest to the given start node.
     * This recurses and keeps finding and, if necessary, adding the numeric text of valid nodes,
     * collecting the PageParamInfo.PageInfo's for the current adjacency group.
     * For backward search, i.e. nodes before start node, search terminates (i.e. recursion stops)
     * once a text node or anchor is encountered.  If the text node contains numeric text, it's
     * added to the current adjacency group.  Otherwise, a new group is created to break the
     * adjacency.
     * For forward search, i.e. nodes after start node, search continues (i.e. recursion continues)
     * until a text node or anchor with non-numeric text is encountered.  In the process, text nodes
     * and anchors with numeric text are added to the current adjacency group.  When a non-numeric
     * text node or anchor is encountered, a new group is started to break the adjacency, and search
     * ends.
     *
     * @return true to continue search, false to stop.
     *
     * @param start node to work on.
     * @param checkStart true to check start node.  Otherwise, the previous or next sibling of the
     * start node is checked.
     * @param backward true to search backward (i.e. nodes before start node), false to search
     * forward (i.e. nodes after start node).
     * @param baseAnchor created for the current document, only needed for forward search.
     */
    private boolean findAndAddClosestValidLeafNodes(Node start, boolean checkStart,
            boolean backward, AnchorElement baseAnchor) {
        Node node = checkStart ? start :
                (backward ? start.getPreviousSibling() : start.getNextSibling());
        if (node == null) {  // No sibling, try parent.
            node = start.getParentNode();
            if (sInvalidParentWrapper == null) {
                sInvalidParentWrapper = RegExp.compile("(BODY)|(HTML)");
            }
            if (sInvalidParentWrapper.test(node.getNodeName())) return false;
            return findAndAddClosestValidLeafNodes(node, false, backward, baseAnchor);
        }

        checkStart = false;
        switch (node.getNodeType()) {
            case Node.TEXT_NODE:
                String text = node.getNodeValue();
                // Text must contain words.
                if (text.isEmpty() || StringUtil.countWords(text) == 0) break;
                boolean added = addNonLinkTextIfValid(node.getNodeValue());
                // For backward search, we're done regardless if text was added.
                // For forward search, we're done only if text was invalid, otherwise continue.
                if (backward || !added) return false;
                break;

            case Node.ELEMENT_NODE:
                Element e = Element.as(node);
                if (e.hasTagName("A")) {
                    // For backward search, we're done because we've already processed the anchor.
                    if (backward) return false;
                    // For forward search, we're done only if link was invalid, otherwise continue.
                    mNumForwardLinksProcessed++;
                    if (!addLinkIfValid(AnchorElement.as(e), baseAnchor)) return false;
                    break;
                }
                // Intentionally fall through.

            default:
                // Check children nodes.
                if (!node.hasChildNodes()) break;
                checkStart = true;  // We want to check the child node.
                if (backward) {
                    // Start the backward search with the rightmost child i.e. last and closest to
                    // given node.
                    node = node.getLastChild();
                } else {
                    // Start the forward search with the leftmost child i.e. first and closest to
                    // given node.
                    node = node.getFirstChild();
                }
                break;
        }

        return findAndAddClosestValidLeafNodes(node, checkStart, backward, baseAnchor);
    }

    private static RegExp sTermsRegExp = null;  // Match terms i.e. words.
    private static RegExp sSurroundingDigitsRegExp = null;  // Match term with only digits.

    /**
     * Handle the text for a non-link node.  Each numeric term in the text that is a valid plain
     * page number adds a PageParamInfo.PageInfo into the current adjacent group.  All other terms
     * break the adjacency in the current group, adding a new group instead.
     *
     * @Return true if text was added to current group of adjacent numbers.  Otherwise, false with
     * a new group created to break the current adjacency.
     */
    private boolean addNonLinkTextIfValid(String text) {
        if (!StringUtil.containsDigit(text)) {
            // The text does not contain valid number(s); if necessary, current group of adjacent
            // numbers should be closed, adding a new group if possible.
            mAdjacentNumbersGroups.addGroup();
            return false;
        }

        if (sTermsRegExp == null) {
            sTermsRegExp = RegExp.compile("(\\S*[\\w\u00C0-\u1FFF\u2C00-\uD7FF]\\S*)", "gi");
        } else {
            sTermsRegExp.setLastIndex(0);
        }
        if (sSurroundingDigitsRegExp == null) {
            sSurroundingDigitsRegExp = RegExp.compile("^[\\W_]*(\\d+)[\\W_]*$", "i");
        }

        // Extract terms from the text, differentiating between those that contain only digits and
        // those that contain non-digits.
        boolean added = false;
        while (true) {
            MatchResult match = sTermsRegExp.exec(text);
            if (match == null) break;
            if (match.getGroupCount() <= 1) continue;

            String term = match.getGroup(1);
            MatchResult termWithDigits = sSurroundingDigitsRegExp.exec(term);
            int number = -1;
            if (termWithDigits != null && termWithDigits.getGroupCount() > 1) {
                number = StringUtil.toNumber(termWithDigits.getGroup(1));
            }
            if (isPlainPageNumber(number)) {
                // This text is a valid candidate of plain text page number, add it to last group of
                // adjacent numbers.
                mAdjacentNumbersGroups.addNumber(number, "");
                added = true;
            } else {
                // The text is not a valid number, so current group of adjacent numbers should be
                // closed, adding a new group if possible.
               mAdjacentNumbersGroups.addGroup();
            }
        }  // while there're matches

        return added;
    }

    /**
     * Adds PageParamInfo.PageInfo to the current adjacent group for a link if its text is numeric.
     * Otherwise, add a new group to break the adjacency.
     *
     * @Return true if link was added, false otherwise.
     */
    private boolean addLinkIfValid(AnchorElement link, AnchorElement baseAnchor) {
        PageInfoAndText pageInfoAndText = getPageInfoAndText(link, baseAnchor);
        if (pageInfoAndText != null) {
            mAdjacentNumbersGroups.addPageInfo(pageInfoAndText.mPageInfo);
            return true;
        }
        mAdjacentNumbersGroups.addGroup();
        return false;
    }

    /**
     * @return true if link is disabled i.e. not clickable because it has a text cursor.
     */
    private static boolean isDisabledLink(AnchorElement link) {
        Style style = DomUtil.getComputedStyle(link);
        return Style.Cursor.valueOf(style.getCursor().toUpperCase()) == Style.Cursor.TEXT;
    }

    /**
     * @return true if href starts with "javascript:".
     */
    private static boolean isJavascriptHref(String href) {
        return href.startsWith("javascript:");
    }

    private static String resolveLinkHref(AnchorElement link, AnchorElement baseAnchor) {
        // Anchors without "href" attribute are not considered potential pagination links.
        String linkHref = link.getAttribute("href");
        if (linkHref.isEmpty()) return "";
        baseAnchor.setAttribute("href", linkHref);
        return baseAnchor.getHref();
    }

    private static int linkTextToNumber(String linkText) {
        linkText = linkText.replaceAll("[()\\[\\]{}]", "");
        linkText = linkText.trim();  // Remove leading and trailing white spaces.
        return StringUtil.toNumber(linkText);
    }

    /**
     * @returns true if number is >= 0 && < MAX_NUM_FOR_PAGE_PARAM.
     */
    private static boolean isPlainPageNumber(int number) {
        return number >= 0 && number <= MAX_NUM_FOR_PAGE_PARAM;
    }

}
