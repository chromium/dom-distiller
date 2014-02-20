// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

public class StringUtil {
    /**
     * This is an approximation to Java's Character.isWhitespace(). That function is not available
     * with GWT.
     *
     * TODO(cjhopman): Handle unicode whitespace.
     */
    public static boolean isWhitespace(Character c) {
        return " \t\n\r".indexOf(c) != -1;
    }

    public static boolean isStringAllWhitespace(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }
}
