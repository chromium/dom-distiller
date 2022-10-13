// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class PageParameterDetectorTest extends DomDistillerJsTestCase {

    public void testDynamicPara() {
        final String testUrl = "http://bbs.globalimporter.net/bbslist-N11101107-33-0.htm";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://bbs.globalimporter.net/bbslist-N11101107-32-0.htm", 32),
            NumberInPlainText(33),
            NumericOutlink("http://bbs.globalimporter.net/bbslist-N11101107-34-0.htm", 34),
            NumericOutlink("http://bbs.globalimporter.net/bbslist-N11101107-35-0.htm", 35),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(32, page.mPageNum);
        assertEquals("http://bbs.globalimporter.net/bbslist-N11101107-32-0.htm", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(34, page.mPageNum);
        assertEquals("http://bbs.globalimporter.net/bbslist-N11101107-34-0.htm", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(35, page.mPageNum);
        assertEquals("http://bbs.globalimporter.net/bbslist-N11101107-35-0.htm", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://bbs.globalimporter.net/bbslist-N11101107-34-0.htm",
                info.mNextPagingUrl);
    }

    public void testDynamicParaForComma() {
        final String testUrl = "http://forum.interia.pl/forum/praca,1094,2,2515,0,0";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", 2),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", 3),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", 4),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", 5),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(4, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(2, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(4, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(3);
        assertEquals(5, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://forum.interia.pl/forum/praca,1094,3,2515,0,0",
                info.mNextPagingUrl);
    }

    public void testStaticPara() {
        final String testUrl = "http://www.google.com/forum";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/forum?page=2", 2),
            NumericOutlink("http://www.google.com/forum?page=3", 3),
            NumericOutlink("http://www.google.com/forum?page=4", 4),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(4, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/forum", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/forum?page=2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/forum?page=3", page.mUrl);
        page = info.mAllPageInfo.get(3);
        assertEquals(4, page.mPageNum);
        assertEquals("http://www.google.com/forum?page=4", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://www.google.com/forum?page=2", info.mNextPagingUrl);
    }

    public void testStaticParaPageAtSuffix() {
        final String testUrl = "http://www.google.com/forum/0";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/forum/1", 2),
            NumericOutlink("http://www.google.com/forum/2", 3),
            NumericOutlink("http://www.google.com/forum/3", 4),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/forum/1", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/forum/2", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(4, page.mPageNum);
        assertEquals("http://www.google.com/forum/3", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(-1, info.mFormula.mDelta);
        assertEquals("http://www.google.com/forum/1", info.mNextPagingUrl);
    }

    public void testHandleOnlyHasPreviousPage() {
        // The current doc is the 2nd page and has only digital outlink which points to 1st page.
        final String testUrl = "http://www.google.com/forum/thread-20";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum/thread-0", 1),
            NumberInPlainText(2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/forum/thread-0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/forum/thread-20", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(20, info.mFormula.mCoefficient);
        assertEquals(-20, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testRejectOnlyPage1LinkWithoutCurrentPageText() {
        // Although there is a digital outlink to 1st page, there is no plain text "2" after it, so
        // there is no pagination.
        final String testUrl = "http://www.google.com/forum/thread-20";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum/thread-0", 1),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testHandleOnlyHasNextPage() {
        // The current doc is the 1st page and has only digital outlink which points to 2nd page.
        final String testUrl = "http://www.google.com/forum?page=0";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/forum?page=1", 2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/forum?page=0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/forum?page=1", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(-1, info.mFormula.mDelta);
        assertEquals("http://www.google.com/forum?page=1", info.mNextPagingUrl);
    }

    public void testRejectOnlyPage2LinkWithoutCurrentPageText() {
        // Although there is a digital outlink to 2nd page, there is no plain text "1" before it,
        // so there is no pagination.
        final String testUrl = "http://www.google.com/forum?page=0";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum?page=1", 2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testHandleOnlyTwoPartialPages() {
        final String testUrl = "http://www.google.com/forum/1";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum/0", 1),
            NumberInPlainText(2),
            NumericOutlink("http://www.google.com/forum/2", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/forum/0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/forum/2", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(-1, info.mFormula.mDelta);
        assertEquals("http://www.google.com/forum/2", info.mNextPagingUrl);
    }

    public void testHandleCalendarPageDynamic() {
        final String testUrl = "http://www.google.com/forum/1";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum/1?m=20101201", 1),
            NumericOutlink("http://www.google.com/forum/1?m=20101202", 2),
            NumericOutlink("http://www.google.com/forum/1?m=20101204", 4),
            NumericOutlink("http://www.google.com/forum/1?m=20101206", 6),
        };
        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testHandleCalendarPageStatic() {
        final String testUrl = "http://www.google.com/forum/foo";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/forum/foo/2010/12/01", 1),
            NumericOutlink("http://www.google.com/forum/foo/2010/12/02", 2),
            NumericOutlink("http://www.google.com/forum/foo/2010/12/04", 4),
            NumericOutlink("http://www.google.com/forum/foo/2010/12/06", 6),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testHandleOutlinksInCalendar() {
        final String testUrl = "http://www.google.com/forum/foo";
        final int ARRAY_SIZE = 32;
        final PageParamContentInfo[] allContentInfo = new PageParamContentInfo[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            allContentInfo[i] = NumericOutlink("http://www.google.com/forum/foo/" + (i + 1), i + 1);
        }

        // Outlinks "1" through "30", likely to be a calendar.
        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, 30);
        expectEmptyPageParamInfo(info);

        // 27 outlinks, not a calendar.
        info = detectPageParameter(testUrl, allContentInfo, 27);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(27, info.mAllPageInfo.size());

        // 32 outlinks, not a calendar.
        info = detectPageParameter(testUrl, allContentInfo, 32);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(32, info.mAllPageInfo.size());
    }

    public void testFilterYearCalendar() {
        final String testUrl = "http://technet.microsoft.com/en-us/sharepoint/ff628785.aspx";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://technet.microsoft.com/en-us/sharepoint/fp123618", 2010),
            NumericOutlink("http://technet.microsoft.com/en-us/sharepoint/fp142366", 2013),
        };
        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    // Some pages only have two partial pages while the first page doesn't specify the page
    // parameter, like www.slate.com/id/2278628.
    public void testHandleFirstPageWithoutParam1() {
        final String testUrl = "http://www.google.com/id/2278628";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/id/2278628/pagenum/2", 2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628/pagenum/2", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://www.google.com/id/2278628/pagenum/2", info.mNextPagingUrl);
    }

    // When there is only one digital link on the page, we insert the doc url into page num vector
    // as well.
    public void testHandleFirstPageWithoutParam2() {
        final String testUrl = "http://www.google.com/id/2278628/pagenum/2";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/id/2278628", 1),
            NumberInPlainText(2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628/pagenum/2", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testHandleFirstPageWithoutParam3() {
        final String testUrl = "http://www.google.com/id/2278628/pagenum/2";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/id/2278628", 1),
            NumberInPlainText(2),
            NumericOutlink("http://www.google.com/id/2278628/pagenum/3", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628/pagenum/3", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://www.google.com/id/2278628/pagenum/3", info.mNextPagingUrl);
    }

    public void testHandleFirstPageWithoutParam4() {
        final String testUrl = "http://www.google.com/id/2278628/";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/id/2278628?pagenum=2", 2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628/", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/id/2278628?pagenum=2", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://www.google.com/id/2278628?pagenum=2", info.mNextPagingUrl);
    }

    public void testBadPageParamName() {
        final String testUrl = "http://www.google.com/12345/tag/1";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/12345/tag/2.html", 2),
            NumericOutlink("http://www.google.com/12345/tag/3.html", 3),
        };
        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testCheckFirstPathComponent() {
        final String testUrl = "http://www.google.com/1";
        final PageParamContentInfo[] allContentInfo = {
            NumberInPlainText(1),
            NumericOutlink("http://www.google.com/2", 2),
            NumericOutlink("http://www.google.com/3", 3),
        };
        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testPatternValidation() {
        {
            final String testUrl = "http://www.google.com/download/foo";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/2", 2),
                NumericOutlink("http://www.google.com/article/3", 3),
            };
            // If the page param is or part of a path component, both the pattern and document url
            // must have similar path: "article" differs from "download/foo".
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            expectEmptyPageParamInfo(info);
        }
        {
            final String testUrl = "http://www.google.com/article/foo";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3),
            };
            // If the page param is a query, both the pattern and document url must have same path
            // components: "article/foo" differs from "article/bar".
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            expectEmptyPageParamInfo(info);
        }
        {
            final String testUrl = "http://www.google.com/foo-bar-article";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/foo-bar-article-2", 2),
                NumericOutlink("http://www.google.com/foo-bar-article-3", 3),
            };
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
            assertEquals(3, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(1, page.mPageNum);
            assertEquals("http://www.google.com/foo-bar-article", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/foo-bar-article-2", page.mUrl);
            page = info.mAllPageInfo.get(2);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/foo-bar-article-3", page.mUrl);
            assertTrue(info.mFormula != null);
            assertEquals(1, info.mFormula.mCoefficient);
            assertEquals(0, info.mFormula.mDelta);
            assertEquals("http://www.google.com/foo-bar-article-2", info.mNextPagingUrl);
        }
        {
            final String testUrl = "http://www.google.com/foo/bar/article";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/foo-bar-article-2", 2),
                NumericOutlink("http://www.google.com/foo-bar-article-3", 3),
            };
            // If the page param is or part of a path component, both the pattern and document url
            // must have similar path: "foo/bar/article" differs from "foo-bar-article".
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            expectEmptyPageParamInfo(info);
        }
    }

    public void testTooManyPagingDocuments() {
        final String docUrl = "http://www.google.com/thread/page/1";

        MonotonicPageInfosGroups pages = new MonotonicPageInfosGroups();
        pages.addGroup();

        for (int i = 1; i <= PageParameterDetector.MAX_PAGING_DOCS; i++) {
            pages.addNumber(i, "http://www.google.com/thread/page/" + i);
        }

        PageParamInfo info = PageParameterDetector.detect(pages, docUrl);
        assertEquals(PageParameterDetector.MAX_PAGING_DOCS, info.mAllPageInfo.size());

        pages.addNumber(PageParameterDetector.MAX_PAGING_DOCS + 1,
                "http://www.google.com/thread/page/" +
                        (PageParameterDetector.MAX_PAGING_DOCS + 1));
        info = PageParameterDetector.detect(pages, docUrl);
        expectEmptyPageParamInfo(info);
    }

    public void testInsertFirstPage() {
        {
            final String testUrl = "http://www.google.com/article/bar";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3),
            };
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
            // The current document is inserted as first page.
            assertEquals(3, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(1, page.mPageNum);
            assertEquals("http://www.google.com/article/bar", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2", page.mUrl);
            page = info.mAllPageInfo.get(2);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3", page.mUrl);
            assertTrue(info.mFormula != null);
            assertEquals(1, info.mFormula.mCoefficient);
            assertEquals(0, info.mFormula.mDelta);
            assertEquals("http://www.google.com/article/bar?page=2", info.mNextPagingUrl);
        }
        {
            // Current document url has same length as other paginated pages, so it shouldn't be
            // inserted as first page.
            final String testUrl = "http://www.google.com/article/bar?page=1";
            final PageParamContentInfo[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3),
            };
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
            assertEquals(2, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3", page.mUrl);
            assertTrue(info.mFormula != null);
            assertEquals(1, info.mFormula.mCoefficient);
            assertEquals(0, info.mFormula.mDelta);
            assertEquals("http://www.google.com/article/bar?page=2", info.mNextPagingUrl);
        }
        {
            // Current document url is the last page, so shouldn't be inserted as first page.
            final String testUrl = "http://www.google.com/article/bar?page=4";
            final PageParamContentInfo[] allContentInfo = {
                NumericOutlink("http://www.google.com/article/bar", 1),
                NumericOutlink("http://www.google.com/article/bar?page=2&s=11", 2),
                NumericOutlink("http://www.google.com/article/bar?page=3&s=11", 3),
                NumberInPlainText(4),
            };
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
            assertEquals(2, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2&s=11", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3&s=11", page.mUrl);
            assertTrue(info.mFormula != null);
            assertEquals(1, info.mFormula.mCoefficient);
            assertEquals(0, info.mFormula.mDelta);
            assertTrue(info.mNextPagingUrl.isEmpty());
        }
        {
            // Page one has an outlink to itself, should be inserted as first page.
            final String testUrl = "http://www.google.com/article/bar";
            final PageParamContentInfo[] allContentInfo = {
                NumericOutlink("http://www.google.com/article/bar", 1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3),
            };
            PageParamInfo info = detectPageParameter(testUrl, allContentInfo,
                    allContentInfo.length);
            assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
            assertEquals(3, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(1, page.mPageNum);
            assertEquals("http://www.google.com/article/bar", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2", page.mUrl);
            page = info.mAllPageInfo.get(2);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3", page.mUrl);
            assertTrue(info.mFormula != null);
            assertEquals(1, info.mFormula.mCoefficient);
            assertEquals(0, info.mFormula.mDelta);
            assertEquals("http://www.google.com/article/bar?page=2", info.mNextPagingUrl);
        }
    }

    public void testRejectNonAdjacentOutlinks() {
        final String testUrl = "http://forum.interia.pl/forum/praca,1094,2,2515,0,0";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", 2),
            UnrelatedTerms(),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", 3),
            UnrelatedTerms(),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", 4),
            UnrelatedTerms(),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", 5),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        expectEmptyPageParamInfo(info);
    }

    public void testAcceptAdjacentOutlinks() {
        final String testUrl = "http://www.google.com/test/list.php?start=0";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/foo/2", 2),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?start=0", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=10", 2),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(2, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(-10, info.mFormula.mDelta);
        assertEquals("http://www.google.com/test/list.php?start=10", info.mNextPagingUrl);
    }

    public void testAcceptDuplicatePatterns() {
        final String testUrl = "http://forum.interia.pl/forum/praca,1094,2,2515,0,0";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", 2),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", 3),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", 4),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", 5),
            UnrelatedTerms(),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", 2),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", 3),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", 4),
            NumericOutlink("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", 5),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(4, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(2, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,2,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(3, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,3,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(4, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,4,2515,0,0", page.mUrl);
        page = info.mAllPageInfo.get(3);
        assertEquals(5, page.mPageNum);
        assertEquals("http://forum.interia.pl/forum/praca,1094,5,2515,0,0", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(1, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertEquals("http://forum.interia.pl/forum/praca,1094,3,2515,0,0",
                info.mNextPagingUrl);
    }

    public void testPreferPageNumberThanPageSize() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=10", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 3),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 4),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?size=20", 20),
            NumericOutlink("http://www.google.com/test/list.php?size=50", 50),
            NumericOutlink("http://www.google.com/test/list.php?size=100", 100),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
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

    public void testRejectMultiPageNumberPatterns() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php?start=10", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 3),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 4),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?offset=20", 2),
            NumericOutlink("http://www.google.com/test/list.php?offset=30", 3),
            NumericOutlink("http://www.google.com/test/list.php?offset=40", 4),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
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

    public void testPreferLinearFormulaPattern() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php?start=10", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?size=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?size=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?size=32523516", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=20", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=30", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testPreferLinearFormulaPattern2() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php?size=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?size=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?size=32523516", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?foo=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?foo=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?foo=32523516", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?start=10", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=20", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=30", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testPreferLinearFormulaPattern3() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php?size=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?size=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?size=32523516", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?start=10", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?foo=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?foo=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?foo=32523516", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=20", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=30", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    public void testPreferLinearFormulaPattern4() {
        final String testUrl = "http://www.google.com/test/list.php";
        final PageParamContentInfo[] allContentInfo = {
            NumericOutlink("http://www.google.com/test/list.php?start=10", 1),
            NumericOutlink("http://www.google.com/test/list.php?start=20", 2),
            NumericOutlink("http://www.google.com/test/list.php?start=30", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?size=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?size=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?size=32523516", 3),
            UnrelatedTerms(),
            NumericOutlink("http://www.google.com/test/list.php?foo=21324235", 1),
            NumericOutlink("http://www.google.com/test/list.php?foo=21435252", 2),
            NumericOutlink("http://www.google.com/test/list.php?foo=32523516", 3),
        };

        PageParamInfo info = detectPageParameter(testUrl, allContentInfo, allContentInfo.length);
        assertEquals(PageParamInfo.Type.PAGE_NUMBER, info.mType);
        assertEquals(3, info.mAllPageInfo.size());
        PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
        assertEquals(1, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=10", page.mUrl);
        page = info.mAllPageInfo.get(1);
        assertEquals(2, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=20", page.mUrl);
        page = info.mAllPageInfo.get(2);
        assertEquals(3, page.mPageNum);
        assertEquals("http://www.google.com/test/list.php?start=30", page.mUrl);
        assertTrue(info.mFormula != null);
        assertEquals(10, info.mFormula.mCoefficient);
        assertEquals(0, info.mFormula.mDelta);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

    private static PageParamContentInfo UnrelatedTerms() {
        return PageParamContentInfo.UnrelatedTerms();
    }

    private static PageParamContentInfo NumberInPlainText(int number) {
        return PageParamContentInfo.NumberInPlainText(number);
    }

    private static PageParamContentInfo NumericOutlink(String targetUrl, int number) {
        return PageParamContentInfo.NumericOutlink(targetUrl, number);
    }

    private static PageParamInfo detectPageParameter(String docUrl,
            PageParamContentInfo[] allContentInfo, int contentInfoSize) {
        MonotonicPageInfosGroups adjacentNumbersGroups = new MonotonicPageInfosGroups();
        adjacentNumbersGroups.addGroup();

        for (int i = 0; i < contentInfoSize; i++) {
            PageParamContentInfo content = allContentInfo[i];
            switch (content.mType) {
                case UNRELATED_TERMS:
                    adjacentNumbersGroups.addGroup();
                    break;
                case NUMBER_IN_PLAIN_TEXT:
                case NUMERIC_OUTLINK:
                    adjacentNumbersGroups.addNumber(content.mNumber,
                            content.mType == PageParamContentInfo.Type.NUMERIC_OUTLINK ?
                                    content.mTargetUrl : "");
                    break;
            }
        }

        return PageParameterDetector.detect(adjacentNumbersGroups, docUrl);
    }

    static void expectEmptyPageParamInfo(PageParamInfo info) {
        assertEquals(PageParamInfo.Type.UNSET, info.mType);
        assertEquals(0, info.mAllPageInfo.size());
        assertTrue(info.mFormula == null);
        assertTrue(info.mNextPagingUrl.isEmpty());
    }

}
