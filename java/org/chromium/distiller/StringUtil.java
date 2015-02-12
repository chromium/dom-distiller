// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.RegExp;

public class StringUtil {
    // For the whitespace-related functions below, Java's and Javascript's versions of '\s' and '\S'
    // are different.  E.g. java doesn't recognize &nbsp; in a text node as whitespace but
    // javascript does.  The former causes GWT tests to fail; the latter is what we want.
    // Don't use the "g" global search flag, or subsequent searches, even with different Character
    // or String, become unpredictable.

    public static native boolean isWhitespace(Character c) /*-{
        return /\s/.test(c);
    }-*/;

    public static native boolean isStringAllWhitespace(String s) /*-{
        return !/\S/.test(s);
    }-*/;

    public static native String jsTrim(String s) /*-{
        return s.trim();
    }-*/;

    public static String[] split(String input, String regex) {
        // TODO(cjhopman): investigate using native String.split()
        return input.split(regex);
    }

    public static int splitLength(String input, String regex) {
        return StringUtil.split(input, regex).length;
    }

    public static boolean match(String input, String regex) {
        return RegExp.compile(regex, "i").test(input);
    }

    public static String findAndReplace(String input, String regex, String replace) {
        return RegExp.compile(regex, "gi").replace(input, replace);
    }

    public static native boolean containsWordCharacter(String s) /*-{
        return /[\w\u00C0-\u1FFF\u2C00-\uD7FF]/.test(s);
    }-*/;

    public static native int countWords(String s) /*-{
        var m = s.match(/(\S*[\w\u00C0-\u1FFF\u2C00-\uD7FF]\S*)/g);
        return m ? m.length : 0;
    }-*/;

    public static native String regexEscape(String s) /*-{
        return s.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, "\\$&");
    }-*/;
}
