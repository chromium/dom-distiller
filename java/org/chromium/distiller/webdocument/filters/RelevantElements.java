// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;

public class RelevantElements {
    public static boolean process(WebDocument document) {
        boolean changes = false;
        boolean inContent = false;

        for (WebElement e : document.getElements()) {
            if (e.getIsContent()) {
                inContent = true;
            } else if (e instanceof WebText) {
                inContent = false;
            } else {
                if (inContent) {
                    e.setIsContent(true);
                    changes = true;
                }
            }
        }
        return changes;
    }
}
