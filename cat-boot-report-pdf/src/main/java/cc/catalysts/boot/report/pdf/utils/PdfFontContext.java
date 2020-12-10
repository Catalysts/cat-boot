package cc.catalysts.boot.report.pdf.utils;

import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfFontEncodingExceptionHandler;
import cc.catalysts.boot.report.pdf.exception.PdfBoxHelperException;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Holds thread-local font data (e.g. TTF fonts which need to be reloaded before every export because they are modified
 * upon embedding: PDType0Font#subset()
 */
public class PdfFontContext implements AutoCloseable {

    private static ThreadLocal<PdfFontContext> pdfFontContexts = new ThreadLocal<>();

    /**
     * Fonts currently loaded.
     */
    private Map<String, PdfFont> type0Fonts = new HashMap<>();

    /**
     * Fallback fonts to use when a target font doesn't support a given glyph.
     */
    private List<PDFont> fallbackFonts = new ArrayList();

    private PdfFontEncodingExceptionHandler fontEncodingExceptionHandler = null;

    public static PdfFontContext create() {
        final PdfFontContext oldContext = current();
        if (oldContext != null) {
            oldContext.close();
        }

        PdfFontContext context = new PdfFontContext();
        pdfFontContexts.set(context);
        return context;
    }

    /**
     * Gets the currently set stylesheet from the context or null if none set
     */
    public static PdfFontContext current() {
        return pdfFontContexts.get();
    }

    public static PdfFontContext currentOrCreate() {
        final PdfFontContext current = current();
        if (current == null) {
            return create();
        }
        return current;
    }

    private PdfFontContext() {
    }

    public PdfFont getFont(String fontName) {
        return type0Fonts.get(fontName);
    }

    public Collection<String> getAllFontNames() {
        return type0Fonts.keySet();
    }

    /**
     * Finds a font (PDFont not PdfFont wrapper) by name or returns null if not found.
     */
    public PDFont getInternalFont(String name) {
        return type0Fonts.values()
                .stream()
                .map(it -> it.getStyleByFontName(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> PdfFont.getInternalFont(name));
    }

    public PdfFont registerFont(PDType0Font font) {
        String fontBaseName = font.getName();
        String fontStyle = "regular";

        if (font.getDescendantFont() instanceof PDCIDFontType2) {
            PDCIDFontType2 tmpFont = (PDCIDFontType2) font.getDescendantFont();
            NamingTable ttfNamingTable = (NamingTable) tmpFont.getTrueTypeFont().getTableMap().get("name");

            fontBaseName = ttfNamingTable.getFontFamily();
            fontStyle = ttfNamingTable.getFontSubFamily().toLowerCase();
        }

        PdfFont f;
        if (type0Fonts.containsKey(fontBaseName)) {
            f = type0Fonts.get(fontBaseName);
            f.addStyle(fontStyle, font);
        } else {
            f = new PdfFont(fontBaseName);
            f.addStyle(fontStyle, font);
            type0Fonts.put(fontBaseName, f);
        }

        return f;
    }

    /**
     * Set global fallback fonts.
     */
    public void setFallbackFonts(List<PDFont> fallbacks) {
        if (fallbacks.stream().anyMatch(it -> it == null)) {
            throw new IllegalArgumentException("Must not pass null fonts.");
        }
        this.fallbackFonts.clear();
        this.fallbackFonts.addAll(fallbacks);
    }

    public List<PDFont> getPossibleFonts(PDFont font) {
        List<PDFont> fonts = new ArrayList();
        fonts.add(font);
        fonts.addAll(fallbackFonts);
        return fonts;
    }

    /**
     * Register a custom font encoding exception handler which gives the program a chance to either
     * replace the problematic codepoints or simply log/throw custom exceptions.
     */
    public void registerFontEncodingExceptionHandler(PdfFontEncodingExceptionHandler handler) {
        this.fontEncodingExceptionHandler = handler;
    }

    public String handleFontEncodingException(String text, String codePointString, int start, int end) {
        if (this.fontEncodingExceptionHandler != null) {
            return this.fontEncodingExceptionHandler.handleFontEncodingException(text, codePointString, start, end);
        }
        throw new PdfBoxHelperException("Cannot encode '" + codePointString + "' in '" + text + "'.");
    }


    @Override
    public void close() {
        type0Fonts.clear();

        pdfFontContexts.remove();
    }

}