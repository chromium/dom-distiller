// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.TestUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Text;

public class WebTextTest extends DomDistillerJsTestCase {
    public void testGenerateOutputMultipleContentNodes() {
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

    public void testGenerateOutputBRElements() {
        Element container = Document.get().createDivElement();
        mBody.appendChild(container);

        Element content1 = Document.get().createPElement();
        content1.appendChild(Document.get().createTextNode("Words"));
        content1.appendChild(Document.get().createBRElement());
        content1.appendChild(Document.get().createTextNode("split"));
        content1.appendChild(Document.get().createBRElement());
        content1.appendChild(Document.get().createTextNode("with"));
        content1.appendChild(Document.get().createBRElement());
        content1.appendChild(Document.get().createTextNode("lines"));
        container.appendChild(content1);

        WebTextBuilder builder = new WebTextBuilder();
        builder.textNode(Text.as(content1.getChild(0)), 0);
        builder.lineBreak(content1.getChild(1));
        builder.textNode(Text.as(content1.getChild(2)), 0);
        builder.lineBreak(content1.getChild(3));
        builder.textNode(Text.as(content1.getChild(4)), 0);
        builder.lineBreak(content1.getChild(5));
        builder.textNode(Text.as(content1.getChild(6)), 0);

        WebText text = builder.build(0);
        String got = text.generateOutput(false);
        String want = "<p>Words<br>split<br>with<br>lines</p>";
        assertEquals(want, TestUtil.removeAllDirAttributes(got));

        got = text.generateOutput(true);
        want = "Words\nsplit\nwith\nlines";
        assertEquals(want, got);
    }

    public void testStripUnsafeAttributes() {
        Element container = Document.get().createDivElement();
        mBody.appendChild(container);

        Element content1 = Document.get().createPElement();

        // This should be passed through.
        content1.setAttribute("allowfullscreen", "true");

        // This should be stripped.
        content1.setAttribute("onclick", "alert(1)");

        content1.appendChild(Document.get().createTextNode("Text"));
        container.appendChild(content1);

        WebTextBuilder builder = new WebTextBuilder();
        builder.textNode(Text.as(content1.getChild(0)), 0);

        WebText text = builder.build(0);
        String got = text.generateOutput(false);
        String want = "<p allowfullscreen=\"true\">Text</p>";
        assertEquals(want, TestUtil.removeAllDirAttributes(got));
    }

    public void testGenerateOutputLIElements() {
        Element container = Document.get().createLIElement();
        mBody.appendChild(container);

        container.appendChild(TestUtil.createText("Some text content 1."));

        WebTextBuilder builder = new WebTextBuilder();
        builder.textNode(Text.as(container.getChild(0)), 0);

        WebText text = builder.build(0);
        String got = text.generateOutput(false);
        String want = "Some text content 1.";
        assertEquals(want, TestUtil.removeAllDirAttributes(got));
    }
}
