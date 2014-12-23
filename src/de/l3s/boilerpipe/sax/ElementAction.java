// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package de.l3s.boilerpipe.sax;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import de.l3s.boilerpipe.labels.DefaultLabels;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

import com.dom_distiller.client.DomUtil;

import java.util.regex.Pattern;

public class ElementAction {
    public boolean changesTagLevel = false;
    public boolean flush = false;
    public boolean isAnchor = false;
    public JsArrayString labels = JavaScriptObject.createArray().<JsArrayString>cast();

    private static final Pattern PAT_COMMENT = Pattern.compile("\\bcomments?\\b");

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
        if (!tagName.equals("BODY")) {
            String className = element.getAttribute("class");
            String id = element.getAttribute("id");
            if (PAT_COMMENT.matcher(className).find() || PAT_COMMENT.matcher(id).find()) {
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
