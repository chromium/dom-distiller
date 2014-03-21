// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.labels.DefaultLabels;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import org.xml.sax.AttributesImpl;
import org.xml.sax.SAXException;

@Export()
public class ContentExtractor implements Exportable {
    static Logger logger = Logger.getLogger("DomDistiller");

    public static String extractContent() {
        BoilerpipeHTMLContentHandler htmlParser = new BoilerpipeHTMLContentHandler();
        List<Node> textNodes = null;

        try {
            htmlParser.startDocument();
            Element documentElement = Document.get().getDocumentElement();
            textNodes = DomToSaxParser.parse(documentElement, htmlParser);
            htmlParser.endDocument();
        } catch (SAXException e) {
            logger.warning("Parsing failed.");
            return "";
        }

        TextDocument document = htmlParser.toTextDocument();
        try {
            CommonExtractors.ARTICLE_EXTRACTOR.process(document);
        } catch (BoilerpipeProcessingException e) {
            logger.warning("Processing failed.");
            return "";
        }

        List<Integer> contentTextIndexes = new ArrayList<Integer>();
        for (TextBlock tb : document.getTextBlocks()) {
            if (!tb.hasLabel(DefaultLabels.TITLE)) {
                contentTextIndexes.addAll(tb.getContainedTextElements());
            }
        }
        Collections.sort(contentTextIndexes);

        // Boilerpipe's text node indexes start at 1.
        List<Node> contentNodes = new ArrayList<Node>(contentTextIndexes.size());
        for (Integer i : contentTextIndexes) {
            contentNodes.add(textNodes.get(i - 1));
        }

        List<Node> contentAndImages = RelevantImageFinder.findAndAddImages(
                contentNodes, Document.get().getDocumentElement());

        Node clonedSubtree = NodeListExpander.expand(contentAndImages).cloneSubtree();

        if (clonedSubtree.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }
        // TODO(cjhopman): this discards the top element and just returns its children. This might
        // break in some cases.
        return Element.as(clonedSubtree).getInnerHTML();
    }
}
