// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.util;

import com.dom_distiller.client.StringUtil;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.util.UnicodeTokenizer;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextBlockBuilder {
    private static final String ANCHOR_TEXT_START = "$\ue00a<";
    private static final String ANCHOR_TEXT_END = ">\ue00a$";

    private final StringBuilder tokenBuffer = new StringBuilder();
    private final StringBuilder textBuffer = new StringBuilder();

    private boolean lastWasWhitespace = false;
    private boolean inAnchor = false;
    private int blockTagLevel = -1;

    private final List<Node> nonWhitespaceTextElements = new LinkedList<Node>();
    private final List<Node> allTextElements = new LinkedList<Node>();

    public void textNode(Text textNode, int tagLevel) {
        String text = textNode.getData();

        if (text.isEmpty()) {
            return;
        }

        textBuffer.append(text);
        tokenBuffer.append(text);
        allTextElements.add(textNode);

        lastWasWhitespace = StringUtil.isWhitespace(text.charAt(text.length() - 1));
        if (StringUtil.isStringAllWhitespace(text)) {
            return;
        }

        if (blockTagLevel == -1) {
            blockTagLevel = tagLevel;
        }

        nonWhitespaceTextElements.add(textNode);
    }

    public void reset() {
        textBuffer.setLength(0);
        tokenBuffer.setLength(0);
        nonWhitespaceTextElements.clear();
        allTextElements.clear();
        blockTagLevel = -1;
        if (inAnchor) {
            tokenBuffer.append(ANCHOR_TEXT_START);
            tokenBuffer.append(' ');
        }
    }

    public TextBlock build(int offsetBlocks) {
        if (allTextElements.isEmpty() || nonWhitespaceTextElements.isEmpty() ||
                StringUtil.isStringAllWhitespace(tokenBuffer.toString())) {
            reset();
            return null;
        }
        final String[] tokens = UnicodeTokenizer.tokenize(tokenBuffer.toString());

        int numWords = 0;
        int numLinkedWords = 0;
        int numTokens = 0;
        int numWordsCurrentLine = 0;

        boolean inAnchorText = false;
        for (String token : tokens) {
            if (ANCHOR_TEXT_START.equals(token)) {
                inAnchorText = true;
            } else if (ANCHOR_TEXT_END.equals(token)) {
                inAnchorText = false;
            } else if (isWord(token)) {
                numTokens++;
                numWords++;
                if (inAnchorText) {
                    numLinkedWords++;
                }
            } else {
                numTokens++;
            }
        }

        TextBlock tb = null;
        if (numTokens != 0) {
            tb = new TextBlock(StringUtil.javaTrim(textBuffer.toString()),
                    nonWhitespaceTextElements, allTextElements, numWords, numLinkedWords,
                    offsetBlocks);
            tb.setTagLevel(blockTagLevel);
        }

        reset();

        return tb;
    }

    private void addWhitespaceIfNecessary() {
        if (!lastWasWhitespace) {
            tokenBuffer.append(' ');
            textBuffer.append(' ');
            lastWasWhitespace = true;
        }
    }

    public void enterAnchor() {
        addWhitespaceIfNecessary();
        tokenBuffer.append(ANCHOR_TEXT_START);
        tokenBuffer.append(' ');
        lastWasWhitespace = true;
        inAnchor = true;
    }

    public void exitAnchor() {
        addWhitespaceIfNecessary();
        tokenBuffer.append(ANCHOR_TEXT_END);
        tokenBuffer.append(' ');
        inAnchor = false;
    }

    private static boolean isWord(final String token) {
        return PAT_VALID_WORD_CHARACTER.matcher(token).find();
    }

    // This is generated by tools/UnicodePatternGenerator.java for the pattern:
    // "[\\p{L}\\p{Nd}\\p{Nl}\\p{No}]"
    private static final Pattern PAT_VALID_WORD_CHARACTER = Pattern.compile("["
            + "\u0030-\u0039\u0041-\u005a\u0061-\u007a\u00aa\u00b2-\u00b3\u00b5\u00b9-\u00ba"
            + "\u00bc-\u00be\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u0236\u0250-\u02c1\u02c6-\u02d1"
            + "\u02e0-\u02e4\u02ee\u037a\u0386\u0388-\u038a\u038c\u038e-\u03a1\u03a3-\u03ce"
            + "\u03d0-\u03f5\u03f7-\u03fb\u0400-\u0481\u048a-\u04ce\u04d0-\u04f5\u04f8-\u04f9"
            + "\u0500-\u050f\u0531-\u0556\u0559\u0561-\u0587\u05d0-\u05ea\u05f0-\u05f2"
            + "\u0621-\u063a\u0640-\u064a\u0660-\u0669\u066e-\u066f\u0671-\u06d3\u06d5"
            + "\u06e5-\u06e6\u06ee-\u06fc\u06ff\u0710\u0712-\u072f\u074d-\u074f\u0780-\u07a5"
            + "\u07b1\u0904-\u0939\u093d\u0950\u0958-\u0961\u0966-\u096f\u0985-\u098c"
            + "\u098f-\u0990\u0993-\u09a8\u09aa-\u09b0\u09b2\u09b6-\u09b9\u09bd\u09dc-\u09dd"
            + "\u09df-\u09e1\u09e6-\u09f1\u09f4-\u09f9\u0a05-\u0a0a\u0a0f-\u0a10\u0a13-\u0a28"
            + "\u0a2a-\u0a30\u0a32-\u0a33\u0a35-\u0a36\u0a38-\u0a39\u0a59-\u0a5c\u0a5e\u0a66-\u0a6f"
            + "\u0a72-\u0a74\u0a85-\u0a8d\u0a8f-\u0a91\u0a93-\u0aa8\u0aaa-\u0ab0\u0ab2-\u0ab3"
            + "\u0ab5-\u0ab9\u0abd\u0ad0\u0ae0-\u0ae1\u0ae6-\u0aef\u0b05-\u0b0c\u0b0f-\u0b10"
            + "\u0b13-\u0b28\u0b2a-\u0b30\u0b32-\u0b33\u0b35-\u0b39\u0b3d\u0b5c-\u0b5d\u0b5f-\u0b61"
            + "\u0b66-\u0b6f\u0b71\u0b83\u0b85-\u0b8a\u0b8e-\u0b90\u0b92-\u0b95\u0b99-\u0b9a\u0b9c"
            + "\u0b9e-\u0b9f\u0ba3-\u0ba4\u0ba8-\u0baa\u0bae-\u0bb5\u0bb7-\u0bb9\u0be7-\u0bf2"
            + "\u0c05-\u0c0c\u0c0e-\u0c10\u0c12-\u0c28\u0c2a-\u0c33\u0c35-\u0c39\u0c60-\u0c61"
            + "\u0c66-\u0c6f\u0c85-\u0c8c\u0c8e-\u0c90\u0c92-\u0ca8\u0caa-\u0cb3\u0cb5-\u0cb9\u0cbd"
            + "\u0cde\u0ce0-\u0ce1\u0ce6-\u0cef\u0d05-\u0d0c\u0d0e-\u0d10\u0d12-\u0d28\u0d2a-\u0d39"
            + "\u0d60-\u0d61\u0d66-\u0d6f\u0d85-\u0d96\u0d9a-\u0db1\u0db3-\u0dbb\u0dbd\u0dc0-\u0dc6"
            + "\u0e01-\u0e30\u0e32-\u0e33\u0e40-\u0e46\u0e50-\u0e59\u0e81-\u0e82\u0e84\u0e87-\u0e88"
            + "\u0e8a\u0e8d\u0e94-\u0e97\u0e99-\u0e9f\u0ea1-\u0ea3\u0ea5\u0ea7\u0eaa-\u0eab"
            + "\u0ead-\u0eb0\u0eb2-\u0eb3\u0ebd\u0ec0-\u0ec4\u0ec6\u0ed0-\u0ed9\u0edc-\u0edd"
            + "\u0f00\u0f20-\u0f33\u0f40-\u0f47\u0f49-\u0f6a\u0f88-\u0f8b\u1000-\u1021\u1023-\u1027"
            + "\u1029-\u102a\u1040-\u1049\u1050-\u1055\u10a0-\u10c5\u10d0-\u10f8\u1100-\u1159"
            + "\u115f-\u11a2\u11a8-\u11f9\u1200-\u1206\u1208-\u1246\u1248\u124a-\u124d\u1250-\u1256"
            + "\u1258\u125a-\u125d\u1260-\u1286\u1288\u128a-\u128d\u1290-\u12ae\u12b0\u12b2-\u12b5"
            + "\u12b8-\u12be\u12c0\u12c2-\u12c5\u12c8-\u12ce\u12d0-\u12d6\u12d8-\u12ee\u12f0-\u130e"
            + "\u1310\u1312-\u1315\u1318-\u131e\u1320-\u1346\u1348-\u135a\u1369-\u137c\u13a0-\u13f4"
            + "\u1401-\u166c\u166f-\u1676\u1681-\u169a\u16a0-\u16ea\u16ee-\u16f0\u1700-\u170c"
            + "\u170e-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176c\u176e-\u1770\u1780-\u17b3\u17d7"
            + "\u17dc\u17e0-\u17e9\u17f0-\u17f9\u1810-\u1819\u1820-\u1877\u1880-\u18a8\u1900-\u191c"
            + "\u1946-\u196d\u1970-\u1974\u1d00-\u1d6b\u1e00-\u1e9b\u1ea0-\u1ef9\u1f00-\u1f15"
            + "\u1f18-\u1f1d\u1f20-\u1f45\u1f48-\u1f4d\u1f50-\u1f57\u1f59\u1f5b\u1f5d\u1f5f-\u1f7d"
            + "\u1f80-\u1fb4\u1fb6-\u1fbc\u1fbe\u1fc2-\u1fc4\u1fc6-\u1fcc\u1fd0-\u1fd3\u1fd6-\u1fdb"
            + "\u1fe0-\u1fec\u1ff2-\u1ff4\u1ff6-\u1ffc\u2070-\u2071\u2074-\u2079\u207f-\u2089\u2102"
            + "\u2107\u210a-\u2113\u2115\u2119-\u211d\u2124\u2126\u2128\u212a-\u212d\u212f-\u2131"
            + "\u2133-\u2139\u213d-\u213f\u2145-\u2149\u2153-\u2183\u2460-\u249b\u24ea-\u24ff"
            + "\u2776-\u2793\u3005-\u3007\u3021-\u3029\u3031-\u3035\u3038-\u303c\u3041-\u3096"
            + "\u309d-\u309f\u30a1-\u30fa\u30fc-\u30ff\u3105-\u312c\u3131-\u318e\u3192-\u3195"
            + "\u31a0-\u31b7\u31f0-\u31ff\u3220-\u3229\u3251-\u325f\u3280-\u3289\u32b1-\u32bf"
            + "\u3400-\u4db5\u4e00-\u9fa5\ua000-\ua48c\uac00-\ud7a3\uf900-\ufa2d\ufa30-\ufa6a"
            + "\ufb00-\ufb06\ufb13-\ufb17\ufb1d\ufb1f-\ufb28\ufb2a-\ufb36\ufb38-\ufb3c\ufb3e"
            + "\ufb40-\ufb41\ufb43-\ufb44\ufb46-\ufbb1\ufbd3-\ufd3d\ufd50-\ufd8f\ufd92-\ufdc7"
            + "\ufdf0-\ufdfb\ufe70-\ufe74\ufe76-\ufefc\uff10-\uff19\uff21-\uff3a\uff41-\uff5a"
            + "\uff66-\uffbe\uffc2-\uffc7\uffca-\uffcf\uffd2-\uffd7\uffda-\uffdc"
            + "]");
}