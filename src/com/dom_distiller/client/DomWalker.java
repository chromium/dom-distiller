// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Used to walk the subtree of the DOM rooted at a particular Node. It provides a Visitor interface
 * to allow some processing to be done at each node of the walk.
 */
class DomWalker {
    private final Visitor visitor;

    public interface Visitor {
        /**
         * Called when reaching a Node during the walk.
         *
         * @return Whether to process the subtree rooted at this node. If false, all children of
         * this node will be skipped and exit() will not be called for this node.
         */
        public boolean visit(Node n);

        /**
         * Called when exiting a node. I.e. after visiting all of its children.
         */
        public void exit(Node n);
    }

    public DomWalker(Visitor v) {
        visitor = v;
    }

    /**
     * Walk the subtree rooted at n.
     */
    public void walk(Node n) {
        if (!visitor.visit(n)) return;

        NodeList<Node> children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            walk(children.getItem(i));
        }
        visitor.exit(n);
    }
}
