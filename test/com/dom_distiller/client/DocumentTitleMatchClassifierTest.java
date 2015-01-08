// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.filters.heuristics.DocumentTitleMatchClassifier;
import de.l3s.boilerpipe.labels.DefaultLabels;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DocumentTitleMatchClassifierTest extends DomDistillerTestCase {
    private static final String CONTENT_TEXT = "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
    private static final String TITLE_TEXT = "I am the document title";

    public void testLabelsTitle() throws BoilerpipeProcessingException {
        DocumentTitleMatchClassifier classifier =
                new DocumentTitleMatchClassifier(newList(TITLE_TEXT));

        TextBlock titleBlock = new TextBlock(TITLE_TEXT);
        TextBlock contentBlock = new TextBlock(CONTENT_TEXT);
        TextDocument document = new TextDocument(
                Arrays.asList(new TextBlock[]{titleBlock, contentBlock}));
        classifier.process(document);

        assertTrue(titleBlock.hasLabel(DefaultLabels.TITLE));
        assertFalse(contentBlock.hasLabel(DefaultLabels.TITLE));
    }

    // Mimics leading and trailing breadcrumbs containing the title.
    public void testLabelsMultipeTitle() throws BoilerpipeProcessingException {
        DocumentTitleMatchClassifier classifier =
                new DocumentTitleMatchClassifier(newList(TITLE_TEXT));

        TextBlock titleBlockAsLi = new TextBlock(TITLE_TEXT);
        titleBlockAsLi.addLabel(DefaultLabels.LI);
        TextBlock titleBlock = new TextBlock(TITLE_TEXT);
        TextBlock contentBlock = new TextBlock(CONTENT_TEXT);
        TextBlock trailingTitleBlockAsLi = new TextBlock(TITLE_TEXT);
        trailingTitleBlockAsLi.addLabel(DefaultLabels.LI);

        TextDocument document = new TextDocument(Arrays.asList(new TextBlock[]{
                titleBlockAsLi, titleBlock, contentBlock, trailingTitleBlockAsLi}));
        classifier.process(document);

        assertTrue(titleBlockAsLi.hasLabel(DefaultLabels.TITLE));
        assertTrue(titleBlock.hasLabel(DefaultLabels.TITLE));
        assertFalse(contentBlock.hasLabel(DefaultLabels.TITLE));
        assertTrue(trailingTitleBlockAsLi.hasLabel(DefaultLabels.TITLE));
    }

    public void testDoesNotLabelTitleInContent() throws BoilerpipeProcessingException {
        DocumentTitleMatchClassifier classifier =
                new DocumentTitleMatchClassifier(newList(TITLE_TEXT));

        TextBlock titleAndContentBlock = new TextBlock(TITLE_TEXT + " " + CONTENT_TEXT);

        TextDocument document = new TextDocument(
                Arrays.asList(new TextBlock[]{titleAndContentBlock}));
        classifier.process(document);

        assertFalse(titleAndContentBlock.hasLabel(DefaultLabels.TITLE));
    }

    // Non-exhaustive test for the type of partial-matches that Boilerpipe performs.
    public void testLabelsPartialTitleMatch() throws BoilerpipeProcessingException {
        DocumentTitleMatchClassifier classifier = new DocumentTitleMatchClassifier(
                newList("BreakingNews » " + TITLE_TEXT));

        TextBlock titleBlock = new TextBlock(TITLE_TEXT);
        TextBlock contentBlock = new TextBlock(CONTENT_TEXT);
        TextDocument document = new TextDocument(
                Arrays.asList(new TextBlock[]{titleBlock, contentBlock}));
        classifier.process(document);

        assertTrue(titleBlock.hasLabel(DefaultLabels.TITLE));
        assertFalse(contentBlock.hasLabel(DefaultLabels.TITLE));
    }

    public void testMatchesMultipleTitles() throws BoilerpipeProcessingException {
        String secondTitleText = "I am another document title";

        DocumentTitleMatchClassifier classifier = new DocumentTitleMatchClassifier(
                newList(TITLE_TEXT, secondTitleText));

        TextBlock titleBlock = new TextBlock(TITLE_TEXT);
        TextBlock secondTitleBlock = new TextBlock(secondTitleText);
        TextBlock contentBlock = new TextBlock(CONTENT_TEXT);

        TextDocument document = new TextDocument(
                Arrays.asList(new TextBlock[]{titleBlock, secondTitleBlock, contentBlock}));
        classifier.process(document);

        assertTrue(titleBlock.hasLabel(DefaultLabels.TITLE));
        assertTrue(secondTitleBlock.hasLabel(DefaultLabels.TITLE));
        assertFalse(contentBlock.hasLabel(DefaultLabels.TITLE));
    }

    public void testTitleWithExtraCharacters() throws Exception {
        String text = "title:?! :?!text";

        DocumentTitleMatchClassifier classifier =
                new DocumentTitleMatchClassifier(newList("title text"));

        TextBlock titleBlock = new TextBlock(text);
        TextBlock secondTitleBlock = new TextBlock(text);

        TextDocument document =
                new TextDocument(Arrays.asList(new TextBlock[] {titleBlock, secondTitleBlock}));
        classifier.process(document);

        assertTrue(titleBlock.hasLabel(DefaultLabels.TITLE));
        assertTrue(secondTitleBlock.hasLabel(DefaultLabels.TITLE));
    }

    private static List<String> newList(String... text) {
        List<String> result = new LinkedList<String>();
        for (String t: text) {
            result.add(t);
        }
        return result;
    }
}
