package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

public class WebTagTest extends DomDistillerJsTestCase {

    public void testOLGenerateOutput() {
        WebTag olStartWebTag = new WebTag("ol", WebTag.TagType.START);
        WebTag olEndWebTag = new WebTag("ol", WebTag.TagType.END);
        String startResult = olStartWebTag.generateOutput(false);
        String endResult = olEndWebTag.generateOutput(false);
        assertEquals(startResult, "<ol>");
        assertEquals(endResult, "</ol>");
    }

    public void testULGenerateOutput() {
        WebTag ulStartWebTag = new WebTag("ul", WebTag.TagType.START);
        WebTag ulEndWebTag = new WebTag("ul", WebTag.TagType.END);
        String startResult = ulStartWebTag.generateOutput(false);
        String endResult = ulEndWebTag.generateOutput(false);
        assertEquals(startResult, "<ul>");
        assertEquals(endResult, "</ul>");
    }

    public void testLIGenerateOutput() {
        WebTag liStartWebTag = new WebTag("li", WebTag.TagType.START);
        WebTag liEndWebTag = new WebTag("li", WebTag.TagType.END);
        String startResult = liStartWebTag.generateOutput(false);
        String endResult = liEndWebTag.generateOutput(false);
        assertEquals(startResult, "<li>");
        assertEquals(endResult, "</li>");
    }

    public void testBlockquoteGenerateOutput() {
        WebTag blockquoteStartWebTag = new WebTag("blockquote",
                WebTag.TagType.START);
        WebTag blockquoteEndWebTag = new WebTag("blockquote",
                WebTag.TagType.END);
        String startResult = blockquoteStartWebTag.generateOutput(false);
        String endResult = blockquoteEndWebTag.generateOutput(false);
        assertEquals(startResult, "<blockquote>");
        assertEquals(endResult, "</blockquote>");
    }

    public void testCanBeNested() {
        assertTrue(WebTag.canBeNested("LI"));
        assertTrue(WebTag.canBeNested("UL"));
        assertTrue(WebTag.canBeNested("OL"));
        assertTrue(WebTag.canBeNested("BLOCKQUOTE"));
        assertFalse(WebTag.canBeNested("SPAN"));
    }
}
