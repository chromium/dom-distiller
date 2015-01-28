// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.chromium.distiller.document;

import java.util.List;

/**
 * A text document, consisting of one or more {@link TextBlock}s.
 *
 * @author Christian Kohlschütter
 */
public class TextDocument implements Cloneable {
    private final List<TextBlock> textBlocks;

    /**
     * Creates a new {@link TextDocument} with given {@link TextBlock}s, and no
     * title.
     *
     * @param textBlocks
     *            The text blocks of this document.
     */
    public TextDocument(final List<TextBlock> textBlocks) {
        this.textBlocks = textBlocks;
    }

    /**
     * Returns the {@link TextBlock}s of this document.
     *
     * @return A list of {@link TextBlock}s, in sequential order of appearance.
     */
    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void applyToModel() {
        for (TextBlock tb : getTextBlocks()) {
            tb.applyToModel();
        }
    }

    /**
     * Returns detailed debugging information about the contained {@link TextBlock}s.
     */
    public String debugString() {
        String s = "";
        for(TextBlock tb : getTextBlocks()) {
            s += tb.toString() + "\n";
        }
        return s;
    }
}
