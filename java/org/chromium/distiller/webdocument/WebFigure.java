package org.chromium.distiller.webdocument;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import org.chromium.distiller.DomUtil;

/**
 * WebFigure represents a figure element, containing an image and optionally a caption.
 */
public class WebFigure extends WebImage {

    private Element figCaption;

    /**
     * Build a figure element.
     * @param e The element detected as an image.
     * @param w The original width of the image.
     * @param h The original height of the image.
     * @param src The source URL of the image being extracted.
     * @param caption The element containing the caption of the image.
     */
    public WebFigure(Element e, int w, int h, String src, Element caption) {
        super(e, w, h, src);
        figCaption = caption;
    }

    /**
     * WebFigure extends WebImage so it can use WebImage generated output
     * and just handle the caption since an html figure is basically a
     * placeholder for an image and a caption.
     */
    @Override
    public String generateOutput(boolean textOnly) {
        Element figcaption = DomUtil.cloneAndProcessTree(figCaption);
        if (textOnly) {
            return DomUtil.getTextFromTreeForTest(figcaption);
        }

        Element figure = Document.get().createElement("FIGURE");
        figure.appendChild(getProcessedNode());
        if (!figCaption.getInnerHTML().isEmpty()) {
            figure.appendChild(figcaption);
        }
        return figure.getString();
    }
}
