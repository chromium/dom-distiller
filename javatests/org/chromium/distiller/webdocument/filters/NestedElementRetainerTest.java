package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.webdocument.TestWebDocumentBuilder;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebTag;

public class NestedElementRetainerTest extends DomDistillerJsTestCase {
    public void testOrderedListStructure() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebTag olStart = builder.addTagStart();
        WebTag liStart = builder.addTagStart();
        builder.addText("text 1").setIsContent(false);
        WebTag liEnd = builder.addTagEnd();
        WebTag liStart2 = builder.addTagStart();
        builder.addText("text 2").setIsContent(false);
        WebTag liEnd2 = builder.addTagEnd();
        WebTag liStart3 = builder.addTagStart();
        builder.addText("text 3").setIsContent(true);
        WebTag liEnd3 = builder.addTagEnd();
        WebTag olEnd = builder.addTagEnd();
        WebDocument document = builder.build();
        NestedElementRetainer.process(document);
        assertTrue(olStart.getIsContent());
        assertFalse(liStart.getIsContent());
        assertFalse(liEnd.getIsContent());
        assertFalse(liStart2.getIsContent());
        assertFalse(liEnd2.getIsContent());
        assertTrue(liStart3.getIsContent());
        assertTrue(liEnd3.getIsContent());
        assertTrue(olEnd.getIsContent());
    }

    public void testUnorderedListStructure() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebTag ulStart = builder.addTagStart();
        WebTag liStart = builder.addTagStart();
        builder.addText("text 1").setIsContent(true);
        WebTag ulStart2 = builder.addTagStart();
        WebTag liStart2 = builder.addTagStart();
        builder.addText("text 2").setIsContent(false);
        WebTag liEnd2 = builder.addTagEnd();
        WebTag ulEnd2 = builder.addTagEnd();
        WebTag liEnd = builder.addTagEnd();
        WebTag ulEnd = builder.addTagEnd();
        WebDocument document = builder.build();

        NestedElementRetainer.process(document);
        assertTrue(ulStart.getIsContent());
        assertTrue(liStart.getIsContent());
        assertFalse(ulStart2.getIsContent());
        assertFalse(liStart2.getIsContent());
        assertFalse(liEnd2.getIsContent());
        assertFalse(ulEnd2.getIsContent());
        assertTrue(liEnd.getIsContent());
        assertTrue(ulEnd.getIsContent());

    }

    public void testContentFromListStrcture() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebTag olStartLevel1 = builder.addTagStart();
        WebTag olStartLevel2 = builder.addTagStart();
        WebTag liStart1 = builder.addTagStart();
        builder.addText("text 1").setIsContent(false);
        WebTag liEnd1 = builder.addTagEnd();
        WebTag olStartLevel3 = builder.addTagStart();
        WebTag liStart2 = builder.addTagStart();
        builder.addText("text 2").setIsContent(true);
        WebTag liEnd2 = builder.addTagEnd();
        WebTag olStartLevel4 = builder.addTagStart();
        WebTag liStart3 = builder.addTagStart();
        builder.addText("text 3").setIsContent(false);
        WebTag liEnd3 = builder.addTagEnd();
        WebTag liStart4 = builder.addTagStart();
        builder.addText("text 4").setIsContent(false);
        WebTag liEnd4 = builder.addTagEnd();
        WebTag liStart5 = builder.addTagStart();
        builder.addText("text 5").setIsContent(false);
        WebTag liEnd5 = builder.addTagEnd();
        WebTag liStart6 = builder.addTagStart();
        builder.addText("text 6").setIsContent(false);
        WebTag liEnd6 = builder.addTagEnd();
        WebTag olEndLevel4 = builder.addTagEnd();
        WebTag olEndLevel3 = builder.addTagEnd();
        WebTag liStart7 = builder.addTagStart();
        builder.addText("text 7").setIsContent(true);
        WebTag liEnd7 = builder.addTagEnd();
        WebTag olEndLevel2 = builder.addTagEnd();
        WebTag olEndLevel1 = builder.addTagEnd();
        WebDocument document = builder.build();

        NestedElementRetainer.process(document);
        assertTrue(olStartLevel1.getIsContent());
        assertTrue(olStartLevel2.getIsContent());
        assertFalse(liStart1.getIsContent());
        assertFalse(liEnd1.getIsContent());
        assertTrue(olStartLevel3.getIsContent());
        assertTrue(liStart2.getIsContent());
        assertTrue(liEnd2.getIsContent());
        assertFalse(olStartLevel4.getIsContent());
        assertFalse(liStart3.getIsContent());
        assertFalse(liEnd3.getIsContent());
        assertFalse(liStart4.getIsContent());
        assertFalse(liEnd4.getIsContent());
        assertFalse(liStart5.getIsContent());
        assertFalse(liEnd5.getIsContent());
        assertFalse(liStart6.getIsContent());
        assertFalse(liEnd6.getIsContent());
        assertFalse(olEndLevel4.getIsContent());
        assertTrue(olEndLevel3.getIsContent());
        assertTrue(liStart7.getIsContent());
        assertTrue(liEnd7.getIsContent());
        assertTrue(olEndLevel2.getIsContent());
        assertTrue(olEndLevel1.getIsContent());
    }

    public void testNoContentFromListStructure() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebTag olStartLevel1 = builder.addTagStart();
        WebTag olStartLevel2 = builder.addTagStart();
        WebTag liStart1 = builder.addTagStart();
        builder.addText("text 1").setIsContent(false);
        WebTag liEnd1 = builder.addTagEnd();
        WebTag olStartLevel4 = builder.addTagStart();
        WebTag liStart3 = builder.addTagStart();
        builder.addText("text 3").setIsContent(false);
        WebTag liEnd3 = builder.addTagEnd();
        WebTag liStart4 = builder.addTagStart();
        builder.addText("text 4").setIsContent(false);
        WebTag liEnd4 = builder.addTagEnd();
        WebTag liStart5 = builder.addTagStart();
        builder.addText("text 5").setIsContent(false);
        WebTag liEnd5 = builder.addTagEnd();
        WebTag liStart6 = builder.addTagStart();
        builder.addText("text 6").setIsContent(false);
        WebTag liEnd6 = builder.addTagEnd();
        WebTag olEndLevel4 = builder.addTagEnd();
        WebTag olEndLevel2 = builder.addTagEnd();
        WebTag olEndLevel1 = builder.addTagEnd();
        WebDocument document = builder.build();

        NestedElementRetainer.process(document);
        assertFalse(olStartLevel1.getIsContent());
        assertFalse(olStartLevel2.getIsContent());
        assertFalse(liStart1.getIsContent());
        assertFalse(liEnd1.getIsContent());
        assertFalse(olStartLevel4.getIsContent());
        assertFalse(liStart3.getIsContent());
        assertFalse(liEnd3.getIsContent());
        assertFalse(liStart4.getIsContent());
        assertFalse(liEnd4.getIsContent());
        assertFalse(liStart5.getIsContent());
        assertFalse(liEnd5.getIsContent());
        assertFalse(liStart6.getIsContent());
        assertFalse(liEnd6.getIsContent());
        assertFalse(olEndLevel4.getIsContent());
        assertFalse(olEndLevel2.getIsContent());
        assertFalse(olEndLevel1.getIsContent());
    }

    public void testNestedListStructure() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebTag ulStart = builder.addTagStart();
        WebTag liStart = builder.addTagStart();
        builder.addText("text 1").setIsContent(true);
        WebTag liEnd = builder.addTagEnd();
        WebTag olStart = builder.addTagStart();
        WebTag liOLStart = builder.addTagStart();
        builder.addText("text 2").setIsContent(true);
        WebTag liOLEnd = builder.addTagEnd();
        WebTag olEnd = builder.addTagEnd();
        WebTag ulEnd = builder.addTagEnd();
        WebDocument document = builder.build();
        NestedElementRetainer.process(document);
        assertTrue(ulStart.getIsContent());
        assertTrue(liStart.getIsContent());
        assertTrue(olStart.getIsContent());
        assertTrue(liOLStart.getIsContent());
        assertTrue(liOLEnd.getIsContent());
        assertTrue(olEnd.getIsContent());
        assertTrue(liEnd.getIsContent());
        assertTrue(ulEnd.getIsContent());
    }
}
