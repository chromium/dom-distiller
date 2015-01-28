// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * Used to walk the subtree of the DOM rooted at a particular Node. It provides a Visitor interface
 * to allow some processing to be done at each node of the walk.
 */
public class DomWalker {
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

        /**
         * Called when skipping an element. A normal walk doesn't skip any elements.
         */
        public void skip(Element e);
    }

    public DomWalker(Visitor v) {
        visitor = v;
    }

    /**
     * Walk the subtree rooted at n.
     */
    public void walk(Node top) {
        // Conceptually, this maintains a pointer to the currently "walked" node. When first seeing
        // the node, it calls visit() on it. The next node to visit is then (1) the first child, (2)
        // the next sibling, or (3) the next sibling of the first ancestor w/ a next sibling.
        //
        // Every time the walk "crosses" the "exit" of a node (i.e. when the pointer goes from
        // somewhere in the node's subtree to somewhere outside of that subtree), exit() is called
        // for that node (unless visit() for that node returned false).
        if (!visitor.visit(top)) return;
        Node n = top.getFirstChild();
        if (n != null) {
            while (n != top) {
                // shouldExit is used to suppress the exit call for the current node when visit()
                // returns false.
                boolean shouldExit = false;
                if (visitor.visit(n)) {
                    Node c = n.getFirstChild();
                    if (c != null) {
                        n = c;
                        continue;
                    }
                    shouldExit = true;
                }

                while (n != top) {
                    if (shouldExit) visitor.exit(n);
                    Node s = n.getNextSibling();
                    if (s != null) {
                        n = s;
                        break;
                    }
                    n = n.getParentNode();
                    shouldExit = true;
                }
            }
        }
        visitor.exit(top);
    }
}
