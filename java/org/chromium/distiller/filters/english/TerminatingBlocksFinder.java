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
package org.chromium.distiller.filters.english;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.StringUtil;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Finds blocks which are potentially indicating the end of an article text and
 * marks them with {@link DefaultLabels#STRICTLY_NOT_CONTENT}.
 *
 * @author Christian Kohlschütter
 * @see IgnoreBlocksAfterContentFilter
 */
public class TerminatingBlocksFinder implements BoilerpipeFilter {
    public static final TerminatingBlocksFinder INSTANCE = new TerminatingBlocksFinder();

    /**
     * Returns the singleton instance for TerminatingBlocksFinder.
     */
    public static TerminatingBlocksFinder getInstance() {
        return INSTANCE;
    }

    public static final RegExp REG_TERMINATING = RegExp.compile("("
                    + "^(comments|© reuters|please rate this|post a comment|"
                    + "\\d+\\s+(comments|users responded in)"
                    + ")"
                    + "|what you think\\.\\.\\."
                    + "|add your comment"
                    + "|add comment"
                    + "|reader views"
                    + "|have your say"
                    + "|reader comments"
                    + "|rätta artikeln"
                    + "|^thanks for your comments - this feedback is now closed$"
                    + ")",
            "i");

    public static boolean isTerminatingText(String longText) {
        return REG_TERMINATING.test(longText);
    }

    public static boolean isTerminating(TextBlock tb) {
        if (tb.getNumWords() > 14) return false;
        String text = StringUtil.jsTrim(tb.getText());

        if (text.length() >= 8) {
            return isTerminatingText(text);
        } else if (tb.getLinkDensity() == 1.0) {
            return text.equals("Comment");
        } else if (text.equals("Shares")) {
            // Skip social and sharing elements.
            // See crbug.com/692553
            return true;
        }
        return false;
    }

    @Override
    public boolean process(TextDocument doc) {
        boolean changes = false;

        for (TextBlock tb : doc.getTextBlocks()) {
            if (isTerminating(tb)) {
                tb.addLabel(DefaultLabels.STRICTLY_NOT_CONTENT);
                changes = true;
            }
        }

        return changes;
    }
}
