package cc.catalysts.boot.report.pdf.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to force elements to be rendered on the same page (if isSplitable = false).
 * <p>
 * Created by skaupper on 04.08.2016.
 */
public class ReportCompositeElement implements ReportElement {

    private final List<ReportElement> elements = new ArrayList<>();
    private Collection<ReportImage.ImagePrintIntent> intents = new LinkedList<ReportImage.ImagePrintIntent>();
    private boolean isSplitable = false;

    public ReportCompositeElement addElement(ReportElement element) {
        elements.add(element);
        return this;
    }

    public void setSplitable(boolean splitable) {
        isSplitable = splitable;
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth) throws IOException {
        float lastY = startY;
        for (ReportElement element : elements) {
            lastY = element.print(document, stream, pageNumber, startX, lastY, allowedWidth);
            intents.addAll(element.getImageIntents());
        }
        return lastY;
    }

    @Override
    public float getHeight(float allowedWidth) {
        float height = 0;
        for (ReportElement element : elements) {
            if (element instanceof ReportPadding) {
                height += ((ReportPadding) element).getPadding();
            } else {
                height += element.getHeight(allowedWidth);
            }
        }
        return height;
    }

    @Override
    public boolean isSplitable() {
        return isSplitable;
    }

    @Override
    public float getFirstSegmentHeight(float allowedWidth) {
        float height = 0;
        for (ReportElement element: elements) {
            if (!element.isSplitable()) {
                if (element instanceof ReportPadding) {
                    height += ((ReportPadding) element).getPadding();
                } else {
                    height += element.getHeight(allowedWidth);
                }
            } else {
                height += element.getFirstSegmentHeight(allowedWidth);
                break;
            }
        }

        return height;
    }

    @Override
    public ReportElement[] split(float allowedWidth) {
        throw new UnsupportedOperationException("Horizontal split not allowed!");
    }

    @Override
    public ReportElement[] split(float allowedWidth, float allowedHeight) {
        ReportCompositeElement first = new ReportCompositeElement();
        ReportCompositeElement next = new ReportCompositeElement();

        boolean foundFirstSplittableElement = false;
        for (ReportElement element: elements) {
            if (foundFirstSplittableElement) {
                next.addElement(element);
            } else {
                if (element.isSplitable()) {
                    foundFirstSplittableElement = true;
                    ReportElement[] splitted = element.split(allowedWidth, allowedHeight);
                    first.addElement(splitted[0]);
                    next.addElement(splitted[1]);
                } else {
                    first.addElement(element);
                }
            }
        }

        return new ReportElement[]{first, next};
    }

    @Override
    public float getHeightOfElementToSplit(float allowedWidth, float allowedHeight) {
        float heightOfElementToSplit = getHeight(allowedWidth);

        float remainingAllowedHeight = allowedHeight;
        for (ReportElement element: elements) {
            if (!element.isSplitable()) {
                remainingAllowedHeight -= element.getHeight(allowedWidth);
            } else {
                heightOfElementToSplit = element.getHeightOfElementToSplit(allowedWidth, remainingAllowedHeight);
                break;
            }
        }

        return heightOfElementToSplit;
    }

    @Override
    public Collection<ReportImage.ImagePrintIntent> getImageIntents() {
        return intents;
    }
}
