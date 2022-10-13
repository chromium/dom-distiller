// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;


public class MarkupParserTest extends DomDistillerJsTestCase {

    public void testNullOpenGraphProtocolParser() {
        // To have a null OpenGraphProtocolParser, don't create its required
        // meta tags.  Instead, create tags that IEReadingViewParser will
        // recognize and legitimize as title.
        String expectedTitle = "Testing null OpenGraphProtocolParser.";
        mHead.appendChild(TestUtil.createTitle(expectedTitle));
        createMeta("title", expectedTitle);
        mBody.appendChild(TestUtil.createHeading(1, expectedTitle));

        MarkupParser parser = new MarkupParser(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    // TODO(kuan): write more tests if or when we determine:
    // - which parser takes precedence
    // - how we merge the different values retrieved from the different parsers.

    private void createMeta(String name, String content) {
        mHead.appendChild(TestUtil.createMetaName(name, content));
    }
}
