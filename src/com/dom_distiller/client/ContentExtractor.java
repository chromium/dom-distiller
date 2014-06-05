// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.labels.DefaultLabels;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Export()
public class ContentExtractor implements Exportable {
    static Logger logger = Logger.getLogger("DomDistiller");

    public static String extractContent() {
        return extractContent(false);
    }

    public static String extractContent(boolean text_only) {
        BoilerpipeHTMLContentHandler htmlParser = new BoilerpipeHTMLContentHandler();

        htmlParser.startDocument();
        Element documentElement = Document.get().getDocumentElement();
        DomToSaxVisitor domToSaxVisitor = new DomToSaxVisitor(htmlParser);
        FilteringDomVisitor filteringDomVisitor = new FilteringDomVisitor(domToSaxVisitor);
        new DomWalker(filteringDomVisitor).walk(documentElement);
        htmlParser.endDocument();

        TextDocument document = htmlParser.toTextDocument();
        document.setTitle(Document.get().getTitle().trim());
        try {
            CommonExtractors.ARTICLE_EXTRACTOR.process(document);
        } catch (BoilerpipeProcessingException e) {
            logger.warning("Processing failed.");
            return "";
        }

        if (text_only) {
            return document.getText(true, false);
        }

        List<Node> contentNodes = getContentNodesForTextDocument(document);

        List<Node> contentAndRelevantElements = RelevantElementsFinder.findAndAddElements(
                contentNodes, filteringDomVisitor.getHiddenElements(),
                filteringDomVisitor.getDataTables(), Document.get().getDocumentElement());

        if (contentAndRelevantElements.isEmpty()) {
            return "";
        }

        Node clonedSubtree = NodeListExpander.expand(contentAndRelevantElements).cloneSubtree();

        if (clonedSubtree.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        // The base URL in the distilled page viewer is different from that in
        // the live page.  This breaks all relative links (in anchors and
        // images), so make them absolute in the distilled content.
        makeAllLinksAbsolute(clonedSubtree);

        // TODO(cjhopman): this discards the top element and just returns its children. This might
        // break in some cases.
        return Element.as(clonedSubtree).getInnerHTML();
    }

    private static List<Node> getContentNodesForTextDocument(TextDocument document) {
        List<Node> contentTextNodes = new ArrayList<Node>();
        for (TextBlock tb : document.getTextBlocks()) {
            if (!tb.isContent()) {
                continue;
            }
            if (!tb.hasLabel(DefaultLabels.TITLE)) {
                contentTextNodes.addAll(tb.getContainedTextElements());
            }
        }
        return contentTextNodes;
    }

    private static void makeAllLinksAbsolute(Node rootNode) {
        Element root = Element.as(rootNode);

        // AnchorElement.getHref() and ImageElement.getSrc() both return the
        // absolute URI, so simply set them as the respective attributes.

        NodeList<Element> allLinks = root.getElementsByTagName("A");
        for (int i = 0; i < allLinks.getLength(); i++) {
            AnchorElement link = AnchorElement.as(allLinks.getItem(i));
            link.setHref(link.getHref());
        }

        NodeList<Element> allImages = root.getElementsByTagName("IMG");
        for (int i = 0; i < allImages.getLength(); i++) {
            ImageElement image = ImageElement.as(allImages.getItem(i));
            image.setSrc(image.getSrc());
        }
    }
}
