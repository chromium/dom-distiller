// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;
import org.chromium.distiller.DomWalker;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.TableClassifier;

import com.google.gwt.dom.client.AnchorElement;
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
import java.util.Stack;

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

    private boolean isMobileFriendly;
    private boolean hasArticleElement;
    private boolean isHiddenClass = false;
    private Stack<Boolean> isHiddenStack = new Stack<>();

    public DomConverter(WebDocumentBuilderInterface builder) {
        hiddenElements = new HashSet<>();
        this.builder = builder;

        extractors = new ArrayList<>();
        extractors.add(new ImageExtractor());
        extractors.add(new TwitterExtractor());
        extractors.add(new VimeoExtractor());
        extractors.add(new YouTubeExtractor());

        embedTagNames = new HashSet<>();
        for (EmbedExtractor extractor : extractors) {
            embedTagNames.addAll(extractor.getRelevantTagNames());
        }
    }

    public void setIsMobileFriendly(boolean mobileFriendly) {
        isMobileFriendly = mobileFriendly;
    }

    public void setHasArticleElement(boolean hasArticle) {
        hasArticleElement = hasArticle;
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
        boolean keepAnyway = false;
        boolean hasHiddenClassName = false;
        if (!visible) {
            // Process more hidden elements in a marked article in mobile-friendly pages
            // because some sites hide the lower part of the article.
            if (isMobileFriendly && hasArticleElement) {
                if (!isHiddenClass) {
                    hasHiddenClassName = DomUtil.hasClassName(e, "hidden");
                }
                if (isHiddenClass || hasHiddenClassName) {
                    // See crbug.com/599121
                    keepAnyway = true;
                }
            }
            if (isMobileFriendly) {
                if (e.getAttribute("class").contains("continue")) {
                    // See crbug.com/687071
                    keepAnyway = true;
                }
                if (e.getAttribute("aria-expanded").equals("false")) {
                    // Unhide folded elements, like folded sections on mobile Wikipedia.
                    // See crbug.com/647667
                    keepAnyway = true;
                }
            }
        }
        logVisibilityInfo(e, visible || keepAnyway);
        if (!visible && !keepAnyway) {
            hiddenElements.add(e);
            return false;
        }

        // Node-type specific extractors check for elements they are interested in here. Everything
        // else will be filtered through the switch below.

        try {
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
        } catch (Exception exception) {
            LogUtil.logToConsole(
                "Exception happened in EmbedExtractors: " + exception.getMessage());
        }

        String className = e.getAttribute("class");
        String component = e.getAttribute("data-component");
        if (className.equals("sharing") || className.equals("socialArea") ||
            component.equals("share")) {
            // Skip social and sharing elements.
            // See crbug.com/692553, crbug.com/696556, and crbug.com/674557
            return false;
        }

        // Create a placeholder for the elements we want to preserve.
        if (WebTag.canBeNested(e.getTagName())) {
            builder.tag(new WebTag(e.getTagName(), WebTag.TagType.START));
        }

        switch (e.getTagName()) {
            case "A":
                // The "section" parameter is to differentiate with "redlinks".
                // Ref: https://en.wikipedia.org/wiki/Wikipedia:Red_link
                String editPattern = "action=edit&section=";
                boolean isEdit = AnchorElement.as(e).getHref().indexOf(editPattern) != -1;
                if (isEdit) {
                    // Skip "edit section" on mediawiki.
                    // See crbug.com/647667.
                    return false;
                }
                break;
            case "SPAN":
                if (className.equals("mw-editsection")) {
                    // Skip "[edit]" on mediawiki desktop version.
                    // See crbug.com/647667.
                    return false;
                }
                break;
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
            case "IFRAME":
            case "svg": // The svg tag is actually in small case.
                return false;
        }

        builder.startElement(e);
        isHiddenStack.push(isHiddenClass);
        isHiddenClass |= hasHiddenClassName;
        return true;
    }

    @Override
    public void exit(Node n) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            Element e = Element.as(n);
            if (WebTag.canBeNested(e.getTagName())) {
                builder.tag(new WebTag(e.getTagName(), WebTag.TagType.END));
            }
        }
        builder.endElement();
        isHiddenClass = isHiddenStack.pop();
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
                ", class=" + e.getAttribute("class") +
                ", parent=[" + parent.getTagName() +
                ", id=" + parent.getId() +
                ", class=" + parent.getAttribute("class") +
                "]");
    }
}
