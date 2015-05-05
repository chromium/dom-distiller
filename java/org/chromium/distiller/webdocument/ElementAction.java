// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomUtil;
import org.chromium.distiller.labels.DefaultLabels;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.regexp.shared.RegExp;

public class ElementAction {
    public boolean changesTagLevel = false;
    public boolean flush = false;
    public boolean isAnchor = false;
    public JsArrayString labels = JavaScriptObject.createArray().<JsArrayString>cast();

    private static final RegExp REG_COMMENT = RegExp.compile("\\bcomments?\\b");
    private static final int MAX_CLASS_COUNT = 5;

    public static ElementAction getForElement(Element element) {
        Style style = DomUtil.getComputedStyle(element);
        ElementAction action = new ElementAction();
        switch (style.getDisplay()) {
            case "inline":
                break;
            case "inline-block":
            case "inline-flex":
                action.changesTagLevel = true;
                break;
            // See http://www.w3.org/TR/CSS2/tables.html#table-display
            // and http://www.w3.org/TR/css-flexbox-1/#flex-containers
            // The default case includes the following display types:
            // block
            // list-item
            // inline-table
            // table-row
            // table-row-group
            // table-header-group
            // table-footer-group
            // table-column
            // table-column-group
            // table-cell
            // table-caption
            // flex
            default:
                action.flush = true;
                action.changesTagLevel = true;
                break;
        }

        String tagName = element.getTagName();
        if (!"HTML".equals(tagName) && !"BODY".equals(tagName)) {
            String className = element.getAttribute("class");
            int classCount = DomUtil.getClassList(element).length();
            String id = element.getAttribute("id");
            if ((REG_COMMENT.test(className) || REG_COMMENT.test(id)) &&
                    classCount <= MAX_CLASS_COUNT) {
                action.labels.push(DefaultLabels.STRICTLY_NOT_CONTENT);
            }

            switch (tagName) {
                case "ASIDE":
                case "NAV":
                    action.labels.push(DefaultLabels.STRICTLY_NOT_CONTENT);
                    break;
                case "LI":
                    action.labels.push(DefaultLabels.LI);
                    break;
                case "H1":
                    action.labels.push(DefaultLabels.H1);
                    action.labels.push(DefaultLabels.HEADING);
                    break;
                case "H2":
                    action.labels.push(DefaultLabels.H2);
                    action.labels.push(DefaultLabels.HEADING);
                    break;
                case "H3":
                    action.labels.push(DefaultLabels.H3);
                    action.labels.push(DefaultLabels.HEADING);
                    break;
                case "A":
                    // TODO(cjhopman): Anchors probably shouldn't unconditionally change the tag
                    // level.
                    action.changesTagLevel = true;
                    if (element.hasAttribute("href")) {
                        action.isAnchor = true;
                    }
                    break;
            }
        }
        return action;
    }

    private ElementAction() {}
}
