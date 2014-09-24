// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class generates unicode patterns (of the form \u0000-\u0005\u0009\u000c-\u0010) for a
 * couple of character matching routines used in boilerpipe (e.g. unicode character classes, etc.).
 *
 * It only supports Unicode's Basic Multilingual Plane (i.e. code points \u0000 to \uFFFF).
 */
class UnicodePatternGenerator {
    public static void main(String[] args) {
        String range = createRange(PAT_VALID_WORD_CHARACTER);
        String verify = createRange(Pattern.compile("[" + range + "]"));
        if (range.equals(verify)) {
            System.out.println("SUCCESS");
            System.out.println(range);
        } else {
            System.out.println("FAILURE");
        }

        String spaceRange = createRange(new Checker() {
            public boolean check(char c) {
                return Character.isWhitespace(c);
            }
        });
        String verifySpaceRange = createRange(Pattern.compile("[" + spaceRange + "]"));
        if (spaceRange.equals(verifySpaceRange)) {
            System.out.println("SUCCESS");
            System.out.println(spaceRange);
        } else {
            System.out.println("FAILURE");
        }
    }

    private static interface Checker {
        boolean check(char s);
    }

    private static class PatternChecker implements Checker {
        PatternChecker(Pattern p) { this.p = p; }
        public boolean check(char c) {
            return p.matcher(Character.toString(c)).find();
        }
        Pattern p;
    }

    private static final Pattern PAT_VALID_WORD_CHARACTER = Pattern
            .compile("[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]");

    public static String toCodePoint(Integer i) {
        return String.format("\\u%04x", i);
    }

    public static String createRange(Pattern p) {
        return createRange(new PatternChecker(p));
    }

    public static String createRange(Checker c) {
        String range = "";
        int start = -1;
        for (int i = 0; i < (1 << 16); ++i) {
            if (!c.check((char)i)) {
                if (start >= 0) {
                    range += toCodePoint(start);
                    if (start != i - 1) {
                        range += "-";
                        range += toCodePoint(i - 1);
                    }
                }
                start = -1;
            } else if (start == -1) {
                start = i;
            }
        }
        return range;
    }
}
