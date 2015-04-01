// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebEmbed is the base class for many site-specific embedded elements (Twitter, YouTube, etc.).
 */
public class WebEmbed extends WebElement {

    // The element that was extracted.
    protected List<Node> embedNodes;
    // The ID associated with the embed.
    private String id;
    // The type of embed that this is.
    private String type;
    // Other parameters that may have been found on the embed URL or in attributes.
    private final Map<String, String> altParams;

    /**
     * Build an embed element.
     * @param e The element detected as an embed.
     * @param t The type of embed that this is.
     * @param embedId The ID of the embedded object.
     * @param params Extra parameters that the embed might have associated with it.
     */
    public WebEmbed(Element e, String t, String embedId, Map<String, String> params) {
        embedNodes = new ArrayList<>();
        id = embedId;
        embedNodes.add(e);
        setType(t);
        if (params == null) {
            altParams = new HashMap<>();
        } else {
            altParams = params;
        }
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) return "";
        // Generate a placeholder for javascript to replace with the real embed.
        Element container = Document.get().createDivElement();
        Element embed = Document.get().createDivElement();
        embed.setClassName("embed-placeholder");
        embed.setAttribute("data-type", type);
        embed.setAttribute("data-id", id);
        container.appendChild(embed);
        return container.getInnerHTML();
    }

    /**
     * Get the map of parameters associated with this embed.
     * @return A map of the parameters or an empty map if there are no parameters.
     */
    public Map<String, String> getParams() {
        return altParams;
    }

    /**
     * Get the ID of this embed.
     * @return Embed ID.
     */
    public String getId() {
        return id;
    }

    protected void setType(String t) {
        type = t;
    }

    public String getType() {
        if (type == null) return "";
        return type;
    }
}
