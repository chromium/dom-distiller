// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.filters.heuristics;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

import java.util.List;
import java.util.ListIterator;

/**
 * Fuses headings with the blocks after them.
 *
 * If the heading was marked as boilerplate, the fused block will be labeled to prevent
 * BlockProximityFusion from merging through it.
 */
public final class HeadingFusion implements BoilerpipeFilter {

    /**
     * Creates a new {@link HeadingFusion} instance.
     */
    public HeadingFusion() {
    }

    @Override
    public boolean process(TextDocument doc) {
        List<TextBlock> textBlocks = doc.getTextBlocks();
        if (textBlocks.size() < 2) {
            return false;
        }

        boolean changes = false;
        ListIterator<TextBlock> it = textBlocks.listIterator();
        TextBlock prevBlock = null, currBlock = it.next();
        while (it.hasNext()) {
            prevBlock = currBlock;
            currBlock = it.next();

            if (!prevBlock.hasLabel(DefaultLabels.HEADING)) {
                continue;
            }

            if (prevBlock.hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)
                    || currBlock.hasLabel(DefaultLabels.STRICTLY_NOT_CONTENT)) {
                continue;
            }

            if (prevBlock.hasLabel(DefaultLabels.TITLE)
                    || currBlock.hasLabel(DefaultLabels.TITLE)) {
                continue;
            }

            if (currBlock.isContent()) {
                changes = true;

                boolean headingWasContent = prevBlock.isContent();
                prevBlock.mergeNext(currBlock);
                currBlock = prevBlock;
                it.remove();

                currBlock.removeLabel(DefaultLabels.HEADING);
                if (!headingWasContent) {
                    currBlock.addLabel(DefaultLabels.BOILERPLATE_HEADING_FUSED);
                }
            } else if (prevBlock.isContent()) {
                changes = true;
                prevBlock.setIsContent(false);
            }
        }

        return changes;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
