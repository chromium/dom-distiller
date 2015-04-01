// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;

import com.google.gwt.dom.client.Element;

public class WebTable extends WebElement {
    private Element tableElement;
    public WebTable(Element tableRoot) {
        tableElement = tableRoot;
    }

    @Override
    public String generateOutput(boolean textOnly) {
        return DomUtil.generateOutputFromTree(tableElement, textOnly);
    }

    public Element getTableElement() {
        return tableElement;
    }
}
