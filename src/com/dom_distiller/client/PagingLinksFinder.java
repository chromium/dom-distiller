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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.NodeList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * This class finds the next and previous page links for the distilled document.  The functionality
 * for next page links is migrated from readability.getArticleTitle() in chromium codebase's
 * third_party/readability/js/readability.js, and then expanded for previous page links; boilerpipe
 * doesn't have such capability.
 * First, it determines the base URL of the document.  Then, for each anchor in the document, its
 * href and text are compared to the base URL and examined for next- or previous-paging-related
 * information.  If it passes, its score is then determined by applying various heuristics on its
 * href, text, class name and ID,  Lastly, the page link with the highest score of at least 50 is
 * considered to have enough confidence as the next or previous page link.
 */
@Export()
public class PagingLinksFinder implements Exportable {
    // Match for next page: next, continue, >, >>, » but not >|, »| as those usually mean last.
    private static final String NEXT_LINK_REGEX = "(next|weiter|continue|>([^\\|]|$)|»([^\\|]|$))";
    private static final String PREV_LINK_REGEX = "(prev|early|old|new|<|«)";
    private static final String POSITIVE_REGEX = "article|body|content|entry|hentry|main|page|pagination|post|text|blog|story";
    private static final String NEGATIVE_REGEX = "combx|comment|com-|contact|foot|footer|footnote|masthead|media|meta|outbrain|promo|related|scroll|shoutbox|sidebar|sponsor|shopping|tags|tool|widget";
    private static final String EXTRANEOUS_REGEX =
            "print|archive|comment|discuss|e[\\-]?mail|share|reply|all|login|sign|single";
    // Examples that match PAGE_NUMBER_REGEX are: "_p3", "-pg3", "p3", "_1", "-12-2".
    // Examples that don't match PAGE_NUMBER_REGEX are: "_p3 ", "p", "p123".
    private static final String PAGE_NUMBER_REGEX = "((_|-)?p[a-z]*|(_|-))[0-9]{1,2}$";

    public static String findNext(Element root) {
    /**
     * @return The next page link for the document.
     */
        return findPagingLink(root, PageLink.NEXT);
    }

    public static String findPrevious(Element root) {
    /**
     * @return The previous page link for the document.
     */
        return findPagingLink(root, PageLink.PREV);
    }

    private static String findPagingLink(Element root, PageLink page_link) {
        String baseUrl = findBaseUrl();
        // Remove trailing '/' from window location href, because it'll be used to compare with
        // other href's whose trailing '/' are also removed.
        String wndLocationHref = StringUtil.findAndReplace(Window.Location.getHref(), "\\/$", "");
        NodeList<Element> allLinks = root.getElementsByTagName("A");
        Map<String, PagingLinkObj> possiblePages = new HashMap<String, PagingLinkObj>();

        // Loop through all links, looking for hints that they may be next- or previous- page links.
        // Things like having "page" in their textContent, className or id, or being a child of a
        // node with a page-y className or id.
        // Also possible: levenshtein distance? longest common subsequence?
        // After we do that, assign each page a score.
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));

            // Remove url anchor and then trailing '/' from link's href.
            // Note that AnchorElement.getHref() returns the absolute URI, so there's no need to
            // worry about relative links.
            String linkHref = StringUtil.findAndReplace(
                StringUtil.findAndReplace(link.getHref(), "#.*$", ""), "\\/$", "");

            // Ignore page link that is empty, not http/https, or same as current window location.
            // If the page link is same as the base URL:
            // - next page link: ignore it, since we would already have seen it.
            // - previous page link: don't ignore it, since some sites will simply have the same
            //                       base URL for the first page.
            if (linkHref.isEmpty() || !StringUtil.match(linkHref, "^https?://") ||
                    linkHref.equalsIgnoreCase(wndLocationHref) ||
                    (page_link == PageLink.NEXT && linkHref.equalsIgnoreCase(baseUrl))) {
                continue;
            }

            // If it's on a different domain, skip it.
            String[] urlSlashes = StringUtil.split(linkHref, "\\/+");
            if (urlSlashes.length < 3 ||  // Expect at least the protocol, domain, and path.
                    !Window.Location.getHost().equalsIgnoreCase(urlSlashes[1])) {
                continue;
            }

            String linkText = link.getInnerText();

            // If the linkText looks like it's not the next or previous page, skip it.
            if (StringUtil.match(linkText, EXTRANEOUS_REGEX) || linkText.length() > 25) {
                continue;
            }

            // For next page link, if the initial part of the URL is identical to the base URL, but
            // the rest of it doesn't contain any digits, it's certainly not a next page link.
            // However, this doesn't apply to previous page link, because most sites will just have
            // the base URL for the first page.
            // TODO(kuan): baseUrl (returned by findBaseUrl()) is NOT the prefix of the current
            // window location, even though it appears to be so the way it's used here.
            // TODO(kuan): do we need to apply this heuristic to previous page links if current page
            // number is not 2?
            if (page_link == PageLink.NEXT) {
                String linkHrefRemaining = StringUtil.findAndReplace(linkHref, baseUrl, "");
                if (!StringUtil.match(linkHrefRemaining, "\\d")) continue;
            }

            PagingLinkObj linkObj = null;
            if (!possiblePages.containsKey(linkHref)) {  // Have not encountered this href.
                linkObj = new PagingLinkObj(0, linkText, linkHref);
                possiblePages.put(linkHref, linkObj);
            } else {  // Have already encountered this href, append its text to existing entry's.
                linkObj = possiblePages.get(linkHref);
                linkObj.mLinkText += " | " + linkText;
            }

            // If the base URL isn't part of this URL, penalize this link.  It could still be the
            // link, but the odds are lower.
            // Example: http://www.actionscript.org/resources/articles/745/1/JavaScript-and-VBScript-Injection-in-ActionScript-3/Page1.html.
            // TODO(kuan): again, baseUrl (returned by findBaseUrl()) is NOT the prefix of the
            // current window location, even though it appears to be so the way it's used here.
            if (linkHref.indexOf(baseUrl) != 0) linkObj.mScore -= 25;

            // Concatenate the link text with class name and id, and determine the score based on
            // existence of various paging-related words.
            String linkData = linkText + " " + link.getClassName() + " " + link.getId();
            if (StringUtil.match(linkData,
                    page_link == PageLink.NEXT ? NEXT_LINK_REGEX : PREV_LINK_REGEX)) {
                linkObj.mScore += 50;
            }
            if (StringUtil.match(linkData, "pag(e|ing|inat)")) linkObj.mScore += 25;
            if (StringUtil.match(linkData, "(first|last)")) {
                // -65 is enough to negate any bonuses gotten from a > or » in the text.
                // If we already matched on "next", last is probably fine.
                // If we didn't, then it's bad.  Penalize.
                // Same for "prev".
                if ((page_link == PageLink.NEXT &&
                        !StringUtil.match(linkObj.mLinkText, NEXT_LINK_REGEX)) ||
                    (page_link == PageLink.PREV &&
                        !StringUtil.match(linkObj.mLinkText, PREV_LINK_REGEX))) {
                    linkObj.mScore -= 65;
                }
            }
            if (StringUtil.match(linkData, NEGATIVE_REGEX) ||
                    StringUtil.match(linkData, EXTRANEOUS_REGEX)) {
                linkObj.mScore -= 50;
            }
            if (StringUtil.match(linkData,
                    page_link == PageLink.NEXT ? PREV_LINK_REGEX : NEXT_LINK_REGEX)) {
                linkObj.mScore -= 200;
            }

            // Check if a parent element contains page or paging or paginate.
            boolean positiveMatch = false, negativeMatch = false;
            Element parent = link.getParentElement();
            while (parent != null && (positiveMatch == false || negativeMatch == false)) {
                String parentClassAndId = parent.getClassName() + " " + parent.getId();
                if (!positiveMatch && StringUtil.match(parentClassAndId, "pag(e|ing|inat)")) {
                    linkObj.mScore += 25;
                    positiveMatch = true;
                }
                // TODO(kuan): to get 1st page for prev page link, this can't be applied; however,
                // the non-application might be the cause of recursive prev page being returned,
                // i.e. for page 1, it may incorrectly return page 3 for prev page link.
                if (!negativeMatch && StringUtil.match(parentClassAndId, NEGATIVE_REGEX)) {
                    // If this is just something like "footer", give it a negative.
                    // If it's something like "body-and-footer", leave it be.
                    if (!StringUtil.match(parentClassAndId, POSITIVE_REGEX)) {
                        linkObj.mScore -= 25;
                        negativeMatch = true;
                    }
                }
                parent = parent.getParentElement();
            }

            // If the URL looks like it has paging in it, add to the score.
            // Things like /page/2/, /pagenum/2, ?p=3, ?page=11, ?pagination=34.
            if (StringUtil.match(linkHref, "p(a|g|ag)?(e|ing|ination)?(=|\\/)[0-9]{1,2}") ||
                    StringUtil.match(linkHref, "(page|paging)")) {
                linkObj.mScore += 25;
            }

            // If the URL contains negative values, give a slight decrease.
            if (StringUtil.match(linkHref, EXTRANEOUS_REGEX)) linkObj.mScore -= 15;

            // If the link text can be parsed as a number, give it a minor bonus, with a slight bias
            // towards lower numbered pages.  This is so that pages that might not have 'next' in
            // their text can still get scored, and sorted properly by score.
            // TODO(kuan): it might be wrong to assume that it knows about other pages in the
            // document and that it starts on the first page.
            int linkTextAsNumber = 0;
            try {
                linkTextAsNumber = Integer.parseInt(linkText, 10);
            } catch (NumberFormatException e) {
            }
            if (linkTextAsNumber > 0) {
                // Punish 1 since we're either already there, or it's probably before what we
                // want anyway.
                if (linkTextAsNumber == 1) {
                    linkObj.mScore -= 10;
                } else {
                    linkObj.mScore += Math.max(0, 10 - linkTextAsNumber);
                }
            }
        }  // for all links

        // Loop through all of the possible pages from above and find the top candidate for the next
        // page URL.  Require at least a score of 50, which is a relatively high confidence that
        // this page is the next link.
        PagingLinkObj topPage = null;
        if (!possiblePages.isEmpty()) {
            Collection<PagingLinkObj> possiblePageObjs = possiblePages.values();
            Iterator<PagingLinkObj> iter = possiblePageObjs.iterator();
            while (iter.hasNext()) {
                PagingLinkObj pageObj = iter.next();
                if (pageObj.mScore >= 50 && (topPage == null || topPage.mScore < pageObj.mScore)) {
                    topPage = pageObj;
                }
            }
        }

        if (topPage != null) {
            String nextHref = StringUtil.findAndReplace(topPage.mLinkHref, "\\/$", "");
            return nextHref;
        }
        return null;
    }

    private static String findBaseUrl() {
        // This extracts relevant parts from the window location's path based on various heuristics
        // to determine the path of the base URL of the document.  This path is then appended to the
        // window location protocol and host to form the base URL of the document.  This base URL is
        // then used as reference for comparison against an anchor's href to to determine if the
        // anchor is a next or previous paging link.

        // First, from the window's location's path, extract the segments delimited by '/'.  Then,
        // because the later segments probably contain less relevant information for the base URL,
        // reverse the segments for easier processing.
        // Note: '?' is a special character in RegEx, so enclose it within [] to specify the actual
        // character.
        String noUrlParams = Window.Location.getPath();
        String[] urlSlashes = StringUtil.split(noUrlParams, "/");
        Collections.reverse(Arrays.asList(urlSlashes));

        // Now, process each segment by extracting relevant information based on various heuristics.
        List<String> cleanedSegments = new ArrayList<String>();
        for (int i = 0; i < urlSlashes.length; i++) {
            String segment = urlSlashes[i];

            // Split off and save anything that looks like a file type.
            if (segment.indexOf(".") != -1) {
                // Because '.' is a special character in RegEx, enclose it within [] to specify the
                // actual character.
                String possibleType = StringUtil.split(segment, "[.]")[1];

                // If the type isn't alpha-only, it's probably not actually a file extension.
                if (!StringUtil.match(possibleType, "[^a-zA-Z]")) {
                    segment = StringUtil.split(segment, "[.]")[0];
                }
            }

            // EW-CMS specific segment replacement. Ugly.
            // Example: http://www.ew.com/ew/article/0,,20313460_20369436,00.html.
            segment = StringUtil.findAndReplace(segment, ",00", "");

            // If the first or second segment has anything looking like a page number, remove it.
            if (i < 2) segment = StringUtil.findAndReplace(segment, PAGE_NUMBER_REGEX, "");

            // Ignore an empty segment.
            if (segment.isEmpty()) continue;
            
            // If this is purely a number in the first or second segment, it's probably a page
            // number, ignore it.
            if (i < 2 && StringUtil.match(segment, "^\\d{1,2}$")) continue;

            // If this is the first segment and it's just "index", ignore it.
            if (i == 0 && segment.equalsIgnoreCase("index")) continue;

            // If the first or second segment is shorter than 3 characters, and the first
            // segment was purely alphas, ignore it.
            if (i < 2 && segment.length() < 3 && !StringUtil.match(urlSlashes[0], "[a-z]")) {
                continue;
            }

            // If we got here, append the segment to cleanedSegments.
            cleanedSegments.add(segment);
        }  // for all urlSlashes

        return Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/" +
                reverseJoin(cleanedSegments, "/");
    }

    private static String reverseJoin(List<String> array, String delim) {
        // As per http://stackoverflow.com/questions/5748044/gwt-string-concatenation-operator-vs-stringbuffer,
        // + operator is faster for javascript than StringBuffer/StringBuilder.
        String joined = "";
        for (int i = array.size() - 1; i >= 0; i--) {
            joined += array.get(i);
            if (i > 0) joined += delim;
        }
        return joined;
    }

    private static class PagingLinkObj {
        private int mScore = 0;
        private String mLinkText;
        private String mLinkHref;

        PagingLinkObj(int score, String linkText, String linkHref) {
            mScore = score;
            mLinkText = linkText;
            mLinkHref = linkHref;
        }
    }

    private enum PageLink {
        NEXT,
        PREV,
    }

}
