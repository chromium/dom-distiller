// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.js.JsExport;

public class JsTestEntry implements EntryPoint {
    public void onModuleLoad() {
        run();
    }

    @JsExport
    public static void run() {
        JsTestSuiteBuilder builder = GWT.<JsTestSuiteBuilder>create(JsTestSuiteBuilder.class);
        builder.build().run(new TestLogger());
    }
}
