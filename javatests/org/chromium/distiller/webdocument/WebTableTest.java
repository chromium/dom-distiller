// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.TestUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.List;

public class WebTableTest extends DomDistillerJsTestCase {
    public void testGenerateOutput() {
        Element table = Document.get().createTableElement();
        String html = "<tbody>" +
                          "<tr>" +
                              "<td>row1col1</td>" +
                              "<td><img src=\"http://example.com/table.png\"></td>" +
                          "</tr>" +
                      "</tbody>";
        table.setInnerHTML(html);
        mBody.appendChild(table);

        WebTable webTable = new WebTable(table);
        String got = webTable.generateOutput(false);

        // Output should be the same as the input in this case.
        assertEquals("<table>" + html + "</table>", TestUtil.removeAllDirAttributes(got));
    }
}
