package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.util.StringUtils;

/**
 * Created by cboehmwalder on 04.08.2016.
 */
public class ReportRichTextBox extends ReportTextBox {

    private String boldFontStyle;
    private String italicFontStyle;

    public ReportRichTextBox(PdfTextStyle textConfig, float lineDistance, String text, String boldFontStyle, String italicFontStyle) {
        this(textConfig, lineDistance, text);
        this.boldFontStyle = boldFontStyle;
        this.italicFontStyle = italicFontStyle;
    }

    public ReportRichTextBox(PdfTextStyle textConfig, float lineDistance, String text) {
        super(textConfig, lineDistance, text);
        this.boldFontStyle = "bold";
        this.italicFontStyle = "italic";
    }

    /**
     * copy constructor with new text
     *
     * @param object reportTextBox object for configurations
     * @param text   text of text box
     */
    public ReportRichTextBox(ReportRichTextBox object, String text) {
        this(object.textConfig, object.lineDistance, text, object.boldFontStyle, object.italicFontStyle);
    }

    @Override
    public ReportTextBox clone(String newString) {
        return new ReportRichTextBox(this, newString);
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float textX, float textY, float allowedWidth) {
        return PdfBoxHelper.addRichText(stream, textConfig, textX, textY, allowedWidth, lineDistance, align, text, boldFontStyle, italicFontStyle);
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
