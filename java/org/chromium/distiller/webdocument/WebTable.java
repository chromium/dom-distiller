// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;

public class WebTable extends WebElement {
    private Element tableElement;
    // Cloned and processed table element.
    private Element cloned;

    public WebTable(Element tableRoot) {
        tableElement = tableRoot;
    }

    private void cloneAndProcessNode() {
        cloned = DomUtil.cloneAndProcessTree(tableElement);
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (cloned == null) {
            cloneAndProcessNode();
        }
        if (textOnly) {
            return DomUtil.getTextFromTreeForTest(cloned);
        }
        return Element.as(cloned).getString();
    }

    public Element getTableElement() {
        return tableElement;
    }

    /**
     * Get the list of source URLs of this image.
     * It's more efficient to call after generateOutput().
     * @return Source URLs or an empty List.
     */
    public List<String> getImageUrlList() {
        if (cloned == null) {
            cloneAndProcessNode();
        }
        List<String> imgUrls = new ArrayList<>();
        NodeList<Element> imgs = DomUtil.querySelectorAll(cloned, "IMG, SOURCE");
        for (int i = 0; i < imgs.getLength(); i++) {
            ImageElement ie = (ImageElement) imgs.getItem(i);
            if (!ie.getSrc().isEmpty()) {
                imgUrls.add(ie.getSrc());
            }
            imgUrls.addAll(DomUtil.getAllSrcSetUrls(ie));
        }
        return imgUrls;
    }
}
