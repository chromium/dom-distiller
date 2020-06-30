// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class WebVideoTest extends DomDistillerJsTestCase {
    public void testGenerateOutput() {
        Element video = Document.get().createVideoElement();
        // This should be stripped.
        video.setAttribute("onfocus", "new XMLHttpRequest();");

        Element child = Document.get().createElement("source");
        child.setAttribute("src", "http://example.com/foo.ogg");
        video.appendChild(child);

        child = Document.get().createElement("track");
        child.setAttribute("src", "http://example.com/foo.vtt");
        // This should be stripped.
        child.setAttribute("onclick", "alert(1)");
        video.appendChild(child);

        String want = "<video>" +
                          "<source src=\"http://example.com/foo.ogg\">" +
                          "<track src=\"http://example.com/foo.vtt\">" +
                      "</video>";
        WebVideo webVideo = new WebVideo(video, 400, 300);

        String got = webVideo.generateOutput(false);

        // Output should be the same as the input in this case.
        assertEquals(want, got);
    }

    public void testGenerateOutputInvalidChildren() {
        Element video = Document.get().createVideoElement();
        Element child = Document.get().createElement("source");
        child.setAttribute("src", "http://example.com/foo.ogg");
        video.appendChild(child);

        child = Document.get().createElement("track");
        child.setAttribute("src", "http://example.com/foo.vtt");
        video.appendChild(child);

        child = Document.get().createDivElement();
        child.setInnerText("We do not use custom error messages!");
        video.appendChild(child);

        String want = "<video>" +
                          "<source src=\"http://example.com/foo.ogg\">" +
                          "<track src=\"http://example.com/foo.vtt\">" +
                      "</video>";
        WebVideo webVideo = new WebVideo(video, 400, 300);

        String got = webVideo.generateOutput(false);

        // Output should ignore anything other than "track" and "source" tags.
        assertEquals(want, got);
    }

    public void testPosterEmpty() {
        Element video = Document.get().createVideoElement();

        String want = "<video></video>";
        WebVideo webVideo = new WebVideo(video, 400, 300);


        String got = webVideo.generateOutput(false);

        assertEquals(want, got);
    }

}
