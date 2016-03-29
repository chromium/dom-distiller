package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import org.chromium.distiller.proto.DomDistillerProtos;

public class OpenGraphProtocolParserAccessor implements MarkupParser.Accessor {

    private final Element mRoot;
    private final DomDistillerProtos.TimingInfo mTimingInfo;

    private OpenGraphProtocolParser mParser;
    private boolean initialized;

    public OpenGraphProtocolParserAccessor(Element root) {
        this(root, null);
    }

    public OpenGraphProtocolParserAccessor(Element root,
                                           DomDistillerProtos.TimingInfo
                                                   timingInfo) {
        mRoot = root;
        mTimingInfo = timingInfo;
        initialized = false;
    }

    /**
     * Calls OpenGraphProtocolParser.parse only once.
     *
     * @return As the parser may return null, in order to avoid
     * NullPointerException when using the mParser, this method returns
     * true if the parser was successfully parsed, false otherwise.
     */
    private boolean init() {
        if (!initialized) {
            mParser = OpenGraphProtocolParser.parse(mRoot, mTimingInfo);
            initialized = true;
        }
        return mParser != null;
    }

    /**
     * Returns the required "title" of the document.
     */
    @Override
    public String getTitle() {
        return init() ? mParser.getPropertyContent(
                OpenGraphProtocolParser.TITLE_PROP) : "";
    }

    /**
     * Returns the required "type" of the document if it's an article,
     * empty string otherwise.
     */
    @Override
    public String getType() {
        String type = init() ? mParser.getPropertyContent(
                OpenGraphProtocolParser.TYPE_PROP) : "";
        return type.equalsIgnoreCase(
                OpenGraphProtocolParser.ARTICLE_OBJTYPE) ?
                MarkupParser.ARTICLE_TYPE : "";
    }

    /**
     * Returns the required "url" of the document.
     */
    @Override
    public String getUrl() {
        return init() ? mParser.getPropertyContent(
                OpenGraphProtocolParser.URL_PROP) : "";
    }

    /**
     * Returns the structured properties of all "image" structures.
     * Each "image" structure consists of image, image:url,
     * image:secure_url, image:type, image:width, and image:height.
     */
    @Override
    public MarkupParser.Image[] getImages() {
        return init() ? mParser.getImages() : new MarkupParser.Image[0];
    }

    /**
     * Returns the optional "description" of the document.
     */
    @Override
    public String getDescription() {
        return init() ? mParser.getPropertyContent(
                OpenGraphProtocolParser.DESCRIPTION_PROP) : "";
    }

    /**
     * Returns the optional "site_name" of the document.
     */
    @Override
    public String getPublisher() {
        return init() ? mParser.getPropertyContent(
                OpenGraphProtocolParser.SITE_NAME_PROP) : "";
    }

    @Override
    public String getCopyright() {
        return "";  // Not supported.
    }

    /**
     * Returns the concatenated first_name and last_name
     * (delimited by a whitespace) of the "profile" object when
     * value of "og:type" is "profile".
     */
    @Override
    public String getAuthor() {
        return init() ? mParser.getFullName() : "";
    }

    /**
     * Returns the properties of the "article" object when value of
     * "og:type" is "article".  The properties are published_time,
     * modified_time and expiration_time, section, and a list of URLs
     * to each author's profile.
     */
    @Override
    public MarkupParser.Article getArticle() {
        if (init()) {
            MarkupParser.Article article = new MarkupParser.Article();
            article.publishedTime = mParser.getPropertyContent(
                    OpenGraphProtocolParser.ARTICLE_PUBLISHED_TIME_PROP);
            article.modifiedTime = mParser.getPropertyContent(
                    OpenGraphProtocolParser.ARTICLE_MODIFIED_TIME_PROP);
            article.expirationTime = mParser.getPropertyContent(
                    OpenGraphProtocolParser.ARTICLE_EXPIRATION_TIME_PROP);
            article.section = mParser.getPropertyContent(
                    OpenGraphProtocolParser.ARTICLE_SECTION_PROP);
            article.authors = mParser.getAuthors();

            if (article.section.isEmpty() &&
                    article.publishedTime.isEmpty() &&
                    article.modifiedTime.isEmpty() &&
                    article.expirationTime.isEmpty() &&
                    article.authors.length == 0) {
                return null;
            }
            return article;
        }
        return null;
    }

    @Override
    public boolean optOut() {
        // While this is not directly supported, the page owner can
        // simply omit the required tags and init() will return
        // a null OpenGraphProtocolParser.
        return false;
    }
}