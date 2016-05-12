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
package org.chromium.distiller.filters.heuristics;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

/**
 * Marks all blocks as content that:
 * <ol>
 * <li>are on the same tag-level as very likely main content (usually the level of the largest block)</li>
 * <li>have a significant number of words, currently: at least 100</li>
 * </ol>
 *
 * @author Christian Kohlschütter
 */
public final class LargeBlockSameTagLevelToContentFilter implements BoilerpipeFilter {
    public static final LargeBlockSameTagLevelToContentFilter INSTANCE = new LargeBlockSameTagLevelToContentFilter();
    private LargeBlockSameTagLevelToContentFilter() {
    }

    @Override
    public boolean process(final TextDocument doc) {
        boolean changes = false;

        int tagLevel = -1;
        for (TextBlock tb : doc.getTextBlocks()) {
        	if(tb.isContent() && tb.hasLabel(DefaultLabels.VERY_LIKELY_CONTENT)) {
        		tagLevel = tb.getTagLevel();
        		break;
        	}
        }

        if(tagLevel == -1) {
        	return false;
        }

        for (TextBlock tb : doc.getTextBlocks()) {
            if (!tb.isContent()) {

            	if(tb.getNumWords() >= 100 && tb.getTagLevel() == tagLevel) {
	                tb.setIsContent(true);
	                changes = true;
            	}
            }
        }

        return changes;

    }
}
