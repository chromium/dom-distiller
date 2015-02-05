// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebEmbed is the base class for many site-specific embedded elements (Twitter, YouTube, etc.).
 */
public class WebEmbed extends WebElement {

    // The element that was extracted.
    private final Element embedNode;
    // The type of embed (YouTube, Twitter, etc.).
    private final String type;
    // Other parameters that may have been found on the embed URL or in attributes.
    private final Map<String, String> altParams;

    /**
     * Build an embed element.
     * @param e The element detected as an embed.
     * @param t The type of embed that this is.
     * @param params Extra parameters that the embed might have associated with it.
     */
    public WebEmbed(Element e, String t, Map<String, String> params) {
        embedNode = e;
        type = t;
        if (params == null) {
            altParams = new HashMap<>();
        } else {
            altParams = params;
        }
    }

    @Override
    public void addOutputNodes(List<Node> nodes, boolean includeTitle) {
        // TODO(mdjones): Add nodes once this is the primary means of output.
    }

    /**
     * Get the type of embed that this is (twitter, youtube, etc.).
     * @return The type of embed that this element is.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the map of parameters associated with this embed.
     * @return A map of the parameters or an empty map if there are no parameters.
     */
    public Map<String, String> getParams() {
        return altParams;
    }
}
