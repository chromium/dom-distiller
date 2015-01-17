// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * boilerpipe
 *
 * Copyright (c) 2012 Christian Kohlschütter
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
package org.chromium.distiller.filters.debug;

import org.chromium.distiller.BoilerpipeFilter;
import org.chromium.distiller.LogUtil;
import org.chromium.distiller.document.TextDocument;


/**
 * Prints debug information about the current state of the TextDocument. (=
 * calls {@link TextDocument#debugString()}.
 *
 * @author Christian Kohlschütter
 */
public final class PrintDebugFilter implements BoilerpipeFilter {
    /**
     * Returns the default instance for {@link PrintDebugFilter},
     * which dumps debug information to <code>System.out</code>
     */
    public static final PrintDebugFilter INSTANCE = new PrintDebugFilter();


    @Override
    public boolean process(TextDocument doc) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_BOILER_PIPE_PHASES)) return false;
        LogUtil.logToConsole(doc.debugString());
        return false;
    }

    public boolean process(TextDocument doc, boolean changed, String header) {
        if (!LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_BOILER_PIPE_PHASES)) return false;
        if (changed) {
            LogUtil.logToConsole(LogUtil.kBlue + "<<<<< " + header + " >>>>>");
            process(doc);
            LogUtil.logToConsole(LogUtil.kBlue + "<<<<<                >>>>>");
        } else {
            LogUtil.logToConsole(LogUtil.kRed + "~~~~~ No Changes: " + header + " ~~~~~");
        }
        return false;
    }
}
