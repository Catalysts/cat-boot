package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReportLink extends AbstractReportElement implements ReportElement {

    private static final Logger LOG = LoggerFactory.getLogger(PdfBoxHelper.class);

    private String text;
    private String link;

    private PdfTextStyle textConfig;
    private final float lineDistance;
    private ReportAlignType align = ReportAlignType.LEFT;

    public ReportLink(String text, String link, PdfTextStyle textConfig, float lineDistance) {
        this.text = text;
        this.link = link;
        this.textConfig = textConfig;
        this.lineDistance = lineDistance;
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth) throws IOException {
        float endTextY = PdfBoxHelper.addText(stream, textConfig, startX, startY, allowedWidth, lineDistance, align, text);
        return addLink(document, pageNumber, startX, endTextY - lineDistance, textConfig);
    }

    @Override
    public float getHeight(float allowedWidth) {
        return textConfig.getFontSize() + lineDistance + getLinkUnderline().getWidth();
    }

    private float addLink(PDDocument document, int pageNumber, float startX, float startY, PdfTextStyle textConfig) {
        PDAnnotationLink txtLink = new PDAnnotationLink();
        txtLink.setColor(textConfig.getColor());

        PDBorderStyleDictionary underline = getLinkUnderline();
        txtLink.setBorderStyle(underline);

        try {
            float textWidth = (textConfig.getFont().getStyle(textConfig.getStyle()).getStringWidth(text) / 1000) * textConfig.getFontSize();

            float startLinkY = startY + textConfig.getFontSize();
            float endLinkY = startY - underline.getWidth();

            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(startX);
            position.setLowerLeftY(startLinkY);
            position.setUpperRightX(startX + textWidth);
            position.setUpperRightY(endLinkY);
            txtLink.setRectangle(position);

            PDActionURI action = new PDActionURI();
            action.setURI(link);
            txtLink.setAction(action);

            PDPage page = document.getDocumentCatalog().getPages().get(pageNumber);
            page.getAnnotations().add(txtLink);

            return endLinkY;
        } catch (IOException e) {
            LOG.warn("Could not add link: " + e.getClass() + " - " + e.getMessage());
            return startY;
        }
    }

    private PDBorderStyleDictionary getLinkUnderline() {
        PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
        underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        return underline;
    }
}
