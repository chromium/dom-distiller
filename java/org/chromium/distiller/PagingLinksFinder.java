// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/*
 * Parts of this file are adapted from Readability.
 *
 * Readability is Copyright (c) 2010 Src90 Inc
 * and licenced under the Apache License, Version 2.0.
 */

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.BaseElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class finds the next and previous page links for the distilled document.  The functionality
 * for next page links is migrated from readability.getArticleTitle() in chromium codebase's
 * third_party/readability/js/readability.js, and then expanded for previous page links; boilerpipe
 * doesn't have such capability.
 * First, it determines the prefix URL of the document.  Then, for each anchor in the document, its
 * href and text are compared to the prefix URL and examined for next- or previous-paging-related
 * information.  If it passes, its score is then determined by applying various heuristics on its
 * href, text, class name and ID,  Lastly, the page link with the highest score of at least 50 is
 * considered to have enough confidence as the next or previous page link.
 */
public class PagingLinksFinder {
    // Match for next page: next, continue, >, >>, » but not >|, »| as those usually mean last.
    private static final RegExp REG_NEXT_LINK =
            RegExp.compile("(next|weiter|continue|>([^\\|]|$)|»([^\\|]|$))", "i");
    private static final RegExp REG_PREV_LINK = RegExp.compile("(prev|early|old|new|<|«)", "i");
    private static final RegExp REG_POSITIVE = RegExp.compile(
            "article|body|content|entry|hentry|main|page|pagination|post|text|blog|story", "i");
    private static final RegExp REG_NEGATIVE = RegExp.compile(
            "combx|comment|com-|contact|foot|footer|footnote|masthead|media|meta"
                    + "|outbrain|promo|related|shoutbox|sidebar|sponsor|shopping|tags"
                    + "|tool|widget",
            "i");
    private static final RegExp REG_EXTRANEOUS = RegExp.compile(
            "print|archive|comment|discuss|e[\\-]?mail|share|reply|all|login|sign|single"
                    + "|as one|article",
            "i");
    private static final RegExp REG_PAGINATION = RegExp.compile("pag(e|ing|inat)", "i");
    private static final RegExp REG_LINK_PAGINATION =
            RegExp.compile("p(a|g|ag)?(e|ing|ination)?(=|\\/)[0-9]{1,2}$", "i");
    private static final RegExp REG_FIRST_LAST = RegExp.compile("(first|last)", "i");
    // Examples that match PAGE_NUMBER_REGEX are: "_p3", "-pg3", "p3", "_1", "-12-2".
    // Examples that don't match PAGE_NUMBER_REGEX are: "_p3 ", "p", "p123".
    private static final RegExp REG_PAGE_NUMBER =
            RegExp.compile("((_|-)?p[a-z]*|(_|-))[0-9]{1,2}$", "gi");

    private static final RegExp REG_HREF_CLEANER = RegExp.compile("/?(#.*)?$");
    private static final RegExp REG_NUMBER = RegExp.compile("\\d");

    public static DomDistillerProtos.PaginationInfo getPaginationInfo(String original_url) {
        DomDistillerProtos.PaginationInfo info = DomDistillerProtos.PaginationInfo.create();
        String next = findNext(Document.get().getDocumentElement(), original_url);
        if (next != null) {
            info.setNextPage(next);
        }
        return info;
    }

    /**
     * @param original_url The original url of the page being processed.
     * @return The next page link for the document.
     */
    public static String findNext(Element root, String original_url) {
        return findPagingLink(root, original_url, PageLink.NEXT);
    }

    /**
     * @param original_url The original url of the page being processed.
     * @return The previous page link for the document.
     */
    public static String findPrevious(Element root, String original_url) {
        return findPagingLink(root, original_url, PageLink.PREV);
    }

    private static String findPagingLink(Element root, String original_url, PageLink pageLink) {
        // findPagingLink() is static, so clear mLinkDebugInfo before processing the links.
        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) {
            mLinkDebugInfo.clear();
        }

        String folderUrl = StringUtil.findAndReplace(original_url, "\\/[^/]*$", "");

        // Remove trailing '/' from window location href, because it'll be used to compare with
        // other href's whose trailing '/' are also removed.
        String wndLocationHref = StringUtil.findAndReplace(original_url, "\\/$", "");
        NodeList<Element> allLinks = root.getElementsByTagName("A");
        Set<PagingLinkObj> possiblePages = new HashSet<PagingLinkObj>();

        AnchorElement baseAnchor = createAnchorWithBase(getBaseUrlForRelative(root, original_url));

        // The trailing "/" is essential to ensure the whole hostname is matched, and not just the
        // prefix of the hostname. It also maintains the requirement of having a "path" in the URL.
        String allowedPrefix = getScheme(original_url) + "://" + getHostname(original_url) + "/";
        RegExp regPrefixNum = RegExp.compile("^" + StringUtil.regexEscape(allowedPrefix) + ".*\\d", "i");

        // Loop through all links, looking for hints that they may be next- or previous- page links.
        // Things like having "page" in their textContent, className or id, or being a child of a
        // node with a page-y className or id.
        // Also possible: levenshtein distance? longest common subsequence?
        // After we do that, assign each page a score.
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));

            // Note that AnchorElement.getHref() returns the absolute URI, so there's no need to
            // worry about relative links.
            String linkHref = resolveLinkHref(link, baseAnchor);

            if (pageLink == PageLink.NEXT) {
                if (!regPrefixNum.test(linkHref)) {
                    appendDbgStrForLink(link, "ignored: not prefix + num");
                    continue;
                }
            } else if (pageLink == PageLink.PREV) {
                if (!linkHref.substring(0, allowedPrefix.length()).equalsIgnoreCase(allowedPrefix)) {
                    appendDbgStrForLink(link, "ignored: prefix");
                    continue;
                }
            }

            int width = link.getOffsetWidth();
            int height = link.getOffsetHeight();
            if (width == 0 || height == 0) {
                appendDbgStrForLink(link, "ignored: sz=" + width + "x" + height);
                continue;
            }

            if (!DomUtil.isVisible(link)) {
                appendDbgStrForLink(link, "ignored: invisible");
                continue;
            }

            // Remove url anchor and then trailing '/' from link's href.
            linkHref = REG_HREF_CLEANER.replace(linkHref, "");
            appendDbgStrForLink(link, "-> " + linkHref);

            // Ignore page link that is the same as current window location.
            // If the page link is same as the folder URL:
            // - next page link: ignore it, since we would already have seen it.
            // - previous page link: don't ignore it, since some sites will simply have the same
            //                       folder URL for the first page.
            if (linkHref.equalsIgnoreCase(wndLocationHref)
                    || (pageLink == PageLink.NEXT && linkHref.equalsIgnoreCase(folderUrl))) {
                appendDbgStrForLink(
                        link, "ignored: same as current or folder url " + folderUrl);
                continue;
            }

            // Use javascript innerText (instead of javascript textContent) to only get visible
            // text.
            String linkText = DomUtil.getInnerText(link);

            // If the linkText looks like it's not the next or previous page, skip it.
            if (REG_EXTRANEOUS.test(linkText) || linkText.length() > 25) {
                appendDbgStrForLink(link, "ignored: one of extra");
                continue;
            }

            // For next page link, if the initial part of the URL is identical to the folder URL, but
            // the rest of it doesn't contain any digits, it's certainly not a next page link.
            // However, this doesn't apply to previous page link, because most sites will just have
            // the folder URL for the first page.
            // TODO(kuan): do we need to apply this heuristic to previous page links if current page
            // number is not 2?
            if (pageLink == PageLink.NEXT) {
                String linkHrefRemaining = linkHref;
                if (linkHref.startsWith(folderUrl)) {
                    linkHrefRemaining = linkHref.substring(folderUrl.length());
                }
                if (!REG_NUMBER.test(linkHrefRemaining)) {
                    appendDbgStrForLink(link, "ignored: no number beyond folder url " + folderUrl);
                    continue;
                }
            }

            PagingLinkObj linkObj = null;
            linkObj = new PagingLinkObj(i, 0, linkText, linkHref);
            possiblePages.add(linkObj);

            // If the folder URL isn't part of this URL, penalize this link.  It could still be the
            // link, but the odds are lower.
            // Example: http://www.actionscript.org/resources/articles/745/1/JavaScript-and-VBScript-Injection-in-ActionScript-3/Page1.html.
            if (linkHref.indexOf(folderUrl) != 0) {
                linkObj.mScore -= 25;
                appendDbgStrForLink(link, "score=" + linkObj.mScore +
                        ": not part of folder url " + folderUrl);
            }

            // Concatenate the link text with class name and id, and determine the score based on
            // existence of various paging-related words.
            String linkData = linkText + " " + link.getClassName() + " " + link.getId();
            appendDbgStrForLink(link, "txt+class+id=" + linkData);
            if (pageLink == PageLink.NEXT ? REG_NEXT_LINK.test(linkData)
                                          : REG_PREV_LINK.test(linkData)) {
                linkObj.mScore += 50;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has " +
                        (pageLink == PageLink.NEXT ? "next" : "prev" + " regex"));
            }
            if (REG_PAGINATION.test(linkData)) {
                linkObj.mScore += 25;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has pag* word");
            }
            if (REG_FIRST_LAST.test(linkData)) {
                // -65 is enough to negate any bonuses gotten from a > or » in the text.
                // If we already matched on "next", last is probably fine.
                // If we didn't, then it's bad.  Penalize.
                // Same for "prev".
                if ((pageLink == PageLink.NEXT && !REG_NEXT_LINK.test(linkObj.mLinkText))
                        || (pageLink == PageLink.PREV && !REG_PREV_LINK.test(linkObj.mLinkText))) {
                    linkObj.mScore -= 65;
                    appendDbgStrForLink(link, "score=" + linkObj.mScore +
                            ": has first|last but no " +
                            (pageLink == PageLink.NEXT ? "next" : "prev") + " regex");
                }
            }
            if (REG_NEGATIVE.test(linkData) || REG_EXTRANEOUS.test(linkData)) {
                linkObj.mScore -= 50;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has neg or extra regex");
            }
            if (pageLink == PageLink.NEXT ? REG_PREV_LINK.test(linkData)
                                          : REG_NEXT_LINK.test(linkData)) {
                linkObj.mScore -= 200;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has opp of " +
                        (pageLink == PageLink.NEXT ? "next" : "prev") + " regex");
            }

            // Check if a parent element contains page or paging or paginate.
            boolean positiveMatch = false, negativeMatch = false;
            Element parent = link.getParentElement();
            while (parent != null && (positiveMatch == false || negativeMatch == false)) {
                String parentClassAndId = parent.getClassName() + " " + parent.getId();
                if (!positiveMatch && REG_PAGINATION.test(parentClassAndId)) {
                    linkObj.mScore += 25;
                    positiveMatch = true;
                    appendDbgStrForLink(link,"score=" + linkObj.mScore +
                            ": posParent - " + parentClassAndId);
                }
                // TODO(kuan): to get 1st page for prev page link, this can't be applied; however,
                // the non-application might be the cause of recursive prev page being returned,
                // i.e. for page 1, it may incorrectly return page 3 for prev page link.
                if (!negativeMatch && REG_NEGATIVE.test(parentClassAndId)) {
                    // If this is just something like "footer", give it a negative.
                    // If it's something like "body-and-footer", leave it be.
                    if (!REG_POSITIVE.test(parentClassAndId)) {
                        linkObj.mScore -= 25;
                        negativeMatch = true;
                        appendDbgStrForLink(link, "score=" + linkObj.mScore + ": negParent - " +
                                parentClassAndId);
                    }
                }
                parent = parent.getParentElement();
            }

            // If the URL looks like it has paging in it, add to the score.
            // Things like /page/2/, /pagenum/2, ?p=3, ?page=11, ?pagination=34.
            if (REG_LINK_PAGINATION.test(linkHref) || REG_PAGINATION.test(linkHref)) {
                linkObj.mScore += 25;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has paging info");
            }

            // If the URL contains negative values, give a slight decrease.
            if (REG_EXTRANEOUS.test(linkHref)) {
                linkObj.mScore -= 15;
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": has extra regex");
            }

            // If the link text is too long, penalize the link.
            if (linkText.length() > 10) {
                linkObj.mScore -= linkText.length();
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": text too long");
            }

            // If the link text can be parsed as a number, give it a minor bonus, with a slight bias
            // towards lower numbered pages.  This is so that pages that might not have 'next' in
            // their text can still get scored, and sorted properly by score.
            // TODO(kuan): it might be wrong to assume that it knows about other pages in the
            // document and that it starts on the first page.
            int linkTextAsNumber = JavaScript.parseInt(linkText, 10);
            if (linkTextAsNumber > 0) {
                // Punish 1 since we're either already there, or it's probably before what we
                // want anyway.
                if (linkTextAsNumber == 1) {
                    linkObj.mScore -= 10;
                } else {
                    linkObj.mScore += Math.max(0, 10 - linkTextAsNumber);
                }
                appendDbgStrForLink(link, "score=" + linkObj.mScore + ": linktxt is a num (" +
                        linkTextAsNumber + ")");
            }
            Integer diff = pageDiff(original_url, linkHref, link, allowedPrefix.length());
            if (diff != null) {
                if (((pageLink == PageLink.NEXT) && (diff == 1))
                        || ((pageLink == PageLink.PREV) && (diff == -1))) {
                    linkObj.mScore += 25;
                    appendDbgStrForLink(link, "score=" + linkObj.mScore + ": diff = " + diff);
                }
            }
        }  // for all links

        // Loop through all of the possible pages from above and find the top candidate for the next
        // page URL.  Require at least a score of 50, which is a relatively high confidence that
        // this page is the next link.
        PagingLinkObj topPage = null;
        if (!possiblePages.isEmpty()) {
            for (PagingLinkObj pageObj : possiblePages) {
                if (pageObj.mScore >= 50 && (topPage == null || topPage.mScore < pageObj.mScore)) {
                    topPage = pageObj;
                }
            }
        }

        String pagingHref = null;
        if (topPage != null) {
            pagingHref = StringUtil.findAndReplace(topPage.mLinkHref, "\\/$", "");
            appendDbgStrForLink(allLinks.getItem(topPage.mLinkIndex), "found: score=" +
                    topPage.mScore + ", txt=[" + topPage.mLinkText + "], " + pagingHref);
        }

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) {
            logDbgInfoToConsole(pageLink, pagingHref, allLinks);
        }

        return pagingHref;
    }

    public static String getBaseUrlForRelative(Element root, String original_url) {
        NodeList<Element> bases = root.getElementsByTagName("BASE");
        if (bases.getLength() == 0) {
            return original_url;
        }
        // Note that base.href can also be relative.
        // If multiple <base> elements are specified, only the first href and
        // first target value are used; all others are ignored.
        // Reference: https://developer.mozilla.org/en-US/docs/Web/HTML/Element/base
        AnchorElement baseAnchor = createAnchorWithBase(original_url);
        return resolveLinkHref(BaseElement.as(bases.getItem(0)).getAttribute("href"), baseAnchor);
    }

    public static AnchorElement createAnchorWithBase(String base_url) {
        Document doc = DomUtil.createHTMLDocument(Document.get());

        BaseElement base = doc.createBaseElement();
        base.setHref(base_url);
        doc.getHead().appendChild(base);

        AnchorElement a = doc.createAnchorElement();
        doc.getBody().appendChild(a);
        return a;
    }

    private static String fixMissingScheme(String url) {
        if (url.isEmpty()) return "";
        if (!url.contains("://")) return "http://" + url;
        return url;
    }

    // The link is resolved using an anchor within a new HTML document with a base tag.
    public static String resolveLinkHref(AnchorElement link, AnchorElement baseAnchor) {
        String linkHref = link.getAttribute("href");
        return resolveLinkHref(linkHref, baseAnchor);
    }

    public static String resolveLinkHref(String linkHref, AnchorElement baseAnchor) {
        baseAnchor.setAttribute("href", linkHref);
        return baseAnchor.getHref();
    }

    private static String getScheme(String url) {
        return StringUtil.split(url, ":\\/\\/")[0];
    }

    // Port number is also included if it exists.
    private static String getHostname(String url) {
        url = StringUtil.split(url, ":\\/\\/")[1];
        if (!url.contains("/")) return url;
        return StringUtil.split(url, "\\/")[0];
    }

    private static String getPath(String url) {
        url = StringUtil.split(url, ":\\/\\/")[1];
        if (!url.contains("/")) return "";
        return StringUtil.findAndReplace(url, "^([^/]*)/", "");
    }

    public static Integer pageDiff(String url, String linkHref, AnchorElement link, int skip) {
        int commonLen = skip;
        int i;
        for (i=skip; i<Math.min(url.length(), linkHref.length()); i++) {
            if (url.charAt(i) != linkHref.charAt(i)) {
                break;
            }
        }
        commonLen = i;
        url = url.substring(commonLen, url.length());
        linkHref = linkHref.substring(commonLen, linkHref.length());
        appendDbgStrForLink(link, "remains: " + url + ", " + linkHref);

        int linkAsNumber = JavaScript.parseInt(linkHref, 10);
        int urlAsNumber = JavaScript.parseInt(url, 10);
        appendDbgStrForLink(link, "remains: " + urlAsNumber + ", " + linkAsNumber);
        if (urlAsNumber > 0 && linkAsNumber > 0) {
            return linkAsNumber - urlAsNumber;
        }
        return null;
    }

    private static void appendDbgStrForLink(Element link, String message) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) return;

        // |message| is concatenated with the existing debug string for |link| (delimited by ";") in
        // mLinkDebugInfo.
        String dbgStr = "";
        if (mLinkDebugInfo.containsKey(link)) dbgStr = mLinkDebugInfo.get(link);
        if (!dbgStr.isEmpty()) dbgStr += "; ";
        dbgStr += message;
        mLinkDebugInfo.put(link, dbgStr);
    }

    private static void logDbgInfoToConsole(PageLink pageLink, String pagingHref,
            NodeList<Element> allLinks) {
        // This logs the following to the console:
        // - number of links processed
        // - the next or previous page link found
        // - for each link: its href, text, concatenated debug string.
        // Location of logging output is different when running in different modes:
        // - "ant test.dev": test output file.
        // - chrome browser distiller viewer: chrome logfile.
        // (TODO)kuan): investigate how to get logging when running "ant test.prod" - currently,
        // nothing appears.  In the meantime, throwing an exception with a log message at suspicious
        // codepoints can produce a call stack and help debugging, albeit tediously.
        LogUtil.logToConsole("numLinks=" + allLinks.getLength() + ", found " +
                (pageLink == PageLink.NEXT ? "next: " : "prev: ") +
                (pagingHref != null ? pagingHref : "null"));

        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));

            // Use javascript innerText (instead of javascript textContent) to get only visible
            // text.
            String text = DomUtil.getInnerText(link);
            // Trim unnecessary whitespaces from text.
            String[] words = StringUtil.split(text, "\\s+");
            text = "";
            for (int w = 0; w < words.length; w++) {
                text += words[w];
                if (w < words.length - 1) text += " ";
            }

            LogUtil.logToConsole(i + ")" + link.getHref() + ", txt=[" + text + "], dbg=[" +
                    mLinkDebugInfo.get(link) + "]");
        }
    }

    private static class PagingLinkObj {
        private int mLinkIndex = -1;
        private int mScore = 0;
        private String mLinkText;
        private final String mLinkHref;

        PagingLinkObj(int linkIndex, int score, String linkText, String linkHref) {
            mLinkIndex = linkIndex;
            mScore = score;
            mLinkText = linkText;
            mLinkHref = linkHref;
        }
    }

    private enum PageLink {
        NEXT,
        PREV,
    }

    private static final Map<Element, String> mLinkDebugInfo = new HashMap<Element, String>();

}
