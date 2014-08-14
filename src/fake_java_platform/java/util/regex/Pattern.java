// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

/**
 * This is a limited implementation of Java's java.util.regex.Pattern.
 */
public class Pattern {
    public static final int CASE_INSENSITIVE = 2;

    public static Pattern compile(String s) {
        return new Pattern(s);
    }

    public static Pattern compile(String s, int f) {
        return new Pattern(s, f);
    }

    public Matcher matcher(CharSequence s) {
        return new Matcher(regexString, flags, s.toString());
    }

    public String[] split(CharSequence s) {
        RegExp regex = RegExp.compile(regexString, flags);
        SplitResult splits = regex.split(s.toString());
        String[] strings = new String[splits.length()];
        for (int i = 0; i < splits.length(); ++i)
            strings[i] = splits.get(i);
        return strings;
    }

    private Pattern(String s) {
        regexString = s;
        flags = "g";
    }

    private Pattern(String s, int f) {
        regexString = s;
        flags = "g";
        if ((f & CASE_INSENSITIVE) != 0)
          flags += "i";
    }

    private String regexString;
    private String flags;
}
