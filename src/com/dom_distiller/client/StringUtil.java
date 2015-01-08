// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

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

    // String.trim() is not defined in GWT, so use String.replace(RegEx) for GWT tests.
    // '\s' in RegEx for String.replace() doesn't include &nbsp (\u00a0), even though Regex.test()
    // does, so specifically add the unicode to the RegEx.
    public static native String jsTrim(String s) /*-{
        if (s.trim) return s.trim();
        return s.replace(/^[\s,\u00a0]+|[\s,\u00a0]+$/g, '');
    }-*/;

    // The version of gwt that we use implements trim improperly (it uses a javascript regex with \s
    // where java's trim explicitly matches \u0000-\u0020). This version is from GWT's trunk.
    public static native String javaTrim(String s) /*-{
        if (s.length == 0 || (s[0] > '\u0020' && s[s.length - 1] > '\u0020')) {
            return s;
        }
        return s.replace(/^[\u0000-\u0020]*|[\u0000-\u0020]*$/g, '');
    }-*/;

    public static String[] split(String input, String regex) {
        // Either use String.split(), which is rumored to be very slow
        // (see http://turbomanage.wordpress.com/2011/07/12/gwt-performance-tip-watch-out-for-string-split/),
        return input.split(regex);

/*
        // OR RegEx.split() via Pattern.split(),
        // TODO(kuan): add test for Pattern.split() if using this.
        Pattern pattern = Pattern.compile(regex);
        return pattern.split(input);
*/
    }

    // OR JSNI to call native Javascript regexp (as suggested by the website above).
    // Currently, "ant test.prod" which is closest to the "real world scenario" but still not very
    // accurate, has RegEx.split as the slowest, while GWT String.split and JSNI String.split as
    // almost the same.
    //private static final native int splitLength(String input, String regex) /*-{
        //return input.split(/regex/);
    //}-*/;

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
}
