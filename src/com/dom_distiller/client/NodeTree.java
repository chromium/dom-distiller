// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.List;
import java.util.LinkedList;

import com.google.gwt.dom.client.Node;

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

}

