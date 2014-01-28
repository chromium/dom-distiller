// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package java.util.regex;

import java.lang.IllegalStateException;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * This is a limited implementation of Java's java.util.regex.Matcher.
 */
public class Matcher {
    private String input;
    private RegExp regex;
    private MatchResult currentMatch;

    Matcher(String r, String in) {
        regex = RegExp.compile(r, "g");
        input = in;
        currentMatch = null;
    }

    public String replaceAll(String r) {
        currentMatch = null;
        regex.setLastIndex(0);
        return regex.replace(input, r);
    }

    public int start() throws IllegalStateException {
        if (currentMatch == null) {
            throw new IllegalStateException();
        }
        return currentMatch.getIndex();
    }

    public int end() throws IllegalStateException {
        if (currentMatch == null) {
            throw new IllegalStateException();
        }
        return currentMatch.getIndex() + currentMatch.getGroup(0).length();
    }

    public String group() {
        return group(0);
    }

    public String group(int i) {
        if (currentMatch == null) {
            throw new IllegalStateException();
        }
        return currentMatch.getGroup(i);
    }

    public boolean find() {
        currentMatch = regex.exec(input);
        return currentMatch != null;
    }

    public boolean matches() {
        // TODO(cjhopman): Confirm that this is how java's Matcher handle matches() after find()s.
        int previousLastIndex = regex.getLastIndex();
        regex.setLastIndex(0);
        MatchResult match = regex.exec(input);
        regex.setLastIndex(previousLastIndex);
        if (match != null) {
            return match.getIndex() == 0 && match.getGroup(0).length() == input.length();
        }
        return false;
    }
}
