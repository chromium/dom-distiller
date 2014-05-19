// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;

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
        return !(style.getDisplay().equals("none") ||
                style.getVisibility().equals("hidden"));
    }
}
