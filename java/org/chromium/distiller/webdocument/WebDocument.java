// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.document.TextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * The WebDocument is a simplified view of the underlying webpage. It contains the logical elements
 * (blocks of text, image + caption, video, etc).
 */
public class WebDocument {
    private final ArrayList<WebElement> elements;

    public WebDocument() {
        elements = new ArrayList<>();
    }

    public void addText(WebText text) {
        elements.add(text);
    }

    public void addTable(WebTable table) {
        elements.add(table);
    }

    public void addEmbed(WebElement embed) {
        elements.add(embed);
    }

    public List<WebElement> getElements() {
        return elements;
    }

    public List<WebImage> getContentImages() {
        List<WebImage> images = new ArrayList<>();
        for (WebElement e : elements) {
            if (e instanceof WebImage && e.getIsContent()) {
                images.add((WebImage) e);
            }
        }
        return images;
    }

    /**
     * This method generates a web document to be processed by boilerpipe. Text groups have been
     * introduced to help retain element order when adding images and embeds.
     * @return TextDocument object built from this web document.
     */
    public TextDocument createTextDocumentView() {
        ArrayList<TextBlock> textBlocks = new ArrayList<>();
        int i = getNextWebTextIndex(0);
        if (i == elements.size()) return new TextDocument(textBlocks);

        int curGroup = ((WebText)elements.get(i)).getGroupNumber();
        int prevGroup = curGroup;
        TextBlock curBlock = new TextBlock(elements, i);

        for (i++; i < elements.size(); i++) {
            if (!(elements.get(i) instanceof WebText)) continue;

            curGroup = ((WebText) elements.get(i)).getGroupNumber();
            if (curGroup == prevGroup) {
                curBlock.mergeNext(new TextBlock(elements, i));
            } else {
                textBlocks.add(curBlock);
                prevGroup = curGroup;
                curBlock = new TextBlock(elements, i);
            }
        }
        textBlocks.add(curBlock);
        return new TextDocument(textBlocks);
    }

    /**
     * Find the next index of a WebText in the WebElement list 'elements'.
     * @param startIndex The index to start from.
     * @return The next index or elements.size if none exists.
     */
    private int getNextWebTextIndex(int startIndex) {
        for (int i = startIndex; i < elements.size(); i++) {
            if (elements.get(i) instanceof WebText) {
                return i;
            }
        }
        return elements.size();
    }

    public String generateOutput(boolean textOnly) {
        StringBuilder output = new StringBuilder();
        for (WebElement e : elements) {
            if (!e.getIsContent()) continue;
            output.append(e.generateOutput(textOnly));
            if (textOnly) {
                // Put some space between paragraphs in text-only mode.
                output.append("\n");
            }
        }
        return output.toString();
    }
}
