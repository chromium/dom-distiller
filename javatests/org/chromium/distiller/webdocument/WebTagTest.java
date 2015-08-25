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
        WebTag u = new WebTag("ul", WebTag.TagType.END);
        String startResult = ulStartWebTag.generateOutput(false);
        String endResult = u.generateOutput(false);
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

    public void testShouldGetInnerHTML() {
        assertTrue(WebTag.canBeNested("LI"));
        assertTrue(WebTag.canBeNested("UL"));
        assertTrue(WebTag.canBeNested("OL"));
        assertFalse(WebTag.canBeNested("SPAN"));
    }
}
