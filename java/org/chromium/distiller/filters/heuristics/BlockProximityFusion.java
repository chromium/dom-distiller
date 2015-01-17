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
package org.chromium.distiller.filters.heuristics;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

import java.util.Iterator;
import java.util.List;

/**
 * Fuses adjacent blocks if their distance (in blocks) does not exceed a certain limit.
 * This probably makes sense only in cases where an upstream filter already has removed some blocks.
 *
 * @author Christian Kohlschütter
 */
public final class BlockProximityFusion implements BoilerpipeFilter {

    public static final BlockProximityFusion PRE_FILTERING = new BlockProximityFusion(false);
    public static final BlockProximityFusion POST_FILTERING =
            new BlockProximityFusion(true);

    private final boolean postFiltering;

    private static final int MAX_BLOCK_DISTANCE = 1;

    /**
     * Creates a new {@link BlockProximityFusion} instance.
     *
     * @param postFiltering
     */
    public BlockProximityFusion(boolean postFiltering) {
        this.postFiltering = postFiltering;
    }

    @Override
    public boolean process(TextDocument doc) {
        List<TextBlock> textBlocks = doc.getTextBlocks();
        if (textBlocks.size() < 2) {
            return false;
        }

        boolean changes = false;
        TextBlock prevBlock = textBlocks.get(0);

        for (Iterator<TextBlock> it = textBlocks.listIterator(1); it.hasNext();) {
            TextBlock block = it.next();
            if (!block.isContent() || !prevBlock.isContent()) {
                prevBlock = block;
                continue;
            }
            int diffBlocks = block.getOffsetBlocksStart()
                    - prevBlock.getOffsetBlocksEnd() - 1;
            if (diffBlocks <= MAX_BLOCK_DISTANCE) {
                boolean ok = true;
                if (postFiltering) {
                    if (prevBlock.getTagLevel() != block.getTagLevel()) {
                        ok = false;
                    }
                } else {
                    if (block.hasLabel(DefaultLabels.BOILERPLATE_HEADING_FUSED)) {
                        ok = false;
                    }
                }
                if (prevBlock.hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT) != block.hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)) {
                    ok = false;
                }
                if (prevBlock.hasLabel(DefaultLabels.TITLE) != block.hasLabel(DefaultLabels.TITLE)) {
                    ok = false;
                }
                if (!prevBlock.isContent() && prevBlock.hasLabel(DefaultLabels.LI) &&
                        !block.hasLabel(DefaultLabels.LI)) {
                    ok = false;
                }

                if (ok) {
                    prevBlock.mergeNext(block);
                    it.remove();
                    changes = true;
                } else {
                    prevBlock = block;
                }
            } else {
                prevBlock = block;
            }
        }
        return changes;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": postFiltering=" + postFiltering;
    }
}
