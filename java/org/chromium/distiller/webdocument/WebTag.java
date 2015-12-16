package org.chromium.distiller.webdocument;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents HTML tags that need to be preserved over
 * the distillation process.
 */
public class WebTag extends WebElement {
    private String tagName;
    private TagType tagType;

    public enum TagType {
        START, END
    }

    private static Set<String> nestingTags;
    static {
        nestingTags = new HashSet<String>();
        nestingTags.add("UL");
        nestingTags.add("OL");
        nestingTags.add("LI");
        nestingTags.add("BLOCKQUOTE");
        nestingTags.add("PRE");
    }

    public WebTag(String tagName, TagType tagType) {
        this.tagName = tagName;
        this.tagType = tagType;
    }

    public boolean isStartTag() {
        return tagType == TagType.START;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String generateOutput(boolean textOnly) {
        if (textOnly) {
            return "";
        }
        return "<" + (isStartTag() ? "" : "/") + tagName + ">";
    }

    public static boolean canBeNested(String tagName) {
        return nestingTags.contains(tagName);
    }
}
