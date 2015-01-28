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
package org.chromium.distiller.extractors;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.filters.debug.PrintDebugFilter;
import org.chromium.distiller.filters.english.NumWordsRulesClassifier;
import org.chromium.distiller.filters.english.TerminatingBlocksFinder;
import org.chromium.distiller.filters.heuristics.BlockProximityFusion;
import org.chromium.distiller.filters.heuristics.DocumentTitleMatchClassifier;
import org.chromium.distiller.filters.heuristics.ExpandTitleToContentFilter;
import org.chromium.distiller.filters.heuristics.HeadingFusion;
import org.chromium.distiller.filters.heuristics.KeepLargestBlockFilter;
import org.chromium.distiller.filters.heuristics.LargeBlockSameTagLevelToContentFilter;
import org.chromium.distiller.filters.heuristics.ListAtEndFilter;
import org.chromium.distiller.filters.heuristics.SimilarSiblingContentExpansion;
import org.chromium.distiller.filters.simple.BoilerplateBlockFilter;
import org.chromium.distiller.filters.simple.LabelToBoilerplateFilter;

import java.util.List;

/**
 * A full-text extractor which is tuned towards news articles. In this scenario
 * it achieves higher accuracy than {@link DefaultExtractor}.
 *
 * @author Christian Kohlschütter
 */
public final class ArticleExtractor {
    public static final ArticleExtractor INSTANCE = new ArticleExtractor();

    /**
     * Returns the singleton instance for {@link ArticleExtractor}.
     */
    public static ArticleExtractor getInstance() {
        return INSTANCE;
    }

    public boolean process(TextDocument doc, List<String> candidateTitles) {
        boolean changed;

        PrintDebugFilter.INSTANCE.process(doc, true, "Start");

        TerminatingBlocksFinder.INSTANCE.process(doc);
        new DocumentTitleMatchClassifier(candidateTitles).process(doc);
        // Intentionally don't print changes from these two steps.

        changed = NumWordsRulesClassifier.INSTANCE.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "Classification Complete");

        changed = LabelToBoilerplateFilter.INSTANCE_STRICTLY_NOT_CONTENT.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "Ignore Strictly Not Content blocks");

        changed = new SimilarSiblingContentExpansion.Builder()
                .allowCrossHeadings()
                .maxLinkDensity(0.5)
                .maxBlockDistance(10)
                .build()
                .process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed,
                "SimilarSiblingContentExpansion: Cross headings");

        changed = new SimilarSiblingContentExpansion.Builder()
                .allowCrossHeadings()
                .allowMixedTags()
                .maxLinkDensity(0.0)
                .maxBlockDistance(10)
                .build()
                .process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed,
                "SimilarSiblingContentExpansion: Mixed tags");

        changed = new HeadingFusion().process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "HeadingFusion");

        changed = BlockProximityFusion.PRE_FILTERING.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "BlockProximityFusion: Distance 1");

        changed = BoilerplateBlockFilter.INSTANCE_KEEP_TITLE.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "BlockFilter");

        changed = BlockProximityFusion.POST_FILTERING.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "BlockProximityFusion: Same level content-only");

        changed = KeepLargestBlockFilter.INSTANCE_EXPAND_TO_SIBLINGS.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "Keep Largest Block");

        changed = ExpandTitleToContentFilter.INSTANCE.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "Expand Title to Content");

        changed = LargeBlockSameTagLevelToContentFilter.INSTANCE.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "Largest Block Same Tag Level -> Content");

        changed = ListAtEndFilter.INSTANCE.process(doc);
        PrintDebugFilter.INSTANCE.process(doc, changed, "List at end filter");

        return true;
    }
}
