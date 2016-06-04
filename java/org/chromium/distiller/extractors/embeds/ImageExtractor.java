// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.webdocument.WebImage;

import java.util.HashSet;
import java.util.Set;

/**
 * This class treats images as another type of embed and provides heuristics for lead image
 * candidacy.
 */
public class ImageExtractor implements EmbedExtractor {
    private static final Set<String> relevantTags = new HashSet<>();
    static {
        // TODO(mdjones): Add "DIV" to this list for css images and possibly captions.
        relevantTags.add("IMG");
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
        String imgSrc = "";
        // Getting OffSetWidth/Height as default values, even they are
        // affected by padding, border, etc.
        int width = e.getOffsetWidth();
        int height = e.getOffsetHeight();
        if ("IMG".equals(e.getTagName())) {
            // This will get the absolute URL of the image and
            // the displayed image dimension.
            ImageElement imageElement = ImageElement.as(e);
            // Try to get lazily-loaded images before falling back to get the src attribute.
            for(String attr: LAZY_IMAGE_ATTRIBUTES) {
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
        }

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("Extracted WebImage: " + imgSrc);
        }
        return new WebImage(e, width, height, imgSrc);
    }
}
