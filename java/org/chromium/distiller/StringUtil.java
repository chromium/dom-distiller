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

    /*
     * Returns true if character is a digit.
     */
    public static native boolean isDigit(Character c) /*-{
        return /\d/.test(c);
    }-*/;

    /**
     * Returns true if the entire string contains only digits.
     */
    public static native boolean isStringAllDigits(String s) /*-{
        return /^\d+$/.test(s);
    }-*/;

    /**
     * Returns true if string contains at least 1 digit.
     */
    public static native boolean containsDigit(String s) /*-{
        return /\d/.test(s);
    }-*/;

    /**
     * Returns the plain number if given string can be converted to one >= 0.
     * Returns -1 if string is empty or not all digits.
     */
    public static int toNumber(String s) {
        if (s.isEmpty() || !StringUtil.isStringAllDigits(s)) return -1;
        return JavaScript.parseInt(s, 10);
    }

}
