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
package de.l3s.boilerpipe.filters.english;

import java.util.List;
import java.util.ListIterator;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;

/**
 * Classifies {@link TextBlock}s as content/not-content through rules that have
 * been determined using the C4.8 machine learning algorithm, as described in the
 * paper "Boilerplate Detection using Shallow Text Features", particularly using
 * text densities and link densities.
 * 
 * @author Christian Kohlschütter
 */
public class DensityRulesClassifier implements
        BoilerpipeFilter {
    public static final DensityRulesClassifier INSTANCE = new DensityRulesClassifier();

    /**
     * Returns the singleton instance for RulebasedBoilerpipeClassifier.
     */
    public static DensityRulesClassifier getInstance() {
        return INSTANCE;
    }

    public boolean process(TextDocument doc)
            throws BoilerpipeProcessingException {
        List<TextBlock> textBlocks = doc.getTextBlocks();

        if (textBlocks.isEmpty()) return false;

        boolean hasChanges = false;

        ListIterator<TextBlock> it = textBlocks.listIterator();
        TextBlock prevBlock = TextBlock.EMPTY_START;
        TextBlock currentBlock = TextBlock.EMPTY_START;
        TextBlock nextBlock = it.next();

        while (nextBlock != TextBlock.EMPTY_START) {
            prevBlock = currentBlock;
            currentBlock = nextBlock;
            nextBlock = it.hasNext() ? it.next() : TextBlock.EMPTY_START;
            hasChanges |= classify(prevBlock, currentBlock, nextBlock);
        }

        return hasChanges;
    }

    protected boolean classify(final TextBlock prev, final TextBlock curr,
            final TextBlock next) {
        final boolean isContent;

        if (curr.getLinkDensity() <= 0.333333) {
            if (prev.getLinkDensity() <= 0.555556) {
                if (curr.getTextDensity() <= 9) {
                    if (next.getTextDensity() <= 10) {
                        if (prev.getTextDensity() <= 4) {
                            isContent = false;
                        } else {
                            isContent = true;
                        }
                    } else {
                        isContent = true;
                    }
                } else {
                    if (next.getTextDensity() == 0) {
                        isContent = false;
                    } else {
                        isContent = true;
                    }
                }
            } else {
                if (next.getTextDensity() <= 11) {
                    isContent = false;
                } else {
                    isContent = true;
                }
            }
        } else {
            isContent = false;
        }

        return curr.setIsContent(isContent);
    }

}
