// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

import com.google.gwt.dom.client.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class instantiates SchemaOrgParser and implements MarkupParser.Accessor interface to provide
 * access to properties that SchemaOrgParser has parsed.
 */
public class SchemaOrgParserAccessor implements MarkupParser.Accessor {

    private SchemaOrgParser mParser;
    private final Element mRoot;
    private final TimingInfo mTimingInfo;

    /**
     * The object that instantiates SchemaOrgParser and implements its MarkupParser.Accessor
     * interface.
     */
    public SchemaOrgParserAccessor(Element root) {
        this(root, (TimingInfo) null);
    }

    public SchemaOrgParserAccessor(Element root, TimingInfo timingInfo) {
        mRoot = root;
        mTimingInfo = timingInfo;
    }

    private void init() {
        if (mParser == null) {
            mParser = new SchemaOrgParser(mRoot, mTimingInfo);
        }
    }

    @Override
    public String getTitle() {
        init();
        String title = "";
        List<SchemaOrgParser.ArticleItem> sortedArticles = sortArticlesByArea(mParser.getArticleItems());

        // Get the "headline" or "name" property of the first article that has it.
        for (int i = 0; i < sortedArticles.size() && title.isEmpty(); i++) {
            SchemaOrgParser.ArticleItem item = sortedArticles.get(i);
            title = item.getStringProperty(SchemaOrgParser.HEADLINE_PROP);
            if (title.isEmpty()) {
                title = item.getStringProperty(SchemaOrgParser.NAME_PROP);
            }
        }

        return title;
    }

    @Override
    public String getType() {
        init();
        // Returns Article if there's an article.
        return mParser.getArticleItems().isEmpty() ? "" : MarkupParser.ARTICLE_TYPE;
    }

    @Override
    public String getUrl() {
        init();
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        return articles.isEmpty() ? "" :
                articles.get(0).getStringProperty(SchemaOrgParser.URL_PROP);
    }

    @Override
    public MarkupParser.Image[] getImages() {
        init();
        List<MarkupParser.Image> images = new ArrayList<MarkupParser.Image>();
        // Images are ordered as follows:
        // 1) the "associatedMedia" or "encoding" image of the article that first declares it,
        // 2) or the first ImageObject with "representativeOfPage" as "true",
        // 3) then, the list of "image" property of remaining articles,
        // 4) lastly, the list of ImageObject's.

        // First, get images from ArticleItem's.
        List<SchemaOrgParser.ArticleItem> articleItems = mParser.getArticleItems();
        SchemaOrgParser.ImageItem associatedImageOfArticle = null;

        for (int i = 0; i < articleItems.size(); i++) {
            SchemaOrgParser.ArticleItem articleItem = articleItems.get(i);
            // If this is the first article with an associated image, remember it for now; it'll be
            // added to the list later when its position in the list can be determined.
            if (associatedImageOfArticle == null) {
                associatedImageOfArticle = articleItem.getRepresentativeImageItem();
                if (associatedImageOfArticle != null) continue;
            }
            MarkupParser.Image image = articleItem.getImage();
            if (image != null) images.add(image);
        }

        // Then, get images from ImageItem's.
        List<SchemaOrgParser.ImageItem> imageItems = mParser.getImageItems();
        boolean hasRepresentativeImage = false;

        for (int i = 0; i < imageItems.size(); i++) {
            SchemaOrgParser.ImageItem imageItem = imageItems.get(i);
            MarkupParser.Image image = imageItem.getImage();
            // Insert |image| at beginning of list if it's the associated image of an article, or
            // it's the first image that's representative of page.
            if (imageItem == associatedImageOfArticle ||
                (!hasRepresentativeImage && imageItem.isRepresentativeOfPage())) {
                hasRepresentativeImage = true;
                images.add(0, image);
            } else {
                images.add(image);
            }
        }

        return images.toArray(new MarkupParser.Image[images.size()]);
    }

    @Override
    public String getDescription() {
        init();
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        return articles.isEmpty() ?  "" :
                articles.get(0).getStringProperty(SchemaOrgParser.DESCRIPTION_PROP);
    }

    @Override
    public String getPublisher() {
        init();
        // Returns either the "publisher" or "copyrightHolder" property of the first article.
        String publisher = "";
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        if (!articles.isEmpty()) {
            SchemaOrgParser.ArticleItem article = articles.get(0);
            publisher = article.getPersonOrOrganizationName(SchemaOrgParser.PUBLISHER_PROP);
            if (publisher.isEmpty()) {
                publisher = article.getPersonOrOrganizationName(
                        SchemaOrgParser.COPYRIGHT_HOLDER_PROP);
            }
        }
        return publisher;
    }

    @Override
    public String getCopyright() {
        init();
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        return articles.isEmpty() ? "" : articles.get(0).getCopyright();
    }

    @Override
    public String getAuthor() {
        init();
        String author = "";
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        if (!articles.isEmpty()) {
             SchemaOrgParser.ArticleItem article = articles.get(0);
             author = article.getPersonOrOrganizationName(SchemaOrgParser.AUTHOR_PROP);
             // If there's no "author" property, use "creator" property.
             if (author.isEmpty()) {
                 author = article.getPersonOrOrganizationName(SchemaOrgParser.CREATOR_PROP);
             }
        }
        // Otherwise, use "rel=author" tag.
        return author.isEmpty() ? mParser.getAuthorFromRel() : author;
    }

    @Override
    public MarkupParser.Article getArticle() {
        init();
        List<SchemaOrgParser.ArticleItem> articles = mParser.getArticleItems();
        return articles.isEmpty() ? null : articles.get(0).getArticle();
    }

    @Override
    public boolean optOut() {
        return false;
    }

    /**
     * Sort a list of {@link SchemaOrgParser.ArticleItem}s by their area
     * in descending order.
     * @param articles List of {@link SchemaOrgParser.ArticleItem}s to
     * be sorted.
     * @return The sorted list.
     */
    private List<SchemaOrgParser.ArticleItem> sortArticlesByArea(
            List<SchemaOrgParser.ArticleItem> articles) {
        List<SchemaOrgParser.ArticleItem> sortedArticles =
                new ArrayList<>(articles);
        Collections.sort(sortedArticles,
                new Comparator<SchemaOrgParser.ArticleItem>() {
                    @Override
                    public int compare(SchemaOrgParser.ArticleItem i1,
                                       SchemaOrgParser.ArticleItem i2) {
                        int area1 = DomUtil.getArea(i1.getElement());
                        int area2 = DomUtil.getArea(i2.getElement());
                        if (area1 > area2) {
                            return -1;
                        } else if (area1 < area2) {
                            return 1;
                        }
                        return 0;
                    }
                });
        return sortedArticles;
    }
}
