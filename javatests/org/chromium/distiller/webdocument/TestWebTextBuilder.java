// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.StringUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;

public class TestWebTextBuilder {
    private final ArrayList<Node> nodes = new ArrayList<>();

    public WebText createForText(String text) {
        return create(text, false);
    }

    public WebText createForAnchorText(String text) {
        return create(text, true);
    }

    private WebText create(String text, boolean isAnchor) {
        nodes.add(Document.get().createTextNode(text));
        int numWords = StringUtil.countWords(text);
        int idx = nodes.size() - 1;
        return new WebText(text, nodes, idx, idx + 1, idx, idx, numWords, isAnchor ? numWords : 0,
                0, idx);
    }
}
