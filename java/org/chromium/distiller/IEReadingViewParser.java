// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class recognizes and parses the IE Reading View markup tags according to
 * http://ie.microsoft.com/testdrive/browser/readingview, and returns the properties that matter to
 * distilled content - title, date, author, publisher, dominant image, inline images, caption,
 * copyright, and opt-out.  Some properties require the scanning and parsing of a lot of nodes, so
 * each property is scanned for and verified for legitimacy lazily, i.e. only upon request.
 */
public class IEReadingViewParser implements MarkupParser.Accessor {
    private Element mRoot = null;
    private NodeList<Element> mAllMeta = null;
    // The following data members are initialized to null to indicate that they haven't been
    // determined.  Once determined, they'll either have legitimate or empty values.
    private String mTitle = null;
    private String mDate = null;
    private String mAuthor = null;
    private String mPublisher = null;
    private String mCopyright = null;
    private boolean mOptOut = false;
    private boolean mDoneOptOut = false;  // Set to true if mOptOut has been determined.
    private List<MarkupParser.Image> mImages = null;

    /**
     * The object that will extract and verify IE Reading View markup tags from |root| upon request.
     */
    public IEReadingViewParser(Element root) {
        mRoot = root;
    }

    private void init() {
        if (mAllMeta == null) {
            mAllMeta = mRoot.getElementsByTagName("META");
        }
    }

    @Override
    public String getTitle() {
        if (mTitle == null) findTitle();
        return mTitle;
    }

    @Override
    public String getType() {
        return "";  // Not supported.
    }

    @Override
    public String getUrl() {
        return "";  // Not supported.
    }

    @Override
    public MarkupParser.Image[] getImages() {
        if (mImages == null) findImages();
        return mImages.toArray(new MarkupParser.Image[mImages.size()]);
    }

    @Override
    public String getDescription() {
        return "";  // Not supported.
    }

    @Override
    public String getPublisher() {
        // The primary indicator is the OpenGraph Protocol "site_name" meta tag, which is handled by
        // OpenGraphProtocolParser.  The secondary indicator is any html tag with the "publisher" or
        // "source_organization" attribute.
        if (mPublisher == null) findPublisher();
        return mPublisher;
    }

    @Override
    public String getCopyright() {
        if (mCopyright == null) findCopyright();
        return mCopyright;
    }

    @Override
    public String getAuthor() {
        if (mAuthor == null) findAuthor();
        return mAuthor;
    }

    @Override
    public MarkupParser.Article getArticle() {
        MarkupParser.Article article = new MarkupParser.Article();
        if (mDate == null) findDate();
        article.publishedTime = mDate;
        String author = getAuthor();
        article.authors = author.isEmpty() ? new String[0] : new String[] { author };
        return article;
    }

    @Override
    public boolean optOut() {
        if (!mDoneOptOut) findOptOut();
        return mOptOut;
    }

    private void findTitle() {
        init();
        mTitle = "";

        if (mAllMeta.getLength() == 0) return;

        // Make sure there's a <title> element.
        NodeList<Element> titles = mRoot.getElementsByTagName("TITLE");
        if (titles.getLength() == 0) return;

        // Extract title text from meta tag with "title" as name.
        for (int i = 0; i < mAllMeta.getLength(); i++) {
            MetaElement meta = MetaElement.as(mAllMeta.getItem(i));
            if (meta.getName().equalsIgnoreCase("title")) {
                mTitle = meta.getContent();
                break;
            }
        }
    }

    private void findDate() {
        init();
        mDate = "";

        // Get date from any element that includes the "dateline" class.
        Element elem = DomUtil.getFirstElementWithClassName(mRoot, "dateline");
        if (elem != null) {
            // Use javascript textContent (instead of javascript innerText) to include invisible
            // text.
            mDate = DomUtil.javascriptTextContent(elem);
        } else {  // Otherwise, get date from meta tag with "displaydate" as name.
            for (int i = 0; i < mAllMeta.getLength(); i++) {
                MetaElement meta = MetaElement.as(mAllMeta.getItem(i));
                if (meta.getName().equalsIgnoreCase("displaydate")) {
                    mDate = meta.getContent();
                    break;
                }
            }
        }
    }

    private void findAuthor() {
        mAuthor = "";

        // Get author from the first element that includes the "byline-name" class.
        // Note that we ignore the order of this element for now.
        Element elem = DomUtil.getFirstElementWithClassName(mRoot, "byline-name");
        // Use javascript textContent (instead of javascript innerText) to include invisible text.
        if (elem != null) mAuthor = DomUtil.javascriptTextContent(elem);
    }

    private void findPublisher() {
        mPublisher = "";

        // Look for "publisher" or "source_organization" attribute in any html tag.
        NodeList<Element> allElems = mRoot.getElementsByTagName("*");
        for (int i = 0; i < allElems.getLength() && mPublisher.isEmpty(); i++) {
            Element e = allElems.getItem(i);
            mPublisher = e.getAttribute("publisher");
            if (mPublisher.isEmpty()) mPublisher = e.getAttribute("source_organization");
        }
    }

    private void findCopyright() {
        init();
        mCopyright = "";

        // Get copyright from meta tag with "copyright" as name.
        for (int i = 0; i < mAllMeta.getLength(); i++) {
            MetaElement meta = MetaElement.as(mAllMeta.getItem(i));
            if (meta.getName().equalsIgnoreCase("copyright")) {
                mCopyright = meta.getContent();
                break;
            }
        }
    }

    private void findImages() {
        mImages = new ArrayList<MarkupParser.Image>();

        NodeList<Element> allImages = mRoot.getElementsByTagName("IMG");
        for (int i = 0; i < allImages.getLength(); i++) {
            ImageElement imgElem = ImageElement.as(allImages.getItem(i));

            // As long as the image has a caption, it's relevant regardless of size;
            // otherwise, it's relevant if its size is good.
            String caption = getCaption(imgElem);
            if ((caption != null && !caption.isEmpty()) || isImageRelevantBySize(imgElem)) {
                // Add relevant image to list.
                MarkupParser.Image image = new MarkupParser.Image();
                image.url = imgElem.getSrc();
                image.caption = caption;
                image.width = imgElem.getWidth();
                image.height = imgElem.getHeight();
                mImages.add(image);
            }
        }
    }

    private void findOptOut() {
        init();
        mDoneOptOut = true;

        // Get optout from meta tag with "IE_RM_OFF" as name.
        for (int i = 0; i < mAllMeta.getLength(); i++) {
            MetaElement meta = MetaElement.as(mAllMeta.getItem(i));
            if (meta.getName().equalsIgnoreCase("IE_RM_OFF")) {
                mOptOut = meta.getContent().equalsIgnoreCase("true");
                break;
            }
        }
    }

    private static boolean isImageRelevantBySize(ImageElement image) {
        // Relevant image size: width >= 400 and aspect ratio between 1.3 and 3.0 inclusively.
        int width = image.getWidth();
        if (width < 400) return false;
        double aspectRatio = (double) width / (double) image.getHeight();
        return aspectRatio >= 1.3 && aspectRatio <= 3.0;
    }

    private static String getCaption(ImageElement image) {
        // If |image| is a child of <figure>, then get the <figcaption> elements.
        Element parent = image.getParentElement();
        if (!parent.hasTagName("FIGURE")) return "";
        NodeList<Element> captions = parent.getElementsByTagName("FIGCAPTION");
        int numCaptions = captions.getLength();
        String caption = "";
        if (numCaptions > 0 && numCaptions <= 2) {
            // Use javascript innerText (instead of javascript textContent) to get only visible
            // captions.
            for (int i = 0; i < numCaptions && caption.isEmpty(); i++) {
                caption = DomUtil.getInnerText(captions.getItem(i));
            }
        }
        return caption;
    }
}
