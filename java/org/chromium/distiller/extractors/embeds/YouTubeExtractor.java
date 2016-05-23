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
import com.google.gwt.dom.client.ObjectElement;

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
        relevantTags.add("OBJECT");
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
        String src = null;
        if ("IFRAME".equals(e.getTagName())) {
            src = IFrameElement.as(e).getSrc();
        } else if ("OBJECT".equals(e.getTagName())) {
            // Deprecated way to embed youtube.
            // Ref: https://www.w3.org/blog/2008/09/howto-insert-youtube-video/
            //      http://xahlee.info/js/html_embed_video.html
            ObjectElement o = ObjectElement.as(e);
            if (o.getAttribute("type").equals("application/x-shockwave-flash")) {
                src = o.getAttribute("data");
            } else {
                NodeList<Element> params = DomUtil.querySelectorAll(e, "param[name=\"movie\"]");
                if (params.getLength() == 1) {
                    src = params.getItem(0).getAttribute("value");
                }
            }
        }
        if (src == null) {
            return null;
        }
        if (!DomUtil.hasRootDomain(src, "youtube.com")) {
            return null;
        }

        // Get specific attributes about the YouTube embed.
        int paramLoc = src.indexOf("?");
        if (paramLoc < 0) {
            // Wrong syntax like "http://www.youtube.com/v/<video-id>&param=value" has been
            // observed in the wild. Youtube seems to be resilient.
            paramLoc = src.indexOf("&");
        }
        if (paramLoc < 0) {
            paramLoc = src.length();
        }
        String path = src.substring(0, paramLoc);

        Map<String, String> paramMap =
                DomUtil.splitUrlParams(src.substring(paramLoc + 1));

        String id = getYouTubeIdFromPath(path);
        if (id == null) {
            return null;
        }

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) {
            LogUtil.logToConsole("YouTube embed extracted:");
            LogUtil.logToConsole("    ID:    " + id);
        }

        return new WebEmbed(e, "youtube", id, paramMap);
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
