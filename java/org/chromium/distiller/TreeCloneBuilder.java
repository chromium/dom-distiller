// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This provides a way to extract the minimal part of the DOM tree that contains the provided list
 * of leaf nodes (the result is still a tree). This is a faster method of generating output than
 * the NodeListExpander.
 */
public class TreeCloneBuilder {
    /**
     * Clone the provided node and attempt to specify text directionality ("dir" attribute).
     * @param node The node to clone.
     * @return The cloned node.
     */
    public static Node cloneNode(Node node) {
        Node clone = node.cloneNode(false);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String direction = DomUtil.getComputedStyle(Element.as(node)).getProperty("direction");
            if (direction.isEmpty()) {
                direction = "auto";
            }
            Element.as(clone).setAttribute("dir", direction);
        }
        return clone;
    }

    private static Node cloneChild(Node clone, Node newChild) {
        Node cl = cloneNode(newChild);
        clone.appendChild(cl);
        return cl;
    }

    private static Node cloneParent(Node clone, Node newParent) {
        Node p = clone.getParentNode();
        if (p == null) {
            p = cloneNode(newParent);
            p.appendChild(clone);
        }
        return p;
    }

    /**
     * This method takes a list of nodes and returns a clone of the minimum tree in the DOM that
     * contains all of them. This is done by going through each node, cloning its parent and adding
     * children to that parent until the next node is not contained in that parent (originally).
     * The list cannot contain a parent of any of the other nodes. Children of the nodes in the
     * provided list are excluded.
     * @param nodes The list of nodes.
     * @return Root node of cloned tree.
     */
    public static Node buildTreeClone(List<Node> nodes) {
        if (nodes.size() == 1) {
            return new NodeTree(nodes.get(0)).cloneSubtree();
        }
        Node n = nodes.get(0);
        Node clone = n.cloneNode(false);
        OrderedNodeMatcher matcher = new OrderedNodeMatcher(nodes);
        while (!matcher.isFinished()) {
            if (matcher.match(n)) {
                if (matcher.isFinished()) break;
            } else {
                n = n.getFirstChild();
                while (!JavaScript.contains(n, matcher.peek())) {
                    n = n.getNextSibling();
                }
                clone = cloneChild(clone, n);
                continue;
            }
            while (true) {
                Node s = n.getNextSibling();
                while (s != null && !JavaScript.contains(s, matcher.peek())) {
                    s = s.getNextSibling();
                }
                if (s != null) {
                    clone = cloneParent(clone, n.getParentNode());
                    clone = cloneChild(clone, s);
                    n = s;
                    break;
                }
                n = n.getParentNode();
                clone = cloneParent(clone, n);
            }
        }
        while (clone.getParentNode() != null) {
            clone = clone.getParentNode();
        }
        return clone;
    }
}
