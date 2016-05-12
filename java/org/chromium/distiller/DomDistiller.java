// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos;
import org.chromium.distiller.proto.DomDistillerProtos.DebugInfo;
import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;
import org.chromium.distiller.DomUtil;
import org.chromium.distiller.StringUtil;

import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.dom.client.Document;

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
        StringUtil.setWordCounter(
                DomUtil.javascriptTextContent(Document.get().getDocumentElement()));
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

        // iOS doesn't support reading window.location.href, so we use document.URL instead.
        String originalUrl =
                options.hasOriginalUrl() ? options.getOriginalUrl() : Document.get().getURL();
        TimingInfo timingInfo = contentExtractor.getTimingInfo();
        double stPaging = DomUtil.getTime();
        String paginationAlgo = options.hasPaginationAlgo() ? options.getPaginationAlgo() : "next";
        if (paginationAlgo.equals("pagenum")) {
            PageParamInfo paramInfo = PageParameterParser.parse(originalUrl, timingInfo);
            DomDistillerProtos.PaginationInfo info = DomDistillerProtos.PaginationInfo.create();
            String next = paramInfo.mNextPagingUrl;
            if (!next.isEmpty()) {
                info.setNextPage(next);
            }
            result.setPaginationInfo(info);
            if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) {
                LogUtil.logToConsole("paging by pagenum: " + paramInfo.toString());
            }
        } else {
            if (LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) {
                LogUtil.logToConsole("paging by next");
            }
            result.setPaginationInfo(PagingLinksFinder.getPaginationInfo(originalUrl));
        }
        LogUtil.addTimingInfo(stPaging, timingInfo, "Pagination");

        result.setMarkupInfo(contentExtractor.getMarkupParser().getMarkupInfo());
        timingInfo.setTotalTime(DomUtil.getTime() - startTime);
        result.setTimingInfo(timingInfo);
        result.setStatisticsInfo(contentExtractor.getStatisticsInfo());
        DebugInfo debugInfo = DebugInfo.create();
        debugInfo.setLog(LogUtil.getAndClearLog());
        result.setDebugInfo(debugInfo);
        return result;
    }
}
