// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.junit.tools.GWTTestSuite;

import com.dom_distiller.client.util.TextBlockBuilderTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DomDistillerTestSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("All GWTTestCase tests");
        suite.addTestSuite(PagingLinksFinderTest.class);
        return suite;
    }
}

