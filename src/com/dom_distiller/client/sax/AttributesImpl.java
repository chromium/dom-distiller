// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesImpl implements Attributes {
    private List<AttributeData> attributes;
    private Map<String, Integer> attributesMap;

    public AttributesImpl() {
        attributes = new ArrayList<AttributeData>();
        attributesMap = new HashMap<String, Integer>();
    }

    @Override
    public String getValue(String key) {
        return getValue(getIndex(key));
    }

    @Override
    public int getIndex(String key) {
        return attributesMap.get(key);
    }

    @Override
    public String getName(int index) {
        return attributes.get(index).name;
    }

    @Override
    public String getValue(int index) {
        return attributes.get(index).value;
    }

    @Override
    public int getLength() {
        return attributes.size();
    }

    public void addAttribute(String name, String value) {
        attributesMap.put(name, attributes.size());
        AttributeData data = new AttributeData(name, value);
        attributes.add(data);
    }

    private static class AttributeData {
        private final String name;
        private final String value;

        private AttributeData(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
