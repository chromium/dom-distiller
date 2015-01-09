// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.document.TextBlock;

class TestTextBlockBuilder {
    public static TextBlock createForText(String text) {
        return new TextBlock(text, null, 0, 0, 0, 0, StringUtil.countWords(text), 0, 0);
    }

    public static TextBlock createForAnchorText(String text) {
        return new TextBlock(text, null, 0, 0, 0, 0, StringUtil.countWords(text),
                StringUtil.countWords(text), 0);
    }
}
