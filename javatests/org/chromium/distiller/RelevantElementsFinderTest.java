// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RelevantElementsFinderTest extends DomDistillerJsTestCase {
    private static final Set<Node> mEmptySet = Collections.emptySet();

    public void testNoImage() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testHeaderImageBeforeContent() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element image = TestUtil.createImage();
        image.getStyle().setProperty("width", "600px");
        image.getStyle().setProperty("height", "350px");
        image.getStyle().setProperty("display", "block");

        Node contentText = TestUtil.createText("content");
        root.appendChild(image);
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(image, contentAndImages.get(0));
        assertEquals(contentText, contentAndImages.get(1));
    }

    public void testNoHeaderImage() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element image = TestUtil.createImage();
        // This image is likely an ad or logo (by size/ratio).
        image.getStyle().setProperty("width", "200px");
        image.getStyle().setProperty("height", "200px");
        image.getStyle().setProperty("display", "block");
        // Nest content to distance image.
        Element div1 = TestUtil.createDiv(1);
        Element div2 = TestUtil.createDiv(2);
        Element div3 = TestUtil.createDiv(3);
        Node contentText = TestUtil.createText("content");
        div3.appendChild(contentText);
        div2.appendChild(div3);
        div1.appendChild(div2);

        root.appendChild(image);
        root.appendChild(div1);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testSmallHeaderImageInFigureBeforeContent() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element image = TestUtil.createImage();
        Element fig = Document.get().createElement("figure");
        image.getStyle().setProperty("width", "300px");
        image.getStyle().setProperty("height", "175px");
        image.getStyle().setProperty("display", "block");
        fig.appendChild(image);

        Node contentText = TestUtil.createText("content");
        root.appendChild(fig);
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(image, contentAndImages.get(0));
        assertEquals(contentText, contentAndImages.get(1));
    }

    public void testMultipleHeaderImageCandidates() {
        // This test should only select the better of the two candidates.
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element fig = Document.get().createElement("figure");
        Element image = TestUtil.createImage();
        image.getStyle().setProperty("width", "600px");
        image.getStyle().setProperty("height", "350px");
        image.getStyle().setProperty("display", "block");
        fig.appendChild(image);

        Element image2 = TestUtil.createImage();
        image2.getStyle().setProperty("width", "300px");
        image2.getStyle().setProperty("height", "175px");
        image2.getStyle().setProperty("display", "block");

        Node contentText = TestUtil.createText("content");
        root.appendChild(image2);
        root.appendChild(fig);
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(image, contentAndImages.get(0));
        assertEquals(contentText, contentAndImages.get(1));
    }

    public void testMultipleHeaderImagesEqualScore() {
        // Only the earliest, high-scoring image should be selected.
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Element goodImage = TestUtil.createImage();
        goodImage.getStyle().setProperty("width", "600px");
        goodImage.getStyle().setProperty("height", "350px");
        goodImage.getStyle().setProperty("display", "block");
        root.appendChild(goodImage);

        Element lateImage = TestUtil.createImage();
        lateImage.getStyle().setProperty("width", "600px");
        lateImage.getStyle().setProperty("height", "350px");
        lateImage.getStyle().setProperty("display", "block");
        root.appendChild(lateImage);

        Node contentText = TestUtil.createText("content");
        root.appendChild(contentText);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(goodImage, contentAndImages.get(0));
        assertEquals(contentText, contentAndImages.get(1));
    }

    public void testImageAfterContent() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        root.appendChild(contentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
        assertEquals(image, contentAndImages.get(1));
    }

    public void testInvisibleImageAfterContentIsHidden() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        Element.as(image).getStyle().setDisplay(Display.NONE);
        root.appendChild(contentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        Set<Node> hiddenElems = Collections.singleton(image);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                hiddenElems, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageAfterContentInInvisibleParentIsHidden() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        Node parent = TestUtil.createDiv(1);
        Element.as(parent).getStyle().setDisplay(Display.NONE);
        Node image = TestUtil.createImage();
        parent.appendChild(image);
        root.appendChild(contentText);
        root.appendChild(parent);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        Set<Node> hiddenElems = Collections.singleton(parent);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                hiddenElems, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageAfterNonContent() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        Node nonContentText = TestUtil.createText("not content");
        Node image = TestUtil.createImage();
        root.appendChild(contentText);
        root.appendChild(nonContentText);
        root.appendChild(image);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(1, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
    }

    public void testImageWithDifferentParent() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node leftChild = TestUtil.createDiv(1);
        Node rightChild = TestUtil.createDiv(2);
        Node contentText = TestUtil.createText("content");
        Node image = TestUtil.createImage();
        leftChild.appendChild(contentText);
        rightChild.appendChild(image);
        root.appendChild(leftChild);
        root.appendChild(rightChild);

        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndImages = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(2, contentAndImages.size());
        assertEquals(contentText, contentAndImages.get(0));
        assertEquals(image, contentAndImages.get(1));
    }

    public void testNonDataTable() {
        Element root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        root.appendChild(contentText);

        Element div = TestUtil.createDiv(1);
        // This is not a data table because there's no <caption> or <thead> or <th> tag.
        String table = "<table>" +
                           "<tbody>" +
                               "<tr>" +
                                   "<td>row1col1</td>" +
                                   "<td>row1col2</td>" +
                               "</tr>" +
                           "</tbody>" +
                       "</table>";
        div.setInnerHTML(table);
        root.appendChild(div);

        // Append some text after table.
        Node afterTable = TestUtil.createText("some text after table");
        root.appendChild(afterTable);
        root.appendChild(TestUtil.createText("some footer text at end of root"));

        // Get the "row1col2" text node.
        NodeList<Element> allTd = div.getElementsByTagName("TD");
        assertEquals(2, allTd.getLength());
        Node td = allTd.getItem(1);
        assertEquals(1, td.getChildCount());
        Node row1col2 = td.getFirstChild();

        // Mark "content" and "row1col2" as content, to emulate real production scenario where
        // boilerpipe identifies some table cells as content.
        List<Node> contentNodes = Arrays.<Node>asList(contentText, row1col2, afterTable);
        List<Node> contentAndTable = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        assertEquals(3, contentAndTable.size());
        assertEquals(contentText, contentAndTable.get(0));
        assertEquals(row1col2, contentAndTable.get(1));
        assertEquals(afterTable, contentAndTable.get(2));
    }

    public void testNonDataTableWithNestedDataTable() {
        Node root = TestUtil.createDiv(0);
        mBody.appendChild(root);
        Node contentText = TestUtil.createText("content");
        root.appendChild(contentText);

        Element div = TestUtil.createDiv(1);
        Node table = Document.get().createTableElement();
        String html = "<tbody>" +
                          "<tr>" +
                              "<td>" +
                                  "<table>" +  // Nested data table.
                                      "<caption>Nested Data Table</caption>" +
                                      "<tbody>" +
                                          "<tr>" +
                                              "<td>row1col1</td>" +
                                          "</tr>" +
                                      "</tbody>" +
                                  "</table>" +
                              "</td>" +
                              "<td>row1col2</td>" +
                          "</tr>" +
                      "</tbody>";
        Element.as(table).setInnerHTML(html);
        div.appendChild(table);
        root.appendChild(div);

        // Mark "content" as content.
        List<Node> contentNodes = Arrays.<Node>asList(contentText);
        List<Node> contentAndTable = RelevantElementsFinder.findAndAddElements(contentNodes,
                mEmptySet, root);

        // Expected nodes: 1 "content" text node.
        assertEquals(1, contentAndTable.size());
        assertEquals(contentText, contentAndTable.get(0));
    }
}
