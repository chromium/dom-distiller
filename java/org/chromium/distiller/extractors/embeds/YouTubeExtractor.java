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
 * YouTubeExtractor is used for extracting YouTube videos and relevant information.
 */
public class YouTubeExtractor implements EmbedExtractor {

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
        if (!DomUtil.hasRootDomain(src, "youtube.com")) {
            return null;
        }

        // Get specific attributes about the YouTube embed.
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setHref(src);
        String path = anchor.getPropertyString("pathname");

        Map<String, String> paramMap =
                DomUtil.splitUrlParams(anchor.getPropertyString("search").substring(1));

        String id = getYouTubeIdFromPath(path);
        if (id == null) {
            return null;
        }
        paramMap.put("videoid", id);

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("YouTube embed extracted:");
            LogUtil.logToConsole("    ID:    " + paramMap.get("videoid"));
        }

        return new WebEmbed(e, "youtube", paramMap);
    }

    /**
     * Get the last non-empty part of the path for a YouTube URL. Stop searching after "embed" as it
     * is the section just before the ID.
     * @param path The full path of the URL.
     * @return Either the ID of the video or null.
     */
    private String getYouTubeIdFromPath(String path) {
        // Video ID will be the last part of the path, account for possible tail slash/empty path
        // sections.
        String[] pathSplit = path.split("/");
        for (int i = pathSplit.length-1; i >=0; i--) {
            if ("embed".equals(pathSplit[i])) {
                return null;
            } else if (pathSplit[i].length() > 0) {
                return pathSplit[i];
            }
        }
        return null;
    }
}
