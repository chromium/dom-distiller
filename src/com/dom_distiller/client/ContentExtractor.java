// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import org.xml.sax.SAXException;

public class ContentExtractor {
    static Logger logger = Logger.getLogger("DomDistiller");

    public static String extractContent() {
        BoilerpipeHTMLContentHandler htmlParser = new BoilerpipeHTMLContentHandler();

        try {
            htmlParser.startDocument();
            Element documentElement = Document.get().getDocumentElement();
            DomToSaxParser.parse(documentElement, htmlParser);
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
        return document.getContent();
    }
}
