// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.VideoElement;
import org.chromium.distiller.DomUtil;

/**
 * WebVideo represents a video in the WebDocument potentially needing extraction.
 */
public class WebVideo extends WebElement {
    /** The main video element. */
    private final Element videoElement;
    /** The original width of the video in pixels. */
    private final int width;
     /** The original height of the video in pixels. */
    private final int height;

    /**
     * Build an video element.
     * @param e The element detected as an video.
     * @param w The original width of the video.
     * @param h The original height of the video.
     */
    public WebVideo(Element e, int w, int h) {
        // TODO(mdjones): Handle multiple nested "source" and "track" tags.
        videoElement = e;
        width = w;
        height = h;
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) return "";
        Element container = Document.get().createDivElement();
        VideoElement ve = (VideoElement) videoElement.cloneNode(false);
        for (int i = 0; i < videoElement.getChildCount(); i++) {
            Node curNode = videoElement.getChild(i);
            if (curNode.getNodeType() != Node.ELEMENT_NODE) continue;

            Element el = Element.as(curNode);
            // Only take "source" and "track" children.
            if ("SOURCE".equals(el.getTagName()) || "TRACK".equals(el.getTagName())) {
                ve.appendChild(el.cloneNode(false));
            }
        }

        if (!ve.getPoster().isEmpty()) {
            ve.setPoster(ve.getPoster());
        }
        DomUtil.makeAllSrcAttributesAbsolute(ve);
        DomUtil.stripIds(ve);
        container.appendChild(ve);
        return container.getInnerHTML();
    }

    /**
     * Get the video element of this WebVideo.
     * @return Video element or null.
     */
    public Element getVideoElement() {
        return videoElement;
    }

    /**
     * Get the width of this video in pixels.
     * @return The width of this video in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this video in pixels.
     * @return The height of this video in pixels.
     */
    public int getHeight() {
        return height;
    }
}
