// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple tree of Nodes.
 */
public class NodeTree {
    private final List<NodeTree> children;
    private final Node node;

    public NodeTree(Node root) {
        node = root;
        children = new LinkedList<NodeTree>();
    }

    public void addChild(Node node) {
        addChildTree(new NodeTree(node));
    }

    public void addChildTree(NodeTree child) {
        children.add(child);
    }

    public List<NodeTree> getChildren() {
        return children;
    }

    public Node getNode() {
        return node;
    }

    public Node cloneSubtree() {
        Node clone = node.cloneNode(false);
        for (NodeTree child : children) {
            clone.appendChild(child.cloneSubtree());
        }
        return clone;
    }

    /**
     * Clone this subtree while retaining text directionality from its computed style. The
     * "dir" attribute for each node will be set for each node.
     *
     * @return The root node of the cloned tree
     */
    public Node cloneSubtreeRetainDirection() {
        Node clone = node.cloneNode(false);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String direction = DomUtil.getComputedStyle(Element.as(node)).getProperty("direction");
            if (direction.isEmpty()) {
                direction = "auto";
            }
            Element.as(clone).setAttribute("dir", direction);
        }
        for (NodeTree child : children) {
            clone.appendChild(child.cloneSubtreeRetainDirection());
        }
        return clone;
    }
}

