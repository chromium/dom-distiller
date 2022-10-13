// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.document;

public class TextDocumentTestUtil {
    public static String getContent(TextDocument document) {
        return getText(document, true);
    }

    public static String getText(TextDocument document, boolean contentOnly) {
        String s = "";
        for (TextBlock tb : document.getTextBlocks()) {
            if (!contentOnly || tb.isContent()) {
                s += tb.getText() + "\n";
            }
        }
        return s;
    }
}
