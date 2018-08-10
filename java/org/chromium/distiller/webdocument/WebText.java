// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import org.chromium.distiller.DomUtil;
import org.chromium.distiller.TreeCloneBuilder;
import org.chromium.distiller.labels.DefaultLabels;
import com.google.gwt.dom.client.Node;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class WebText extends WebElement {
    private List<Node> allTextNodes;
    private String text;
    private int start, end;
    private int firstWordNode, lastWordNode;
    private int numWords, numLinkedWords;
    private Set<String> labels;
    private int tagLevel;
    private int offsetBlock;
    // If this text needs to be split to place an image properly its group will signify how they
    // should be joined again (same group numbers get merged).
    private int groupNumber;
    private static Set<String> sInlineTags = null;

    public WebText(String text, List<Node> allTextNodes, int start, int end, int firstWordNode,
            int lastWordNode, int numWords, int numLinkedWords, int tagLevel, int offsetBlock) {
        assert allTextNodes != null;
        assert start < allTextNodes.size();
        assert end <= allTextNodes.size();
        assert firstWordNode < allTextNodes.size();
        assert lastWordNode < allTextNodes.size();

        this.text = text;
        this.allTextNodes = allTextNodes;
        this.start = start;
        this.end = end;
        this.firstWordNode = firstWordNode;
        this.lastWordNode = lastWordNode;
        this.numWords = numWords;
        this.numLinkedWords = numLinkedWords;
        this.labels = new HashSet<>();
        this.tagLevel = tagLevel;
        this.offsetBlock = offsetBlock;
    }

    private Set<String> getInlineTags() {
        if (sInlineTags == null) {
            sInlineTags = new HashSet<String>();
            // All inline elements except for impossible tags: BR, OBJECT, and SCRIPT.
            // Please refer to DomConverter.visitElement() for skipped tags.
            // Reference: https://developer.mozilla.org/en-US/docs/HTML/Inline_elements
            sInlineTags.add("B");
            sInlineTags.add("BIG");
            sInlineTags.add("I");
            sInlineTags.add("SMALL");
            sInlineTags.add("TT");
            sInlineTags.add("ABBR");
            sInlineTags.add("ACRONYM");
            sInlineTags.add("CITE");
            sInlineTags.add("CODE");
            sInlineTags.add("DFN");
            sInlineTags.add("EM");
            sInlineTags.add("KBD");
            sInlineTags.add("STRONG");
            sInlineTags.add("SAMP");
            sInlineTags.add("TIME");
            sInlineTags.add("VAR");
            sInlineTags.add("A");
            sInlineTags.add("BDO");
            sInlineTags.add("IMG");
            sInlineTags.add("MAP");
            sInlineTags.add("Q");
            sInlineTags.add("SPAN");
            sInlineTags.add("SUB");
            sInlineTags.add("SUP");
            sInlineTags.add("BUTTON");
            sInlineTags.add("INPUT");
            sInlineTags.add("LABEL");
            sInlineTags.add("SELECT");
            sInlineTags.add("TEXTAREA");
        }
        return sInlineTags;
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (hasLabel(DefaultLabels.TITLE)) return "";

        // TODO(mdjones): Instead of doing this next part, in the future track font size weight
        // and etc. and wrap the nodes in a "p" tag.
        Node clonedRoot = TreeCloneBuilder.buildTreeClone(getTextNodes());

        // To keep formatting/structure, at least one parent element should be in the output. This
        // is necessary because many times a WebText is only a single text node.
        if (clonedRoot.getNodeType() != Node.ELEMENT_NODE) {
            Node parentClone = getTextNodes().get(0).getParentElement().cloneNode(false);
            parentClone.appendChild(clonedRoot);
            clonedRoot = parentClone;
        }

        // The BODY element should not be used in the output.
        if ("BODY".equals(Element.as(clonedRoot).getTagName())) {
            Element div = Document.get().createDivElement();
            div.setInnerHTML(Element.as(clonedRoot).getInnerHTML());
            clonedRoot = div;
        }

        // Retain parent tags until the root is not an inline element, to make sure the style is
        // display:block.
        Node srcRoot = null;
        while (getInlineTags().contains(Element.as(clonedRoot).getTagName())) {
            if (srcRoot == null) {
                srcRoot = DomUtil.getNearestCommonAncestor(getTextNodes());
                if (srcRoot.getNodeType() != Node.ELEMENT_NODE) {
                    srcRoot = srcRoot.getParentElement();
                }
            }
            srcRoot = srcRoot.getParentElement();
            if ("BODY".equals(Element.as(srcRoot).getTagName())) break;
            Node parentClone = srcRoot.cloneNode(false);
            parentClone.appendChild(clonedRoot);
            clonedRoot = parentClone;
        }

        // Make sure links are absolute and IDs are gone.
        DomUtil.makeAllLinksAbsolute(clonedRoot);
        DomUtil.stripTargetAttributes(clonedRoot);
        DomUtil.stripIds(clonedRoot);
        DomUtil.stripUnwantedClassNames(clonedRoot);
        DomUtil.stripFontColorAttributes(clonedRoot);
        DomUtil.stripStyleAttributes(clonedRoot);
        // TODO(wychen): if we allow images in WebText later, add stripImageElements().

        // Since there are tag elements that are being wrapped
        // by a pair of {@link WebTag}s, we only need to
        // get the innerHTML, otherwise these tags would be duplicated.
        Element elementClonedRoot = Element.as(clonedRoot);
        if (textOnly) {
            return DomUtil.getTextFromTreeForTest(elementClonedRoot);
        } else if (WebTag.canBeNested(elementClonedRoot.getTagName())) {
            return elementClonedRoot.getInnerHTML();
        }
        return elementClonedRoot.getString();
    }

    public List<Node> getTextNodes() {
        return allTextNodes.subList(start, end);
    }

    public void addLabel(String s) {
        labels.add(s);
    }

    public boolean hasLabel(String s) {
        return labels.contains(s);
    }

    public Node getFirstNonWhitespaceTextNode() {
        return allTextNodes.get(firstWordNode);
    }

    public Node getLastNonWhitespaceTextNode() {
        return allTextNodes.get(lastWordNode);
    }

    public String getText() {
        return text;
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumLinkedWords() {
        return numLinkedWords;
    }

    public int getOffsetBlock() {
        return offsetBlock;
    }

    public int getTagLevel() {
        return tagLevel;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Set<String> takeLabels() {
        Set<String> res = labels;
        labels = new HashSet<String>();
        return res;
    }

    public void setGroupNumber(int group) {
        groupNumber = group;
    }

    public int getGroupNumber() {
        return groupNumber;
    }
}
