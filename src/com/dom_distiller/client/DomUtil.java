// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;

import java.util.ArrayList;
import java.util.List;

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
    // found.  |className| is expected to be in lower case, for the java versions.
    // If javascript querySelector() is defined, use it - GWT's test modes don't seem to support it.
    // Otherwise, if javascript getElementsByClassName() is defined, use it - it may not be
    // supported in earlier browsers.
    // Otherwise, use java versions of getElementsByTagName() and getClassName().
    public static native Element getFirstElementWithClassName(Element root, String className) /*-{
        if (typeof(root.querySelector) == 'function')
            return root.querySelector("." + className);

        if (typeof(root.getElementsByClassName) == 'function') {
            var matches = root.getElementsByClassName(className);
            return matches.length == 0 ? null : matches[0];
        }

        return @com.dom_distiller.client.DomUtil::javaGetFirstElementWithClassName(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(root, className);
    }-*/;

    // Returns the first element with |className| in the tree rooted at |root|, null if none is
    // found.  |className| is expected to be in lower case.
    public static Element javaGetFirstElementWithClassName(Element root, String className) {
        NodeList<Element> allElems = root.getElementsByTagName("*");
        for (int i = 0; i < allElems.getLength(); i++) {
            Element elem = allElems.getItem(i);
            if (hasClassName(elem, className)) return elem;
        }
        return null;
    }

    public static boolean hasClassName(Element elem, String className) {
        String classAttr = elem.getClassName().toLowerCase();
        // Make sure |className| is not the substring of another name in |classAttr|, so check
        // for whitespaces before and after.
        return (" " + classAttr + " ").contains(" " + className + " ");
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
     * However, in GWT, javascript innerText always returns null, so we fall back to the GWT
     * implementation in that case to cater to GWT tests.
     */
    public static String getInnerText(Node node) {
        String text = javascriptInnerText(node);
        if (text != null) return text;
        return node.getNodeType() == Node.ELEMENT_NODE ? Element.as(node).getInnerText() : "";
    }

    private static native String javascriptInnerText(Node node) /*-{
        return node.innerText;
    }-*/;

    public static native double getTime() /*-{
        // window.performance is unavailable in Gwt's dev environment.
        return window.performance ? window.performance.now() : 0;
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
        List<Node> n1Parents = getParentNodes(n1);
        List<Node> n2Parents = getParentNodes(n2);
        int i = n1Parents.size()-1;
        int j = n2Parents.size()-1;
        // This boolean helps in the case where one of the nodes is the root or the common
        // ancestor we are looking for.
        boolean lastMatch = false;
        while (i >= 0 && j >= 0 && n1Parents.get(i).equals(n2Parents.get(j))) {
            lastMatch = true;
            i--;
            j--;
        }
        if (lastMatch) {
            return n1Parents.get(i+1);
        }
        return null;
    }

    // Returns whether querySelectorAll is available
    public static native boolean supportQuerySelectorAll(Element root) /*-{
        return (typeof(root.querySelectorAll) == 'function');
    }-*/;

    // GWT doesn't support querySelectorAll, so testing the caller could be harder.
    public static native NodeList<Element> querySelectorAll(Node l, String selectors) /*-{
        return l.querySelectorAll(selectors);
    }-*/;
}
