// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;
import org.chromium.distiller.DomWalker;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.TableClassifier;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.Text;
import org.chromium.distiller.extractors.embeds.EmbedExtractor;
import org.chromium.distiller.extractors.embeds.ImageExtractor;
import org.chromium.distiller.extractors.embeds.TwitterExtractor;
import org.chromium.distiller.extractors.embeds.VimeoExtractor;
import org.chromium.distiller.extractors.embeds.YouTubeExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This DomWalker.Visitor creates a WebDocument from the walked DOM. It skips hidden and other
 * elements that should not be in the created document. Some of these skipped elements (hidden
 * elements and data tables) are available for retrieval after processing.
 */
public class DomConverter implements DomWalker.Visitor {
    private final WebDocumentBuilderInterface builder;
    private final Set<Node> hiddenElements;
    private final List<EmbedExtractor> extractors;
    // For quick lookup of tags that could possibly be embeds.
    private final HashSet<String> embedTagNames;

    public DomConverter(WebDocumentBuilderInterface builder) {
        hiddenElements = new HashSet<Node>();
        this.builder = builder;

        extractors = new ArrayList<EmbedExtractor>();
        extractors.add(new ImageExtractor());
        extractors.add(new TwitterExtractor());
        extractors.add(new VimeoExtractor());
        extractors.add(new YouTubeExtractor());

        embedTagNames = new HashSet<>();
        for (EmbedExtractor extractor : extractors) {
            embedTagNames.addAll(extractor.getRelevantTagNames());
        }
    }

    public final Set<Node> getHiddenElements() {
        return hiddenElements;
    }

    @Override
    public void skip(Element e) {
        builder.skipElement(e);
    }

    @Override
    public boolean visit(Node n) {
        switch (n.getNodeType()) {
            case Node.TEXT_NODE:
                builder.textNode(Text.as(n));
                return false;
            case Node.ELEMENT_NODE:
                return visitElement(Element.as(n));
            default:
                return false;
        }
    }

    private boolean visitElement(Element e) {
        // Skip invisible or uninteresting elements.
        boolean visible = DomUtil.isVisible(e);
        logVisibilityInfo(e, visible);
        if (!visible) {
            hiddenElements.add(e);
            return false;
        }

        // Node-type specific extractors check for elements they are interested in here. Everything
        // else will be filtered through the switch below.

        // Check for embedded elements that might be extracted.
        if (embedTagNames.contains(e.getTagName())) {
            // If the tag is marked as interesting, check the extractors.
            for (EmbedExtractor extractor : extractors) {
                WebElement embed = extractor.extract(e);
                if (embed != null) {
                    builder.embed(embed);
                    return false;
                }
            }
        }

        switch (e.getTagName()) {
            case "BR":
                builder.lineBreak(e);
                return false;
            // Skip data tables, keep track of them to be extracted by RelevantElementsFinder
            // later.
            case "TABLE":
                TableClassifier.Type type = TableClassifier.table(TableElement.as(e));
                logTableInfo(e, type);
                if (type == TableClassifier.Type.DATA) {
                    builder.dataTable(e);
                    return false;
                }
                break;

            // Some components are revisited later in context as they break text-flow of a
            // document.  e.g. <video> can contain text if format is unsupported.
            case "VIDEO":
                builder.embed(new WebVideo(e, e.getClientHeight(), e.getClientHeight()));
                return false;

            // These element types are all skipped (but may affect document construction).
            case "OPTION":
            case "OBJECT":
            case "EMBED":
            case "APPLET":
                skip(e);
                return false;

            // These types are skipped and don't affect document construction.
            case "HEAD":
            case "STYLE":
            case "SCRIPT":
            case "LINK":
            case "NOSCRIPT":
                return false;
        }
        builder.startElement(e);
        return true;
    }

    @Override
    public void exit(Node n) {
        builder.endElement();
    }

    private static void logVisibilityInfo(Element e, boolean visible) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        Style style = DomUtil.getComputedStyle(e);
        LogUtil.logToConsole((visible ? "KEEP " : "SKIP ") + e.getTagName() +
                ": id=" + e.getId() +
                ", dsp=" + style.getDisplay() +
                ", vis=" + style.getVisibility() +
                ", opaq=" + style.getOpacity());
    }

    private static void logTableInfo(Element e, TableClassifier.Type type) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_VISIBILITY_INFO)) return;
        Element parent = e.getParentElement();
        LogUtil.logToConsole("TABLE: " + type +
                ", id=" + e.getId() +
                ", class=" + e.getClassName() +
                ", parent=[" + parent.getTagName() +
                ", id=" + parent.getId() +
                ", class=" + parent.getClassName() +
                "]");
    }
}
