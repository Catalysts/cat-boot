package cc.catalysts.boot.report.pdf.config;

import org.apache.fontbox.ttf.NamingTable;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cboehmwalder on 08.09.2016.
 */
public class PdfFont {
    public static final PdfFont HELVETICA;
    public static final PdfFont TIMES_ROMAN;
    public static final PdfFont COURIER;
    public static final PdfFont SYMBOL;
    public static final PdfFont ZAPF_DINGBATS;

    private static Map<String, PdfFont> fonts = new HashMap<>();
    private final String basename;

    private Map<String, PDFont> styles = new HashMap<>();

    static {
        HELVETICA = new PdfFont("Helvetica");
        HELVETICA.addStyle("regular", PDType1Font.HELVETICA);
        HELVETICA.addStyle("bold", PDType1Font.HELVETICA_BOLD);
        HELVETICA.addStyle("italic", PDType1Font.HELVETICA_OBLIQUE);
        HELVETICA.addStyle("boldItalic", PDType1Font.HELVETICA_BOLD_OBLIQUE);
        fonts.put("Helvetica", HELVETICA);

        TIMES_ROMAN = new PdfFont("Times-Roman");
        TIMES_ROMAN.addStyle("regular", PDType1Font.TIMES_ROMAN);
        TIMES_ROMAN.addStyle("bold", PDType1Font.TIMES_BOLD);
        TIMES_ROMAN.addStyle("italic", PDType1Font.TIMES_ITALIC);
        TIMES_ROMAN.addStyle("boldItalic", PDType1Font.TIMES_BOLD_ITALIC);
        fonts.put("Times-Roman", TIMES_ROMAN);

        COURIER = new PdfFont("Courier");
        COURIER.addStyle("regular", PDType1Font.COURIER);
        COURIER.addStyle("bold", PDType1Font.COURIER_BOLD);
        COURIER.addStyle("italic", PDType1Font.COURIER_OBLIQUE);
        COURIER.addStyle("boldItalic", PDType1Font.COURIER_BOLD_OBLIQUE);
        fonts.put("Courier", COURIER);

        SYMBOL = new PdfFont("Symbol");
        SYMBOL.addStyle("regular", PDType1Font.SYMBOL);
        fonts.put("Helvetica", SYMBOL);

        ZAPF_DINGBATS = new PdfFont("ZapfDingbats");
        ZAPF_DINGBATS.addStyle("regular", PDType1Font.ZAPF_DINGBATS);
        fonts.put("Zapf-Dingbats", ZAPF_DINGBATS);
    }

    public PdfFont(String basename) {
        this.basename = basename;
    }

    public void addStyle(String style, PDFont pf) {
        styles.put(style.toLowerCase(), pf);
    }

    public static PdfFont registerFont(PDType0Font font) {
        String fontBaseName = font.getName();
        String fontStyle = "regular";

        if (font.getDescendantFont() instanceof PDCIDFontType2) {
            PDCIDFontType2 tmpFont = (PDCIDFontType2) font.getDescendantFont();
            NamingTable ttfNamingTable = (NamingTable) tmpFont.getTrueTypeFont().getTableMap().get("name");

            fontBaseName = ttfNamingTable.getFontFamily();
            fontStyle = ttfNamingTable.getFontSubFamily().toLowerCase();
        }

        PdfFont f;
        if (fonts.containsKey(fontBaseName)) {
            f = fonts.get(fontBaseName);
            f.addStyle(fontStyle, font);
        } else {
            f = new PdfFont(fontBaseName);
            f.addStyle(fontStyle, font);
            fonts.put(fontBaseName, f);
        }

        return f;
    }

    /**
     * NOTE: this is kind of a hack, but since PdfBox doesn't expose the constructor
     * org.apache.pdfbox.pdmodel.font.PDType1Font#PDType1Font(java.lang.String), it is necessary.
     *
     * @param fontName One of the standard 14 font names defined by PostScript or of a font registered with registerFont(PDFont)
     * @return The appropriate PDFont
     */

    public static PdfFont getFont(String fontName) {
        if (fonts.containsKey(fontName)) {
            return fonts.get(fontName);
        }

        throw new IllegalArgumentException("Could not find font " + fontName);
    }

    public PDFont getStyle(String style) {
        style = style.toLowerCase();

        if (styles.containsKey(style)) {
            return styles.get(style);
        }

        throw new IllegalArgumentException("Unknown font style");
    }

    public String getBasename() {
        return basename;
    }
}
