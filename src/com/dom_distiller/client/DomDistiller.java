// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.proto.DomDistillerProtos;
import com.dom_distiller.proto.DomDistillerProtos.TimingInfo;
import com.google.gwt.dom.client.Document;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

@Export()
public class DomDistiller implements Exportable {
  /**
   * Debug level requested by the client for logging to include while distilling.
   */
  private static int sDebugLevel = 0;

  public static final int DEBUG_LEVEL_NONE = 0;
  public static final int DEBUG_LEVEL_BOILER_PIPE_PHASES = 1;
  public static final int DEBUG_LEVEL_VISIBILITY_INFO = 2;
  public static final int DEBUG_LEVEL_PAGING_INFO = 3;

  public static final boolean isLoggable(int level) {
      return sDebugLevel >= level;
  }

  public static DomDistillerProtos.DomDistillerResult apply() {
      return applyWithOptions(DomDistillerProtos.DomDistillerOptions.create());
  }

  public static DomDistillerProtos.DomDistillerResult applyWithOptions(
          DomDistillerProtos.DomDistillerOptions options) {
      double startTime = DomUtil.getTime();
      DomDistillerProtos.DomDistillerResult result = DomDistillerProtos.DomDistillerResult.create();
      ContentExtractor contentExtractor = new ContentExtractor(Document.get().getDocumentElement());
      result.setTitle(contentExtractor.extractTitle());

      sDebugLevel = options.hasDebugLevel() ? options.getDebugLevel() : 0;
      LogUtil.logToConsole("DomDistiller debug level: " + sDebugLevel);

      DomDistillerProtos.DistilledContent content = DomDistillerProtos.DistilledContent.create();
      boolean textOnly = options.hasExtractTextOnly() && options.getExtractTextOnly();
      content.setHtml(contentExtractor.extractContent(textOnly));
      result.setDistilledContent(content);
      result.setPaginationInfo(PagingLinksFinder.getPaginationInfo());
      result.setMarkupInfo(contentExtractor.getMarkupParser().getMarkupInfo());
      TimingInfo timingInfo = contentExtractor.getTimingInfo();
      timingInfo.setTotalTime(DomUtil.getTime() - startTime);
      result.setTimingInfo(timingInfo);
      return result;
  }
}
