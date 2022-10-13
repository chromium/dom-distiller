// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.BaseElement;
import com.google.gwt.dom.client.Document;

public class PageParameterParserTest extends DomDistillerJsTestCase {
    private static final String BASE_URL = "http://www.test.com/";
    private static final String TEST_URL = BASE_URL + "foo/bar";

    public void testBasic() {
        PageParamInfo info = processDocument(
            "1<br>" +
            "<a href=\"/foo/bar/2\">2</a>");
        assertEquals(2, info.mAllPageInfo.size());

        info = processDocument(
            "1<br>" +
            "<a href=\"/foo/bar/2\">2</a>" +
            "<a href=\"/foo/bar/3\">3</a>");
        assertEquals(3, info.mAllPageInfo.size());
    }

    public void testRejectOnlyPage2LinkWithoutCurrentPageText() {
        // Although there is a digital outlink to 2nd page, there is no plain text "1"
        // before it, so there is no pagination.
        PageParamInfo info = processDocument(
            "If there were a '1', pagination should be detected. But there isn't." +
            "<a href=\"/foo/bar/2\">2</a>" +
            "Main content");
        PageParameterDetectorTest.expectEmptyPageParamInfo(info);
    }

    public void testRejectNonAdjacentOutlinks() {
        PageParamInfo info = processDocument(
            "1<br>" +
            "Unrelated terms<br>" +
            "<a href=\"/foo/bar/2\">2</a>" +
            "Unrelated terms<br>" +
            "<a href=\"/foo/bar/3\">3</a>" +
            "<a href=\"/foo/bar/all\">All</a>");
        PageParameterDetectorTest.expectEmptyPageParamInfo(info);
    }

    public void testAcceptAdjacentOutlinks() {
        PageParamInfo info = processDocumentWithoutBase(
            "Unrelated link: <a href=\"http://www.test.com/other/2\">2</a>" +
            "<p>Main content</p>" +
            "1<br>" +
            "<a href=\"http://www.test.com/foo/bar/2\">2</a>" +
            "<a href=\"http://www.test.com/foo/bar/3\">3</a>",
            TEST_URL);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/3", page.mUrl);
        assertEquals(BASE_URL + "foo/bar/2", info.mNextPagingUrl);
    }

    public void testAcceptDuplicatePatterns() {
        PageParamInfo info = processDocument(
            "1<br>" +
            "<a href=\"http://www.test.com/foo/bar/2\">2</a>" +
            "<a href=\"http://www.test.com/foo/bar/3\">3</a>" +
            "<p>Main content</p>" +
            "1<br>" +
            "<a href=\"http://www.test.com/foo/bar/2\">2</a>" +
            "<a href=\"http://www.test.com/foo/bar/3\">3</a>");
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/3", page.mUrl);
        assertEquals(BASE_URL + "foo/bar/2", info.mNextPagingUrl);
    }

    public void testPreferPageNumber() {
        PageParamInfo info = processDocument(
            "<a href=\"http://www.test.com/foo/bar/size-25\">25</a>" +
            "<a href=\"http://www.test.com/foo/bar/size-50\">50</a>" +
            "<a href=\"http://www.test.com/foo/bar/size-100\">100</a>" +
            "<p>Main content</p>" +
            "1<br>" +
            "<a href=\"http://www.test.com/foo/bar/2\">2</a>" +
            "<a href=\"http://www.test.com/foo/bar/3\">3</a>");
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals(BASE_URL + "foo/bar/3", page.mUrl);
        assertEquals(BASE_URL + "foo/bar/2", info.mNextPagingUrl);
    }

    public void testRejectMultiplePageNumberPatterns() {
        PageParamInfo info = processDocumentWithoutBase(
            "<a href=\"http://www.google.com/test/list.php?start=10\">2</a>" +
            "<a href=\"http://www.google.com/test/list.php?start=20\">3</a>" +
            "<a href=\"http://www.google.com/test/list.php?start=30\">4</a>" +
            "<p>Main content</p>" +
            "<a href=\"http://www.google.com/test/list.php?offset=10\">2</a>" +
            "<a href=\"http://www.google.com/test/list.php?offset=20\">3</a>" +
            "<a href=\"http://www.google.com/test/list.php?offset=30\">4</a>" +
            "<a href=\"http://www.google.com/test/list.php?offset=all\">All</a>",
            "http://www.google.com/test/list.php");

        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(4, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=20", page.mUrl);
        page = info.mAllPageInfo.get(3);
        assertEquals(4, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=30", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(-10, info.mFormula.mDelta);
        assertEquals("http://www.google.com/test/list.php?start=10", info.mNextPagingUrl);
    }

    public void testInvalidAndVoidLinks() {
        PageParamInfo info = processDocument(
            "1<br>" +
            "<a href=\"javascript:void(0)\">2</a>");
        PageParameterDetectorTest.expectEmptyPageParamInfo(info);
    }

    public void testDifferentHostLinks() {
        PageParamInfo info = processDocumentWithoutBase(
            "1<br>" +
            "<a href=\"http://www.foo.com/foo/bar/2\">2</a>",
            TEST_URL);
        PageParameterDetectorTest.expectEmptyPageParamInfo(info);
    }

    public void testWhitespaceSibling() {
        PageParamInfo info = processDocument(
            "1<br>" +
            "       " +
            "<a href=\"/foo/bar/2\">2</a>");
        assertEquals(2, info.mAllPageInfo.size());
    }

    public void testPunctuationSibling() {
        PageParamInfo info = processDocument(
            "<a href=\"/foo/bar/1\">1</a>" +
            "," +
            "<a href=\"/foo/bar/2\">2</a>");
        assertEquals(2, info.mAllPageInfo.size());
    }

    public void testSeparatorSibling() {
        PageParamInfo info = processDocument(
            "<div>" +
            "1 | " +
            "<a href=\"/foo/bar/2\">2</a>" +
            " | " +
            "<a href=\"/foo/bar/3\">3</a>" +
            "</div>");
        assertEquals(3, info.mAllPageInfo.size());
    }

    public void testParentSibling0() {
        PageParamInfo info = processDocumentWithoutBase(
            "<div>begin" +
            "<strong>1</strong>" +
            "<div><a href=\"http://www.test.com/foo/bar/2\">2</a></div>" +
            "<div><a href=\"http://www.test.com/foo/bar/3\">3</a></div>" +
            "end</div>",
            TEST_URL);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(TEST_URL, page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(TEST_URL + "/2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals(TEST_URL + "/3", page.mUrl);
        assertEquals("http://www.test.com/foo/bar/2", info.mNextPagingUrl);
    }

    public void testParentSibling1() {
        PageParamInfo info = processDocumentWithoutBase(
            "<div>begin" +
            "<div><a href=\"http://www.test.com/foo/bar\">1</a></div>" +
            "<strong>2</strong>" +
            "<div><a href=\"http://www.test.com/foo/bar/3\">3</a></div>" +
            "end</div>",
            "http://www.test.com/foo/bar/2");
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(TEST_URL, page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals(TEST_URL + "/3", page.mUrl);
        assertEquals("http://www.test.com/foo/bar/3", info.mNextPagingUrl);
    }

    public void testParentSibling2() {
        PageParamInfo info = processDocumentWithoutBase(
            "<div>begin" +
            "<div><a href=\"http://www.test.com/foo/bar\">1</a></div>" +
            "<div><a href=\"http://www.test.com/foo/bar/2\">2</a></div>" +
            "<strong>3</strong>" +
            "end</div>",
            "http://www.test.com/foo/bar/3");
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(TEST_URL, page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(TEST_URL + "/2", page.mUrl);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testNestedStructure() {
        PageParamInfo info = processDocumentWithoutBase(
            "<div>begin" +
            "<span><a href=\"http://www.test.com/foo?page=2\">&lsaquo;&lsaquo; Prev</a></span>" +
            "<span><a href=\"http://www.test.com/foo?page=1\">1</a></span>" +
            "<span><a href=\"http://www.test.com/foo?page=2\">2</a></span>" +
            "<span>3</span>" +
            "<span><a href=\"http://www.test.com/foo?page=4\">4</a></span>" +
            "<span><a href=\"http://www.test.com/foo?page=5\">5</a></span>" +
            "<span>...</span>" +
            "<span><a href=\"http://www.test.com/foo?page=48\">48</a></span>" +
            "<span><a href=\"http://www.test.com/foo?page=4\">Next &rsaquo;&rsaquo;</a></span>" +
            "</div>",
            "http://www.test.com/foo?page=3");
        assertEquals(5, info.mAllPageInfo.size());
        final String urlPrefix = "http://www.test.com/foo?page=";
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals(urlPrefix + "1", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals(urlPrefix + "2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(4, page.mPageNum);
        assertEquals(urlPrefix + "4", page.mUrl);
        page = info.mAllPageInfo.get(3);
        assertEquals(5, page.mPageNum);
        assertEquals(urlPrefix + "5", page.mUrl);
        page = info.mAllPageInfo.get(4);
        assertEquals(48, page.mPageNum);
        assertEquals(urlPrefix + "48", page.mUrl);
        assertEquals(urlPrefix + "4", info.mNextPagingUrl);
    }

    private PageParamInfo processDocument(String content) {
        // Create and add a <base> element so that all anchors are based off it.
        BaseElement baseTag = Document.get().createBaseElement();
        baseTag.setHref(BASE_URL);
        mHead.appendChild(baseTag);

        // Append content to body.
        mBody.setInnerHTML(content);

        PageParamInfo info = PageParameterParser.parse(TEST_URL, null);
        mHead.removeChild(baseTag);
        return info;
    }

    private PageParamInfo processDocumentWithoutBase(String content, String originalUrl) {
        // Append content to body.
        mBody.setInnerHTML(content);
        return PageParameterParser.parse(originalUrl, null);
    }

}
