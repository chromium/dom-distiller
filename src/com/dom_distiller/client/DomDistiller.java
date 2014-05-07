// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;

import com.google.gwt.json.client.JSONObject;
import java.util.logging.Logger;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import com.dom_distiller.proto.DomDistillerProtos;

@Export()
public class DomDistiller implements Exportable {
  public static DomDistillerProtos.DomDistillerResult apply() {
      return applyWithOptions(DomDistillerProtos.DomDistillerOptions.create());
  }

  public static DomDistillerProtos.DomDistillerResult applyWithOptions(
          DomDistillerProtos.DomDistillerOptions options) {
      DomDistillerProtos.DomDistillerResult result = DomDistillerProtos.DomDistillerResult.create();
      result.setTitle(DocumentTitleGetter.getDocumentTitle(
              Document.get().getTitle(), Document.get().getDocumentElement()));

      DomDistillerProtos.DistilledContent content = DomDistillerProtos.DistilledContent.create();
      content.setHtml(ContentExtractor.extractContent());
      result.setDistilledContent(content);

      result.setPaginationInfo(PagingLinksFinder.getPaginationInfo());
      return result;
  }
}
