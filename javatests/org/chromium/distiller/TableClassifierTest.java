// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableElement;

public class TableClassifierTest extends DomDistillerJsTestCase {
    public void testDocumentWidth() {
        assertEquals(800, mRoot.getOffsetWidth());
        assertEquals("800px", DomUtil.getComputedStyle(mRoot).getProperty("width"));
    }

    public void testInputElement() {
        Element input = Document.get().createTextInputElement();
        mBody.appendChild(input);
        TableElement table = createDefaultTableWithTH();
        input.appendChild(table);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.INSIDE_EDITABLE_AREA, TableClassifier.sReason);
    }

    public void testContentEditableAttribute() {
        Element div = TestUtil.createDiv(0);
        mBody.appendChild(div);
        div.setAttribute("contenteditable", "true");
        TableElement table = createDefaultTableWithTH();
        div.appendChild(table);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.INSIDE_EDITABLE_AREA, TableClassifier.sReason);
    }

    public void testRolePresentation() {
        TableElement table = createDefaultTableWithTH();
        table.setAttribute("role", "presentation");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_TABLE, TableClassifier.sReason);
    }

    public void testRoleGrid() {
        TableElement table = createDefaultTableWithNoTH();
        table.setAttribute("role", "grid");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_TABLE, TableClassifier.sReason);
    }

    public void testRoleGridNested() {
        TableElement table = createDefaultTableWithNoTH();
        TableElement nestedTable = createDefaultNestedTableWithNoTH(table);
        nestedTable.setAttribute("role", "grid");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.NESTED_TABLE, TableClassifier.sReason);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(nestedTable));
        assertEquals(TableClassifier.Reason.ROLE_TABLE, TableClassifier.sReason);
    }

    public void testRoleTreeGrid() {
        TableElement table = createDefaultTableWithNoTH();
        table.setAttribute("role", "treegrid");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_TABLE, TableClassifier.sReason);
    }

    public void testRoleGridCell() {
        TableElement table = createDefaultTableWithNoTH();
        setRoleForFirstElement(table, "TD", "gridcell");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleGridCellNested() {
        TableElement table = createDefaultTableWithNoTH();
        TableElement nestedTable = createDefaultNestedTableWithNoTH(table);
        setRoleForFirstElement(nestedTable, "TD", "gridcell");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.NESTED_TABLE, TableClassifier.sReason);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(nestedTable));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleColumnHeader() {
        TableElement table = createDefaultTableWithNoTH();
        setRoleForFirstElement(table, "TD", "columnheader");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleRow() {
        TableElement table = createDefaultTableWithNoTH();
        setRoleForFirstElement(table, "TR", "row");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleRowGroup() {
        TableElement table = createDefaultTableWithNoTH();
        setRoleForFirstElement(table, "TBODY", "rowgroup");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleRowHeader() {
        TableElement table = createDefaultTableWithNoTH();
        setRoleForFirstElement(table, "TR", "rowheader");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testRoleLandmark() {
        TableElement table = createDefaultTableWithNoTH();
        // Test landmark role in <table> element.
        table.setAttribute("role", "application");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_TABLE, TableClassifier.sReason);

        // Test landmark role in table's descendant.
        table.removeAttribute("role");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
        setRoleForFirstElement(table, "TR", "navigation");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ROLE_DESCENDANT, TableClassifier.sReason);
    }

    public void testDatatableAttribute() {
        TableElement table = createDefaultTableWithTH();
        table.setAttribute("datatable", "0");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.DATATABLE_0, TableClassifier.sReason);
    }

    public void testCaptionTag() {
        String caption = "<caption>Testing Caption</caption>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(caption + table.getInnerHTML());
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testEmptyCaptionTag() {
        String caption = "<caption></caption>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(caption + table.getInnerHTML());
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void testAllWhitespacedCaptionTag() {
        String caption = "<caption>&nbsp;  &nbsp;</caption>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(caption + table.getInnerHTML());
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void testTHeadTag() {
        String thead = "<thead>" +
                          "<tr>" +
                              "<th>heading1</th>" +
                              "<th>heading2</th>" +
                          "</tr>" +
                      "</thead>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(thead + table.getInnerHTML());
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testTFootTag() {
        String tfoot = "<tfoot>" +
                          "<tr>" +
                              "<td>total1</td>" +
                              "<td>total2</td>" +
                          "</tr>" +
                      "</tfoot>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(tfoot + table.getInnerHTML());
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testColGroupTag() {
        String colgroup = "<colgroup>" +
                              "<col span=\"2\">" +
                              "<col align=\"left\">" +
                          "</colgroup>";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(colgroup + table.getInnerHTML());
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testColTag() {
        String col = "<col span=\"2\">";
        TableElement table = createDefaultTableWithNoTH();
        table.setInnerHTML(col + table.getInnerHTML());
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testTHTag() {
        TableElement table = createDefaultTableWithTH();
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testTHTagNested() {
        TableElement table = createDefaultTableWithNoTH();
        TableElement nestedTable = createDefaultNestedTableWithTH(table);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.NESTED_TABLE, TableClassifier.sReason);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(nestedTable));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
    }

    public void testEmptyTHTag() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<th></th>" +
                                  "<th></th>" +
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
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void testAllWhitespacedTHTag() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<th>&nbsp;&nbsp;</th>" +
                                  "<th>  </th>" +
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
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void testAbbrAttribute() {
        TableElement table = createDefaultTableWithNoTH();
        setAttributeForFirstElement(table, "TD", "abbr", "HTML");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ABBR_HEADERS_SCOPE, TableClassifier.sReason);
    }

    public void testHeadersAttribute() {
        TableElement table = createDefaultTableWithNoTH();
        setAttributeForFirstElement(table, "TD", "headers", "heading1");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ABBR_HEADERS_SCOPE, TableClassifier.sReason);
    }

    public void testScopeAttribute() {
        TableElement table = createDefaultTableWithNoTH();
        setAttributeForFirstElement(table, "TD", "scope", "colgroup");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ABBR_HEADERS_SCOPE, TableClassifier.sReason);
    }

    public void testSingleAbbrTag() {
        TableElement table = createDefaultTableWithNoTH();
        Element td = Document.get().createElement("TD");
        Element abbr = Document.get().createElement("ABBR");
        abbr.setInnerHTML("HTML");
        td.appendChild(abbr);
        Element tr = getFirstElement(table, "TR");
        tr.appendChild(td);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.ONLY_HAS_ABBR, TableClassifier.sReason);
    }

    public void testWideTable() {
        TableElement table = createDefaultTableWithNoTH();
        int rootWidth = mRoot.getOffsetWidth();
        int width = (int) ((0.95 * rootWidth) + 1.0);
        table.setAttribute("style", "width:" + width + "px");
        assertEquals(rootWidth, mRoot.getOffsetWidth());
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.MORE_95_PERCENT_DOC_WIDTH, TableClassifier.sReason);

        // Test same wide table with viewport meta.
        Element meta = TestUtil.createMetaName("viewport", "width=device-width");
        mHead.appendChild(meta);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
        meta.removeFromParent();
    }

    public void testSummaryAttribute() {
        TableElement table = createDefaultTableWithNoTH();
        table.setAttribute("summary", "Testing summary attribute");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.SUMMARY, TableClassifier.sReason);
    }

    public void testEmptyTable() {
        String tableStr = "<tbody>" +
                              "<p>empty table</p>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_1_ROW, TableClassifier.sReason);
    }

    public void test1Row() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_1_ROW, TableClassifier.sReason);
    }

    public void test1ColInSameCols() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_1_COL, TableClassifier.sReason);
    }

    public void test1ColInDifferentCols() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void test5Cols() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                                  "<td>row1col3</td>" +
                                  "<td>row1col4</td>" +
                                  "<td>row1col5</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                                  "<td>row2col3</td>" +
                                  "<td>row2col4</td>" +
                                  "<td>row2col5</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.MORE_EQ_5_COLS, TableClassifier.sReason);
    }

    public void testBorderAroundCells() {
        TableElement table = createDefaultTableWithNoTH();
        setAttributeForFirstElement(table, "TD", "style", "border-style:inset");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CELLS_HAVE_BORDER, TableClassifier.sReason);
    }

    public void testNoBorderAroundCells() {
        TableElement table = createDefaultTableWithNoTH();
        table.setAttribute("style", "border-style:inset");
        setAttributeForFirstElement(table, "TD", "style", "border-style:none");
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
    }

    public void testDifferentlyColoredRows() {
        TableElement table = createDefaultTableWithNoTH();
        setAttributeForFirstElement(table, "TR", "style", "background-color:red");
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.DIFFERENTLY_COLORED_ROWS, TableClassifier.sReason);
    }

    public void test20Rows() {
        TableElement table = createDefaultTableWithNoTH();
        NodeList<Element> tbodies = table.getElementsByTagName("TBODY");
        assertEquals(1, tbodies.getLength());
        Element tbody = tbodies.getItem(0);
        for (int i = 2; i < 20; i++) {
            Element tr = Document.get().createElement("TR");
            tr.setInnerHTML("<td>row" + i + "col" + i + "</td>");
            tbody.appendChild(tr);
        }
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.MORE_EQ_20_ROWS, TableClassifier.sReason);
    }

    public void testEmbedElement() {
        TableElement table = createBigDefaultTableWithNoTH();
        Element embed = Document.get().createElement("EMBED");
        getFirstElement(table, "TD").appendChild(embed);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.EMBED_OBJECT_APPLET_IFRAME, TableClassifier.sReason);
    }

    public void testObjectElement() {
        TableElement table = createBigDefaultTableWithNoTH();
        Element embed = Document.get().createElement("OBJECT");
        getFirstElement(table, "TD").appendChild(embed);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.EMBED_OBJECT_APPLET_IFRAME, TableClassifier.sReason);
    }

    public void testAppletElement() {
        TableElement table = createBigDefaultTableWithNoTH();
        Element embed = Document.get().createElement("APPLET");
        getFirstElement(table, "TD").appendChild(embed);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.EMBED_OBJECT_APPLET_IFRAME, TableClassifier.sReason);
    }

    public void testIframeElement() {
        int rootWidth = mRoot.getOffsetWidth();
        TableElement table = createBigDefaultTableWithNoTH();
        Element embed = Document.get().createElement("IFRAME");
        getFirstElement(table, "TD").appendChild(embed);
        assertEquals(rootWidth, mRoot.getOffsetWidth());
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.EMBED_OBJECT_APPLET_IFRAME, TableClassifier.sReason);
    }

    public void testTallTable() {
        TableElement table = createBigDefaultTableWithNoTH();
        // With min-height, the height of mRoot remains the same after resizing the table.
        mRoot.getStyle().setProperty("min-height", "200px");
        int rootHeight = mRoot.getOffsetHeight();
        int height = (int) ((0.90 * rootHeight) + 1.0);
        table.setAttribute("style", "height:" + height + "px");
        assertEquals(height, table.getOffsetHeight());
        assertEquals(rootHeight, mRoot.getOffsetHeight());
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.MORE_90_PERCENT_DOC_HEIGHT, TableClassifier.sReason);
    }

    private TableElement createTable(String html) {
        TableElement table = Document.get().createTableElement();
        table.setInnerHTML(html);
        mBody.appendChild(table);
        return table;
    }

    private TableElement createDefaultTableWithTH() {
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
        TableElement table = createTable(tableStr);
        mBody.appendChild(table);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.CAPTION_THEAD_TFOOT_COLGROUP_COL_TH,
                     TableClassifier.sReason);
        return table;
    }

    private TableElement createDefaultTableWithNoTH() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        mBody.appendChild(table);
        assertEquals(TableClassifier.Type.LAYOUT, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.LESS_EQ_10_CELLS, TableClassifier.sReason);
        return table;
    }

    private TableElement createBigDefaultTableWithNoTH() {
        String tableStr = "<tbody>" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                                  "<td>row1col3</td>" +
                                  "<td>row1col4</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row2col1</td>" +
                                  "<td>row2col2</td>" +
                                  "<td>row2col3</td>" +
                                  "<td>row2col4</td>" +
                              "</tr>" +
                              "<tr>" +
                                  "<td>row3col1</td>" +
                                  "<td>row3col2</td>" +
                                  "<td>row3col3</td>" +
                                  "<td>row3col4</td>" +
                              "</tr>" +
                          "</tbody>";
        TableElement table = createTable(tableStr);
        mBody.appendChild(table);
        assertEquals(TableClassifier.Type.DATA, TableClassifier.table(table));
        assertEquals(TableClassifier.Reason.DEFAULT, TableClassifier.sReason);
        return table;
    }

    private void setRoleForFirstElement(Element table, String tagname, String role) {
        setAttributeForFirstElement(table, tagname, "role", role);
    }

    private void setAttributeForFirstElement(Element table, String tagname, String attrName,
            String attrValue) {
        getFirstElement(table, tagname).setAttribute(attrName, attrValue);
    }

    private Element getFirstElement(Element table, String tagname) {
        NodeList<Element> allElems = table.getElementsByTagName(tagname);
        assertTrue(allElems.getLength() > 0);
        return allElems.getItem(0);
    }

    private TableElement createDefaultNestedTableWithTH(Element parentTable) {
        String nestedTableStr = "<tbody>" +
                                    "<tr>" +
                                        "<th>row1col1</th>" +
                                        "<th>row1col2</th>" +
                                    "</tr>" +
                                    "<tr>" +
                                        "<td>row2col1</td>" +
                                        "<td>row2col2</td>" +
                                    "</tr>" +
                                "</tbody>";
        return createNestedTable(nestedTableStr, parentTable);
    }

    private TableElement createDefaultNestedTableWithNoTH(Element parentTable) {
        String nestedTableStr = "<tbody>" +
                                    "<tr>" +
                                        "<td>row1col1</td>" +
                                        "<td>row1col2</td>" +
                                    "</tr>" +
                                "</tbody>";
        return createNestedTable(nestedTableStr, parentTable);
    }

    private TableElement createNestedTable(String nestedTableStr, Element parentTable) {
        TableElement nestedTable = createTable(nestedTableStr);
        // Insert nested table into 1st row of |parentTable|.
        NodeList<Element> rows = parentTable.getElementsByTagName("TR");
        assertEquals(2, rows.getLength());
        rows.getItem(0).appendChild(nestedTable);
        return nestedTable;
   }
}
