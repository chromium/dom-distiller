// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This provides a way to extract the minimal part of the DOM tree that contains the provided list
 * of nodes (the result is still a tree).
 */
public class NodeListExpander {
    /**
     * @param nodes A non-empty list of DOM nodes.
     * @return The minimal part of the Node tree that contains all the nodes provided. I.e. it
     * contains a node only if it is on the shortest path between two of the provided nodes.
     */
    public static NodeTree expand(List<Node> nodes) {
        Visitor visitor = new Visitor(nodes);
        Node top = getTopNode(nodes.get(0));
        new DomWalker(visitor).walk(top);
        return pruneTopNodeChain(visitor.subtree, nodes.get(0));
    }

    private static class Visitor implements DomWalker.Visitor {
        private final OrderedNodeMatcher nodeMatcher;

        // While walking the DOM, currentPath will hold the current node and all of its ancestors.
        // subtreePath will contain pointers to the corresponding NodeTrees. Each NodeTree will be
        // null unless one of its visited descendants has been matched by the nodeMatcher. When
        // visiting a matched node, null entries in subtreePath will be converted to an actual
        // NodeTree and added to its parent's NodeTree's children.
        private final List<Node> currentPath;
        private final List<NodeTree> subtreePath;

        // The "root" of the subtree.
        private NodeTree subtree;

        Visitor(List<Node> nodes) {
            nodeMatcher = new OrderedNodeMatcher(nodes);
            currentPath = new ArrayList<Node>();
            subtreePath = new ArrayList<NodeTree>();
        }

        @Override
        public void skip(Element e) {
        }

        @Override
        public boolean visit(Node n) {
            if (nodeMatcher.isFinished()) return false;

            currentPath.add(n);
            subtreePath.add(null);

            if (subtreePath.size() == 1) {
                // This is the root node so update the top-level subtree.
                subtree = new NodeTree(n);
                subtreePath.set(0, subtree);
            }

            if (nodeMatcher.match(n)) {
                convertNullSubtreeAncestors();
            }

            return true;
        }

        @Override
        public void exit(Node n) {
            currentPath.remove(currentPath.size() - 1);
            subtreePath.remove(subtreePath.size() - 1);
        }

        /**
         * Creates a NodeTree for each null entry in subtreePath and appends it to the NodeTree (in
         * subtreePath) one level higher.
         */
        private void convertNullSubtreeAncestors() {
            for (int i = 0; i < currentPath.size(); i++) {
                if (subtreePath.get(i) == null) {
                    subtreePath.set(i, new NodeTree(currentPath.get(i)));
                    subtreePath.get(i - 1).addChildTree(subtreePath.get(i));
                }
            }
        }
    }

    /**
     * Prunes any unnecessary chain at the top of the tree. If the root of the subtree has only a
     * single non-text child, then it can be removed while still containing all the requested nodes
     * (unless the root is the first requested node).
     */
    private static NodeTree pruneTopNodeChain(NodeTree subtree, Node firstContentNode) {
        while (subtree.getChildren().size() == 1 && !subtree.getNode().equals(firstContentNode) &&
                subtree.getChildren().get(0).getNode().getNodeType() != Node.TEXT_NODE) {
            subtree = subtree.getChildren().get(0);
        }
        return subtree;
    }

    private static Node getTopNode(Node n) {
        for (Node next = n.getParentNode();
                next != null && next.getNodeType() != Node.DOCUMENT_NODE;
                n = next, next = n.getParentNode()) {}
        return n;
    }
}
