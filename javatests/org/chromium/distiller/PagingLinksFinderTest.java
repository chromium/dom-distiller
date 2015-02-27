// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.BaseElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

public class PagingLinksFinderTest extends DomDistillerJsTestCase {
    private static String EXAMPLE_URL = "http://example.com/path/toward/news.php";

    private static String resolveLinkHref(String link, String base) {
        return resolveLinkHref(TestUtil.createAnchor(link, ""), base);
    }

    private static String resolveLinkHref(AnchorElement anchor, String base) {
        AnchorElement baseAnchor = PagingLinksFinder.createAnchorWithBase(base);
        return PagingLinksFinder.resolveLinkHref(anchor, baseAnchor);
    }

    private static void checkResolveLinkHref(AnchorElement anchor, String original_url, String expected, String href) {
        anchor.setHref(href);
        AnchorElement baseAnchor = PagingLinksFinder.createAnchorWithBase(original_url);
        assertEquals(expected, PagingLinksFinder.resolveLinkHref(anchor, baseAnchor));
    }

    public void testResolveLinkHref() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("", "");
        root.appendChild(anchor);

        String url = "http://example.com/path/toward/page.html";

        checkResolveLinkHref(anchor, url, "http://dummy/link", "http://dummy/link");
        checkResolveLinkHref(anchor, url, "https://dummy/link", "https://dummy/link");
        checkResolveLinkHref(anchor, url, "http://example.com/next", "/next");
        checkResolveLinkHref(anchor, url, "http://example.com/path/toward/next", "next");
        checkResolveLinkHref(anchor, url, "http://example.com/path/next", "../next");
        checkResolveLinkHref(anchor, url, "http://example.com/1/2/next", "../../1/3/../2/next");
        checkResolveLinkHref(anchor, url, "javascript:void(0)", "javascript:void(0)");
        checkResolveLinkHref(anchor, url, "mailto:user@example.com", "mailto:user@example.com");
        checkResolveLinkHref(anchor, url, "http://example.com/path/toward/page.html?page=2#table_of_content", "?page=2#table_of_content");
    }

    private static void checkLinks(AnchorElement next, AnchorElement prev, Element root) {
        checkLinks(next, prev, root, EXAMPLE_URL);
    }

    private static void checkLinks(AnchorElement next, AnchorElement prev, Element root, String original_url) {
        AnchorElement baseAnchor = PagingLinksFinder.createAnchorWithBase(original_url);
        if (next == null) {
            assertNull(PagingLinksFinder.findNext(root, original_url));
        } else {
            String href = PagingLinksFinder.resolveLinkHref(next, baseAnchor);
            assertEquals(href, PagingLinksFinder.findNext(root, original_url));
        }
        if (prev == null) {
            assertNull(PagingLinksFinder.findPrevious(root, original_url));
        } else {
            String href = PagingLinksFinder.resolveLinkHref(prev, baseAnchor);
            assertEquals(href, PagingLinksFinder.findPrevious(root, original_url));
        }
    }

    public void testNoLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);

        checkLinks(null, null, root);
    }

    public void test1NextLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("next", "next page");
        root.appendChild(anchor);

        checkLinks(null, null, root);
    }

    public void test1NextLinkWithDifferentDomain() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("http://testing.com/page2", "next page");
        root.appendChild(anchor);

        checkLinks(null, null, root);
    }

    public void test1NextLinkWithOriginalDomain() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("http://testing.com/page2", "next page");
        root.appendChild(anchor);

        checkLinks(anchor, null, root, "http://testing.com");
    }

    public void testCaseInsensitive() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("HTTP://testing.COM/page2", "next page");
        root.appendChild(anchor);

        checkLinks(anchor, null, root, "http://testing.com");
    }

    public void test1PageNumberedLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);

        AnchorElement anchor = TestUtil.createAnchor("page2", "page 2");
        root.appendChild(anchor);

        // The word "page" in the link text increases its score confidently enough to be considered
        // as a paging link.
        checkLinks(anchor, anchor, root);
    }

    public void test3NumberedLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor1 = TestUtil.createAnchor("page1", "1");
        AnchorElement anchor2 = TestUtil.createAnchor("page2", "2");
        AnchorElement anchor3 = TestUtil.createAnchor("page3", "3");
        root.appendChild(anchor1);
        root.appendChild(anchor2);
        root.appendChild(anchor3);

        // Because link text contains only digits with no paging-related words, no link has a score
        // high enough to be confidently considered paging link.
        checkLinks(null, null, root);
    }

    public void test2NextLinksWithSameHref() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor1 = TestUtil.createAnchor("page2", "dummy link");
        AnchorElement anchor2 = TestUtil.createAnchor("page2", "next page");
        root.appendChild(anchor1);
        root.appendChild(anchor2);

        // anchor1 by itself is not a confident next page link, but anchor2's link text helps bump
        // up the score for the shared href, so anchor1 is now a confident next page link.
        checkLinks(anchor1, null, root);
    }

    public void testPagingParent() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element div = TestUtil.createDiv(1);
        div.setClassName("page");
        root.appendChild(div);
        AnchorElement anchor = TestUtil.createAnchor("page1", "dummy link");
        div.appendChild(anchor);

        // While it may seem strange that both previous and next links are the same, this test is
        // testing that the anchor's parents will affect its paging score even if it has a
        // meaningless link text like "dummy link".
        checkLinks(anchor, anchor, root);
    }

    public void test1PrevLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("prev", "prev page");
        root.appendChild(anchor);

        checkLinks(null, anchor, root);
    }

    public void test1PrevAnd1NextLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement prevAnchor = TestUtil.createAnchor("prev", "prev page");
        AnchorElement nextAnchor = TestUtil.createAnchor("page2", "next page");
        root.appendChild(prevAnchor);
        root.appendChild(nextAnchor);

        checkLinks(nextAnchor, prevAnchor, root);
    }

    public void testPopularBadLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement nextAnchor = TestUtil.createAnchor("page2", "next page");
        root.appendChild(nextAnchor);
        // If the same bad URL can get scores accumulated across links,
        // it would wrongly get selected.
        AnchorElement bad1 = TestUtil.createAnchor("not-page1", "not");
        root.appendChild(bad1);
        AnchorElement bad2 = TestUtil.createAnchor("not-page1", "not");
        root.appendChild(bad2);
        AnchorElement bad3 = TestUtil.createAnchor("not-page1", "not");
        root.appendChild(bad3);
        AnchorElement bad4 = TestUtil.createAnchor("not-page1", "not");
        root.appendChild(bad4);
        AnchorElement bad5 = TestUtil.createAnchor("not-page1", "not");
        root.appendChild(bad5);

        checkLinks(nextAnchor, null, root);
    }

    public void testHeldBackLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement nextAnchor = TestUtil.createAnchor("page2", "next");
        root.appendChild(nextAnchor);
        // If "page2" gets bad scores from other links, it would be missed.
        AnchorElement bad = TestUtil.createAnchor("page2", "prev or next");
        root.appendChild(bad);

        checkLinks(nextAnchor, null, root);
    }

    public void testBaseTag() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);

        // Base.href should not be mixed up with original_url.
        String base = resolveLinkHref("page2", EXAMPLE_URL);
        BaseElement baseTag = Document.get().createBaseElement();
        baseTag.setHref(base);
        mHead.appendChild(baseTag);

        AnchorElement nextAnchor = TestUtil.createAnchor("page2", "next page");
        root.appendChild(nextAnchor);

        assertEquals(base, PagingLinksFinder.getBaseUrlForRelative(mRoot, EXAMPLE_URL));
        checkLinks(nextAnchor, null, mRoot);

        mHead.removeChild(baseTag);
    }

    public void testFirstPageLinkAsFolderUrl() {
        // Some sites' first page links are the same as the folder URL, previous page link needs to
        // recognize this.

        String href = StringUtil.findAndReplace(EXAMPLE_URL, "\\/[^/]*$", "");

        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(href, "PREV");
        root.appendChild(anchor);

        checkLinks(null, anchor, root);
    }

    public void testNonHttpOrHttpsLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("javascript:void(0)",
                                                     "NEXT");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));

        anchor.setHref("file://test.html");
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));
    }

    public void testNextArticleLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "next article");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));
    }

    public void testAsOneLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "view as one page");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));
    }

    public void testLinksWithLongText() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "page 2 with long text)");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));
    }

    public void testNonTailPageInfo() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("gap/12/somestuff"), "page down");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, EXAMPLE_URL));
    }

    public void testNoBase() {
        Element doc = Document.get().getDocumentElement();
        String base = PagingLinksFinder.getBaseUrlForRelative(doc, EXAMPLE_URL);
        assertEquals(base, EXAMPLE_URL);
    }

    public void testBaseUrlForRelative() {
        BaseElement base = Document.get().createBaseElement();
        mHead.appendChild(base);

        BaseElement bogusBase = Document.get().createBaseElement();
        bogusBase.setHref("https://itsatrap.com/");
        mHead.appendChild(bogusBase);

        Element doc = Document.get().getDocumentElement();
        String[] baseUrls = {
                "http://example.com",
                "https://example.com/no/trailing/slash.php",
                "http://example.com/trailingslash/",
                "/another/path/index.html",
                "section/page2.html",
                "//testing.com/",
        };

        String[] expected = {
                "http://example.com/", // Note the trailing slash.
                "https://example.com/no/trailing/slash.php",
                "http://example.com/trailingslash/",
                "http://example.com/another/path/index.html",
                "http://example.com/path/toward/section/page2.html",
                "http://testing.com/",
        };

        for (int i = 0; i < baseUrls.length; i++) {//String baseUrl: baseUrls) {
            String baseUrl = baseUrls[i];
            base.setHref(baseUrl);
            assertEquals(expected[i], PagingLinksFinder.getBaseUrlForRelative(doc, EXAMPLE_URL));
        }

        mHead.removeChild(base);
        mHead.removeChild(bogusBase);
    }

    public void testPageDiff() {
        assertNull(PagingLinksFinder.pageDiff("", "", null, 0));
        assertNull(PagingLinksFinder.pageDiff("asdf", "qwer", null, 0));
        assertNull(PagingLinksFinder.pageDiff("commonA", "commonB", null, 0));
        assertEquals((Integer) 1, PagingLinksFinder.pageDiff("common1", "common2", null, 0));
        assertNull(PagingLinksFinder.pageDiff("common1", "common2", null, 7));
        assertNull(PagingLinksFinder.pageDiff("common1", "Common2", null, 0));
        assertEquals((Integer) (-8), PagingLinksFinder.pageDiff("common10", "common02", null, 0));
        assertNull(PagingLinksFinder.pageDiff("commonA10", "commonB02", null, 0));
        assertNull(PagingLinksFinder.pageDiff("common10", "commonB02", null, 0));
        assertNull(PagingLinksFinder.pageDiff("commonA10", "common02", null, 0));
        assertEquals((Integer) (-7), PagingLinksFinder.pageDiff("common11", "common4", null, 0));
    }
}
