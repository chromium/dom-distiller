// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

import org.chromium.distiller.DomUtil;

import java.util.ArrayList;
import java.util.List;

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
    // Cloned and processed element.
    private ImageElement clonedImg;

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

    private void cloneAndProcessNode() {
        ImageElement ie = ImageElement.as(Element.as(imgElement.cloneNode(false)));
        ie.setSrc(srcUrl);
        ie.setSrc(ie.getSrc());
        // If computed width or height is zero, do not override them
        // to keep them visible.
        if (width > 0 && height > 0) {
            ie.setWidth(width);
            ie.setHeight(height);
        }
        DomUtil.makeSrcSetAbsolute(ie);
        DomUtil.stripImageElement(ie);

        clonedImg = ie;
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) return "";
        if (clonedImg == null) {
            cloneAndProcessNode();
        }
        return clonedImg.getString();
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
     * Get the list of source URLs of this image.
     * It's more efficient to call after generateOutput().
     * @return Source URLs or an empty List.
     */
    public List<String> getUrlList() {
        if (clonedImg == null) {
            cloneAndProcessNode();
        }
        List<String> list = new ArrayList<>();
        list.add(srcUrl);
        list.addAll(DomUtil.getSrcSetUrls(clonedImg));
        return list;
    }

    protected ImageElement getProcessedNode() {
        if (clonedImg == null) {
            cloneAndProcessNode();
        }
        return clonedImg;
    }
}
