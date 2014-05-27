// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;

/**
 * Filters out elements that are to be ignored from the DOM tree, and passes other nodes and
 * elements on to the actual DOM visitor for processing.
 */
public class FilteringDomVisitor implements DomWalker.Visitor {
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
                visible = DomUtil.isVisible(e);
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
        if (!DomDistiller.isLoggable(DomDistiller.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        LogUtil.logToConsole((visible ? "KEEP " : "SKIP ") + e.getTagName() + ": id=" + e.getId() +
                ", hiddenAttr=" + (e.hasAttribute("hidden") ? "yes" : "no") +
                (style == null ? "" :
                        (", dsp=" + style.getDisplay() + ", vis=" + style.getVisibility())));
    }
}
