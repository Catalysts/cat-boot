package cc.catalysts.boot.report.pdf.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to force elements to be rendered on the same page.
 *
 * Created by skaupper on 04.08.2016.
 */
public class ReportCompositeElement extends AbstractReportElement {

    private final List<ReportElement> elements = new ArrayList<>();
    private Collection<ReportImage.ImagePrintIntent> intents = new LinkedList<ReportImage.ImagePrintIntent>();

    public ReportCompositeElement addElement(ReportElement element) {
        elements.add(element);
        return this;
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
            height += element.getHeight(allowedWidth);
        }
        return height;
    }

    @Override
    public Collection<ReportImage.ImagePrintIntent> getImageIntents() {
        return intents;
    }
}
