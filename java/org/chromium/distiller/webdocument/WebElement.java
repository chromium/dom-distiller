// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

/**
 * A WebElement is some logical part of a web document (text block, image, video, table, etc.).
 */
public abstract class WebElement {
    private boolean isContent;

    /**
     * Generate the HTML output for this WebElement.
     * @return Displayable HTML content representing this WebElement.
     */
    public abstract String generateOutput(boolean textOnly);

    public void setIsContent(boolean isContent) {
        this.isContent = isContent;
    }

    public boolean getIsContent() {
        return isContent;
    }
}
