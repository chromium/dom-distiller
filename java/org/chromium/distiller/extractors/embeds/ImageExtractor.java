// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import org.chromium.distiller.DomUtil;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.webdocument.WebFigure;
import org.chromium.distiller.webdocument.WebImage;

import java.util.HashSet;
import java.util.Set;

/**
 * This class treats images as another type of embed and provides heuristics for lead image
 * candidacy.
 */
public class ImageExtractor implements EmbedExtractor {
    private static final Set<String> relevantTags = new HashSet<>();
    private String imgSrc;
    private int width;
    private int height;

    static {
        // TODO(mdjones): Add "DIV" to this list for css images and possibly captions.
        relevantTags.add("IMG");
        relevantTags.add("FIGURE");
    }

    private static final String[] LAZY_IMAGE_ATTRIBUTES =
            {"data-src", "data-original", "datasrc", "data-url"};

    @Override
    public Set<String> getRelevantTagNames() {
        return relevantTags;
    }

    @Override
    public WebImage extract(Element e) {
        if (!relevantTags.contains(e.getTagName())) {
            return null;
        }
        imgSrc = "";

        if ("IMG".equals(e.getTagName())) {
            extractImageAttributes(ImageElement.as(e));
            return new WebImage(e, width, height, imgSrc);
        } else if ("FIGURE".equals(e.getTagName())) {
            Element img = getFirstElementByTagName(e, "IMG");
            if (img != null) {
                extractImageAttributes(ImageElement.as(img));
                Element figcaption;
                Element cap = getFirstElementByTagName(e, "FIGCAPTION");
                if (cap != null) {
                    // We look for links because some sites put non-caption
                    // elements into <figcaption>. For example: image credit
                    // could contain a link. So we get the whole DOM structure within
                    // <figcaption> only when it contains links, otherwise we get the innerText.
                    figcaption = getFirstElementByTagName(cap, "A") != null ?
                            cap : createFigcaptionElement(cap);
                } else {
                    figcaption = createFigcaptionElement(e);
                }
                return new WebFigure(img, width, height, imgSrc, figcaption);
            }
        }
        return null;
    }

    private void extractImageAttributes(ImageElement imageElement) {
        // This will get the absolute URL of the image and
        // the displayed image dimension.
        // Try to get lazily-loaded images before falling back to get the src attribute.
        for (String attr : LAZY_IMAGE_ATTRIBUTES) {
            imgSrc = imageElement.getAttribute(attr);
            if (!imgSrc.isEmpty())
                break;
        }
        if (!imgSrc.isEmpty()) {
            // We cannot trust the dimension if the image is not loaded yet.
            // In some cases there are 1x1 placeholder images.
            width = 0;
            height = 0;
        } else {
            imgSrc = imageElement.getSrc();
            // As an ImageElement is manipulated here, it is possible
            // to get the real dimensions.
            width = imageElement.getWidth();
            height = imageElement.getHeight();
        }
        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("Extracted WebImage: " + imgSrc);
        }
    }

    private Element getFirstElementByTagName(Element e, String tagName) {
        NodeList<Element> elements = e.getElementsByTagName(tagName);
        if (elements.getLength() > 0) {
            return elements.getItem(0);
        }
        return null;
    }

    private Element createFigcaptionElement(Element element) {
        Element figcaption = Document.get().createElement("FIGCAPTION");
        figcaption.setInnerText(DomUtil.getInnerText(element));
        return figcaption;
    }
}
