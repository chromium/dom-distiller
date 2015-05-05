// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.dom.client.TitleElement;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A mixed bag of stuff used in tests.
 */
public class TestUtil {
    public final static String expectedDivTreeHtml =
                "<div id=\"0\">" +
                    "<div id=\"1\">" +
                        "<div id=\"2\">" +
                            "<div id=\"3\"></div>" +
                            "<div id=\"4\"></div>" +
                        "</div>" +
                        "<div id=\"5\">" +
                            "<div id=\"6\"></div>" +
                            "<div id=\"7\"></div>" +
                        "</div>" +
                    "</div>" +
                    "<div id=\"8\">" +
                        "<div id=\"9\">" +
                            "<div id=\"10\"></div>" +
                            "<div id=\"11\"></div>" +
                        "</div>" +
                        "<div id=\"12\">" +
                            "<div id=\"13\"></div>" +
                            "<div id=\"14\"></div>" +
                        "</div>" +
                    "</div>" +
                "</div>";

    /**
     * Creates a binary tree of divs with 4 levels. The ids of the divs are ("0", "1", ...), in that
     * order for a pre-order traversal.
     */
    public static List<Element> createDivTree() {
        List<Element> divs = new ArrayList<Element>();
        divs.add(createDiv(divs.size()));
        createDivTreeImpl(divs.get(0), 0, divs);
        return divs;
    }

    /**
     * Creates a div with the integer id as its id.
     */
    public static Element createDiv(int id) {
        Element e = Document.get().createDivElement();
        e.setId(Integer.toString(id));
        return e;
    }

    public static Text createText(String value) {
        return Document.get().createTextNode(value);
    }

    public static ImageElement createImage() {
        return Document.get().createImageElement();
    }

    public static IFrameElement createIframe() {
        return Document.get().createIFrameElement();
    }

    public static TitleElement createTitle(String value) {
        TitleElement t = Document.get().createTitleElement();
        t.setInnerHTML(value);
        return t;
    }

    public static HeadingElement createHeading(int n, String value) {
        HeadingElement h = Document.get().createHElement(n);
        h.setInnerHTML(value);
        return h;
    }

    public static AnchorElement createAnchor(String href, String text) {
        AnchorElement l = Document.get().createAnchorElement();
        l.setHref(href);
        l.setInnerHTML(text);
        return l;
    }

    public static MetaElement createMetaProperty(String property,
            String content) {
        MetaElement m = Document.get().createMetaElement();
        m.setAttribute("property", property);
        m.setContent(content);
        return m;
    }

    public static MetaElement createMetaName(String name, String content) {
        MetaElement m = Document.get().createMetaElement();
        m.setName(name);
        m.setContent(content);
        return m;
    }

    public static Element createSpan(String value) {
        Element s = Document.get().createElement("SPAN");
        s.setInnerHTML(value);
        return s;
    }

    private static void createDivTreeImpl(Element e, int depth, List<Element> divs) {
        if (depth > 2) return;
        for (int i = 0; i < 2; i++) {
            Element child = createDiv(divs.size());
            divs.add(child);
            e.appendChild(child);
            createDivTreeImpl(child, depth + 1, divs);
        }
    }

    public static String getElementAsString(Element e) {
        Element div = Document.get().createDivElement();
        div.appendChild(e.cloneNode(true));
        return div.getInnerHTML();
    }

    public static String formHrefWithWindowLocationPath(String strToAppend) {
        String noUrlParams = Window.Location.getPath();
        // Append '/' if necessary.
        if (!strToAppend.isEmpty() && !StringUtil.match(noUrlParams, "\\/$")) {
            noUrlParams += "/";
        }
        return noUrlParams + strToAppend;
    }

    public static String removeAllDirAttributes(String originalHtml) {
        return originalHtml.replaceAll(" dir=\\\"(ltr|rtl|inherit|auto)\\\"","");
    }

    /**
     * Randomly shuffle the list in-place.
     */
    public static void shuffle(List<?> list) {
        int size = list.size();
        for (int i=size; i>1; i--) {
            Collections.swap(list, i-1, Random.nextInt(i));
        }
    }
}
