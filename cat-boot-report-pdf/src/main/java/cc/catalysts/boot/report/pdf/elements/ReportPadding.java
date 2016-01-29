package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.Map;

/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Klaus Lehner
 */
public class ReportPadding extends AbstractReportElement implements ReportElement {

    private float padding;

    public ReportPadding(float padding) {
        this.padding = padding;
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth, Map<ReportFontType, PDFont> fontLibrary) throws IOException {
        return startY - padding;
    }

    @Override
    public float getHeight(float allowedWidth) {
        //to avoid causing page breaks
        return 0;
    }
}
