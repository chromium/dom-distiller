// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.proto.DomDistillerProtos;
import com.dom_distiller.proto.DomDistillerProtos.DebugInfo;
import com.dom_distiller.proto.DomDistillerProtos.TimingInfo;
import com.google.gwt.dom.client.Document;
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsNamespace;

@JsExport("DomDistiller")
public class DomDistiller {
    @JsExport
    public static DomDistillerProtos.DomDistillerResult apply() {
        return applyWithOptions(DomDistillerProtos.DomDistillerOptions.create());
    }

    @JsExport
    public static DomDistillerProtos.DomDistillerResult applyWithOptions(
            DomDistillerProtos.DomDistillerOptions options) {
        double startTime = DomUtil.getTime();
        DomDistillerProtos.DomDistillerResult result =
                DomDistillerProtos.DomDistillerResult.create();
        ContentExtractor contentExtractor =
                new ContentExtractor(Document.get().getDocumentElement());
        result.setTitle(contentExtractor.extractTitle());

        LogUtil.setDebugLevel(
                options.hasDebugLevel() ? options.getDebugLevel() : LogUtil.DEBUG_LEVEL_NONE);
        LogUtil.logToConsole("DomDistiller debug level: " + LogUtil.getDebugLevel());

        DomDistillerProtos.DistilledContent content = DomDistillerProtos.DistilledContent.create();
        boolean textOnly = options.hasExtractTextOnly() && options.getExtractTextOnly();
        content.setHtml(contentExtractor.extractContent(textOnly));
        result.setDistilledContent(content);
        result.setTextDirection(contentExtractor.getTextDirection());

        for (String url : contentExtractor.getImageUrls()) {
            result.addContentImages().setUrl(url);
        }

        String original_domain = options.hasOriginalDomain() ? options.getOriginalDomain() : "";
        result.setPaginationInfo(PagingLinksFinder.getPaginationInfo(original_domain));
        result.setMarkupInfo(contentExtractor.getMarkupParser().getMarkupInfo());
        TimingInfo timingInfo = contentExtractor.getTimingInfo();
        timingInfo.setTotalTime(DomUtil.getTime() - startTime);
        result.setTimingInfo(timingInfo);
        result.setStatisticsInfo(contentExtractor.getStatisticsInfo());
        DebugInfo debugInfo = DebugInfo.create();
        debugInfo.setLog(LogUtil.getAndClearLog());
        result.setDebugInfo(debugInfo);
        return result;
    }
}
