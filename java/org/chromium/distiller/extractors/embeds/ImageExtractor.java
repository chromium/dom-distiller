// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
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
            imgSrc = imageElement.getSrc();
            // As an ImageElement is manipulated here, it is possible
            // to get the real dimensions.
            width = imageElement.getWidth();
            height = imageElement.getHeight();
        }

        return new WebImage(e, width, height, imgSrc);
    }
}
