// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import org.chromium.distiller.DomUtil;
import org.chromium.distiller.JavaScript;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.StringUtil;
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
        relevantTags.add("PICTURE");
        relevantTags.add("FIGURE");
        relevantTags.add("SPAN");
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

        ImageElement ie = ImageElement.as(DomUtil.getFirstElementByTagNameInc(e, "IMG"));

        if ("FIGURE".equals(e.getTagName())) {
            Element img = DomUtil.getFirstElementByTagName(e, "PICTURE");
            if (img == null) {
                img = DomUtil.getFirstElementByTagName(e, "IMG");
            }
            if (img == null) {
                return null;
            }
            extractImageAttributes(ie);
            Element figcaption;
            Element cap = DomUtil.getFirstElementByTagName(e, "FIGCAPTION");
            if (cap != null) {
                // We look for links because some sites put non-caption
                // elements into <figcaption>. For example: image credit
                // could contain a link. So we get the whole DOM structure within
                // <figcaption> only when it contains links, otherwise we get the innerText.
                NodeList<Element> links = DomUtil.querySelectorAll(cap, "A[HREF]");
                figcaption = links.getLength() > 0 ?
                        cap : createFigcaptionElement(cap);
            } else {
                figcaption = createFigcaptionElement(e);
            }
            return new WebFigure(img, width, height, imgSrc, figcaption);
        }

        if ("SPAN".equals(e.getTagName())) {
            if (!e.getAttribute("class").contains("lazy-image-placeholder")) {
                return null;
            }
            // Image lazy loading on Wikipedia.
            ie = Document.get().createImageElement();
            imgSrc = e.getAttribute("data-src");
            width = JavaScript.parseInt(e.getAttribute("data-width"));
            height = JavaScript.parseInt(e.getAttribute("data-height"));
            ie.setAttribute("srcset", e.getAttribute("data-srcset"));
            return new WebImage(ie, width, height, imgSrc);
        }

        extractImageAttributes(ie);
        return new WebImage(e, width, height, imgSrc);
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

    private Element createFigcaptionElement(Element element) {
        Element figcaption = Document.get().createElement("FIGCAPTION");
        // element.innerText might contain leading/trailing new lines or whitespaces when running
        // in older version of Chrome (before http://crrev.com/c/1114673). Both behaviors are
        // acceptable.
        // See https://crbug.com/651764.
        figcaption.setInnerText(StringUtil.jsTrim(DomUtil.getInnerText(element)));
        return figcaption;
    }
}
