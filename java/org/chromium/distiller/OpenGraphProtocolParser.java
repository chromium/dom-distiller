// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class recognizes and parses the Open Graph Protocol markup tags and returns the properties
 * that matter to distilled content.
 * First, it extracts the prefix and/or xmlns attributes from the HTML or HEAD tags to determine the
 * prefixes that will be used for the procotol.  If no prefix is specified, we fall back to the
 * commonly used ones, e.g. "og".  Then, it scans the OpenGraph Protocol <meta> elements that we
 * care about, extracts their content, and stores them semantically, i.e. taking into consideration
 * arrays, structures, and object types.  Callers call get* to access these properties.
 * The properties we care about are:
 * - 4 required properties: title, type, image, url.
 * - 2 optional properties: description, site_name.
 * - image structured properties: image:url, image:secure_url, image:type, image:width, image:height
 * - profile object properties: first_name, last_name
 * - article object properties: section, published_time, modified_time, expiration_time, author;
 *                              each author is a URL to the author's profile.
 */
public class OpenGraphProtocolParser implements MarkupParser.Accessor {
    private static final String TITLE_PROP = "title";
    private static final String TYPE_PROP = "type";
    private static final String IMAGE_PROP = "image";
    private static final String URL_PROP = "url";
    private static final String DESCRIPTION_PROP = "description";
    private static final String SITE_NAME_PROP = "site_name";
    private static final String IMAGE_STRUCT_PROP_PFX = "image:";
    private static final String IMAGE_URL_PROP = "image:url";
    private static final String IMAGE_SECURE_URL_PROP = "image:secure_url";
    private static final String IMAGE_TYPE_PROP = "image:type";
    private static final String IMAGE_WIDTH_PROP = "image:width";
    private static final String IMAGE_HEIGHT_PROP = "image:height";
    private static final String PROFILE_FIRSTNAME_PROP = "first_name";
    private static final String PROFILE_LASTNAME_PROP = "last_name";
    private static final String ARTICLE_SECTION_PROP = "section";
    private static final String ARTICLE_PUBLISHED_TIME_PROP = "published_time";
    private static final String ARTICLE_MODIFIED_TIME_PROP = "modified_time";
    private static final String ARTICLE_EXPIRATION_TIME_PROP = "expiration_time";
    private static final String ARTICLE_AUTHOR_PROP = "author";

    private static final String PROFILE_OBJTYPE = "profile";
    private static final String ARTICLE_OBJTYPE = "article";

    private enum Prefix {
        OG,
        PROFILE,
        ARTICLE,
    }

    private final TimingInfo mTimingInfo;

    /**
     * Called when parsing a stateful property, returns true if the property and its content should
     * be added to the property table.
     */
    private interface Parser {
        public boolean parse(String property, String content, Map<String, String> propertyTable);
    }

    private class PropertyRecord {
        private String mName = null;
        private Prefix mPrefix;
        private Parser mParser = null;

        PropertyRecord(String name, Prefix prefix, Parser parser) {
            mName = name;
            mPrefix = prefix;
            mParser = parser;
        }
    }

    private final Map<String, String> mPropertyTable;
    private final Map<Prefix, String> mPrefixes;
    private final ImageParser mImageParser = new ImageParser();
    private final ProfileParser mProfileParser = new ProfileParser();
    private final ArticleParser mArticleParser = new ArticleParser();
    private final PropertyRecord[] mProperties = {
        new PropertyRecord(TITLE_PROP, Prefix.OG, null),
        new PropertyRecord(TYPE_PROP, Prefix.OG, null),
        new PropertyRecord(URL_PROP, Prefix.OG, null),
        new PropertyRecord(DESCRIPTION_PROP, Prefix.OG, null),
        new PropertyRecord(SITE_NAME_PROP, Prefix.OG, null),
        new PropertyRecord(IMAGE_PROP, Prefix.OG, mImageParser),
        new PropertyRecord(IMAGE_STRUCT_PROP_PFX, Prefix.OG, mImageParser),
        new PropertyRecord(PROFILE_FIRSTNAME_PROP, Prefix.PROFILE, mProfileParser),
        new PropertyRecord(PROFILE_LASTNAME_PROP, Prefix.PROFILE, mProfileParser),
        new PropertyRecord(ARTICLE_SECTION_PROP, Prefix.ARTICLE, mArticleParser),
        new PropertyRecord(ARTICLE_PUBLISHED_TIME_PROP, Prefix.ARTICLE, mArticleParser),
        new PropertyRecord(ARTICLE_MODIFIED_TIME_PROP, Prefix.ARTICLE, mArticleParser),
        new PropertyRecord(ARTICLE_EXPIRATION_TIME_PROP, Prefix.ARTICLE, mArticleParser),
        new PropertyRecord(ARTICLE_AUTHOR_PROP, Prefix.ARTICLE, mArticleParser),
    };

    /**
     * OpenGraph Protocol prefixes are determined and properties are parsed.  Returns the
     * OpenGraphProtocolParser object if the properties conform to the protocol, i.e. all required
     * properties exist.  Otherwise, return null.
     *
     */
    public static OpenGraphProtocolParser parse(Element root) {
        return parse(root, (TimingInfo) null);
    }

    public static OpenGraphProtocolParser parse(Element root, TimingInfo timingInfo) {
        try {
            double startTime = DomUtil.getTime();
            OpenGraphProtocolParser og = new OpenGraphProtocolParser(root, timingInfo);
            LogUtil.addTimingInfo(startTime, timingInfo, "OpenGraphProtocolParser.parse");
            return og;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the required "title" of the document.
     */
    @Override
    public String getTitle() {
        return getPropertyContent(TITLE_PROP);
    }

    /**
     * Returns the required "type" of the document if it's an article, empty string otherwise.
     */
    @Override
    public String getType() {
        String type = getPropertyContent(TYPE_PROP);
        return type.equalsIgnoreCase(ARTICLE_OBJTYPE) ? MarkupParser.ARTICLE_TYPE : "";
    }

    /**
     * Returns the required "url" of the document.
     */
    @Override
    public String getUrl() {
        return getPropertyContent(URL_PROP);
    }

    /**
     * Returns the structured properties of all "image" structures.  Each "image" structure consists
     * of image, image:url, image:secure_url, image:type, image:width, and image:height.
     */
    @Override
    public MarkupParser.Image[] getImages() {
        return mImageParser.getImages();
    }

    /**
     * Returns the optional "description" of the document.
     */
    @Override
    public String getDescription() {
        return getPropertyContent(DESCRIPTION_PROP);
    }

    /**
     * Returns the optional "site_name" of the document.
     */
    @Override
    public String getPublisher() {
        return getPropertyContent(SITE_NAME_PROP);
    }

    @Override
    public String getCopyright() {
        return "";  // Not supported.
    }

    /**
     * Returns the concatenated first_name and last_name (delimited by a whitespace) of the
     * "profile" object when value of "og:type" is "profile".
     */
    @Override
    public String getAuthor() {
        return mProfileParser.getFullName(mPropertyTable);
    }

    /**
     * Returns the properties of the "article" object when value of "og:type" is "article".  The
     * properties are published_time, modified_time and expiration_time, section, and a list of URLs
     * to each author's profile.
     */
    @Override
    public MarkupParser.Article getArticle() {
        MarkupParser.Article article = new MarkupParser.Article();
        article.publishedTime = getPropertyContent(ARTICLE_PUBLISHED_TIME_PROP);
        article.modifiedTime = getPropertyContent(ARTICLE_MODIFIED_TIME_PROP);
        article.expirationTime = getPropertyContent(ARTICLE_EXPIRATION_TIME_PROP);
        article.section = getPropertyContent(ARTICLE_SECTION_PROP);
        article.authors = mArticleParser.getAuthors();

        if (article.section.isEmpty() && article.publishedTime.isEmpty() &&
                article.modifiedTime.isEmpty() && article.expirationTime.isEmpty() &&
                article.authors.length == 0) {
            return null;
        }

        return article;
    }

    @Override
    public boolean optOut() {
        // While this is not directly supported, the page owner can simply omit the required tags
        // and parse() will return a null OpenGraphProtocolParser.
        return false;
    }

    /**
     * The object that has successfully extracted OpenGraphProtocol markup information from |root|.
     *
     * @throws Exception if the properties do not conform to the procotol i.e. not all required
     * properties exist.
     */
    private OpenGraphProtocolParser(Element root, TimingInfo timingInfo) throws Exception {
        mPropertyTable = new HashMap<String, String>();
        mPrefixes = new EnumMap<Prefix, String>(Prefix.class);
        mTimingInfo = timingInfo;

        double startTime = DomUtil.getTime();
        findPrefixes(root);
        LogUtil.addTimingInfo(startTime, mTimingInfo, "OpenGraphProtocolParser.findPrefixes");

        startTime = DomUtil.getTime();
        parseMetaTags(root);
        LogUtil.addTimingInfo(startTime, mTimingInfo, "OpenGraphProtocolParser.parseMetaTags");

        startTime = DomUtil.getTime();
        mImageParser.verify();
        LogUtil.addTimingInfo(startTime, mTimingInfo, "OpenGraphProtocolParser.imageParser.verify");

        startTime = DomUtil.getTime();
        String prefix = mPrefixes.get(Prefix.OG) + ":";
        if (getTitle().isEmpty())
            throw new Exception("Required \"" + prefix + "title\" property is missing.");
        if (getPropertyContent(TYPE_PROP).isEmpty())
            throw new Exception("Required \"" + prefix + "type\" property is missing.");
        if (getUrl().isEmpty())
            throw new Exception("Required \"" + prefix + "url\" property is missing.");
        if (getImages().length == 0)
            throw new Exception("Required \"" + prefix + "image\" property is missing.");
        LogUtil.addTimingInfo(startTime, mTimingInfo, "OpenGraphProtocolParser.checkRequired");
    }

    private static final String sOgpNsPrefixRegex =
            "((\\w+):\\s+(http:\\/\\/ogp.me\\/ns(\\/\\w+)*#))\\s*";
    private static final RegExp sOgpNsPrefixRegExp = RegExp.compile(sOgpNsPrefixRegex, "gi");

    private static final RegExp sOgpNsNonPrefixNameRegExp = RegExp.compile("^xmlns:(\\w+)", "i");
    private static final String sOgpNsNonPrefixValueRegex =
            "^http:\\/\\/ogp.me\\/ns(\\/\\w+)*#";
    private static final RegExp sOgpNsNonPrefixValueRegExp =
            RegExp.compile(sOgpNsNonPrefixValueRegex, "i");

    // The compile-time option(s) for parseMetaTags can be tuned according to the target data sets.
    // According to current benchmark, doing prefix filtering is faster than not.

    // Doing attribute prefix filtering is usually faster than not.
    private static final boolean doPrefixFiltering = true;

    private void findPrefixes(Element root) {
        String prefixes = "";

        // See if HTML tag has "prefix" attribute.
        if (root.hasTagName("HTML")) prefixes = root.getAttribute("prefix");

        // Otherwise, see if HEAD tag has "prefix" attribute.
        if (prefixes.isEmpty()) {
            NodeList<Element> heads = root.getElementsByTagName("HEAD");
            if (heads.getLength() == 1)
                prefixes = heads.getItem(0).getAttribute("prefix");
        }

        // If there's "prefix" attribute, its value is something like
        // "og: http://ogp.me/ns# profile: http://og.me/ns/profile# article:
        // http://ogp.me/ns/article#".
        if (!prefixes.isEmpty()) {
            sOgpNsPrefixRegExp.setLastIndex(0);
            while (true) {
                MatchResult match = sOgpNsPrefixRegExp.exec(prefixes);
                if (match == null) break;
                setPrefixForObjectType(match.getGroup(2), match.getGroup(4));
            }
        } else {
            // Still no "prefix" attribute, see if HTMl tag has "xmlns" attributes e.g.:
            // - "xmlns:og="http://ogp.me/ns#"
            // - "xmlns:profile="http://ogp.me/ns/profile#"
            // - "xmlns:article="http://ogp.me/ns/article#".
            final JsArray<Node> attributes = DomUtil.getAttributes(root);
            for (int i = 0; i < attributes.length(); i++) {
                final Node node = attributes.get(i);
                // Look for attribute name that starts with "xmlns:".
                String attributeName = node.getNodeName().toLowerCase();
                MatchResult nameMatch = sOgpNsNonPrefixNameRegExp.exec(attributeName);
                if (nameMatch == null) continue;

                // Extract OGP namespace URI from attribute value, if available.
                String attributeValue = node.getNodeValue();
                MatchResult valueMatch = sOgpNsNonPrefixValueRegExp.exec(attributeValue);
                if (valueMatch != null) {
                    setPrefixForObjectType(nameMatch.getGroup(1), valueMatch.getGroup(1));
                }
            }
        }

        setDefaultPrefixes();
    }

    private void setPrefixForObjectType(String prefix, String objTypeWithLeadingSlash) {
        if (objTypeWithLeadingSlash == null || objTypeWithLeadingSlash.isEmpty()) {
            mPrefixes.put(Prefix.OG, prefix);
            return;
        }

        // Remove leading '/'.
        String objType = objTypeWithLeadingSlash.substring("/".length());
        if (objType.equals(PROFILE_OBJTYPE)) {
            mPrefixes.put(Prefix.PROFILE, prefix);
        } else if (objType.equals(ARTICLE_OBJTYPE)) {
            mPrefixes.put(Prefix.ARTICLE, prefix);
        }
    }

    private void setDefaultPrefixes() {
        // For any unspecified prefix, use common ones:
        // - "og": http://ogp.me/ns#
        // - "profile": http://ogp.me/ns/profile#
        // - "article": http://ogp.me/ns/article#.
        if (mPrefixes.get(Prefix.OG) == null) mPrefixes.put(Prefix.OG, "og");
        if (mPrefixes.get(Prefix.PROFILE) == null) mPrefixes.put(Prefix.PROFILE, PROFILE_OBJTYPE);
        if (mPrefixes.get(Prefix.ARTICLE) == null) mPrefixes.put(Prefix.ARTICLE, ARTICLE_OBJTYPE);
    }

    private void parseMetaTags(Element root) {
        NodeList<Element> allMeta = null;
        if (doPrefixFiltering) {
            // Attribute selectors with prefix
            // https://developer.mozilla.org/en-US/docs/Web/CSS/Attribute_selectors
            String query = "";
            for (Map.Entry<Prefix, String> entry : mPrefixes.entrySet()) {
                query += "meta[property^=\"" + entry.getValue() + "\"],";
            }
            query = query.substring(0, query.length() - 1);

            allMeta = DomUtil.querySelectorAll(root, query);
        } else {
            allMeta = DomUtil.querySelectorAll(root, "meta[property]");
        }

        for (int i = 0; i < allMeta.getLength(); i++) {
            MetaElement meta = MetaElement.as(allMeta.getItem(i));
            String property = meta.getAttribute("property").toLowerCase();

            // Only store properties that we care about for distillation.
            for (int j = 0; j < mProperties.length; j++) {
                String prefixWithColon = mPrefixes.get(mProperties[j].mPrefix) + ":";
                // Note that property.equals() won't work here because |mProperties| uses "image:"
                // (IMAGE_STRUCT_PROP_PFX) for all image structured properties, so as to prevent
                // repetitive property name comparison - here and then again in ImageParser.
                if (!property.startsWith(prefixWithColon + mProperties[j].mName)) continue;
                property = property.substring(prefixWithColon.length());

                boolean addProperty = true;
                if (mProperties[j].mParser != null) {
                    addProperty = mProperties[j].mParser.parse(property, meta.getContent(),
                            mPropertyTable);
                }
                if (addProperty) mPropertyTable.put(mProperties[j].mName, meta.getContent());
            }
        }
    }

    private String getPropertyContent(String property) {
        return !mPropertyTable.containsKey(property) ? "" : mPropertyTable.get(property);
    }

    private class ImageParser implements Parser {
        private final String[] mProperties = {
            IMAGE_PROP,
            IMAGE_URL_PROP,
            IMAGE_SECURE_URL_PROP,
            IMAGE_TYPE_PROP,
            IMAGE_WIDTH_PROP,
            IMAGE_HEIGHT_PROP,
        };

        private final List<String[]> mImages;

        @Override
        public boolean parse(String property, String content, Map<String, String> propertyTable) {
            String[] image = null;

            if (property.equals(IMAGE_PROP)) {  // Root property means end of current structure.
                image = new String[mProperties.length];
                image[0] = content;
                mImages.add(image);
            } else {  // Non-root property means it's for current structure.
                if (mImages.isEmpty()) {  // No image yet, create new one.
                    image = new String[mProperties.length];
                    mImages.add(image);
                } else {  // Property is for current structure, i.e. last in list.
                    image = mImages.get(mImages.size() - 1);
                }
                // 0th property is IMAGE_PROP, which is already handled above.
                for (int i = 1; i < mProperties.length; i++) {
                    if (property.equals(mProperties[i])) {
                        image[i] = content;
                        break;
                    }
                }
            }

            return false;   // Don't insert into property table.
        }

        private ImageParser() {
            mImages = new ArrayList<String[]>();
        }

        private MarkupParser.Image[] getImages() {
            MarkupParser.Image[] imagesOut = new MarkupParser.Image[mImages.size()];
            for (int i = 0; i < mImages.size(); i++) {
                String[] imageIn = mImages.get(i);
                MarkupParser.Image imageOut = new MarkupParser.Image();
                imagesOut[i] = imageOut;
                imageOut.url = imageIn[1] != null && !imageIn[1].isEmpty() ?
                        imageIn[1] : imageIn[0];
                if (imageIn[2] != null) imageOut.secureUrl = imageIn[2];
                if (imageIn[3] != null) imageOut.type = imageIn[3];
                // Caption is not supoprted, so ignore it.
                if (imageIn[4] != null) imageOut.width = JavaScript.parseInt(imageIn[4], 10);
                if (imageIn[5] != null) imageOut.height = JavaScript.parseInt(imageIn[5], 10);
            }
            return imagesOut;
        }

        private void verify() {
            if (mImages.isEmpty()) return;

            // Remove any image without the required root IMAGE_PROP.
            for (int i = mImages.size() - 1; i >= 0; i--) {
                String image_prop = mImages.get(i)[0];
                if (image_prop == null || image_prop.isEmpty()) mImages.remove(i);
            }
        }
    }

    private class ProfileParser implements Parser {
        private boolean mCheckedType;
        private boolean mIsProfileType;

        @Override
        public boolean parse(String property, String content, Map<String, String> propertyTable) {
            if (!mCheckedType) {  // Check that "type" property exists and has "profile" value.
                String requiredType = propertyTable.get(TYPE_PROP);
                mIsProfileType = requiredType != null &&
                        requiredType.equalsIgnoreCase(PROFILE_OBJTYPE);
                mCheckedType = true;
            }

            return mIsProfileType;  // If it's profile object, insert into property table.
        }

        private ProfileParser() {
            mCheckedType = false;
            mIsProfileType = false;
        }

        private String getFullName(Map<String, String> propertyTable) {
            if (!mIsProfileType) return "";

            String fullname = propertyTable.get(PROFILE_FIRSTNAME_PROP);
            if (fullname == null) fullname = "";
            String lastname = propertyTable.get(PROFILE_LASTNAME_PROP);
            if (lastname != null && !fullname.isEmpty() && !lastname.isEmpty()) fullname += " ";
            fullname += lastname;
            return fullname;
        }
    }

    private class ArticleParser implements Parser {
        private boolean mCheckedType;
        private boolean mIsArticleType;
        private final List<String> mAuthors;

        @Override
        public boolean parse(String property, String content, Map<String, String> propertyTable) {
            if (!mIsArticleType) {  // Check that "type" property exists and has "article" value.
                String requiredType = propertyTable.get(TYPE_PROP);
                mIsArticleType = requiredType != null &&
                        requiredType.equalsIgnoreCase(ARTICLE_OBJTYPE);
                mCheckedType = true;
            }
            if (!mIsArticleType) return false;

            // "author" property is an array of URLs, so we special-handle it here.
            if (property.equals(ARTICLE_AUTHOR_PROP)) {
                mAuthors.add(content);
                return false; // We've handled it, don't insert into propertyTable.
            }

            // Other properties are stateless, so inserting into property table is good enough.
            return true;
        }

        private ArticleParser() {
            mCheckedType = false;
            mIsArticleType = false;
            mAuthors = new ArrayList<String>();
        }

        private String[] getAuthors() {
            return mAuthors.toArray(new String[mAuthors.size()]);
        }
    }
}
