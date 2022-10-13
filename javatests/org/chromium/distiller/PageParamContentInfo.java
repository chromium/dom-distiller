// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

/**
 * Helper class to create content for PageParameterDetectorTest.java and PageParamInfoTest.java.
 */
class PageParamContentInfo {

    static enum Type {
        UNRELATED_TERMS,
        NUMBER_IN_PLAIN_TEXT,
        NUMERIC_OUTLINK,
    }

    Type mType;
    String mTargetUrl;
    int mNumber;

    static PageParamContentInfo UnrelatedTerms() {
        return new PageParamContentInfo(Type.UNRELATED_TERMS, "", -1);
    }

    static PageParamContentInfo NumberInPlainText(int number) {
        return new PageParamContentInfo(Type.NUMBER_IN_PLAIN_TEXT, "", number);
    }

    static PageParamContentInfo NumericOutlink(String targetUrl, int number) {
        return new PageParamContentInfo(Type.NUMERIC_OUTLINK, targetUrl, number);
    }

    private PageParamContentInfo(Type type, String targetUrl, int number) {
        mType = type;
        mTargetUrl = targetUrl;
        mNumber = number;
    }

}
