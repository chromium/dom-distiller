// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Node;

/**
 * Provides implementations of various javascript/DOM functions not available in GWT.
 */
public final class JavaScript {
    public static native boolean contains(Node l, Node r) /*-{
        return l.contains(r);
    }-*/;

    public static native double parseFloat(String s) /*-{
        return parseFloat(s);
    }-*/;

    public static int parseInt(String s) {
        return parseInt(s, 10);
    }

    public static native int parseInt(String s, int radix) /*-{
        return parseInt(s, radix) | 0;
    }-*/;

    private JavaScript() {
    }
}
