// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.TestUtil;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class WebTableTest extends DomDistillerJsTestCase {
    public void testGetImageUrlList() {
        mHead.setInnerHTML("<base href=\"http://example.com/\">");
        Element table = Document.get().createTableElement();

        String html =
                "<tbody>" +
                  "<tr>" +
                    "<td><img src=\"http://example.com/table.png\" " +
                             "srcset=\"image100 100w, //example.org/image300 300w\"></td>" +
                    "<td>" +
                      "<picture>" +
                        "<source srcset=\"image200 200w, //example.org/image400 400w\">" +
                        "<img>" +
                      "</picture>" +
                    "</td>" +
                  "</tr>" +
                "</tbody>";

        table.setInnerHTML(html);
        mBody.appendChild(table);

        WebTable webTable = new WebTable(table);
        List<String> urls = webTable.getImageUrlList();
        assertEquals(5, urls.size());
        assertEquals("http://example.com/table.png", urls.get(0));
        assertEquals("http://example.com/image100", urls.get(1));
        assertEquals("http://example.org/image300", urls.get(2));
        assertEquals("http://example.com/image200", urls.get(3));
        assertEquals("http://example.org/image400", urls.get(4));
    }

    public void testGenerateOutput() {
        Element table = Document.get().createTableElement();
        String html = "<tbody>" +
                          "<tr>" +
                              "<td>row1col1</td>" +
                              "<td><img src=\"http://example.com/table.png\"></td>" +
                              "<td><picture><img></picture></td>" +
                          "</tr>" +
                      "</tbody>";
        table.setInnerHTML(html);
        mBody.appendChild(table);

        WebTable webTable = new WebTable(table);
        String got = webTable.generateOutput(false);

        // Output should be the same as the input in this case.
        assertEquals("<table>" + html + "</table>", TestUtil.removeAllDirAttributes(got));

        // Test getImageUrlList() as well.
        List<String> imgUrls = webTable.getImageUrlList();
        assertEquals(1, imgUrls.size());
        assertEquals("http://example.com/table.png", imgUrls.get(0));
    }
}
