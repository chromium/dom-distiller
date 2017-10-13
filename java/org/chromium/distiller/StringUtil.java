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

    public static native String[] jsSplit(String input, String sep) /*-{
        return input.split(sep);
    }-*/;

    public static native String join(String[] strings, String sep) /*-{
        return strings.join(sep);
    }-*/;

    public static native String decodeURIComponent(String input) /*-{
        return decodeURIComponent(input);
    }-*/;

    public static int splitLength(String input, String regex) {
        return StringUtil.split(input, regex).length;
    }

    public static boolean match(String input, String regex) {
        return RegExp.compile(regex, "i").test(input);
    }

    public static String findAndReplace(String input, String regex, String replace) {
        return RegExp.compile(regex, "gi").replace(input, replace);
    }

    /**
     * For some languages, counting the number of words relies on non-trivial word
     * segmentation algorithms, or even huge look-up tables. This function needs to
     * be reasonably fast, so the word count for some languages would only be an
     * approximation.
     * Read https://crbug.com/484750 for more info.
     */
    public static interface WordCounter {
        public int count(String s);
    }

    public static class FullWordCounter implements WordCounter {
        @Override
        public native int count(String s) /*-{
            // The following range includes broader alphabetical letters and Hangul Syllables.
            var m = s.match(/(\S*[\w\u00C0-\u1FFF\uAC00-\uD7AF]\S*)/g);
            var c = (m ? m.length : 0);
            // The following range includes Hiragana, Katakana, and CJK Unified Ideographs.
            // Hangul Syllables are not included.
            m = s.match(/([\u3040-\uA4CF])/g);
            c += Math.ceil((m ? m.length : 0) * 0.55);
            return c;
        }-*/;
    }

    public static class LetterWordCounter implements WordCounter {
        @Override
        public native int count(String s) /*-{
            // The following range includes broader alphabetical letters and Hangul Syllables.
            var m = s.match(/(\S*[\w\u00C0-\u1FFF\uAC00-\uD7AF]\S*)/g);
            return (m ? m.length : 0);
        }-*/;
    }

    public static class FastWordCounter implements WordCounter {
        @Override
        public native int count(String s) /*-{
            // The following range includes broader alphabetical letters.
            var m = s.match(/(\S*[\w\u00C0-\u1FFF]\S*)/g);
            return (m ? m.length : 0);
        }-*/;
    }

    public static void setWordCounter(String text) {
        sWordCounter = selectWordCounter(text);
    }

    public static WordCounter selectWordCounter(String text) {
        final RegExp rFull = RegExp.compile("[\\u3040-\\uA4CF]", "g");
        final RegExp rLetter = RegExp.compile("[\\uAC00-\\uD7AF]", "g");

        if (rFull.test(text)) {
            return new FullWordCounter();
        } else if (rLetter.test(text)) {
            return new LetterWordCounter();
        } else {
            return new FastWordCounter();
        }
    }

    // Use the safest version of WordCounter as the default.
    static WordCounter sWordCounter = new FullWordCounter();

    public static int countWords(String s) {
        return sWordCounter.count(s);
    }

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
