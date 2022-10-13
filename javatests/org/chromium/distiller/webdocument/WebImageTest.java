// Copyright 2016 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import org.chromium.distiller.DomUtil;

public class WebImageTest extends DomDistillerJsTestCase {
    public void testGetSrcList() {
        mHead.setInnerHTML("<base href=\"http://example.com/\">");

        ImageElement img = Document.get().createImageElement();
        img.setSrc("image");
        img.setAttribute("srcset",
                "image200 200w, image400 400w");
        WebImage wi = new WebImage(img, 1, 1, img.getSrc());
        List<String> urls = wi.getUrlList();
        assertEquals(3, urls.size());
        assertEquals("http://example.com/image", urls.get(0));
        assertEquals("http://example.com/image200", urls.get(1));
        assertEquals("http://example.com/image400", urls.get(2));
    }

    public void testGetSrcListInPicture() {
        mHead.setInnerHTML("<base href=\"http://example.com/\">");

        String html =
            "<picture>" +
              "<source data-srcset=\"image200 200w, //example.org/image400 400w\">" +
              "<source srcset=\"image100 100w, //example.org/image300 300w\">" +
              "<img>" +
            "</picture>";
        Element container = Document.get().createDivElement();
        container.setInnerHTML(html);
        WebImage wi = new WebImage(container.getFirstChildElement(), 1, 1, "");
        List<String> urls = wi.getUrlList();
        assertEquals(4, urls.size());
        assertEquals("http://example.com/image200", urls.get(0));
        assertEquals("http://example.org/image400", urls.get(1));
        assertEquals("http://example.com/image100", urls.get(2));
        assertEquals("http://example.org/image300", urls.get(3));
    }

    public void testGenerateOutput() {
        mHead.setInnerHTML("<base href=\"http://example.com/\">");

        String html =
                "<picture>" +
                  "<source srcset=\"image\">" +
                  "<img dirty-attributes>" +
                "</picture>";
        Element container = Document.get().createDivElement();
        container.setInnerHTML(html);
        WebImage wi = new WebImage(container.getFirstChildElement(), 0, 0, "");
        assertEquals("<picture><source srcset=\"http://example.com/image\"><img></picture>",
                wi.generateOutput(false));
    }
}
