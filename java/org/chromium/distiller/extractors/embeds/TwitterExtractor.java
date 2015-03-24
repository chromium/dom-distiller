// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.extractors.embeds;

import org.chromium.distiller.DomUtil;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.webdocument.WebEmbed;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TwitterExtractor is used to look for Twitter embeds. This class looks for both rendered and
 * unrendered tweets since distillation may happen before or after that particular javascript runs.
 */
public class TwitterExtractor implements EmbedExtractor {

    private static final Set<String> relevantTags = new HashSet<>();
    static {
        relevantTags.add("BLOCKQUOTE");
        relevantTags.add("IFRAME");
    }

    @Override
    public Set<String> getRelevantTagNames() {
        return relevantTags;
    }

    @Override
    public WebEmbed extract(Element e) {
        if (e == null || !relevantTags.contains(e.getTagName())) {
            return null;
        }

        // Twitter embeds are blockquote tags operated on by some javascript.
        WebEmbed result = null;
        if ("BLOCKQUOTE".equals(e.getTagName())) {
            result = handleNotRendered(e);
        } else if ("IFRAME".equals(e.getTagName())) {
            result = handleRendered(e);
        }

        if (result != null && LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("Twitter embed extracted:");
            LogUtil.logToConsole("    ID: " + result.getParams().get("tweetid"));
        }

        return result;
    }

    /**
     * Handle a Twitter embed that has not yet been rendered.
     * @param e The root element of the embed (should be a "blockquote").
     * @return EmbeddedElement object representing the embed or null.
     */
    private WebEmbed handleNotRendered(Element e) {
        // Make sure the characteristic class name for Twitter exists.
        if (!e.getClassName().contains("twitter-tweet")) {
            return null;
        }

        // Get the last anchor element in this section; it should contain the tweet id.
        NodeList<Element> anchors = e.getElementsByTagName("a");
        if (anchors.getLength() == 0) {
            return null;
        }

        AnchorElement tweetAnchor = AnchorElement.as(anchors.getItem(anchors.getLength() - 1));

        if (!DomUtil.hasRootDomain(tweetAnchor.getHref(), "twitter.com")) {
            return null;
        }

        // Get specific attributes about the Twitter embed.
        String path = tweetAnchor.getPropertyString("pathname");

        String id = getTweetIdFromPath(path);
        if (id == null) {
            return null;
        }

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("tweetid", id);

        return new WebEmbed(e, "twitter", attributes);
    }

    /**
     * Get the last non-empty part of the path for a Twitter URL.
     * @param path The full path of the URL.
     * @return Either the ID of the tweet or null.
     */
    private String getTweetIdFromPath(String path) {
        // Tweet ID will be the last part of the path, account for possible tail slash/empty path
        // sections.
        String[] pathSplit = path.split("/");
        for (int i = pathSplit.length-1; i >=0; i--) {
            if (pathSplit[i].length() > 0) {
                return pathSplit[i];
            }
        }
        return null;
    }

    /**
     * Handle a Twitter embed that has already been rendered.
     * @param e The root element of the embed (should be an "iframe").
     * @return EmbeddedElement object representing the embed or null.
     */
    private WebEmbed handleRendered(Element e) {
        // Twitter embeds are blockquote tags operated on by some javascript.
        if (!"IFRAME".equals(e.getTagName())) {
            return null;
        }
        IFrameElement iframe = IFrameElement.as(e);

        // If the iframe has no "src" attribute, explore further.
        if (!iframe.getSrc().isEmpty()) {
            return null;
        }
        Document iframeDoc = iframe.getContentDocument();
        if (iframeDoc == null) {
            return null;
        }

        // The iframe will contain a blockquote element that has information including tweet id.
        NodeList blocks = iframeDoc.getElementsByTagName("blockquote");
        if (blocks.getLength() < 1) {
            return null;
        }
        Element tweetBlock = Element.as(blocks.getItem(0));

        String id = tweetBlock.getAttribute("data-tweet-id");

        if (id.isEmpty()) {
            return null;
        }

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("tweetid", id);

        return new WebEmbed(e, "twitter", attributes);
    }
}
