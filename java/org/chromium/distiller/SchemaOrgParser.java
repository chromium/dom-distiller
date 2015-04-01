// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class recognizes and parses schema.org markup tags, and returns the properties that matter
 * to distilled content.
 * Schema.org markup (http://schema.org) is based on the microdata format
 * (http://www.whatwg.org/specs/web-apps/current-work/multipage/microdata.html).
 * For the basic Schema.org Thing type, the basic properties are: name, url, description, image.
 * In addition, for each type that we support, we also parse more specific properties:
 * - Article: headline (i.e. title), publisher, copyright year, copyright holder, date published,
 *            date modified, author, article section
 * - ImageObject: headline (i.e. title), publisher, copyright year, copyright holder, content url,
 *                encoding format, caption, representative of page, width, height
 * - Person: family name, given name
 * - Organization: legal name.
 * The value of a Schema.Org property can be a Schema.Org type, i.e. embedded.  E.g., the author or
 * publisher of article or publisher of image could be a Schema.Org Person or Organization type;
 * in fact, this is the reason we support Person and Organization types.
 */
public class SchemaOrgParser {
    static final String NAME_PROP = "name";
    static final String URL_PROP = "url";
    static final String DESCRIPTION_PROP = "description";
    static final String IMAGE_PROP = "image";
    static final String HEADLINE_PROP = "headline";
    static final String PUBLISHER_PROP = "publisher";
    static final String COPYRIGHT_HOLDER_PROP = "copyrightHolder";
    static final String COPYRIGHT_YEAR_PROP = "copyrightYear";
    static final String CONTENT_URL_PROP = "contentUrl";
    static final String ENCODING_FORMAT_PROP = "encodingFormat";
    static final String CAPTION_PROP = "caption";
    static final String REPRESENTATIVE_PROP = "representativeOfPage";
    static final String WIDTH_PROP = "width";
    static final String HEIGHT_PROP = "height";
    static final String DATE_PUBLISHED_PROP = "datePublished";
    static final String DATE_MODIFIED_PROP = "dateModified";
    static final String AUTHOR_PROP = "author";
    static final String CREATOR_PROP = "creator";
    static final String SECTION_PROP = "articleSection";
    static final String ASSOCIATED_MEDIA_PROP = "associatedMedia";
    static final String ENCODING_PROP = "encoding";
    static final String FAMILY_NAME_PROP = "familyName";
    static final String GIVEN_NAME_PROP = "givenName";
    static final String LEGAL_NAME_PROP = "legalName";
    static final String AUTHOR_REL = "author";

    enum Type {  // All these types are extended from Thing, directly or indirectly.
        IMAGE,
        ARTICLE,
        PERSON,
        ORGANIZATION,
        UNSUPPORTED,
    }

    static class ThingItem {
        private final Type mType;
        private final Map<String, String> mStringProperties;
        private final Map<String, ThingItem> mItemProperties;

        ThingItem(Type type) {
            mType = type;
            mStringProperties = new HashMap<String, String>();
            mItemProperties = new HashMap<String, ThingItem>();

           addStringPropertyName(NAME_PROP);
           addStringPropertyName(URL_PROP);
           addStringPropertyName(DESCRIPTION_PROP);
           addStringPropertyName(IMAGE_PROP);
        }

        final void addStringPropertyName(String name) {
            mStringProperties.put(name, "");
        }

        final void addItemPropertyName(String name) {
            mItemProperties.put(name, null);
        }

        final String getStringProperty(String name) {
            return !mStringProperties.containsKey(name) ? "" : mStringProperties.get(name);
        }

        final ThingItem getItemProperty(String name) {
            return !mItemProperties.containsKey(name) ? null : mItemProperties.get(name);
        }

        final Type getType() { return mType; }

        final boolean isSupported() { return mType != Type.UNSUPPORTED; }

        // Store |value| for property with |name|, unless the property already has a non-empty
        // value, in which case |value| will be ignored.  This means we only keep the first value.
        final void putStringValue(String name, String value) {
            if (mStringProperties.containsKey(name) && mStringProperties.get(name).isEmpty()) {
                mStringProperties.put(name, value);
            }
        }

        // Store |value| for property with |name|, unless the property already has a non-null value,
        // in which case, |value| will be ignored.  This means we only keep the first value.
        final void putItemValue(String name, ThingItem value) {
            if (mItemProperties.containsKey(name)) mItemProperties.put(name, value);
        }
    }

    private final List<ThingItem> mItemScopes = new ArrayList<ThingItem>();
    private final Map<Element, ThingItem> mItemElement = new HashMap<Element, ThingItem>();
    private String mAuthorFromRel = "";
    private static final Map<String, Type> sTypeUrls;

    static {
        sTypeUrls = new HashMap<String, Type>();
        sTypeUrls.put("http://schema.org/ImageObject", Type.IMAGE);
        sTypeUrls.put("http://schema.org/Article", Type.ARTICLE);
        sTypeUrls.put("http://schema.org/BlogPosting", Type.ARTICLE);
        sTypeUrls.put("http://schema.org/NewsArticle", Type.ARTICLE);
        sTypeUrls.put("http://schema.org/ScholarlyArticle", Type.ARTICLE);
        sTypeUrls.put("http://schema.org/TechArticle", Type.ARTICLE);
        sTypeUrls.put("http://schema.org/Person", Type.PERSON);
        sTypeUrls.put("http://schema.org/Organization", Type.ORGANIZATION);
        sTypeUrls.put("http://schema.org/Corporation", Type.ORGANIZATION);
        sTypeUrls.put("http://schema.org/EducationalOrganization", Type.ORGANIZATION);
        sTypeUrls.put("http://schema.org/GovernmentOrganization", Type.ORGANIZATION);
        sTypeUrls.put("http://schema.org/NGO", Type.ORGANIZATION);
    }

    private final TimingInfo mTimingInfo;

    /**
     * The object that extracts and verifies Schema.org markup tags from |root|.
     */
    public SchemaOrgParser(Element root, TimingInfo timingInfo) {
        // TODO(kuan): Parsing all tags is pretty expensive, should we do so only lazily?
        // If parse lazily, all get* methods will need to check for parsed state and, if necessary,
        // parse before returning the requested properties.
        // Note that the <html> element can also be the start of a Schema.org item, and hence needs
        // to be parsed.
        mTimingInfo = timingInfo;
        double startTime = DomUtil.getTime();
        parse(root);
        LogUtil.addTimingInfo(startTime, mTimingInfo, "SchemaOrgParser.parse");
    }

    final List<ArticleItem> getArticleItems() {
        List<ArticleItem> articles = new ArrayList<ArticleItem>();
        for (int i = 0; i < mItemScopes.size(); i++) {
            ThingItem item = mItemScopes.get(i);
            if (item.mType == Type.ARTICLE) articles.add((ArticleItem) item);
        }
        return articles;
    }

    final List<ImageItem> getImageItems() {
        List<ImageItem> images = new ArrayList<ImageItem>();
        for (int i = 0; i < mItemScopes.size(); i++) {
            ThingItem item = mItemScopes.get(i);
            if (item.mType == Type.IMAGE) images.add((ImageItem) item);
        }
        return images;
    }

    final String getAuthorFromRel() { return mAuthorFromRel; }

    private void parse(Element root) {
        NodeList<Element> allProp = DomUtil.querySelectorAll(root, "[ITEMPROP],[ITEMSCOPE]");

        // Root node (html) is not included in the result of querySelectorAll, so need to
        // handle it explicitly here.
        parseElement(root, null);

        for (int i = 0; i < allProp.getLength(); i++) {
            Element e = allProp.getItem(i);
            parseElement(e, getItemScopeParent(e));
        }

        // As per http://schema.org/author (or http://schema.org/Article and search for "author"
        // property), if <a> or <link> tags specify rel="author", extract it.
        allProp = DomUtil.querySelectorAll(root, "A[rel=author],LINK[rel=author]");
        for (int i = 0; i < allProp.getLength(); i++) {
            Element e = allProp.getItem(i);
            if (mAuthorFromRel.isEmpty()) mAuthorFromRel = getAuthorFromRelAttribute(e);
        }
    }

    // It is assumed the ItemScope parent of Element e is already parsed.
    // For querySelectorAll(), parent nodes are guaranteed
    // to appear before child nodes, so this assumption is met.
    private ThingItem getItemScopeParent(Element e) {
        ThingItem parentItem = null;

        Element parentElement = e;
        while (parentElement != null) {
            parentElement = parentElement.getParentElement();
            if (parentElement == null) break;
            if (isItemScope(parentElement)) {
                if (mItemElement.containsKey(parentElement)) {
                    parentItem = mItemElement.get(parentElement);
                }
                break;
            }
        }
        return parentItem;
    }

    private void parseElement(Element e, ThingItem parentItem) {
        ThingItem newItem = null;
        boolean isItemScope = isItemScope(e);
        // A non-null |parentItem| means we're currently parsing the elements for a schema.org type.
        String[] propertyNames = parentItem != null ? getItemProp(e) : new String[0];

        if (isItemScope) {
            // The "itemscope" and "itemtype" attributes of |e| indicate the start of an item.
            // Create the corresponding extended-ThingItem, and add it to the list if:
            // 1) its type is supported, and
            // 2) if the parent is an unsupported type, it's not an "itemprop" attribute of the
            //    parent, based on the rule that an item is a top-level item if its element doesn't
            //    have an itemprop attribute.
            newItem = createItemForElement(e);
            if (newItem != null && newItem.isSupported() &&
                (parentItem == null || parentItem.isSupported() || propertyNames.length == 0)) {
                mItemScopes.add(newItem);
                mItemElement.put(e, newItem);
            }
        }

        // If parent is a supported type, parse the element for >= 1 properties in "itemprop"
        // attribute.
        if (propertyNames.length > 0 && parentItem.isSupported() &&
            (newItem == null || newItem.isSupported())) {
            for (int i = 0; i < propertyNames.length; i++) {
                // If a new item was created above, the property value of this "itemprop" attribute
                // is an embedded item, so add it to the parent item.
                if (newItem != null) {
                    parentItem.putItemValue(propertyNames[i], newItem);
                } else {
                   // Otherwise, extract the property value from the tag itself, and add it to the
                   // parent item.
                   parentItem.putStringValue(propertyNames[i], getPropertyValue(e));
                }
            }
        }
    }

    private Type getItemType(Element e) {
        // "itemtype" attribute is case-sensitive.
        String type = e.getAttribute("ITEMTYPE");
        return sTypeUrls.containsKey(type) ? sTypeUrls.get(type) : Type.UNSUPPORTED;
    }

    private ThingItem createItemForElement(Element e) {
        ThingItem newItem = null;
        Type type = getItemType(e);
        switch (type) {
            case IMAGE:
                newItem = new ImageItem();
                break;
            case ARTICLE:
                newItem = new ArticleItem();
                break;
            case PERSON:
                newItem = new PersonItem();
                break;
            case ORGANIZATION:
                newItem = new OrganizationItem();
                break;
            case UNSUPPORTED:
                newItem = new UnsupportedItem();
                break;
            default:
                return null;
        }
        return newItem;
    }

    static class ImageItem extends ThingItem {
        ImageItem() {
            super(Type.IMAGE);

            addStringPropertyName(CONTENT_URL_PROP);
            addStringPropertyName(ENCODING_FORMAT_PROP);
            addStringPropertyName(CAPTION_PROP);
            addStringPropertyName(REPRESENTATIVE_PROP);
            addStringPropertyName(WIDTH_PROP);
            addStringPropertyName(HEIGHT_PROP);
        }

        final boolean isRepresentativeOfPage() {
            return getStringProperty(REPRESENTATIVE_PROP).equalsIgnoreCase("true");
        }

        final MarkupParser.Image getImage() {
            MarkupParser.Image image = new MarkupParser.Image();
            image.url = getStringProperty(CONTENT_URL_PROP);
            if (image.url.isEmpty()) image.url = getStringProperty(URL_PROP);
            image.type = getStringProperty(ENCODING_FORMAT_PROP);
            image.caption = getStringProperty(CAPTION_PROP);
            image.width = JavaScript.parseInt(getStringProperty(WIDTH_PROP), 10);
            image.height = JavaScript.parseInt(getStringProperty(HEIGHT_PROP), 10);
            return image;
        }
    }

    static class ArticleItem extends ThingItem {
        ArticleItem() {
            super(Type.ARTICLE);

            addStringPropertyName(HEADLINE_PROP);
            addStringPropertyName(PUBLISHER_PROP);
            addStringPropertyName(COPYRIGHT_HOLDER_PROP);
            addStringPropertyName(COPYRIGHT_YEAR_PROP);
            addStringPropertyName(DATE_MODIFIED_PROP);
            addStringPropertyName(DATE_PUBLISHED_PROP);
            addStringPropertyName(AUTHOR_PROP);
            addStringPropertyName(CREATOR_PROP);
            addStringPropertyName(SECTION_PROP);

            addItemPropertyName(PUBLISHER_PROP);
            addItemPropertyName(COPYRIGHT_HOLDER_PROP);
            addItemPropertyName(AUTHOR_PROP);
            addItemPropertyName(CREATOR_PROP);
            addItemPropertyName(ASSOCIATED_MEDIA_PROP);
            addItemPropertyName(ENCODING_PROP);
        }

        final MarkupParser.Article getArticle() {
            MarkupParser.Article article = new MarkupParser.Article();
            article.publishedTime = getStringProperty(DATE_PUBLISHED_PROP);
            article.modifiedTime = getStringProperty(DATE_MODIFIED_PROP);
            article.section = getStringProperty(SECTION_PROP);
            String author = getPersonOrOrganizationName(AUTHOR_PROP);
            if (author.isEmpty()) author = getPersonOrOrganizationName(CREATOR_PROP);
            article.authors = author.isEmpty() ? new String[0] : new String[] { author };
            return article;
        }

        final String getCopyright() {
            // Returns a concatenated string of copyright year and copyright holder of the article,
            // delimited by a whitespace.
            String copyright = concat(getStringProperty(COPYRIGHT_YEAR_PROP),
                                      getPersonOrOrganizationName(COPYRIGHT_HOLDER_PROP));
            return copyright.isEmpty() ? copyright : "Copyright " + copyright;
        }

        final String getPersonOrOrganizationName(String propertyName) {
            // Returns either the string value of |propertyName| or the value returned by getName()
            // of PersonItem or OrganizationItem.
            String value = getStringProperty(propertyName);
            if (!value.isEmpty()) return value;

            ThingItem valueItem = getItemProperty(propertyName);
            if (valueItem != null) {
                if (valueItem.getType() == Type.PERSON) {
                    value = ((PersonItem) valueItem).getName();
                } else if (valueItem.getType() == Type.ORGANIZATION) {
                    value = ((OrganizationItem) valueItem).getName();
                }
            }
            return value;
        }

        final ImageItem getRepresentativeImageItem() {
            // Returns the corrresponding ImageItem for "associatedMedia" or "encoding" property.
            ThingItem imageItem = getItemProperty(ASSOCIATED_MEDIA_PROP);
            if (imageItem == null) imageItem = getItemProperty(ENCODING_PROP);
            return imageItem != null && imageItem.getType() == Type.IMAGE ?
                    (ImageItem) imageItem : null;
        }

        final MarkupParser.Image getImage() {
            // Use value of "image" property to create a MarkupParser.Image.
            String imageUrl = getStringProperty(IMAGE_PROP);
            if (imageUrl.isEmpty()) return null;
            MarkupParser.Image image = new MarkupParser.Image();
            image.url = imageUrl;
            return image;
        }
    }

    private static class PersonItem extends ThingItem {
        PersonItem() {
            super(Type.PERSON);

            addStringPropertyName(FAMILY_NAME_PROP);
            addStringPropertyName(GIVEN_NAME_PROP);
        }

        String getName() {
            // Returns either the value of NAME_PROP, or concatenated values of GIVEN_NAME_PROP and
            // FAMILY_NAME_PROP delimited by a whitespace.
            String name = getStringProperty(NAME_PROP);
            return !name.isEmpty() ? name :
                    concat(getStringProperty(GIVEN_NAME_PROP), getStringProperty(FAMILY_NAME_PROP));
        }
    }

    private static class OrganizationItem extends ThingItem {
        OrganizationItem() {
            super(Type.ORGANIZATION);

            addStringPropertyName(LEGAL_NAME_PROP);
        }

        String getName() {
            // Returns either the value of NAME_PROP or LEGAL_NAME_PROP.
            String name = getStringProperty(NAME_PROP);
            return !name.isEmpty() ? name : getStringProperty(LEGAL_NAME_PROP);
        }
    }

    private static class UnsupportedItem extends ThingItem {
        UnsupportedItem() {
            super(Type.UNSUPPORTED);
        }
    }

    private static boolean isItemScope(Element e) {
        return e.hasAttribute("ITEMSCOPE") && e.hasAttribute("ITEMTYPE");
    }

    private static String[] getItemProp(Element e) {
        // "itemprop" attribute is case-sensitive, and can have multiple properties.
        String itemprop = e.getAttribute("ITEMPROP");
        if (itemprop.isEmpty()) return new String[0];
        String[] splits = StringUtil.split(itemprop, "\\s+");
        return splits.length > 0 ? splits : new String[] { itemprop };
    }

    private static final Map<String, String> sTagAttributeMap;

    static {
        // The key for |sTagAttributeMap| is the tag name, while the entry value is an array of
        // attributes in the specified tag from which to extract information:
        // - 0th attribute: contains the value for the property specified in itemprop
        // - 1st attribute: if available, contains the value for the author property.
        sTagAttributeMap = new HashMap<String, String>();
        sTagAttributeMap.put("IMG", "SRC");
        sTagAttributeMap.put("AUDIO", "SRC");
        sTagAttributeMap.put("EMBED", "SRC");
        sTagAttributeMap.put("IFRAME", "SRC");
        sTagAttributeMap.put("SOURCE", "SRC");
        sTagAttributeMap.put("TRACK", "SRC");
        sTagAttributeMap.put("VIDEO", "SRC");
        sTagAttributeMap.put("A", "HREF");
        sTagAttributeMap.put("LINK", "HREF");
        sTagAttributeMap.put("AREA", "HREF");
        sTagAttributeMap.put("META", "CONTENT");
        sTagAttributeMap.put("TIME", "DATETIME");
        sTagAttributeMap.put("OBJECT", "DATA");
        sTagAttributeMap.put("DATA", "VALUE");
        sTagAttributeMap.put("METER", "VALUE");
    }

    // Extracts the property value from |e|.  For some tags, the value is a specific attribute,
    // while for others, it's the text between the start and end tags.
    private static String getPropertyValue(Element e) {
        String value = "";
        String tagName = e.getTagName();
        if (sTagAttributeMap.containsKey(tagName)) {
            value = e.getAttribute(sTagAttributeMap.get(tagName));
        }
        // Use javascript textContent (instead of javascript innerText) to include invisible text.
        if (value.isEmpty()) value = DomUtil.javascriptTextContent(e);
        return value;
    }

    // Extracts the author property from the "rel=author" attribute of an anchor or a link element.
    private static String getAuthorFromRelAttribute(Element e) {
        String author = "";
        String tagName = e.getTagName();
        if ((tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("LINK")) &&
                e.getAttribute("REL").equalsIgnoreCase(AUTHOR_REL)) {
            // Use javascript textContent (instead of javascript innerText) to include invisible
            // text.
            author = DomUtil.javascriptTextContent(e);
        }
        return author;
    }

    private static String concat(String first, String second) {
        String concat = first;
        if (!concat.isEmpty() && !second.isEmpty()) concat += " ";
        concat += second;
        return concat;
    }
}
