// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xml.sax;

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

  public int getIndex(String key) {
      return attributesMap.get(key);
  }

  public String getValue(String key) {
      return getValue(getIndex(key));
  }

  public String getURI(int index) {
      return attributes.get(index).uri;
  }

  public String getLocalName(int index) {
      return attributes.get(index).localName;
  }

  public String getQName(int index) {
      return attributes.get(index).qName;
  }

  public String getType(int index) {
      return attributes.get(index).type;
  }

  public String getValue(int index) {
      return attributes.get(index).value;
  }

  public int getLength() {
      return attributes.size();
  }

  public void addAttribute(String uri, String localName, String qName, String type, String value) {
      attributesMap.put(qName, attributes.size());
      AttributeData data = new AttributeData(uri, localName, qName, type, value);
      attributes.add(data);
  }

  private static class AttributeData {
      String uri;
      String localName;
      String qName;
      String type;
      String value;

      AttributeData(String uri, String localName, String qName, String type, String value) {
          this.uri = uri;
          this.localName = localName;
          this.qName = qName;
          this.type = type;
          this.value = value;
      }
  }
}
