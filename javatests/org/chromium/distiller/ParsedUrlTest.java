// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class ParsedUrlTest extends JsTestCase {

    private static final String VALID_URL =
            "http://fooUser:fooPwd@www.foo.com/path0/path1/;pathParams?qA=B&qC=D";

    public void testAllGet() {
        ParsedUrl url = ParsedUrl.create(VALID_URL);
        assertTrue(url != null);
        assertEquals("www.foo.com", url.getHost());
        assertEquals("http://www.foo.com", url.getOrigin());
        assertEquals("/path0/path1/;pathParams", url.getPath());
        assertEquals("path0/path1", url.getTrimmedPath());
        assertEquals("?qA=B&qC=D", url.getQuery());
        assertEquals("fooUser", url.getUsername());
        assertEquals("fooPwd", url.getPassword());
        String[] pathComponents = url.getPathComponents();
        assertEquals(2, pathComponents.length);
        assertEquals("path0", pathComponents[0]);
        assertEquals("path1", pathComponents[1]);
        String[][] queryParams = url.getQueryParams();
        assertEquals(2, queryParams.length);
        assertEquals("qA", queryParams[0][0]);
        assertEquals("B", queryParams[0][1]);
        assertEquals("qC", queryParams[1][0]);
        assertEquals("D", queryParams[1][1]);
    }

    public void testInvalid() {
        ParsedUrl url = ParsedUrl.create("abc");
        assertEquals(null, url);
    }

    public void testSetUsernameAndPassword() {
        ParsedUrl url = ParsedUrl.create(VALID_URL);
        assertTrue(url != null);
        assertEquals("fooUser", url.getUsername());
        assertEquals("fooPwd", url.getPassword());
        url.setUsername("newFooUser");
        url.setPassword("newFooPwd");
        assertEquals("newFooUser", url.getUsername());
        assertEquals("newFooPwd", url.getPassword());
        assertEquals("http://newFooUser:newFooPwd@www.foo.com/path0/path1/;pathParams?qA=B&qC=D",
                url.toString());
        url.setUsername("");
        url.setPassword("");
        assertEquals("", url.getUsername());
        assertEquals("", url.getPassword());
        assertEquals("http://www.foo.com/path0/path1/;pathParams?qA=B&qC=D", url.toString());
    }

    public void testReplaceQueryValue() {
        ParsedUrl url = ParsedUrl.create(VALID_URL);
        assertTrue(url != null);
        assertEquals("?qA=B&qC=D", url.getQuery());
        assertEquals("http://fooUser:fooPwd@www.foo.com/path0/path1/;pathParams?qA=E&qC=D",
                url.replaceQueryValue("qA", "B", "E"));
        // Original query shouldn't change.
        assertEquals("?qA=B&qC=D", url.getQuery());
    }

}
