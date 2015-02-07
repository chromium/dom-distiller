// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

public class PagingLinksFinderTest extends DomDistillerJsTestCase {
    // EXAMPLE_URL has to have a file extension, or findBaseUrl() would be
    // the same as URL, and this would break testFirstPageLinkAsBaseUrl().
    private static String EXAMPLE_URL = "http://example.com/path/toward/news.php";

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

    private static String formHrefMockedUrl(String strToAppend) {
        String url = StringUtil.findAndReplace(EXAMPLE_URL, "^.*/", "");
        if (strToAppend != "") {
            url = url + "/" + strToAppend;
        }
        return url;
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

    public void test1PageNumberedLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor = TestUtil.createAnchor(
                formHrefMockedUrl("page2"), "page 2");
        root.appendChild(anchor);

        // The word "page" in the link text increases its score confidently enough to be considered
        // as a paging link.
        checkLinks(anchor, anchor, root);
    }

    public void test3NumberedLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor1 = TestUtil.createAnchor(
                formHrefMockedUrl("page1"), "1");
        AnchorElement anchor2 = TestUtil.createAnchor(
                formHrefMockedUrl("page2"), "2");
        AnchorElement anchor3 = TestUtil.createAnchor(
                formHrefMockedUrl("page3"), "3");
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
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor1 = TestUtil.createAnchor(
                formHrefMockedUrl("page2"), "dummy link");
        AnchorElement anchor2 = TestUtil.createAnchor(
                formHrefMockedUrl("page2"), "next page");
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
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor = TestUtil.createAnchor(
                formHrefMockedUrl("page1"), "dummy link");
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

    public void testFirstPageLinkAsBaseUrl() {
        // Some sites' first page links are the same as the base URL, previous page link needs to
        // recognize this.

        // For testcases, Window.Location.getPath() returns a ".html" file that will be stripped
        // when determining the base URL in PagingLinksFinder.findBaseUrl(), so we need to do the
        // same to use a href identical to base URL.
        String href = StringUtil.findAndReplace(EXAMPLE_URL, "\\.[^.]*$", "");

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
        //checkLinks(anchor.getHref(), "", root);
    }
}
