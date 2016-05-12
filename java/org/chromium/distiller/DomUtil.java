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
        // window.performance is unavailable in Gwt's dev environment and even referencing it on iOS
        // causes a crash.
        if ((typeof distiller_on_ios === 'undefined' || !distiller_on_ios) && window.performance) {
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
     * @return A list of the provided node's parents.
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
     * Get the nearest common ancestor of nodes.
     */
    public static Node getNearestCommonAncestor(final NodeList ns) {
        if (ns.getLength() == 0) return null;
        Node parent = ns.getItem(0);
        for (int i = 1; i < ns.getLength(); i++) {
            parent = getNearestCommonAncestor(parent, ns.getItem(i));
        }
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
        stripStyleAttributes(clonedSubtree);
        stripImageElements(clonedSubtree);

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

        if ("A".equals(root.getTagName())) {
            AnchorElement link = AnchorElement.as(root);
            if (!link.getHref().isEmpty()) {
                link.setHref(link.getHref());
            }
        }
        NodeList<Element> allLinks = root.getElementsByTagName("A");
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));
            if (!link.getHref().isEmpty()) {
                link.setHref(link.getHref());
            }
        }
        if (root.getTagName().equals("VIDEO")) {
            VideoElement video = (VideoElement) root;
            if (!video.getPoster().isEmpty()) {
                video.setPoster(video.getPoster());
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

        makeSrcSetAbsolute(root);
    }

    private static void makeSrcSetAbsolute(Element root) {
        if (root.getTagName().equals("IMG")) {
            makeSrcSetAbsolute(ImageElement.as(root));
        }
        NodeList<Element> imgs = DomUtil.querySelectorAll(root, "IMG[SRCSET]");
        for (int i = 0; i < imgs.getLength(); i++) {
            makeSrcSetAbsolute(ImageElement.as(imgs.getItem(i)));
        }
    }

    public static void makeSrcSetAbsolute(ImageElement ie) {
        String srcset = ie.getAttribute("srcset");
        if (srcset == "") {
            ie.removeAttribute("srcset");
            return;
        }

        String oldsrc = ie.getSrc();
        String[] sizes = StringUtil.jsSplit(srcset, ",");
        for(int i = 0; i < sizes.length; i++) {
            String size = StringUtil.jsTrim(sizes[i]);
            if (size.isEmpty()) continue;
            String[] comp = size.split(" ");
            ie.setSrc(comp[0]);
            comp[0] = ie.getSrc();
            sizes[i] = StringUtil.join(comp, " ");
        }
        ie.setAttribute("srcset", StringUtil.join(sizes, ", "));
        ie.setSrc(oldsrc);
    }

    public static void stripImageElements(Node root) {
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element element = Element.as(root);
            if (element.getTagName().equals("IMG")) {
                stripImageElement(ImageElement.as(element));
            }
        }
        NodeList<Element> imgs = DomUtil.querySelectorAll(root, "IMG");
        for (int i = 0; i < imgs.getLength(); i++) {
            stripImageElement(ImageElement.as(imgs.getItem(i)));
        }
    }

    /**
     * Only keep some attributes for image elements.
     * @param imgElement The image element to strip in-place.
     */
    public static void stripImageElement(ImageElement imgElement) {
        JsArray<Node> attrs = getAttributes(imgElement);
        for (int i = 0; i < attrs.length(); ) {
            String name = attrs.get(i).getNodeName();
            if (!"src".equals(name) &&
                !"alt".equals(name) &&
                !"srcset".equals(name) &&
                !"dir".equals(name) &&
                !"width".equals(name) &&
                !"height".equals(name) &&
                !"title".equals(name)) {
                imgElement.removeAttribute(name);
            } else {
                i++;
            }
        }
    }

    /**
     * Makes all "img", "source", "track", and "video" tags have an absolute "src" attribute.
     * @param root The root element to look through.
     */
    public static native void makeAllSrcAttributesAbsolute(Element root) /*-{
        if (root.tagName == "IMG" || root.tagName == "SOURCE" || root.tagName == "TRACK" ||
            root.tagName == "VIDEO") {
            if (root.src) {
                root.src = root.src;
            }
        }
        var elementsWithSrc = root.querySelectorAll('img,source,track,video');
        for (var key in elementsWithSrc) {
            if (elementsWithSrc[key].src) {
                elementsWithSrc[key].src = elementsWithSrc[key].src;
            }
        }
    }-*/;

    /**
     * Strips some attribute from certain tags in the tree rooted at |rootNode|, including root.
     * @param tagNames The tag names to be processed. ["*"] means all.
     */
    @SuppressWarnings("unused")
    public static void stripAttributeFromTags(Node rootNode, String attribute, String[] tagNames) {
        Element root = Element.as(rootNode);
        for (String tag: tagNames) {
            if (root.getTagName().equals(tag) || tag.equals("*")) {
                root.removeAttribute(attribute);
            }
        }

        for (String tag: tagNames) {
            tag += "[" + attribute + "]";
        }
        String query = StringUtil.join(tagNames, ", ");
        NodeList<Element> tags = DomUtil.querySelectorAll(root, query);
        for (int i = 0; i < tags.getLength(); i++) {
            tags.getItem(i).removeAttribute(attribute);
        }
    }

    /**
     * Strips all "id" attributes from all nodes in the tree rooted at |node|
     */
    public static void stripIds(Node node) {
        stripAttributeFromTags(node, "ID", new String[]{"*"});
    }

    /**
     * Strips all "color" attributes from "font" nodes in the tree rooted at |rootNode|
     */
    public static void stripFontColorAttributes(Node rootNode) {
        stripAttributeFromTags(rootNode, "COLOR", new String[]{"FONT"});
    }

    /**
     * Strips all "bgcolor" attributes from table nodes in the tree rooted at |rootNode|
     */
    public static void stripTableBackgroundColorAttributes(Node rootNode) {
        stripAttributeFromTags(rootNode, "BGCOLOR", new String[]{"TABLE", "TR", "TD", "TH"});
    }

    /**
     * Strips all "style" attributes from all nodes in the tree rooted at |rootNode|
     */
    public static void stripStyleAttributes(Node rootNode) {
        stripAttributeFromTags(rootNode, "STYLE", new String[]{"*"});
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

    public static int getArea(Element e) {
        if (e != null) {
            return e.getOffsetHeight() * e.getOffsetWidth();
        }
        return 0;
    }

    /**
     * Generate HTML/text output for a given node tree/subtree. This will ignore hidden
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
