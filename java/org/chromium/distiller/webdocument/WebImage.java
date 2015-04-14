// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

import org.chromium.distiller.DomUtil;

/**
 * WebImage represents an image in the WebDocument potentially needing extraction.
 */
public class WebImage extends WebElement {
    // The main image element.
    Element imgElement;
    // The absolute source of the image.
    private String srcUrl;
    // The original width of the image in pixels.
    private int width;
    // The original height of the image in pixels.
    private int height;

    /**
     * Build an image element.
     * @param e The element detected as an image.
     * @param w The original width of the image.
     * @param h The original height of the image.
     * @param src The source URL of the image being extracted.
     */
    public WebImage(Element e, int w, int h, String src) {
        imgElement = e;
        width = w;
        height = h;
        srcUrl = src;
        if (srcUrl == null) {
            srcUrl = "";
        }
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) return "";
        Element container = Document.get().createDivElement();
        ImageElement ie = ImageElement.as(Element.as(imgElement.cloneNode(false)));
        ie.setSrc(ie.getSrc()); // Get the absolute src and give it back to the image.
        DomUtil.handleSrcSetAttribute(ie);
        container.appendChild(ie);
        return container.getInnerHTML();
    }

    /**
     * Get the image element of this WebImage.
     * @return Image element or null.
     */
    public Element getImageNode() {
        return imgElement;
    }

    /**
     * Get the width of this image in pixels.
     * @return The width of this image in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this image in pixels.
     * @return The height of this image in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the source URL of this image.
     * @return Source URL or an empty string.
     */
    public String getSrc() {
        return srcUrl;
    }
}
