// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to identify the elements that are relevant to a list of content nodes.
 * An element is included if it is after a content node without any non-content non-whitespace text
 * node between them.
 * Relevant elements include images and data tables, for now.
 */
public class RelevantElementsFinder {
    /**
     * @return An ordered list of both content and relevant element nodes.
     */
    public static List<Node> findAndAddElements(final List<Node> contentNodes,
            final Set<Node> hiddenElements, final Node root) {
        RelevantElementsFinder finder = new RelevantElementsFinder(contentNodes, hiddenElements);
        finder.find(root);

        // Remove all but one potential header image.
        ImageInfo topImage = null;
        for (ImageInfo info : finder.headerImageScores) {
            if (topImage == null || info.imageScore > topImage.imageScore) {
                if (topImage != null) {
                    finder.contentAndElements.remove(topImage.imageNode);
                }
                topImage = info;
            } else {
                finder.contentAndElements.remove(info.imageNode);
            }
        }

        return finder.contentAndElements;
    }

    private final Set<Node> hiddenElements;
    private final OrderedNodeMatcher nodeMatcher;
    private final List<Node> contentAndElements;

    private final HeaderImageFinder finder;
    private final List<ImageInfo> headerImageScores;

    private RelevantElementsFinder(final List<Node> contentNodes, final Set<Node> hiddenElements) {
        nodeMatcher = new OrderedNodeMatcher(contentNodes);
        this.hiddenElements = hiddenElements;
        contentAndElements = new ArrayList<Node>();

        Node firstContentNode = null;
        if (contentNodes != null && contentNodes.size() > 0) {
            firstContentNode = contentNodes.get(0);
        }
        finder = new HeaderImageFinder(firstContentNode);
        headerImageScores = new ArrayList<ImageInfo>();
    }

    /**
     * Finds the relevant elements rooted at |root|.
     */
    private void find(final Node root) {
        DomVisitor domVisitor = new DomVisitor();
        new DomWalker(domVisitor).walk(root);
    }

    private static final Set<String> sRelevantTags;
    static {
        sRelevantTags = new HashSet<String>();
        sRelevantTags.add("BR");
        sRelevantTags.add("FIGURE");
        sRelevantTags.add("IMG");
        sRelevantTags.add("VIDEO");
    }

    /**
     * This class traverses the root element pre-orderly and determines if a visible element is
     * relevant with respect to Boilerpipe's content nodes.  A relevant element is then possibly
     * extracted by ElementVisitor.  The final output is a combined ordered list of the content
     * nodes and extracted elements.
     */
    private class DomVisitor implements DomWalker.Visitor {
        private boolean inContent;

        DomVisitor() {
            inContent = false;
        }

        @Override
        public void skip(Element e) {
        }

        @Override
        public boolean visit(Node n) {
            if (nodeMatcher.isFinished() && !inContent) {
                return false;
            }

            boolean isContent = nodeMatcher.match(n);

            if (isContent) {
                contentAndElements.add(n);
            }

            switch (n.getNodeType()) {
                case Node.TEXT_NODE:
                    inContent = isContent || (inContent &&
                            StringUtil.isStringAllWhitespace(n.getNodeValue()));
                    return false;

                case Node.ELEMENT_NODE:
                    if (isContent) {
                        return true;
                    }
                    Element e = Element.as(n);
                    if (hiddenElements.contains(e)) {
                        return false;
                    }
                    // Check if element needs to be extracted.
                    if (inContent && sRelevantTags.contains(e.getTagName())) {
                        return addElementAndChildren(e);
                    }
                    // if this node is an image, reconsider retaining it
                    if ("IMG".equals(e.getTagName())) {
                        int score = finder.scoreNonContentImage(e);
                        if (score > HeaderImageFinder.MINIMUM_ACCEPTED_SCORE) {
                            headerImageScores.add(new ImageInfo(e, score));
                            contentAndElements.add(n);
                        }
                    }
                    return true;
                case Node.DOCUMENT_NODE:
                default:
                    return false;  // Don't recurse into comments or sub-documents.
            }
        }

        @Override
        public void exit(Node n) {
        }

        /**
         * @Returns true if caller should recurse into |e|.
         */
        private boolean addElementAndChildren(Element e) {
            logAddElement(e);
            ElementVisitor visitor = new ElementVisitor();
            new DomWalker(visitor).walk(e);
            return false;
        }

        private void logDiscardElement(Element e, String reason) {
            if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
            LogUtil.logToConsole("NOT adding [" +
                    "tag=" + e.getTagName() +
                    ", id=" + e.getId() +
                    ", parent=" + e.getParentElement().getTagName() +
                    ", bcos=\"" + reason +
                    "\"]");
        }

        private void logAddElement(Element e) {
            if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
            int last = contentAndElements.size() - 1;
            Node n = last < 0 ? null : contentAndElements.get(last);
            LogUtil.logToConsole("Adding [" +
                    "tag=" + e.getTagName() +
                    ", id=" + e.getId() +
                    ", parent=" + e.getParentElement().getTagName() +
                    (!e.hasTagName("IMG") ? "" : (", src=" + e.getAttribute("src"))) +
                    (e.hasTagName("IMG") ? "" : (", class=" + e.getAttribute("class"))) +
                    ", sz=" + e.getOffsetWidth() + "x" + e.getOffsetHeight() +
                    "] after node " + last +
                    (n == null ? "" : " [name=" + n.getNodeName() +
                                      ", value=" + n.getNodeValue() +
                                      ", parent=" + n.getParentElement().getTagName() +
                                      "]"));
        }
    }  // DomVisitor

    /**
     * This class traverses a relevant element pre-orderly and extracts:
     * - <table>: visible data tables and their children nodes
     * - others: visible nodes.
     */
    private class ElementVisitor implements DomWalker.Visitor {
        @Override
        public boolean visit(Node n) {
            // We don't care about matching contents since we're adding the nodes ourselves, but
            // we need to advance the ordered list if there's a match so as to match up against
            // the nodes after this relevant element.
            nodeMatcher.match(n);

            switch (n.getNodeType()) {
                case Node.TEXT_NODE:
                    contentAndElements.add(n);
                    return false;

                case Node.ELEMENT_NODE:
                    Element e = Element.as(n);
                    if (hiddenElements.contains(e)) return false;
                    contentAndElements.add(e);
                    return true;  // Extract children of this element by recursing into it.

                case Node.DOCUMENT_NODE:
                default:
                    return false;  // Don't recurse into comments or sub-documents.
            }
        }

        @Override
        public void skip(Element e) {
        }

        @Override
        public void exit(Node n) {
        }
    }  // ElementVisitor

    /**
     * This class is used to track potential header image information as the dom is being walked.
     */
    private static class ImageInfo {
        public final Node imageNode;
        public final int imageScore;

        public ImageInfo(Node image, int score) {
            imageNode = image;
            imageScore = score;
        }
    }

}
