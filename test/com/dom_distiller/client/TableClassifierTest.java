// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableElement;

import com.google.gwt.junit.client.GWTTestCase;

public class TableClassifierTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testUnknown() {
        TableElement table = Document.get().createTableElement();
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>heading1</td>" +
                                  "<td>heading2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);
        assertEquals(TableClassifier.Type.UNKNOWN, TableClassifier.getType(table));
    }

    public void testRoleAttribute() {
        TableElement table = Document.get().createTableElement();
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<th>heading1</th>" +
                                  "<th>heading2</th>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);
        table.setAttribute("role", "presentation");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.getType(table));
    }

    public void testCaptionTag() {
        TableElement table = Document.get().createTableElement();
        String tableStr = "<caption>Testing Caption</caption>" +
                          "<tbody>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.getType(table));
    }

    public void testTHeadTag() {
        TableElement table = Document.get().createTableElement();
        String tableStr = "<thead>" +
                              "<tr>" +
                                  "<th>heading1</th>" +
                                  "<th>heading2</th>" +
                              "</tr>" +
                          "</thead>" +
                          "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.getType(table));
    }

    public void testTHTagInTable() {
        TableElement table = Document.get().createTableElement();
        // This is identical to the table used for testRoleAttribute().
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<th>heading1</th>" +
                                  "<th>heading2</th>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.getType(table));
    }

    public void testTHTagInNestedTable() {
        TableElement table = Document.get().createTableElement();
        // This is identical to the table used for testRoleAttribute().
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  // Will insert a nested table node.
                              "</tr>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                          "</tbody>";
        table.setInnerHTML(tableStr);

        // Create and insert nested table node.
        TableElement nestedTable = Document.get().createTableElement();
        String nestedTableStr = "<tbody>" +
                                    "<tr>" +
                                        "<th>heading1</th>" +
                                        "<th>heading2</th>" +
                                    "</tr>" +
                                "</tbody>";
        nestedTable.setInnerHTML(nestedTableStr);
        NodeList<Element> rows = table.getElementsByTagName("TR");
        assertEquals(2, rows.getLength());
        rows.getItem(0).appendChild(nestedTable);

        assertEquals(TableClassifier.Type.UNKNOWN, TableClassifier.getType(table));
        assertEquals(TableClassifier.Type.DATA, TableClassifier.getType(nestedTable));
    }
}
