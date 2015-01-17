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
import org.chromium.distiller.StringUtil;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.regexp.shared.RegExp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Marks {@link TextBlock}s which contain parts of the HTML
 * <code>&lt;TITLE&gt;</code> tag, using some heuristics which are quite
 * specific to the news domain.
 *
 * @author Christian Kohlschütter
 */
public final class DocumentTitleMatchClassifier implements BoilerpipeFilter {

    private  Set<String> potentialTitles;

    public DocumentTitleMatchClassifier(List<String> titles) {
        if (titles == null) {
            this.potentialTitles = null;
        } else {
            this.potentialTitles = new HashSet<String>();

            for (String title : titles) {
                processPotentialTitle(title);
            }
        }
    }

    private void processPotentialTitle(String title) {
        title = title.replace('\u00a0', ' ');
        title = title.replace("'", "");

        title = title.trim().toLowerCase();

        if (title.length() != 0) {
            if (!potentialTitles.add(title)) {
                return;
            }

            String p;

            p = getLongestPart(title, "[ ]*[\\|»|-][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }
            p = getLongestPart(title, "[ ]*[\\|»|:][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }
            p = getLongestPart(title, "[ ]*[\\|»|:\\(\\)][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }
            p = getLongestPart(title, "[ ]*[\\|»|:\\(\\)\\-][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }
            p = getLongestPart(title, "[ ]*[\\|»|,|:\\(\\)\\-][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }
            p = getLongestPart(title, "[ ]*[\\|»|,|:\\(\\)\\-\u00a0][ ]*");
            if (p != null) {
                potentialTitles.add(p);
            }

            addPotentialTitles(potentialTitles, title, "[ ]+[\\|][ ]+", 4);
            addPotentialTitles(potentialTitles, title, "[ ]+[\\-][ ]+", 4);

            potentialTitles.add(title.replaceFirst(" - [^\\-]+$", ""));
            potentialTitles.add(title.replaceFirst("^[^\\-]+ - ", ""));
        }
    }

    public Set<String> getPotentialTitles() {
        return potentialTitles;
    }

    private void addPotentialTitles(final Set<String> potentialTitles, final String title, final String pattern, final int minWords) {
        String[] parts = title.split(pattern);
        if (parts.length == 1) {
            return;
        }
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.contains(".com")) {
                continue;
            }
            final int numWords = StringUtil.countWords(p);
            if (numWords >=minWords) {
                potentialTitles.add(p);
            }
        }
    }

    private String getLongestPart(final String title, final String pattern) {
        String[] parts = title.split(pattern);
        if (parts.length == 1) {
            return null;
        }
        int longestNumWords = 0;
        String longestPart = "";
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.contains(".com")) {
                continue;
            }
            final int numWords = StringUtil.countWords(p);
            if (numWords > longestNumWords || p.length() > longestPart.length()) {
                longestNumWords = numWords;
                longestPart = p;
            }
        }
        if (longestPart.length() == 0) {
            return null;
        } else {
            return longestPart.trim();
        }
    }

    private static final RegExp REG_REMOVE_CHARACTERS = RegExp.compile("[\\?\\!\\.\\-\\:]+", "g");

    @Override
    public boolean process(TextDocument doc) {
        if (potentialTitles == null) {
            return false;
        }
        boolean changes = false;

        for (final TextBlock tb : doc.getTextBlocks()) {
            String text = tb.getText();

            text = text.replace('\u00a0', ' ');
            text = text.replace("'", "");

            text = text.trim().toLowerCase();

            if (potentialTitles.contains(text)) {
                tb.addLabel(DefaultLabels.TITLE);
                changes = true;
            }

            text = REG_REMOVE_CHARACTERS.replace(text, "").trim();
            if (potentialTitles.contains(text)) {
                tb.addLabel(DefaultLabels.TITLE);
                changes = true;
            }
        }
        return changes;
    }
}
