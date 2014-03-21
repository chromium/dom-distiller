// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.EntryPoint;

import java.util.logging.Logger;

import org.timepedia.exporter.client.ExporterUtil;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DomDistiller implements EntryPoint {
  static Logger logger = Logger.getLogger("DomDistiller");

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      ExporterUtil.exportAll();
  }
}
