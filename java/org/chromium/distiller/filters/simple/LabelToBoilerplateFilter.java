// Copyright 2015 The Chromium Authors
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
import org.chromium.distiller.labels.DefaultLabels;

/**
 * Marks all blocks that contain a given label as "boilerplate".
 *
 * @author Christian Kohlschütter
 */
public final class LabelToBoilerplateFilter implements BoilerpipeFilter {
	public static final LabelToBoilerplateFilter INSTANCE_STRICTLY_NOT_CONTENT = new LabelToBoilerplateFilter(DefaultLabels.STRICTLY_NOT_CONTENT);

    private String[] labels;

    public LabelToBoilerplateFilter(final String... label) {
        this.labels = label;
    }

    @Override
    public boolean process(final TextDocument doc) {
        boolean changes = false;

        BLOCK_LOOP: for (TextBlock tb : doc.getTextBlocks()) {
            if (tb.isContent()) {
                for (String label : labels) {
                    if (tb.hasLabel(label)) {
                        tb.setIsContent(false);
                        changes = true;
                        continue BLOCK_LOOP;
                    }
                }
            }
        }

        return changes;
    }
}
