package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cboehmwalder on 04.08.2016.
 */
public class ReportRichTextBox extends ReportTextBox {

    public ReportRichTextBox(PdfTextStyle textConfig, float lineDistance, String text) {
        super(textConfig, lineDistance, text);
    }

    /**
     * copy constructor with new text
     *
     * @param object reportTextBox object for configurations
     * @param text   text of text box
     */
    public ReportRichTextBox(ReportRichTextBox object, String text) {
        super(object, text);
    }

    @Override
    public ReportTextBox clone(String newString) {
        return new ReportRichTextBox(this, newString);
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float textX, float textY, float allowedWidth) {
        return PdfBoxHelper.addRichText(stream, textConfig, textX, textY, allowedWidth, lineDistance, align, text);
    }

    @Override
    public ReportElement[] split(float allowedWidth) {
        String[] split = split(allowedWidth, text);
        ReportElement firstLine = new ReportRichTextBox(this, split[0]);
        ReportElement nextLines = new ReportRichTextBox(this, split[1]);
        return new ReportElement[]{firstLine, nextLines};
    }

    @Override
    public ReportElement[] split(float allowedWidth, float allowedHeight) {
        StringBuilder sb = new StringBuilder();
        float currHeight = getFirstSegmentHeight(allowedWidth) + lineDistance;
        String currText = text;
        while (!StringUtils.isEmpty(currText) && currHeight <= allowedHeight) {
            if (currHeight + getFirstSegmentHeight(allowedWidth) <= allowedHeight) {
                String[] split = split(allowedWidth, currText);
                sb.append(split[0].trim());
                sb.append(System.getProperty("line.separator"));
                currText = split[1];
                currHeight += getFirstSegmentHeight(allowedWidth) + lineDistance;
            } else {
                break;
            }
        }
        ReportElement firstPart = new ReportRichTextBox(this, sb.toString());
        ReportElement nextLines = StringUtils.isEmpty(currText) ? null : new ReportRichTextBox(this, currText);
        return new ReportElement[]{firstPart, nextLines};
    }
}
