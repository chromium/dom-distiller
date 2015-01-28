// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.filters.english.TerminatingBlocksFinder;

public class TerminatingBlocksFinderTest extends DomDistillerJsTestCase {
    private String[] positiveExamples = {
            // Startswith cases.
            "comments foo", "© reuters", "© reuters foo bar", "please rate this",
            "please rate this foo", "post a comment", "post a comment foo", "123 comments",
            "9 comments foo", "1346213423 users responded in", "1346213423 users responded in foo",

            // Contains cases.
            "foo what you think... bar", "what you think...", "foo what you think...",
            "add your comment", "foo add your comment", "add comment bar", "reader views bar",
            "have your say bar", "foo reader comments", "foo rätta artikeln",

            // Equals cases.
            "thanks for your comments - this feedback is now closed",

            // Check some case insensitivity.
            "Thanks for your comments - this feedback is now closed", "Add Comment Bar",
            "READER VIEWS BAR", "Comments FOO",
    };

    private String[] negativeExamples = {
            // Startswith cases.
            "lcomments foo", "xd© reuters", "not please rate this", "xx post a comment",
            "users responded in", "123users responded in foo",

            // Contains cases.
            "what you think..", "addyour comment", "ad comment", "readerviews",

            // Equals cases.
            "thanks for your comments - this feedback is now closed foo",
            "foo thanks for your comments - this feedback is now closed",

            // Long case.
            "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15",
    };

    private final TestTextBlockBuilder builder = new TestTextBlockBuilder();

    public void testPositives() {
        for (String ex : positiveExamples) {
            TextBlock tb = builder.createForText(ex);
            assertTrue("TerminatingBlocksFinder.isTerminating(createTextBlock(\"" + ex
                            + "\"))=false"
                            + ", expected true",
                    TerminatingBlocksFinder.isTerminating(tb));
        }
    }

    public void testNegatives() {
        for (String ex : negativeExamples) {
            TextBlock tb = builder.createForText(ex);
            assertFalse("TerminatingBlocksFinder.isTerminating(createTextBlock(\"" + ex
                            + "\"))=true"
                            + ", expected false",
                    TerminatingBlocksFinder.isTerminating(tb));
        }
    }

    public void testCommentLink() {
        assertTrue(TerminatingBlocksFinder.isTerminating(builder.createForAnchorText("Comment")));
        assertFalse(TerminatingBlocksFinder.isTerminating(builder.createForText("Comment")));
        assertFalse(TerminatingBlocksFinder.isTerminating(builder.createForAnchorText("comment")));
        assertFalse(TerminatingBlocksFinder.isTerminating(builder.createForAnchorText("foobar")));
    }
}
