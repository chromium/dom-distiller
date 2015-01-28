// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Node;

import java.util.List;

/**
 * A WebElement is some logical part of a web document (text block, image, video, table, etc.).
 */
public abstract class WebElement {
    private boolean isContent;

    /**
     * Adds this element's content nodes to the provided List.
     */
    public abstract void addOutputNodes(List<Node> nodes, boolean includeTitle);

    public void setIsContent(boolean isContent) {
        this.isContent = isContent;
    }

    public boolean getIsContent() {
        return isContent;
    }
}
