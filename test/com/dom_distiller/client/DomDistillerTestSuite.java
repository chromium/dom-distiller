// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DomDistillerTestSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("All GWTTestCase tests");
        suite.addTestSuite(BlockProximityFusionTest.class);
        suite.addTestSuite(BoilerpipeHTMLContentHandlerTest.class);
        suite.addTestSuite(ContentExtractorTest.class);
        suite.addTestSuite(DocumentTitleGetterTest.class);
        suite.addTestSuite(DocumentTitleMatchClassifierTest.class);
        suite.addTestSuite(DomToSaxVisitorTest.class);
        suite.addTestSuite(DomUtilTest.class);
        suite.addTestSuite(DomWalkerTest.class);
        suite.addTestSuite(FilteringDomVisitorTest.class);
        suite.addTestSuite(GwtOverlayProtoTest.class);
        suite.addTestSuite(HeaderImageFinderTest.class);
        suite.addTestSuite(HeadingFusionTest.class);
        suite.addTestSuite(IEReadingViewParserTest.class);
        suite.addTestSuite(ImageHeuristicsTest.class);
        suite.addTestSuite(MarkupParserProtoTest.class);
        suite.addTestSuite(MarkupParserTest.class);
        suite.addTestSuite(NodeDirectionalityTest.class);
        suite.addTestSuite(NodeListExpanderTest.class);
        suite.addTestSuite(OpenGraphProtocolParserTest.class);
        suite.addTestSuite(OrderedNodeMatcherTest.class);
        suite.addTestSuite(PagingLinksFinderTest.class);
        suite.addTestSuite(RegExpTest.class);
        suite.addTestSuite(RelevantElementsFinderTest.class);
        suite.addTestSuite(SchemaOrgParserAccessorTest.class);
        suite.addTestSuite(SimilarSiblingContentExpansionTest.class);
        suite.addTestSuite(TableClassifierTest.class);
        suite.addTestSuite(TestUtilTest.class);
        suite.addTestSuite(TextDocumentStatisticsTest.class);
        suite.addTestSuite(TextDocumentConstructionTest.class);
        suite.addTestSuite(UnicodeTokenizerTest.class);
        return suite;
    }
}

