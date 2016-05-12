// Copyright 2015 The Chromium Authors. All rights reserved.
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
package org.chromium.distiller.filters.simple;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;

/**
 * Keeps only those content blocks which contain at least <em>k</em> words.
 *
 * @author Christian Kohlschütter
 */
public final class MinWordsFilter implements BoilerpipeFilter {
    private final int minWords;

    public MinWordsFilter(final int minWords) {
        this.minWords = minWords;
    }

    @Override
    public boolean process(final TextDocument doc) {
        boolean changes = false;

        for (TextBlock tb : doc.getTextBlocks()) {
            if (!tb.isContent()) {
                continue;
            }
            if (tb.getNumWords() < minWords) {
                tb.setIsContent(false);
                changes = true;
            }

        }

        return changes;

    }
}
