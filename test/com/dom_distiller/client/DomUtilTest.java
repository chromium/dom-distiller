// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;

import com.google.gwt.junit.client.GWTTestCase;

public class DomUtilTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testJavaGetFirstElementWithClassName() {
        Element rootDiv = TestUtil.createDiv(0);
        
        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("abcd");
        rootDiv.appendChild(div1);

        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("test");
        div2.addClassName("xyz");
        rootDiv.appendChild(div2);

        Element div3 = TestUtil.createDiv(2);
        div3.addClassName("foobar foo");
        rootDiv.appendChild(div3);

        assertEquals(div1, DomUtil.javaGetFirstElementWithClassName(rootDiv, "abcd"));
        assertEquals(div2, DomUtil.javaGetFirstElementWithClassName(rootDiv, "test"));
        assertEquals(div2, DomUtil.javaGetFirstElementWithClassName(rootDiv, "xyz"));
        assertEquals(null, DomUtil.javaGetFirstElementWithClassName(rootDiv, "bc"));
        assertEquals(null, DomUtil.javaGetFirstElementWithClassName(rootDiv, "t xy"));
        assertEquals(null, DomUtil.javaGetFirstElementWithClassName(rootDiv, "tes"));
        assertEquals(div3, DomUtil.javaGetFirstElementWithClassName(rootDiv, "foo"));
    }
}
