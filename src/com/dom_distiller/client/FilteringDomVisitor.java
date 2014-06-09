// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Filters out elements that are to be ignored from the DOM tree, and passes other nodes and
 * elements on to the actual DOM visitor for processing.
 */
public class FilteringDomVisitor implements DomWalker.Visitor {
    private final DomWalker.Visitor domVisitor;
    private final Set<Node> hiddenElements;
    private final Set<Node> dataTables;

    FilteringDomVisitor(DomWalker.Visitor v) {
        domVisitor = v;
        hiddenElements = new HashSet<Node>();
        dataTables = new HashSet<Node>();
    }

    public final Set<Node> getHiddenElements() {
        return hiddenElements;
    }

    public final Set<Node> getDataTables() {
        return dataTables;
    }

    @Override
    public boolean visit(Node n) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            // Skip invisible elements.
            Element e = Element.as(n);
            boolean visible = DomUtil.isVisible(e);
            logVisibilityInfo(e, visible);
            if (!visible) {
                hiddenElements.add(e);
                return false;
            }

            // Skip data tables, keep track of them to be extracted by RelevantElementsFinder later.
            if (e.hasTagName("TABLE")) {
                TableClassifier.Type type = TableClassifier.getType(TableElement.as(e));
                logTableInfo(e, type);
                if (type == TableClassifier.Type.DATA) {
                    dataTables.add(e);
                    return false;
                }
            }

            // Don't traverse into text that will be restored into the DOM as part of a logical
            // block when reconstructing the HTML.
            if (e.hasTagName("FIGURE") || e.hasTagName("VIDEO")) {
                return false;
            }
        }
        return domVisitor.visit(n);
    }

    @Override
    public void exit(Node n) {
        domVisitor.exit(n);
    }

    private static void logVisibilityInfo(Element e, boolean visible) {
        if (!DomDistiller.isLoggable(DomDistiller.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        Style style = DomUtil.getComputedStyle(e);
        LogUtil.logToConsole((visible ? "KEEP " : "SKIP ") + e.getTagName() + ": id=" + e.getId() +
                ", hiddenAttr=" + (e.hasAttribute("hidden") ? "yes" : "no") +
                (", dsp=" + style.getDisplay() + ", vis=" + style.getVisibility()));
    }

    private static void logTableInfo(Element e, TableClassifier.Type type) {
        if (!DomDistiller.isLoggable(DomDistiller.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        LogUtil.logToConsole("TABLE: " + type +
                ", id=" + e.getId() +
                ", parent=" + e.getParentElement().getTagName());
    }
}
