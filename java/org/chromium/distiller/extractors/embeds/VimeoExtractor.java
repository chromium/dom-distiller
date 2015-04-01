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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * VimeoExtractor is used for extracting Vimeo videos and relevant information.
 */
public class VimeoExtractor implements EmbedExtractor {

    private static final Set<String> relevantTags = new HashSet<>();
    static {
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
        String src = IFrameElement.as(e).getSrc();
        if (!DomUtil.hasRootDomain(src, "player.vimeo.com")) {
            return null;
        }

        // Get specific attributes about the Vimeo embed.
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setHref(src);
        String path = anchor.getPropertyString("pathname");

        Map<String, String> paramMap =
                DomUtil.splitUrlParams(anchor.getPropertyString("search").substring(1));

        String id = getVimeoIdFromPath(path);
        if (id == null) {
            return null;
        }

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("Vimeo embed extracted:");
            LogUtil.logToConsole("    ID:    " + id);
        }

        return new WebEmbed(e, "vimeo", id, paramMap);
    }

    /**
     * Get the last non-empty part of the path for a Vimeo URL. Stop searching after "video" as it
     * is the section just before the ID.
     * @param path The full path of the URL.
     * @return Either the ID of the video or null.
     */
    private String getVimeoIdFromPath(String path) {
        // Video ID will be the last part of the path, account for possible tail slash/empty path
        // sections.
        String[] pathSplit = path.split("/");
        for (int i = pathSplit.length-1; i >=0; i--) {
            if ("video".equals(pathSplit[i])) {
                return null;
            } else if (pathSplit[i].length() > 0) {
                return pathSplit[i];
            }
        }
        return null;
    }
}
