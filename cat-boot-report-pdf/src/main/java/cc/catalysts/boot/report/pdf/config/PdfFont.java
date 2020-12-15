package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.utils.PdfFontContext;
import com.google.common.base.Objects;
import com.google.common.collect.Streams;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by cboehmwalder on 08.09.2016.
 */
public class PdfFont {
    public static final PdfFont HELVETICA;
    public static final PdfFont TIMES_ROMAN;
    public static final PdfFont COURIER;
    public static final PdfFont SYMBOL;
    public static final PdfFont ZAPF_DINGBATS;

    private static final Logger LOG = getLogger(PdfFont.class);
    /**
     * NOTE: only type 1 fonts can be shared, type 0 fonts are modified when subsetted into document and can therefore
     * not be shared.
     */
    private static Map<String, PdfFont> type1Fonts = new HashMap<>();
    private final String basename;

    private Map<String, PDFont> styles = new HashMap<>();

    static {
        HELVETICA = new PdfFont("Helvetica");
        HELVETICA.addStyle("regular", PDType1Font.HELVETICA);
        HELVETICA.addStyle("bold", PDType1Font.HELVETICA_BOLD);
        HELVETICA.addStyle("italic", PDType1Font.HELVETICA_OBLIQUE);
        HELVETICA.addStyle("boldItalic", PDType1Font.HELVETICA_BOLD_OBLIQUE);
        type1Fonts.put("Helvetica", HELVETICA);

        TIMES_ROMAN = new PdfFont("Times-Roman");
        TIMES_ROMAN.addStyle("regular", PDType1Font.TIMES_ROMAN);
        TIMES_ROMAN.addStyle("bold", PDType1Font.TIMES_BOLD);
        TIMES_ROMAN.addStyle("italic", PDType1Font.TIMES_ITALIC);
        TIMES_ROMAN.addStyle("boldItalic", PDType1Font.TIMES_BOLD_ITALIC);
        type1Fonts.put("Times-Roman", TIMES_ROMAN);

        COURIER = new PdfFont("Courier");
        COURIER.addStyle("regular", PDType1Font.COURIER);
        COURIER.addStyle("bold", PDType1Font.COURIER_BOLD);
        COURIER.addStyle("italic", PDType1Font.COURIER_OBLIQUE);
        COURIER.addStyle("boldItalic", PDType1Font.COURIER_BOLD_OBLIQUE);
        type1Fonts.put("Courier", COURIER);

        SYMBOL = new PdfFont("Symbol");
        SYMBOL.addStyle("regular", PDType1Font.SYMBOL);
        type1Fonts.put("Helvetica", SYMBOL);

        ZAPF_DINGBATS = new PdfFont("ZapfDingbats");
        ZAPF_DINGBATS.addStyle("regular", PDType1Font.ZAPF_DINGBATS);
        type1Fonts.put("Zapf-Dingbats", ZAPF_DINGBATS);
    }

    public PdfFont(String basename) {
        this.basename = basename;
    }

    public void addStyle(String style, PDFont pf) {
        styles.put(style.toLowerCase(), pf);
    }

    /**
     * NOTE: this is kind of a hack, but since PdfBox doesn't expose the constructor
     * org.apache.pdfbox.pdmodel.font.PDType1Font#PDType1Font(java.lang.String), it is necessary.
     *
     * @param fontName One of the standard 14 font names defined by PostScript or of a font registered with registerFont(PDFont)
     * @return The appropriate PDFont
     */

    public static PdfFont getFont(String fontName) {
        if (type1Fonts.containsKey(fontName)) {
            return type1Fonts.get(fontName);
        }

        final PdfFontContext currentContext = PdfFontContext.current();
        Collection<String> fontsFromContext = Collections.EMPTY_LIST;
        if (currentContext != null) {
            final PdfFont font = currentContext.getFont(fontName);
            if (font != null) {
                return font;
            }
            fontsFromContext = currentContext.getAllFontNames();
        }

        final String availableFonts = Streams.concat(
                type1Fonts.keySet().stream(),
                fontsFromContext.stream())
                .collect(Collectors.joining(", "));

        throw new IllegalArgumentException("Could not find font " + fontName + " in: " + availableFonts);
    }

    public PDFont getStyle(String style) {
        style = style.toLowerCase();

        if (styles.containsKey(style)) {
            return styles.get(style);
        }

        throw new IllegalArgumentException("Unknown font style");
    }

    public PDFont getStyleByFontName(String name) {
        return styles.values()
                .stream()
                .filter(it -> Objects.equal(it.getName(), name))
                .findFirst()
                .orElse(null);
    }

    public static PDFont getInternalFont(String name) {
        return type1Fonts.values().stream()
                .flatMap(it -> it.styles.values().stream())
                .filter(it -> Objects.equal(name, it.getName())).findFirst()
                .orElse(null);
    }

    public String getBasename() {
        return basename;
    }

}
