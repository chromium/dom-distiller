// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import org.chromium.distiller.webdocument.WebEmbed;

import com.google.gwt.dom.client.Element;

import java.util.Set;

/**
 * This interface is used to represent an extractor for a particular type of embedded element.
 */
public interface EmbedExtractor {

    /**
     * Get a set of HTML tag names that are relevant to this extractor.
     * @return set of HTML tag names.
     */
    public Set<String> getRelevantTagNames();

    /**
     * Give a particular element, detect if it should be extracted as an embedded element; if not
     * return null.
     * @param e The element to test.
     * @return A {@link WebEmbed} object that contains information about the embed including node and type.
     */
    public WebEmbed extract(Element e);
}
