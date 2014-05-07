// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.Arrays;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;

import com.dom_distiller.test.proto.TestProtos;

/**
 * Tests of protoc generated gwt overlay types.
 */
public class GwtOverlayProtoTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testSimpleMessage() {
        TestProtos.SimpleMessage message = TestProtos.SimpleMessage.create();
        assertNotNull(message);
        assertEquals("{}", new JSONObject(message).toString());
    }

    public void testPrimitiveTypes() {
        TestProtos.PrimitiveTypes message = TestProtos.PrimitiveTypes.create();

        boolean boolVal = true;
        int int32Val = 123;
        float floatVal = 123.456f;
        double doubleVal = 456.123;
        String stringVal = "string";

        assertFalse(message.hasTypeBool());
        assertFalse(message.hasTypeInt32());
        assertFalse(message.hasTypeFloat());
        assertFalse(message.hasTypeDouble());
        assertFalse(message.hasTypeString());

        // First, just verify that getting an un-set field throws a javascript exception.
        try {
            message.getTypeBool();
            fail("Exception was not thrown.");
        } catch (JavaScriptException e) {
            // This exception is expected.
        }

        // Now, for each type. Set a value, check that the message has it and that it is correct and
        // then clear it and verify the clear.
        message.setTypeBool(boolVal);
        assertTrue(message.hasTypeBool());
        assertEquals(boolVal, message.getTypeBool());
        message.clearTypeBool();
        assertFalse(message.hasTypeBool());

        message.setTypeInt32(int32Val);
        assertTrue(message.hasTypeInt32());
        assertEquals(int32Val, message.getTypeInt32());
        message.clearTypeInt32();
        assertFalse(message.hasTypeInt32());

        message.setTypeFloat(floatVal);
        assertTrue(message.hasTypeFloat());
        assertEquals(floatVal, message.getTypeFloat());
        message.clearTypeFloat();
        assertFalse(message.hasTypeFloat());

        message.setTypeDouble(doubleVal);
        assertTrue(message.hasTypeDouble());
        assertEquals(doubleVal, message.getTypeDouble());
        message.clearTypeDouble();
        assertFalse(message.hasTypeDouble());

        message.setTypeString(stringVal);
        assertTrue(message.hasTypeString());
        assertEquals(stringVal, message.getTypeString());
        message.clearTypeString();
        assertFalse(message.hasTypeString());
    }

    public void testChangingPrimitiveField() {
        TestProtos.PrimitiveTypes message = TestProtos.PrimitiveTypes.create();

        for (Integer i : Arrays.asList(123, 0, -1000, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            message.setTypeInt32(i);
            assertEquals(i.intValue(), message.getTypeInt32());
        }
    }

    public void testEnum() {
        assertEquals(1, TestProtos.Enum.Values.ONE);
        assertEquals(2, TestProtos.Enum.Values.TWO);
        assertEquals(3, TestProtos.Enum.Values.THREE);

        TestProtos.Enum message = TestProtos.Enum.create();
        assertFalse(message.hasValue());
        message.setValue(TestProtos.Enum.Values.ONE);
        assertTrue(message.hasValue());
        assertEquals(TestProtos.Enum.Values.ONE, message.getValue());
    }

    public void testMessageField() {
        TestProtos.MessageField message = TestProtos.MessageField.create();
        TestProtos.SimpleMessage simpleMessage = TestProtos.SimpleMessage.create();

        assertFalse(message.hasSimpleMessage());
        message.setSimpleMessage(simpleMessage);
        assertTrue(message.hasSimpleMessage());

        // Check that simple_message and message.getSimpleMessage actually wrap the same javascript
        // object and that changes to one affect the other.
        assertFalse(message.getSimpleMessage().hasValue());
        simpleMessage.setValue(true);
        assertTrue(message.getSimpleMessage().hasValue());
        assertTrue(message.getSimpleMessage().getValue());

        message.getSimpleMessage().setValue(false);
        assertFalse(simpleMessage.getValue());
    }

    public void testRepeatedFields() {
        TestProtos.RepeatedTypes message = TestProtos.RepeatedTypes.create();
        assertEquals(0, message.getRepeatedInt32Count());
        assertEquals(0, message.getRepeatedInt32List().size());

        assertEquals(0, message.getRepeatedSimpleMessageCount());
        assertEquals(0, message.getRepeatedSimpleMessageList().size());

        try {
            message.getRepeatedInt32(1);
            fail("Exception was not thrown.");
        } catch (JavaScriptException e) {
            // This exception is expected.
        }

        message.addRepeatedInt32(1);
        message.addRepeatedInt32(-1);
        message.addRepeatedInt32(0);
        assertEquals(3, message.getRepeatedInt32Count());

        assertEquals(1, message.getRepeatedInt32(0));
        assertEquals(-1, message.getRepeatedInt32(1));
        assertEquals(0, message.getRepeatedInt32(2));

        message.setRepeatedInt32(1, 111);
        assertEquals(111, message.getRepeatedInt32(1));

        try {
            message.getRepeatedInt32(5);
            fail("Exception was not thrown.");
        } catch (JavaScriptException e) {
            // This exception is expected.
        }

        message.addRepeatedSimpleMessage();
        message.addRepeatedSimpleMessage().setValue(true);
        message.addRepeatedSimpleMessage().setValue(false);

        assertEquals(3, message.getRepeatedSimpleMessageCount());
        assertTrue(message.getRepeatedSimpleMessage(1).getValue());
        assertFalse(message.getRepeatedSimpleMessage(2).getValue());
    }

    public void testInnerMessage() {
        TestProtos.OuterMessage outerMessage = TestProtos.OuterMessage.create();
        TestProtos.OuterMessage.InnerMessage innerMessage =
                TestProtos.OuterMessage.InnerMessage.create();

        assertFalse(outerMessage.hasInnerMessage());
        outerMessage.setInnerMessage(innerMessage);
        assertTrue(outerMessage.hasInnerMessage());

        innerMessage.setValue(true);
        assertTrue(outerMessage.getInnerMessage().getValue());
    }

    public void testQualifiedMessageName() {
        TestProtos.SimpleMessage simpleMessage = TestProtos.SimpleMessage.create();
        TestProtos.QualifiedMessageField outerMessage = TestProtos.QualifiedMessageField.create();

        assertFalse(outerMessage.hasSimpleMessage());
        outerMessage.setSimpleMessage(simpleMessage);
        assertTrue(outerMessage.hasSimpleMessage());

        simpleMessage.setValue(true);
        assertTrue(outerMessage.getSimpleMessage().getValue());
    }
}
