// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2010 Christian Kohlschütter
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
package de.l3s.boilerpipe.sax;

import de.l3s.boilerpipe.labels.DefaultLabels;
import de.l3s.boilerpipe.labels.LabelAction;

import java.util.HashMap;


/**
 * Default {@link TagAction}s. Seem to work well.
 *
 * @see TagActionMap
 */
public class DefaultTagActionMap {
    public static final DefaultTagActionMap INSTANCE = new DefaultTagActionMap();

    private HashMap<String, TagAction> actions = new HashMap<String, TagAction>();

    protected DefaultTagActionMap() {
        setTagAction("A", CommonTagActions.TA_ANCHOR_TEXT);

        setTagAction("LI", new CommonTagActions.BlockTagLabelAction(
            new LabelAction(DefaultLabels.LI)));
        setTagAction("H1", new CommonTagActions.BlockTagLabelAction(
            new LabelAction(DefaultLabels.H1, DefaultLabels.HEADING)));
        setTagAction("H2", new CommonTagActions.BlockTagLabelAction(
            new LabelAction(DefaultLabels.H2, DefaultLabels.HEADING)));
        setTagAction("H3", new CommonTagActions.BlockTagLabelAction(
            new LabelAction(DefaultLabels.H3, DefaultLabels.HEADING)));
    }

    public boolean containsKey(String tag) {
        return actions.containsKey(tag.toUpperCase());
    }

    public TagAction get(String tag) {
        return actions.get(tag.toUpperCase());
    }

    private void setTagAction(String tag, TagAction action) {
        actions.put(tag.toUpperCase(), action);
    }
}
