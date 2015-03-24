// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.document.TextDocumentStatistics;
import org.chromium.distiller.extractors.ArticleExtractor;
import org.chromium.distiller.proto.DomDistillerProtos.StatisticsInfo;
import org.chromium.distiller.proto.DomDistillerProtos.TimingEntry;
import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;
import org.chromium.distiller.webdocument.DomConverter;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebDocumentBuilder;
import org.chromium.distiller.webdocument.filters.RelevantElements;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.VideoElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ContentExtractor {
    private final Element documentElement;
    private final List<String> candidateTitles;
    private final TimingInfo mTimingInfo;
    private final StatisticsInfo mStatisticsInfo;
    private final MarkupParser parser;
    private final List<String> imageUrls;
    private String textDirection;

    private class WebDocumentInfo {
        WebDocument document;
        Set<Node> hiddenElements;
    }

    public ContentExtractor(Element root) {
        documentElement = root;
        candidateTitles = new LinkedList<String>();
        mTimingInfo = TimingInfo.create();
        mStatisticsInfo = StatisticsInfo.create();
        imageUrls = new ArrayList<String>();

        double startTime = DomUtil.getTime();
        parser = new MarkupParser(root, mTimingInfo);
        mTimingInfo.setMarkupParsingTime(DomUtil.getTime() - startTime);
        textDirection = "";
    }

    // Grabs a list of candidate titles in descending priority order:
    // 1) meta-information
    // 2) The document's title element, modified based on some readability heuristics
    // 3) The document's title element, if it's a string
    private void ensureTitleInitialized() {
        if (candidateTitles.size() > 0) return;

        String title = parser.getTitle();
        if (!title.isEmpty()) {
            candidateTitles.add(title);
        }
        candidateTitles.add(DocumentTitleGetter.getDocumentTitle(
                    Document.get().getTitle(), Document.get().getDocumentElement()));
        if (Document.get().getTitle().getClass() == String.class) {
            candidateTitles.add(Document.get().getTitle());
        }
    }

    public MarkupParser getMarkupParser() { return parser; }

    public String extractTitle() {
        ensureTitleInitialized();
        assert candidateTitles.size() > 0;
        return candidateTitles.get(0);
    }

    public String extractContent() {
        return extractContent(false);
    }

    public String extractContent(boolean textOnly) {
        double now = DomUtil.getTime();
        WebDocumentInfo documentInfo = createWebDocumentInfoFromPage();
        mTimingInfo.setDocumentConstructionTime(DomUtil.getTime() - now);

        now = DomUtil.getTime();
        processDocument(documentInfo.document);
        RelevantElements.process(documentInfo.document);
        List<Node> contentNodes = documentInfo.document.getContentNodes(false);
        contentNodes =
                RelevantElementsFinder.findAndAddElements(contentNodes, documentInfo.hiddenElements,
                        Document.get().getDocumentElement());

        mTimingInfo.setArticleProcessingTime(DomUtil.getTime() - now);

        if (contentNodes.isEmpty()) return "";

        now = DomUtil.getTime();
        String html = formatExtractedNodes(textOnly, contentNodes);
        mTimingInfo.setFormattingTime(DomUtil.getTime() - now);

        if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_TIMING_INFO)) {
            for (int i = 0; i < mTimingInfo.getOtherTimesCount(); i++) {
                TimingEntry entry =  mTimingInfo.getOtherTimes(i);
                LogUtil.logToConsole("Timing: " + entry.getName() + " = " + entry.getTime());
            }

            LogUtil.logToConsole(
                    "Timing: MarkupParsingTime = " +
                    mTimingInfo.getMarkupParsingTime() +
                    "\nTiming: DocumentConstructionTime = " +
                    mTimingInfo.getDocumentConstructionTime() +
                    "\nTiming: ArticleProcessingTime = " +
                    mTimingInfo.getArticleProcessingTime() +
                    "\nTiming: FormattingTime = " +
                    mTimingInfo.getFormattingTime()
                    );
        }
        return html;
    }

    /**
     * Returns timing information about the most recent extraction run.
     * @return an instance of DomDistillerProtos.TimingInfo with detailed timing statistics.
     */
    public TimingInfo getTimingInfo() {
        return mTimingInfo;
    }

    /**
     * Returns statistical information about the most recent extraction run.
     * @return an instance of DomDistillerProtos.StatisticsInfo with detailed statistics.
     */
    public StatisticsInfo getStatisticsInfo() {
        return mStatisticsInfo;
    }

    /**
     * Get the page's text directionality ("ltr", "rtl", or "auto").
     * @return The page's text direction (default is "auto").
     */
    public String getTextDirection() {
        if (textDirection == null || textDirection.isEmpty()) {
            textDirection = "auto";
        }
        return textDirection;
    }

    /**
     * Get a list of the content image URLs in the provided document.
     * @return A list of image URLs.
     */
    public List<String> getImageUrls() {
        return imageUrls;
    }

    /**
     * Converts the original HTML page into a WebDocument for analysis.
     */
    private WebDocumentInfo createWebDocumentInfoFromPage() {
        WebDocumentInfo info = new WebDocumentInfo();
        WebDocumentBuilder documentBuilder = new WebDocumentBuilder();
        DomConverter converter = new DomConverter(documentBuilder);
        new DomWalker(converter).walk(documentElement);
        info.document = documentBuilder.toWebDocument();
        ensureTitleInitialized();
        info.hiddenElements = converter.getHiddenElements();

        return info;
    }

    /**
     * Implements the actual analysis of the page content, identifying the core elements of the
     * page.
     *
     * @param document the WebDocument representation of the page extracted from the DOM.
     */
    private void processDocument(WebDocument document) {
        TextDocument textDocument = document.createTextDocumentView();
        ArticleExtractor.INSTANCE.process(textDocument, candidateTitles);
        mStatisticsInfo.setWordCount(TextDocumentStatistics.countWordsInContent(textDocument));
        textDocument.applyToModel();
    }

    /**
     * Creates a new minimal HTML document containing copies of the DOM nodes identified as the
     * core elements of the page. Some additional re-formatting hints may be included in the new
     * document.
     *
     * @param textOnly indicates whether to simply return the aggregated text content instead of
     *        HTML
     * @param contentNodes the DOM nodes containing text to be included in the final docuemnt.
     * @return A HTML or text document which includes the aggregated content of the provided HTML
     *        nodes.
     */
    private String formatExtractedNodes(boolean textOnly, List<Node> contentNodes) {
        NodeTree expandedList = NodeListExpander.expand(contentNodes);
        Node clonedSubtree = expandedList.cloneSubtreeRetainDirection();
        if (clonedSubtree.getNodeType() != Node.ELEMENT_NODE) return "";

        // determine text directionality
        textDirection = Element.as(clonedSubtree).getAttribute("dir");

        // The base URL in the distilled page viewer is different from that in
        // the live page.  This breaks all relative links (in anchors,
        // images, etc.), so make them absolute in the distilled content.
        makeAllLinksAbsolute(clonedSubtree);

        // Get URLs of the extracted images.
        if (clonedSubtree.getNodeType() == Node.ELEMENT_NODE) {
            NodeList<Element> allImages = Element.as(clonedSubtree).getElementsByTagName("IMG");
            for (int i = 0; i < allImages.getLength(); i++) {
                String imageUrl = allImages.getItem(i).getAttribute("src");
                if (!imageUrl.isEmpty()) {
                    imageUrls.add(imageUrl);
                }
            }
        }

        if (textOnly) {
            return getTextFromTree(clonedSubtree);
        }

        // TODO(cjhopman): this discards the top element and just returns its children. This might
        // break in some cases.
        return Element.as(clonedSubtree).getInnerHTML();
    }

    /**
     * Strips all "id" attributes from nodes in the tree rooted at |clonedSubtree|
     */
    private static void stripIds(Node node) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                Element e = Element.as(node);
                if (e.hasAttribute("id")) {
                    e.setAttribute("id", "");
                }
                // Intentional fall-through.
            case Node.DOCUMENT_NODE:
                for (int i = 0; i < node.getChildCount(); i++) {
                    stripIds(node.getChild(i));
                }
        }
    }

    private static String getTextFromTree(Node node) {
        stripIds(node);

        // Temporarily add the node to the DOM so that style is calculated.
        Document.get().getBody().appendChild(node);
        String output = DomUtil.getInnerText(node);

        // And remove it again.
        Document.get().getBody().removeChild(node);
        return output;
    }

    private static void makeAllLinksAbsolute(Node rootNode) {
        Element root = Element.as(rootNode);

        // AnchorElement.getHref() and ImageElement.getSrc() both return the
        // absolute URI, so simply set them as the respective attributes.

        NodeList<Element> allLinks = root.getElementsByTagName("A");
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));
            if (!link.getHref().isEmpty()) {
                link.setHref(link.getHref());
            }
        }
        NodeList<Element> videoTags = root.getElementsByTagName("VIDEO");
        for (int i = 0; i < videoTags.getLength(); i++) {
            VideoElement video = (VideoElement) videoTags.getItem(i);
            if (!video.getPoster().isEmpty()) {
                video.setPoster(video.getPoster());
            }
        }
        makeAllSrcAttributesAbsolute(root);
    }

    private static native void makeAllSrcAttributesAbsolute(Element root) /*-{
        if (!root.querySelectorAll) {
            // In all contexts we intend to support, root.querySelectorAll is
            // available. This is just a hack to allow this function to be
            // run in gwt. Note that the underlying functionality is tested in
            // chromium, so we have coverage for it.
            return;
        }
        var elementsWithSrc = root.querySelectorAll('img,source,track,video');
        for (var key in elementsWithSrc) {
            if (elementsWithSrc[key].src) {
                elementsWithSrc[key].src = elementsWithSrc[key].src;
            }
        }
    }-*/;

}
