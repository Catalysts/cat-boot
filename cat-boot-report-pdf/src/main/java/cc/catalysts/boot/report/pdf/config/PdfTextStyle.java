package cc.catalysts.boot.report.pdf.config;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

public class PdfTextStyle {

    private int fontSize;
    private PDFont font;
    private Color color;

    public PdfTextStyle(int fontSize, PDType1Font defaultFont, Color color) {
        this.fontSize = fontSize;
        this.font = defaultFont;
        this.color = color;
    }

    /**
     * This constructor is used by spring when creating a font from properties.
     *
     * @param config e.g. 10,Times-Roman,#000000
     */
    public PdfTextStyle(String config) {
        String[] split = config.split(",");
        fontSize = Integer.parseInt(split[0]);
        font = new PDType1Font(split[1]);
        color = new Color(Integer.valueOf(split[3].substring(1), 16));
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
