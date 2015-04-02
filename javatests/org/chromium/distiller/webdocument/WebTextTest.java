// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.TestUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Text;

import java.util.List;

public class WebTextTest extends DomDistillerJsTestCase {
    public void testGenerateOutputMultipleContentNodes() {
        mRoot.appendChild(mBody);
        Element container = Document.get().createDivElement();
        mBody.appendChild(container);

        Element content1 = Document.get().createPElement();
        content1.appendChild(Document.get().createTextNode("Some text content 1."));
        container.appendChild(content1);

        Element content2 = Document.get().createPElement();
        content2.appendChild(Document.get().createTextNode("Some text content 2."));
        container.appendChild(content2);

        WebTextBuilder builder = new WebTextBuilder();

        builder.textNode(Text.as(content1.getChild(0)), 0);
        builder.textNode(Text.as(content2.getChild(0)), 0);

        WebText text = builder.build(0);

        String got = text.generateOutput(false);
        String want = "<div><p>Some text content 1.</p><p>Some text content 2.</p></div>";

        assertEquals(want, TestUtil.removeAllDirAttributes(got));
    }

    public void testGenerateOutputSingleContentNode() {
        mRoot.appendChild(mBody);
        Element container = Document.get().createDivElement();
        mBody.appendChild(container);

        Element content1 = Document.get().createPElement();
        content1.appendChild(Document.get().createTextNode("Some text content 1."));
        container.appendChild(content1);

        Element content2 = Document.get().createPElement();
        content2.appendChild(Document.get().createTextNode("Some non-content."));
        container.appendChild(content2);

        WebTextBuilder builder = new WebTextBuilder();

        builder.textNode(Text.as(content1.getChild(0)), 0);

        WebText text = builder.build(0);

        String got = text.generateOutput(false);
        String want = "<p>Some text content 1.</p>";

        assertEquals(want, TestUtil.removeAllDirAttributes(got));
    }

}
