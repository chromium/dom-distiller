// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;

import java.util.logging.Logger;

/**
 * Filters out elements that are to be ignored from the DOM tree, and passes other nodes and
 * elements on to the actual DOM visitor for processing.
 */
public class FilteringDomVisitor implements DomWalker.Visitor {
    // To log debug information about the filtering of processed elements, set this flag to true.
    private static final boolean DEBUG = false;

    private static Logger logger = Logger.getLogger("FilteringDomParser");
    private final DomWalker.Visitor domVisitor;

    FilteringDomVisitor(DomWalker.Visitor v) {
        domVisitor = v;
    }

    @Override
    public boolean visit(Node n) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            // Skip invisible elements.
            Element e = Element.as(n);
            boolean visible = true;
            Style style = null;
            if (e.hasAttribute("hidden")) {
                visible = false;
            } else {
                style = DomUtil.getComputedStyle(e);
                visible = !(style.getDisplay().equals("none") ||
                            style.getVisibility().equals("hidden"));
            }
            logDbgInfo(e, visible, style);
            if (!visible) return false;
        }
        return domVisitor.visit(n);
    }

    @Override
    public void exit(Node n) {
        domVisitor.exit(n);
    }

    private void logDbgInfo(Element e, boolean visible, Style style) {
        if (!DEBUG) return;
        LogUtil.logToConsole((visible ? "KEEP " : "SKIP ") + e.getTagName() + ": id=" + e.getId() +
                ", hiddenAttr=" + (e.hasAttribute("hidden") ? "yes" : "no") +
                (style == null ? "" :
                        (", dsp=" + style.getDisplay() + ", vis=" + style.getVisibility())));
    }
}
