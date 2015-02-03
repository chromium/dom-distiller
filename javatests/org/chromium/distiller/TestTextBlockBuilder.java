// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.webdocument.TestWebTextBuilder;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;

import java.util.ArrayList;

class TestTextBlockBuilder {
    private ArrayList<WebElement> elements = new ArrayList<WebElement>();
    private TestWebTextBuilder webTextBuilder = new TestWebTextBuilder();

    public TextBlock createForText(String text) {
        WebText wt = webTextBuilder.createForText(text);
        elements.add(wt);
        return new TextBlock(elements, elements.size() - 1);
    }

    public TextBlock createForAnchorText(String text) {
        WebText wt = webTextBuilder.createForAnchorText(text);
        elements.add(wt);
        return new TextBlock(elements, elements.size() - 1);
    }
}
