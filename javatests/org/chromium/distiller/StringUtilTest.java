// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.RegExp;
import java.util.ArrayList;
import java.util.List;

public class StringUtilTest extends JsTestCase {
    public void testFastWordCounter() {
        List<StringUtil.WordCounter> counters = new ArrayList();
        counters.add(new StringUtil.FastWordCounter());
        counters.add(new StringUtil.LetterWordCounter());
        counters.add(new StringUtil.FullWordCounter());

        for(StringUtil.WordCounter counter: counters) {
            assertEquals(0, counter.count(""));
            assertEquals(0, counter.count("  -@# ';]"));
            assertEquals(1, counter.count("word"));
            assertEquals(1, counter.count("b'fore"));
            assertEquals(1, counter.count(" _word.under_score_ "));
            assertEquals(2, counter.count(" \ttwo\nwords"));
            assertEquals(2, counter.count(" \ttwo @^@^&(@#$([][;;\nwords"));
            // Norwegian
            assertEquals(5, counter.count("dør når på svært dårlig"));
            assertEquals(5, counter.count("svært få dør av blåbærsyltetøy"));
            // Greek
            assertEquals(11, counter.count(
                    "Παρέμβαση των ΗΠΑ για τα τεχνητά νησιά που κατασκευάζει η Κίνα"));
            // Arabic
            assertEquals(6, counter.count("زلزال بقوة 8.5 درجات يضرب اليابان"));
            // Tibetan
            assertEquals(1, counter.count("༧གོང་ས་མཆོག་གི་ནང་གི་ངོ་སྤྲོད་ཀྱི་གསུང་ཆོས་ལེགས་གྲུབ།"));
            // Thai
            assertEquals(3, counter.count("โซลาร์ อิมพัลส์ทู เหินฟ้าข้ามมหาสมุทร"));
        }
    }

    public void testLetterWordCounter() {
        List<StringUtil.WordCounter> counters = new ArrayList();
        counters.add(new StringUtil.LetterWordCounter());
        counters.add(new StringUtil.FullWordCounter());

        for(StringUtil.WordCounter counter: counters) {
            // Hangul uses space as word delimiter like English.
            assertEquals(1, counter.count("어"));
            assertEquals(2, counter.count("한국어 단어"));
            assertEquals(5, counter.count("한 국 어 단 어"));
            assertEquals(8, counter.count(
                    "예비군 훈련장 총기 난사범 최모씨의 군복에서 발견된 유서."));
        }
    }

    public void testFullWordCounter() {
        StringUtil.WordCounter counter = new StringUtil.FullWordCounter();
        // One Chinese sentence, or a series of Japanese glyphs should not be treated
        // as a single word.
        assertTrue(counter.count("一個中文句子不應該當成一個字") > 1); // zh-Hant
        assertTrue(counter.count("中国和马来西亚使用简体字") > 1); // zh-Hans
        assertTrue(counter.count("ファイナルファンタジー") > 1); // Katakana
        assertTrue(counter.count("いってらっしゃい") > 1); // Hiragana
        assertTrue(counter.count("仏仮駅辺") > 1); // Kanji
        // However, treating each Chinese/Japanese glyph as a word is also wrong.
        assertTrue(counter.count("一個中文句子不應該當成一個字") < 14);
        assertTrue(counter.count("中国和马来西亚使用简体字") < 12);
        assertTrue(counter.count("ファイナルファンタジー") < 11);
        assertTrue(counter.count("いってらっしゃい") < 8);
        assertTrue(counter.count("仏仮駅辺") < 4);
        // Even if they are separated by spaces.
        assertTrue(counter.count("一 個 中 文 句 子 不 應 該 當 成 一 個 字") < 14);
        assertTrue(counter.count("中 国 和 马 来 西 亚 使 用 简 体 字") < 12);
        assertTrue(counter.count("フ ァ イ ナ ル フ ァ ン タ ジ ー") < 11);
        assertTrue(counter.count("い っ て ら っ し ゃ い") < 8);
        assertTrue(counter.count("仏 仮 駅 辺") < 4);

        assertEquals(1, counter.count("字"));
        assertEquals(1, counter.count("が"));

        // Mixing ASCII words and Chinese/Japanese glyphs
        assertEquals(2, counter.count("word字"));
        assertEquals(2, counter.count("word 字"));
    }

    public void testSelectWordCounter() {
        StringUtil.WordCounter counter;

        counter = StringUtil.selectWordCounter("abc");
        assertTrue(counter instanceof StringUtil.FastWordCounter);

        counter = StringUtil.selectWordCounter("어");
        assertTrue(counter instanceof StringUtil.LetterWordCounter);

        counter = StringUtil.selectWordCounter("字");
        assertTrue(counter instanceof StringUtil.FullWordCounter);
    }

    public void testCountWords() {
        StringUtil.setWordCounter("");
        assertEquals(2, StringUtil.countWords("two words"));
        assertEquals(0, StringUtil.countWords("어"));
        StringUtil.setWordCounter("어");
        assertEquals(1, StringUtil.countWords("어"));
        assertEquals(0, StringUtil.countWords("字"));
        StringUtil.setWordCounter("字");
        assertEquals(1, StringUtil.countWords("字"));
        // Make sure the internal WordCounter is restored to FullWordCounter in the end.
    }

    public void testIsWhitespace() {
        assertTrue(StringUtil.isWhitespace(' '));
        assertTrue(StringUtil.isWhitespace('\t'));
        assertTrue(StringUtil.isWhitespace('\n'));
        assertTrue(StringUtil.isWhitespace('\u00a0'));
        assertFalse(StringUtil.isWhitespace('a'));
        assertFalse(StringUtil.isWhitespace('$'));
        assertFalse(StringUtil.isWhitespace('_'));
        assertFalse(StringUtil.isWhitespace('\u0460'));
    }

    public void testIsStringAllWhitespace() {
        assertTrue(StringUtil.isStringAllWhitespace(""));
        assertTrue(StringUtil.isStringAllWhitespace(" \t\r\n"));
        assertTrue(StringUtil.isStringAllWhitespace(" \u00a0     \t\t\t"));
        assertFalse(StringUtil.isStringAllWhitespace("a"));
        assertFalse(StringUtil.isStringAllWhitespace("     a  "));
        assertFalse(StringUtil.isStringAllWhitespace("\u00a0\u0460"));
        assertFalse(StringUtil.isStringAllWhitespace("\n\t_ "));
    }

    public void testFindAndReplace() {
        assertEquals("", StringUtil.findAndReplace("sdf", ".", ""));
        assertEquals("abc", StringUtil.findAndReplace(" a\tb  c ", "\\s", ""));
    }

    private RegExp toRegex(String s) {
        return RegExp.compile(StringUtil.regexEscape(s));
    }

    public void testRegexEscape() {
        assertTrue(toRegex(".*").test(".*"));
        assertFalse(toRegex(".*").test("test"));
        assertFalse(toRegex("[a-z]+").test("az"));
        assertFalse(toRegex("[a-z]+").test("[a-z]"));
        assertTrue(toRegex("[a-z]+").test("[a-z]+"));
        assertTrue(toRegex("\t\n\\\\d[").test("\t\n\\\\d["));
    }

    public void testIsDigit() {
        assertTrue(StringUtil.isDigit('1'));
        assertTrue(StringUtil.isDigit('0'));
        assertFalse(StringUtil.isDigit(' '));
        assertFalse(StringUtil.isDigit('a'));
        assertFalse(StringUtil.isDigit('$'));
        assertFalse(StringUtil.isDigit('_'));
        assertFalse(StringUtil.isDigit('\u0460'));
    }

    public void testIsStringAllDigits() {
        assertTrue(StringUtil.isStringAllDigits("0"));
        assertTrue(StringUtil.isStringAllDigits("018"));
        assertFalse(StringUtil.isStringAllDigits(""));
        assertFalse(StringUtil.isStringAllDigits("a0"));
        assertFalse(StringUtil.isStringAllDigits("0a"));
        assertFalse(StringUtil.isStringAllDigits(" "));
        assertFalse(StringUtil.isStringAllDigits(" 8"));
        assertFalse(StringUtil.isStringAllDigits("8 "));
        assertFalse(StringUtil.isStringAllDigits("'8_"));
        assertFalse(StringUtil.isStringAllDigits("\u00a0\u0460"));
    }

    public void testContainsDigit() {
        assertTrue(StringUtil.containsDigit("0"));
        assertTrue(StringUtil.containsDigit("018"));
        assertTrue(StringUtil.containsDigit("a0"));
        assertTrue(StringUtil.containsDigit("0a"));
        assertTrue(StringUtil.containsDigit(" 8"));
        assertTrue(StringUtil.containsDigit("8 "));
        assertTrue(StringUtil.containsDigit("'8_"));
        assertFalse(StringUtil.containsDigit(""));
        assertFalse(StringUtil.containsDigit(" "));
        assertFalse(StringUtil.containsDigit("\u00a0\u0460"));
        assertFalse(StringUtil.containsDigit("abc"));
        assertFalse(StringUtil.containsDigit("$"));
        assertFalse(StringUtil.containsDigit("_"));
    }

    public void testToNumber() {
        assertEquals(0, StringUtil.toNumber("0"));
        assertEquals(18, StringUtil.toNumber("018"));
        assertEquals(-1, StringUtil.toNumber("a0"));
        assertEquals(-1, StringUtil.toNumber("0a"));
        assertEquals(-1, StringUtil.toNumber(" 8"));
        assertEquals(-1, StringUtil.toNumber("8 "));
        assertEquals(-1, StringUtil.toNumber("'8_"));
        assertEquals(-1, StringUtil.toNumber(""));
        assertEquals(-1, StringUtil.toNumber(" "));
        assertEquals(-1, StringUtil.toNumber("\u00a0\u0460"));
        assertEquals(-1, StringUtil.toNumber("abc"));
        assertEquals(-1, StringUtil.toNumber("$"));
        assertEquals(-1, StringUtil.toNumber("_"));
    }

}
