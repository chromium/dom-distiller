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
package org.chromium.distiller.extractors;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.filters.simple.MarkEverythingContentFilter;

/**
 * Marks everything as content.
 *
 * @author Christian Kohlschütter
 */
public final class KeepEverythingExtractor {

    public static final KeepEverythingExtractor INSTANCE = new KeepEverythingExtractor();

    private KeepEverythingExtractor() {

    }

    public boolean process(TextDocument doc) {
        return MarkEverythingContentFilter.INSTANCE.process(doc);
    }

}
