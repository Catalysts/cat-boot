package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Klaus Lehner
 */
public class ReportTextBox implements ReportElement {

    protected PdfTextStyle textConfig;
    protected String text;
    protected final float lineDistance;
    protected ReportAlignType align = ReportAlignType.LEFT;
    protected Map<CacheKey, String[]> cache = new HashMap<CacheKey, String[]>();

    public ReportTextBox(PdfTextStyle textConfig, float lineDistance, String text) {
        this.textConfig = textConfig;
        this.text = text == null ? "" : text;
        this.lineDistance = lineDistance;
    }

    /**
     * copy constructor with new text
     *
     * @param object reportTextBox object for configurations
     * @param text   text of text box
     */
    public ReportTextBox(ReportTextBox object, String text) {
        this.text = text == null ? "" : text;
        this.textConfig = object.textConfig;
        this.lineDistance = object.lineDistance;
        this.align = object.align;
    }

    public ReportTextBox clone(String newString) {
        return new ReportTextBox(this, newString);
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float textX, float textY, float allowedWidth) {
        return PdfBoxHelper.addTextWithLineBreaks(stream, textConfig, textX, textY, allowedWidth, lineDistance, align, text);
    }

    @Override
    public float getHeight(float allowedWidth) {
        int height = 0;
        String currText = text;
        while (currText != null) {
            String[] split = split(allowedWidth, currText);
            height += getFirstSegmentHeight(allowedWidth) + lineDistance;
            if (split[1] != null && split[1].equals(currText)) {
                // see PdfBoxHelper:441, it could be that text is not reduced, but just put in a second row,
                // in that case we would run in an endless loop
                // that needs to be fixed in PdfBoxHelper, this is just the attempt to ensure that we do not
                // run into an endless loop here

                height += getFirstSegmentHeight(allowedWidth) + lineDistance;
                break;
            }
            currText = split[1];

        }
        return height;
    }

    @Override
    public boolean isSplitable() {
        return true;
    }

    @Override
    public float getFirstSegmentHeight(float allowedWidth) {
        return textConfig.getFontSize();
    }

    @Override
    public ReportElement[] split(float allowedWidth) {
        String[] split = split(allowedWidth, text);
        ReportElement firstLine = new ReportTextBox(this, split[0]);
        ReportElement nextLines = new ReportTextBox(this, split[1]);
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

        ReportElement firstPart = new ReportTextBox(this, sb.toString());
        ReportElement nextLines = StringUtils.isEmpty(currText) ? null : new ReportTextBox(this, currText);
        return new ReportElement[]{firstPart, nextLines};
    }

    @Override
    public float getHeightOfElementToSplit(float allowedWidth, float allowedHeight) {
        return getHeight(allowedWidth);
    }

    @Override
    public Collection<ReportImage.ImagePrintIntent> getImageIntents() {
        return Collections.emptyList();
    }

    protected String[] split(float allowedWidth, String text) {
        if (text == null) {
            return new String[]{null, null};
        }
        CacheKey key = new CacheKey(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), allowedWidth, text);
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            String[] split = PdfBoxHelper.splitText(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), allowedWidth, text);
            cache.put(key, split);
            return split;
        }
    }

    public ReportAlignType getAlign() {
        return align;
    }

    public void setAlign(ReportAlignType align) {
        this.align = align;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private static final class CacheKey {
        private PDFont font;
        private float fontSize;
        private float width;
        private String text;

        private CacheKey(PDFont font, float fontSize, float width, String text) {
            this.font = font;
            this.fontSize = fontSize;
            this.width = width;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (!text.equals(cacheKey.text)) return false;
            if (fontSize != cacheKey.fontSize) return false;
            if (Float.compare(cacheKey.width, width) != 0) return false;
            return font.equals(cacheKey.font);

        }

        @Override
        public int hashCode() {
            int result = font.hashCode();
            result = 31 * result + (int) fontSize;
            result = 31 * result + text.hashCode();
            result = 31 * result + (width != +0.0f ? Float.floatToIntBits(width) : 0);
            return result;
        }
    }
}
