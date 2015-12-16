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

    public void testGenerateOutput() {
        WebTag startWebTag = new WebTag("anytext", WebTag.TagType.START);
        WebTag endWebTag = new WebTag("anytext", WebTag.TagType.END);
        String startResult = startWebTag.generateOutput(false);
        String endResult = endWebTag.generateOutput(false);
        assertEquals(startResult, "<anytext>");
        assertEquals(endResult, "</anytext>");
    }

    public void testCanBeNested() {
        assertTrue(WebTag.canBeNested("LI"));
        assertTrue(WebTag.canBeNested("UL"));
        assertTrue(WebTag.canBeNested("OL"));
        assertTrue(WebTag.canBeNested("BLOCKQUOTE"));
        assertTrue(WebTag.canBeNested("PRE"));
        assertFalse(WebTag.canBeNested("SPAN"));
    }
}
