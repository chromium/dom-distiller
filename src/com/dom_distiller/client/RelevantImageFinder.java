// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;

/**
 * This class is used to identify the images that correspond to a list of content nodes. An image is
 * included if it is after a content node without any non-content non-whitespace text node between
 * them.
 */
public class RelevantImageFinder {
    /**
     * @return An ordered list of both content and image nodes.
     */
    public static List<Node> findAndAddImages(List<Node> contentNodes, Node root) {
        Visitor visitor = new Visitor(contentNodes);
        new DomWalker(visitor).walk(root);
        return visitor.contentAndImages;
    }

    private static class Visitor implements DomWalker.Visitor {
        private boolean inContent;
        private final OrderedNodeMatcher nodeMatcher;
        private final List<Node> contentAndImages;

        Visitor(List<Node> contentNodes) {
            inContent = false;
            nodeMatcher = new OrderedNodeMatcher(contentNodes);
            contentAndImages = new ArrayList<Node>();
        }

        public boolean visit(Node n) {
            if (nodeMatcher.isFinished() && !inContent) {
                return false;
            }

            if (nodeMatcher.match(n)) {
                inContent = true;
                contentAndImages.add(n);
            } else if (isNonWhitespaceTextNode(n)) {
                inContent = false;
            }

            switch (n.getNodeType()) {
                case Node.TEXT_NODE:
                    return true;
                case Node.ELEMENT_NODE:
                    if (inContent && isImageElement(n)) {
                        contentAndImages.add(n);
                    }
                    return true;
                case Node.DOCUMENT_NODE:
                default:
                    // Don't recurse into comments or sub-documents.
                    return false;
            }
        }

        public void exit(Node n) {}
    }

    private static boolean isImageElement(Node n) {
        return n.getNodeType() == Node.ELEMENT_NODE && Element.as(n).hasTagName("IMG");
    }

    private static boolean isNonWhitespaceTextNode(Node n) {
        return n.getNodeType() == Node.TEXT_NODE &&
                !StringUtil.isStringAllWhitespace(n.getNodeValue());
    }

}
