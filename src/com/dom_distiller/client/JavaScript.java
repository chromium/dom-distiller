// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Node;

/**
 * Provides implementations of various javascript/DOM functions not available in GWT.
 */
public final class JavaScript {
    public static boolean contains(Node left, Node right) {
        if (jsniSupportsContains(left)) {
            return jsniContains(left, right);
        } else {
            return javaContains(left, right);
        }
    }

    private static boolean javaContains(Node l, Node r) {
        for (int i = 0; i < l.getChildCount(); i++) {
            Node c = l.getChild(i);
            if (c.equals(r)) return true;
            if (javaContains(c, r)) return true;
        }
        return false;
    }

    private static native boolean jsniContains(Node l, Node r) /*-{
        return l.contains(r);
    }-*/;

    private static native boolean jsniSupportsContains(Node l) /*-{
        return l.contains !== undefined;
    }-*/;

    private JavaScript() {
    }
}
