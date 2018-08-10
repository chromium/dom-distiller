// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;

public class DocumentTitleGetterTest extends DomDistillerJsTestCase {
    public void testNonStringWithoutRoot() {
        Element img = TestUtil.createImage();
        img.setAttribute("name", "title"); // cause document.title to return the img element.
        String title = DocumentTitleGetter.getDocumentTitle(img, null);
        assertEquals("", title);
    }

    public void testNonStringWithTitlelessRoot() {
        Element root = TestUtil.createDiv(0);
        Element img = TestUtil.createImage();
        img.setAttribute("name", "title"); // cause document.title to return the img element.
        String title = DocumentTitleGetter.getDocumentTitle(img, root);
        assertEquals("", title);
    }

    public void testNonStringWithTitledRoot() {
        Element root = TestUtil.createDiv(0);
        Element img = TestUtil.createImage();
        img.setAttribute("name", "title"); // cause document.title to return the img element.
        String titleString = "testing non-string document.title with a titled root";
        Element titleElem = TestUtil.createTitle(titleString);
        root.appendChild(titleElem);
        String title = DocumentTitleGetter.getDocumentTitle(img, root);
        assertEquals(titleString, title);
    }

    public void testNonStringWithMultiTitledRoot() {
        Element root = TestUtil.createDiv(0);
        Element img = TestUtil.createImage();
        img.setAttribute("name", "title"); // cause document.title to return the img element.
        String titleString = "first testing non-string document.title with a titled root";
        Element titleElem1 = TestUtil.createTitle(titleString);
        Element titleElem2 = TestUtil.createTitle(
            "second testing non-string document.title with a titled root");
        root.appendChild(titleElem1);
        root.appendChild(titleElem2);
        String title = DocumentTitleGetter.getDocumentTitle(img, root);
        assertEquals(titleString, title);
    }

    public void test1Dash2ShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "before dash - after dash", null);
        assertEquals("before dash - after dash", title);
    }

    public void test1Dash2LongParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "part with 6 words before dash - part with 6 words after dash", null);
        assertEquals("part with 6 words before dash", title);
    }

    public void test1Dash2LongPartsChinese() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "比較長一點的句子 - 這是不要的部分", null);
        assertEquals("比較長一點的句子", title);
    }

    public void test1DashLongAndShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "part with 6 words before dash - after dash", null);
        assertEquals("part with 6 words before dash", title);
    }

    public void test1DashShortAndLongParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "before dash - part with 6 words after dash", null);
        assertEquals("part with 6 words after dash", title);
    }

    public void test1DashShortAndLongPartsChinese() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "短語 - 比較長一點的句子", null);
        assertEquals("比較長一點的句子", title);
    }

    public void test2DashesShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "before dash - between dash0 and dash1 - after dash1", null);
        assertEquals("before dash - between dash0 and dash1", title);
    }

    public void test2DashesShortAndLongParts() {
        // TODO(kuan): if using RegExp.split, this fails with "ant test.prod".
        String title = DocumentTitleGetter.getDocumentTitle(
                "before - - part with 6 words after dash", null);
        assertEquals("- part with 6 words after dash", title);
    }

    public void test1Bar2ShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "before bar | after bar", null);
        assertEquals("before bar | after bar", title);
    }

    public void test2ColonsShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "start : midway : end", null);
        assertEquals("start : midway : end", title);
    }

    public void test2ColonsShortPartsChinese() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "開始 : 中間 : 最後", null);
        assertEquals("開始 : 中間 : 最後", title);
    }

    public void test2ColonsShortAndLongParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "start : midway : part with 6 words at end", null);
        assertEquals("part with 6 words at end", title);
    }

    public void test2ColonsShortAndLongPartsChinese() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "開始 : 中間 : 最後比較長的部分", null);
        assertEquals("最後比較長的部分", title);
    }

    public void test2ColonsShortAndLongAndShortParts() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "start : part with 6 words at midway : end", null);
        assertEquals("part with 6 words at midway : end", title);
    }

    public void test2ColonsShortAndLongAndShortPartsChinese() {
        String title = DocumentTitleGetter.getDocumentTitle(
                "開始 : 中間要的部分 : 最後", null);
        assertEquals("中間要的部分 : 最後", title);
    }

    public void testH1WithShortText() {
        Element root = TestUtil.createDiv(0);
        Element h1 = TestUtil.createHeading(1, "short heading");
        root.appendChild(h1);
        mBody.appendChild(root);

        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        assertEquals("short title", title);
    }

    public void testH1WithLongText() {
        Element root = TestUtil.createDiv(0);
        Element h1 = TestUtil.createHeading(1, "long heading with 5 words");
        root.appendChild(h1);
        mBody.appendChild(root);

        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        assertEquals("long heading with 5 words", title);
    }

    public void testMultiHeadingsWithLongText() {
        Element root = TestUtil.createDiv(0);
        Element h1 = TestUtil.createHeading(1, "long heading1 with 5 words");
        Element h2 = TestUtil.createHeading(2, "long heading2 with 5 words");
        root.appendChild(h1);
        root.appendChild(h2);
        mBody.appendChild(root);

        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        assertEquals("long heading1 with 5 words", title);
    }

    public void testH1WithLongHTML() {
        Element root = TestUtil.createDiv(0);
        Element h1 = TestUtil.createHeading(1,
                "<a href=\"http://longheading.com\"><b>long heading</b></a> with <br>5 words");
        root.appendChild(h1);
        mBody.appendChild(root);

        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        // The title is "long heading with \n5 words" in older version of Chrome, when not rendered.
        // It becomes "long heading with\n5 words" after http://crrev.com/c/1114673, when rendered.
        // Note the trailing space is gone. Both results are acceptable.
        assertEquals("long heading with 5 words",
            title.replaceAll("\n", " ").replaceAll("  ", " "));
    }

    public void testH1WithLongHTMLWithNbsp() {
        Element root = TestUtil.createDiv(0);
        Element h1 = TestUtil.createHeading(1,
                "<a href=\"http://longheading.com\"><b> &nbsp;long heading</b></a> with 5 words &nbsp; ");
        root.appendChild(h1);
        mBody.appendChild(root);

        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        assertEquals("long heading with 5 words", title);
    }

    public void testShortTitleWithoutRoot() {
        String title = DocumentTitleGetter.getDocumentTitle("short title", null);
        assertEquals("short title", title);
    }

    public void testShortTitleWithoutH1() {
        Element root = TestUtil.createDiv(0);
        String title = DocumentTitleGetter.getDocumentTitle("short title", root);
        assertEquals("short title", title);
    }
}
