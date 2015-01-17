// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.document;

/**
 * Provides shallow statistics on a given TextDocument.
 */
public final class TextDocumentStatistics {
    /**
     * @return the sum of number of words in content blocks.
     */
    public static int countWordsInContent(TextDocument document) {
        int numWords = 0;
        for (TextBlock tb : document.getTextBlocks()) {
            if (tb.isContent()) numWords += tb.getNumWords();
        }
        return numWords;
    }

    private TextDocumentStatistics() {
    }
}
