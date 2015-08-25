package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebTag;

import java.util.Stack;

/**
 * This class is used to identify what WebTag should be
 * marked as <i>isContent</i> based on its {@link WebElement}s inside.
 * A {@link WebTag} is content when:
 * <ul>
 *    <li>Has any {@link WebElement} which is content.</li>
 *    <li>Has at least one nested {@link WebTag} which is content.</li>
 * </ul>
 */
public class NestedElementRetainer {
    public static void process(WebDocument document) {
        boolean isContent = false;
        int stackMark = -1;
        Stack<WebTag> stack = new Stack<>();

        for (WebElement e : document.getElements()) {
            if (!(e instanceof WebTag)) {
                if (!isContent) {
                    isContent = e.getIsContent();
                }
            } else {
                WebTag webTag = (WebTag) e;
                if (webTag.isStartTag()) {
                    webTag.setIsContent(isContent);
                    stack.push(webTag);
                    isContent = false;
                } else {
                    WebTag startWebTag = stack.pop();
                    isContent |= stackMark >= stack.size();
                    if (isContent) {
                        stackMark = stack.size() - 1;
                    }
                    boolean wasContent = startWebTag.getIsContent();
                    startWebTag.setIsContent(isContent);
                    webTag.setIsContent(isContent);
                    isContent = wasContent;
                }
            }
        }
    }
}
