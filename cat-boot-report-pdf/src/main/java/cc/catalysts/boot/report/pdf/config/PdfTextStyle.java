package cc.catalysts.boot.report.pdf.config;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.font.FontProvider;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.util.Assert;

import java.awt.*;
import java.io.IOException;

public class PdfTextStyle {

    private int fontSize;
    private PDFont font;
    private Color color;

    public PdfTextStyle(int fontSize, PDFont defaultFont, Color color) {
        this.fontSize = fontSize;
        this.font = defaultFont;
        this.color = color;
    }

    public PdfTextStyle(int fontSize, PDType1Font defaultFont, Color color) {
        this.fontSize = fontSize;
        this.font = defaultFont;
        this.color = color;
    }

    /**
     * NOTE: this is kind of a hack, but since PdfBox doesn't expose the constructor
     * org.apache.pdfbox.pdmodel.font.PDType1Font#PDType1Font(java.lang.String), it is necessary.
     *
     * @param name One of the standard 14 font names defined by PostScript
     * @return The appropriate PDFont
     */
    private PDFont resolveStandard14Name(String name) {
        switch(name) {
            case "Times-Roman": return PDType1Font.TIMES_ROMAN;
            case "Times-Bold": return PDType1Font.TIMES_BOLD;
            case "Times-Italic":  return PDType1Font.TIMES_ITALIC;
            case "Times-BoldItalic":  return PDType1Font.TIMES_BOLD_ITALIC;
            case "Helvetica":  return PDType1Font.HELVETICA;
            case "Helvetica-Bold":  return PDType1Font.HELVETICA_BOLD;
            case "Helvetica-Oblique":  return PDType1Font.HELVETICA_OBLIQUE;
            case "Helvetica-BoldOblique":  return PDType1Font.HELVETICA_BOLD_OBLIQUE;
            case "Courier": return PDType1Font.COURIER;
            case "Courier-Bold": return PDType1Font.COURIER_BOLD;
            case "Courier-Oblique": return PDType1Font.COURIER_OBLIQUE;
            case "Courier-BoldOblique": return PDType1Font.COURIER_BOLD_OBLIQUE;
            case "Symbol": return PDType1Font.SYMBOL;
            case "ZapfDingbats": return PDType1Font.ZAPF_DINGBATS;
        }

        throw new IllegalArgumentException("this font name is not recognized");
    }

    /**
     * This constructor is used by spring when creating a font from properties.
     *
     * @param config e.g. 10,Times-Roman,#000000
     */
    public PdfTextStyle(String config) {
        Assert.hasText(config);
        String[] split = config.split(",");
        Assert.isTrue(split.length == 3, "config must look like: 10,Times-Roman,#000000");
        fontSize = Integer.parseInt(split[0]);
        font = resolveStandard14Name(split[1]);
        color = new Color(Integer.valueOf(split[2].substring(1), 16));
    }

    public int getFontSize() {
        return fontSize;
    }

    public PDFont getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

}
