// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

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
        return typeof l.contains == "function";
    }-*/;

    public static double parseFloat(String s) {
        if (jsniSupportsParseFloat()) {
            return jsniParseFloat(s);
        }
        return javaParseFloat(s);
    }

    private static native boolean jsniSupportsParseFloat() /*-{
        return typeof parseFloat == "function";
    }-*/;

    private static native double jsniParseFloat(String s) /*-{
        return parseFloat(s);
    }-*/;

    private static double javaParseFloat(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static int parseInt(String s) {
        return parseInt(s, 10);
    }

    public static int parseInt(String s, int radix) {
        if (jsniSupportsParseInt()) {
            return jsniParseInt(s, radix);
        }
        return javaParseInt(s, radix);
    }

    private static native boolean jsniSupportsParseInt() /*-{
        return typeof parseInt == "function";
    }-*/;

    private static native int jsniParseInt(String s, int radix) /*-{
        return parseInt(s, radix) | 0;
    }-*/;

    private static int javaParseInt(String s, int radix) {
        try {
            return Integer.parseInt(s, radix);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private JavaScript() {
    }
}
