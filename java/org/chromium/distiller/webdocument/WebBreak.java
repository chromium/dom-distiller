// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

/**
 * WebBreak is a placeholder for line breaks in the document.
 */
public class WebBreak extends WebElement {

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) return "\n";
        return "<br/>";
    }
}
