// Copyright 2015 The Chromium Authors. All rights reserved.
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
package org.chromium.distiller.labels;

import org.chromium.distiller.document.TextBlock;

import java.util.Arrays;

/**
 * Helps adding labels to {@link TextBlock}s.
 *
 * @author Christian Kohlschütter
 * @see ConditionalLabelAction
 */
public class LabelAction {
    protected final String[] labels;

    public LabelAction(String... labels) {
        this.labels = labels;
    }

    public void addTo(final TextBlock tb) {
        addLabelsTo(tb);
    }

    protected final void addLabelsTo(final TextBlock tb) {
        tb.addLabels(labels);
    }

    public String[] getLabels() {
        return labels;
    }

    @Override
    public String toString() {
    	return super.toString()+"{"+Arrays.asList(labels)+"}";
    }
}
