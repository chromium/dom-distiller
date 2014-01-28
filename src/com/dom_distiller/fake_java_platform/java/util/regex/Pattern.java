// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package java.util.regex;

import com.google.gwt.regexp.shared.RegExp;

/**
 * This is a limited implementation of Java's java.util.regex.Pattern.
 */
public class Pattern {
    public static Pattern compile(String s) {
        return new Pattern(s);
    }

    public Matcher matcher(CharSequence s) {
        return new Matcher(regexString, s.toString());
    }

    private Pattern(String s) {
        regexString = s;
    }

    private String regexString;
}
