// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.http.client.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomUtil {
    /**
     * GWT does not provide a way to get a list of all attributes that have been explicitly set on a
     * DOM element (only a way to query the value of a particular attribute). In javascript, this
     * list is accessible as elem.attributes.
     *
     * @Return The element's attribute list from javascript.
     */
    public static native JsArray<Node> getAttributes(Element elem) /*-{
        return elem.attributes;
    }-*/;

    // Returns the first element with |className| in the tree rooted at |root|, null if none is
    // found.
    public static native Element getFirstElementWithClassName(Element root, String className) /*-{
        return root.querySelector("." + className);
    }-*/;

    public static native boolean hasClassName(Element elem, String className) /*-{
        return elem.classList.contains(className);
    }-*/;

    public static native JsArrayString getClassList(Element elem) /*-{
        return elem.classList;
    }-*/;

    /**
     * Check to see if a provided URL has the specified root domain (ex. http://a.b.c/foo/bar has
     * root domain of b.c).
     * @param url The URL to test.
     * @param root The root domain to test against.
     * @return True if url has the specified root domain.
     */
    public static boolean hasRootDomain(String url, String root) {
        if (url == null || root == null) {
            return false;
        }
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setHref(url);
        String host = anchor.getPropertyString("host");
        return ("." + host).endsWith("." + root);
    }

    /**
     * Split URL parameters into key/value pairs and return them in a map.
     * @param query The query string after the "?".
     * @return Map of all query parameters or an empty map.
     */
    public static Map<String, String> splitUrlParams(String query) {
        if (query == null || query.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> paramMap = new HashMap<>();
        String[] params = query.split("&");
        for (int i = 0; i < params.length; i++) {}
        for (String currentParam : params) {
            String[] paramSplit = currentParam.split("=");
            if (paramSplit.length > 1) {
                paramMap.put(paramSplit[0], URL.decode(paramSplit[1]));
            }
        }
        return paramMap;
    }

    /**
      * @Return The CSS style of an element after applying the active stylesheets and resolving any
      * basic computation the style's value(s) may contain.
      * @param el - DOM element
    */
    public static native Style getComputedStyle(Element el) /*-{
      return getComputedStyle(el, null);
    }-*/;

    public static boolean isVisible(Element e) {
        Style style = getComputedStyle(e);
        double opacity = JavaScript.parseFloat(style.getOpacity());
        return !(style.getDisplay().equals("none") ||
                style.getVisibility().equals("hidden") ||
                opacity == 0.0F);
    }

    /*
     * We want to use jsni for direct access to javascript's innerText.  This avoids GWT's
     * implementation of Element::getInnerText(), which is intentionally different to mimic an old
     * IE behaviour, which returns text within <script> tags.
     */
    public static native String getInnerText(Node node) /*-{
        return node.innerText;
    }-*/;

    public static native double getTime() /*-{
        // window.performance is unavailable in Gwt's dev environment.
        if (window.performance) {
          return window.performance.now();
        }
        return Date.now();
    }-*/;

    /**
     * Use jsni for direct access to javascript's textContent.  textContent is different from
     * innerText (see http://www.kellegous.com/j/2013/02/27/innertext-vs-textcontent):
     * - textContent is the raw textual content, doesn't require layout, and is basically a
     *   concatenation of the values of all text nodes within a subtree.
     * - innerText is what is presented to the user, requires layout, and excludes text in invisible
     *   elements, e.g. <title> tags.
     */
    public static native String javascriptTextContent(Node node) /*-{
        return node.textContent;
    }-*/;

    /**
     * Get a list of all the parents of this node starting with the node itself.
     * @param n The node to get the parents of.
     * @return A list of the provided node's partnts.
     */
    public static List<Node> getParentNodes(Node n) {
        ArrayList<Node> result = new ArrayList<Node>();
        Node curr = n;
        while (curr != null) {
            result.add(curr);
            curr = curr.getParentNode();
        }
        return result;
    }

    /**
     * Get the depth of the given node in the DOM tree (only counting elements).
     * @param n The node to find the depth of.
     * @return The depth of the provided node; -1 if n is null.
     */
    public static int getNodeDepth(final Node n) {
        return getParentNodes(n).size()-1;
    }

    /**
     * Get the nearest common ancestor of two nodes.
     * @param n1 First node.
     * @param n2 Second node.
     * @return The nearest common ancestor node of n1 and n2.
     */
    public static Node getNearestCommonAncestor(final Node n1, final Node n2) {
        Node parent = n1;
        while (parent != null && !JavaScript.contains(parent, n2)) parent = parent.getParentNode();
        return parent;
    }

    /**
     * Get all text from a tree/sub-tree.
     * @param node The root of the tree.
     * @return The text contained in this tree.
     */
    public static String getTextFromTree(Node node) {
        // Temporarily add the node to the DOM so that style is calculated.
        Document.get().getBody().appendChild(node);
        String output = DomUtil.getInnerText(node);

        // And remove it again.
        Document.get().getBody().removeChild(node);
        return output;
    }

    /**
     * Generate the HTML output for a list of relevant nodes.
     * @param outputNodes The list of nodes in a subtree that are considered relevant.
     * @param textOnly If this function should return text only instead of HTML.
     * @return Displayable HTML content representing this WebElement.
     */
    public static String generateOutputFromList(List<Node> outputNodes, boolean textOnly) {
        if (outputNodes.size() == 0) {
            return "";
        }

        NodeTree expanded = NodeListExpander.expand(outputNodes);
        Node clonedSubtree = expanded.cloneSubtreeRetainDirection();

        if (clonedSubtree.getNodeType() != Node.ELEMENT_NODE) return "";

        stripIds(clonedSubtree);
        makeAllLinksAbsolute(clonedSubtree);
        stripFontColorAttributes(clonedSubtree);
        stripTableBackgroundColorAttributes(clonedSubtree);

        if (textOnly) {
            return DomUtil.getTextFromTree(clonedSubtree);
        }
        return Element.as(clonedSubtree).getString();
    }

    /**
     * Makes all anchors and video posters absolute. This calls "makeAllSrcAttributesAbsolute".
     * @param rootNode The root Node to look through.
     */
    public static void makeAllLinksAbsolute(Node rootNode) {
        Element root = Element.as(rootNode);

        // AnchorElement.getHref() and ImageElement.getSrc() both return the
        // absolute URI, so simply set them as the respective attributes.

        NodeList<Element> allLinks = root.getElementsByTagName("A");
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));
            if (!link.getHref().isEmpty()) {
                link.setHref(link.getHref());
            }
        }
        NodeList<Element> videoTags = root.getElementsByTagName("VIDEO");
        for (int i = 0; i < videoTags.getLength(); i++) {
            VideoElement video = (VideoElement) videoTags.getItem(i);
            if (!video.getPoster().isEmpty()) {
                video.setPoster(video.getPoster());
            }
        }
        makeAllSrcAttributesAbsolute(root);

        handleSrcSetAttribute(root);
    }

    private static void handleSrcSetAttribute(Element root) {
        NodeList<Element> imgs = DomUtil.querySelectorAll(root, "IMG[SRCSET]");
        for (int i = 0; i < imgs.getLength(); i++) {
            handleSrcSetAttribute(ImageElement.as(imgs.getItem(i)));
        }
    }

    // TODO(wychen): make all srcset attributes absolute
    public static void handleSrcSetAttribute(ImageElement ie) {
        ie.removeAttribute("srcset");
    }

    /**
     * Makes all "img", "source", "track", and "video" tags have an absolute "src" attribute.
     * @param root The root element to look through.
     */
    public static native void makeAllSrcAttributesAbsolute(Element root) /*-{
        var elementsWithSrc = root.querySelectorAll('img,source,track,video');
        for (var key in elementsWithSrc) {
            if (elementsWithSrc[key].src) {
                elementsWithSrc[key].src = elementsWithSrc[key].src;
            }
        }
    }-*/;

    /**
     * Strips all "id" attributes from nodes in the tree rooted at |node|
     */
    public static void stripIds(Node node) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                Element e = Element.as(node);
                if (e.hasAttribute("id")) {
                    e.removeAttribute("id");
                }
                // Intentional fall-through.
            case Node.DOCUMENT_NODE:
                for (int i = 0; i < node.getChildCount(); i++) {
                    stripIds(node.getChild(i));
                }
        }
    }

    /**
     * Strips all "color" attributes from "font" nodes in the tree rooted at |rootNode|
     */
    public static void stripFontColorAttributes(Node rootNode) {
        Element root = Element.as(rootNode);
        if (root.getTagName() == "FONT")
            root.removeAttribute("COLOR");

        NodeList<Element> tags = DomUtil.querySelectorAll(root, "FONT[COLOR]");
        for (int i = 0; i < tags.getLength(); i++) {
            Element fontElement = tags.getItem(i);
            fontElement.removeAttribute("COLOR");
        }
    }

    /**
     * Strips all "bgcolor" attributes from table nodes in the tree rooted at |rootNode|
     */
    public static void stripTableBackgroundColorAttributes(Node rootNode) {
        Element root = Element.as(rootNode);
        if (root.getTagName().equals("TABLE")) {
            root.removeAttribute("BGCOLOR");
        }

        NodeList<Element> tags = DomUtil.querySelectorAll(root,
            "TABLE[BGCOLOR], TR[BGCOLOR], TD[BGCOLOR], TH[BGCOLOR]");
        for (int i = 0; i < tags.getLength(); i++) {
            tags.getItem(i).removeAttribute("BGCOLOR");
        }
    }

    /**
     * Get a list of relevant nodes from a subtree.
     * @param root The root of the subtree.
     * @return A list of relevant nodes.
     */
    public static List<Node> getOutputNodes(Node root) {
        final List<Node> nodes = new ArrayList<>();
        new DomWalker(new DomWalker.Visitor() {
            @Override
            public boolean visit(Node n) {
                switch (n.getNodeType()) {
                    case Node.TEXT_NODE:
                        nodes.add(n);
                        return false;
                    case Node.ELEMENT_NODE:
                        if (!DomUtil.isVisible(Element.as(n))) return false;
                        nodes.add(n);
                        return true;
                    case Node.DOCUMENT_NODE:
                    default:
                        return false;
                }
            }

            @Override
            public void exit(Node n) {
            }

            @Override
            public void skip(Element e) {
            }
        }).walk(root);
        return nodes;
    }

    /**
     * Generate HTML/text output for a given node tree/subree. This will ignore hidden
     * elements.
     * @param subtree The root of the subtree.
     * @param textOnly If this function should return text only and not HTML.
     * @return The output for the provided subtree.
     */
    public static String generateOutputFromTree(Node subtree, boolean textOnly) {
        return generateOutputFromList(getOutputNodes(subtree), textOnly);
    }

    // Returns whether querySelectorAll is available
    public static native boolean supportQuerySelectorAll(Element root) /*-{
        return (typeof(root.querySelectorAll) == 'function');
    }-*/;

    // GWT doesn't support querySelectorAll, so testing the caller could be harder.
    public static native NodeList<Element> querySelectorAll(Node l, String selectors) /*-{
        return l.querySelectorAll(selectors);
    }-*/;

    public static native Document createHTMLDocument(Document doc) /*-{
        return doc.implementation.createHTMLDocument();
    }-*/;

    public static native Element getFirstElementChild(Document document) /*-{
        return document.firstElementChild;
    }-*/;
}
