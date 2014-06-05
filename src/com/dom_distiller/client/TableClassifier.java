// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableElement;

/**
 * Classifies a <table> element as layout or data type.
 */
public class TableClassifier {
    public enum Type {
        DATA,
        LAYOUT,
        UNKNOWN,
    }

    public static Type getType(TableElement t) {
       if (t.getAttribute("role").equalsIgnoreCase("presentation")) return Type.LAYOUT;
       if (t.getCaption() != null || t.getTHead() != null) return Type.DATA;
       NodeList<Element> allTh = t.getElementsByTagName("TH");
       for (int i = 0; i < allTh.getLength(); i++) {
           // Check if the current <th> element belongs directly to the |t| table element in
           // question, as opposed to a nested table in |t|.
           Element e = allTh.getItem(i).getParentElement();
           while (e != null) {
               if (e.hasTagName("TABLE")) {
                   if (e == t) return Type.DATA;
                   break;  // Belongs to a nested table of |t|, ignore.
               }
               e = e.getParentElement();
           }
       }
       return Type.UNKNOWN;
    }
}
