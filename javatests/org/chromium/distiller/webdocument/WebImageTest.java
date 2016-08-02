// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

public class WebImageTest extends DomDistillerJsTestCase {
    public void testGetSrcList() {
        ImageElement img = Document.get().createImageElement();
        img.setSrc("http://example.com/image");
        img.setAttribute("srcset",
                "http://example.com/image200 200w, http://example.com/image400 400w");
        WebImage wi = new WebImage(img, 1, 1, img.getSrc());
        List<String> urls = wi.getUrlList();
        assertEquals(3, urls.size());
        assertEquals("http://example.com/image", urls.get(0));
        assertEquals("http://example.com/image200", urls.get(1));
        assertEquals("http://example.com/image400", urls.get(2));
    }
}
