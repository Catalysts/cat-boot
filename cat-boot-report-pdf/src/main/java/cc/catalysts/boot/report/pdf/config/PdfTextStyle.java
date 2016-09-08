package cc.catalysts.boot.report.pdf.config;

import org.apache.fontbox.ttf.NamingTable;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.util.Assert;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PdfTextStyle {

    static Map<String, PDFont> fonts = new HashMap<>();

    public static void registerFont(PDType0Font font) {
        if(font.getDescendantFont() instanceof PDCIDFontType2) {
            PDCIDFontType2 tmpFont = (PDCIDFontType2) font.getDescendantFont();
            NamingTable ttfNamingTable = (NamingTable) tmpFont.getTrueTypeFont().getTableMap().get("name");

            String fontBaseName = ttfNamingTable.getFontFamily();
            String fontStyle = ttfNamingTable.getFontSubFamily();
        }

        fonts.put(font.getName(), font);
    }


    /**
     * NOTE: this is kind of a hack, but since PdfBox doesn't expose the constructor
     * org.apache.pdfbox.pdmodel.font.PDType1Font#PDType1Font(java.lang.String), it is necessary.
     *
     * @param fontName One of the standard 14 font names defined by PostScript or of a font registered with registerFont(PDFont)
     * @return The appropriate PDFont
     */

    public static PDFont getFont(String fontName) {
        switch(fontName) {
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

        if(fonts.containsKey(fontName)) {
            return fonts.get(fontName);
        }

        throw new IllegalArgumentException("Could not find font " + fontName);
    }


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
     * This constructor is used by spring when creating a font from properties.
     *
     * @param config e.g. 10,Times-Roman,#000000
     */
    public PdfTextStyle(String config) {
        Assert.hasText(config);
        String[] split = config.split(",");
        Assert.isTrue(split.length == 3, "config must look like: 10,Times-Roman,#000000");
        fontSize = Integer.parseInt(split[0]);
        font = getFont(split[1]);
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
