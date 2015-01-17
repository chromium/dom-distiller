// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

public class PagingLinksFinderTest extends DomDistillerJsTestCase {
    private static void checkLinks(String next, String prev, Element root) {
        checkLinks(next, prev, root, "example.com");
    }

    private static void checkLinks(String next, String prev, Element root, String original_domain) {
        if (next == "") {
            assertNull(PagingLinksFinder.findNext(root, original_domain));
        } else {
            next = PagingLinksFinder.mockDomainForFile(next, original_domain);
            assertEquals(next, PagingLinksFinder.findNext(root, original_domain));
        }
        if (prev == "") {
            assertNull(PagingLinksFinder.findPrevious(root, original_domain));
        } else {
            prev = PagingLinksFinder.mockDomainForFile(prev, original_domain);
            assertEquals(prev, PagingLinksFinder.findPrevious(root, original_domain));
        }
    }

    public void testNoLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);

        checkLinks("", "", root);
    }

    public void disabled_test1NextLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("next", "next page");
        root.appendChild(anchor);

        checkLinks("", "", root);
    }

    public void test1NextLinkWithDifferentDomain() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("http://testing.com/page2", "next page");
        root.appendChild(anchor);

        checkLinks("", "", root);
    }

    public void test1NextLinkWithOriginalDomain() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("http://testing.com/page2", "next page");
        root.appendChild(anchor);

        checkLinks(anchor.getHref(), "", root, "testing.com");
    }

    public void disabled_test1PageNumberedLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "page 2");
        root.appendChild(anchor);

        // The word "page" in the link text increases its score confidently enough to be considered
        // as a paging link.
        checkLinks(anchor.getHref(), anchor.getHref(), root);
    }

    public void test3NumberedLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor1 = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page1"), "1");
        AnchorElement anchor2 = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "2");
        AnchorElement anchor3 = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page3"), "3");
        root.appendChild(anchor1);
        root.appendChild(anchor2);
        root.appendChild(anchor3);

        // Because link text contains only digits with no paging-related words, no link has a score
        // high enough to be confidently considered paging link.
        checkLinks("", "", root);
    }

    public void test2NextLinksWithSameHref() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor1 = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "dummy link");
        AnchorElement anchor2 = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page2"), "next page");
        root.appendChild(anchor1);
        root.appendChild(anchor2);

        // anchor1 by itself is not a confident next page link, but anchor2's link text helps bump
        // up the score for the shared href, so anchor1 is now a confident next page link.
        checkLinks(anchor1.getHref(), "", root);
    }

    public void disabled_testPagingParent() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element div = TestUtil.createDiv(1);
        div.setClassName("page");
        root.appendChild(div);
        // Prepend href with window location path so that base URL is part of final href to increase
        // score.
        AnchorElement anchor = TestUtil.createAnchor(
                TestUtil.formHrefWithWindowLocationPath("page1"), "dummy link");
        div.appendChild(anchor);

        // While it may seem strange that both previous and next links are the same, this test is
        // testing that the anchor's parents will affect its paging score even if it has a
        // meaningless link text like "dummy link".
        checkLinks(anchor.getHref(), anchor.getHref(), root);
    }

    public void test1PrevLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("prev", "prev page");
        root.appendChild(anchor);

        checkLinks("", anchor.getHref(), root);
    }

    public void test1PrevAnd1NextLinks() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement prevAnchor = TestUtil.createAnchor("prev", "prev page");
        AnchorElement nextAnchor = TestUtil.createAnchor("page2", "next page");
        root.appendChild(prevAnchor);
        root.appendChild(nextAnchor);

        checkLinks(nextAnchor.getHref(), prevAnchor.getHref(), root);
    }

    public void disabled_testFirstPageLinkAsBaseUrl() {
        // Some sites' first page links are the same as the base URL, previous page link needs to
        // recognize this.

        // For testcases, Window.Location.getPath() returns a ".html" file that will be stripped
        // when determining the base URL in PagingLinksFinder.findBaseUrl(), so we need to do the
        // same to use a href identical to base URL.
        String href = Window.Location.getPath();
        String htmlExt = ".html";
        if (href.indexOf(htmlExt) == href.length() - htmlExt.length()) {
            href = StringUtil.findAndReplace(href, htmlExt, "");
        }

        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor(href, "PREV");
        root.appendChild(anchor);

        checkLinks("", anchor.getHref(), root);
    }

    public void testNonHttpOrHttpsLink() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        AnchorElement anchor = TestUtil.createAnchor("javascript:void(0)",
                                                     "NEXT");
        root.appendChild(anchor);
        assertNull(PagingLinksFinder.findNext(root, "example.com"));

        anchor.setHref("file://test.html");
        assertNull(PagingLinksFinder.findNext(root, "example.com"));
    }

}
