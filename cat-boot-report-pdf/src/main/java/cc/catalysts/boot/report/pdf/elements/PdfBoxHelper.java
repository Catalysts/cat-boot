package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.exception.PdfBoxHelperException;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PdfBoxHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PdfBoxHelper.class);
    private static final char FALLBACK_CHAR = '?';

    //for optimization
    private static final int MAX_CHARS_IN_LINE = 200;

    private PdfBoxHelper() {
    }

    /**
     * Adds text of any length, will parse it if necessary.
     *
     * @param stream       stream
     * @param textConfig   text config
     * @param textX        starting X position of text
     * @param textY        starting Y position of text
     * @param allowedWidth max width of text (wher to wrap)
     * @param lineHeightD  line height delta of text (line height will be: fontSize + this)
     * @param text         text
     * @return ending Y position of this line
     */
    public static float addText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text) {
        String fixedText = text;
        if (textConfig.getFont() == null || textConfig.getFont().getFontEncoding() instanceof WinAnsiEncoding) {
            // only necessary if the font doesn't support unicode
            fixedText = fixString(text);
        }
        float nextLineY = nextLineY((int) textY, textConfig.getFontSize(), lineHeightD);
        try {
            String[] split = splitText(textConfig.getFont(), textConfig.getFontSize(), allowedWidth, fixedText);
            float x;
            switch (align) {
                case LEFT:
                    x = textX;
                    break;
                case RIGHT:
                    float w = getTextWidth(textConfig.getFont(), textConfig.getFontSize(), split[0]);
                    x = textX + allowedWidth - w;
                    break;
                case CENTER:
                    float halfW = getTextWidth(textConfig.getFont(), textConfig.getFontSize(), split[0]) / 2;
                    float absoluteCenter = allowedWidth / 2 + textX;
                    x = absoluteCenter - halfW;
                    break;
                default:
                    throw new IllegalStateException("Align type " + align + " not implemented for text");
            }
            addTextSimple(stream, textConfig, x, nextLineY, split[0]);
            if (!StringUtils.isEmpty(split[1])) {
                return addText(stream, textConfig, textX, nextLineY, allowedWidth, lineHeightD, align, split[1]);
            } else {
                return nextLineY;
            }

        } catch (Exception e) {
            LOG.warn("Could not add text: " + e.getClass() + " - " + e.getMessage());
            return textY;
        }
    }

    private static String fixString(final String original) {
        StringBuilder sb = new StringBuilder();
        try {
            for (char ch : original.toCharArray()) {
                if (WinAnsiEncoding.INSTANCE.hasNameForCode(ch)) {
                    sb.append(ch);
                } else if (ch == (char) 8220 || ch == (char) 8222) {
                    sb.append('"');
                } else if (ch == (char) 8230) {
                    sb.append("...");
                } else if (ch == (char) 8364) { // euro sign
                    sb.append((char) 128); // see http://stackoverflow.com/questions/22260344/pdfbox-encode-symbol-currency-euro
                } else {
                    String decoded = Normalizer.normalize(String.valueOf(ch), Normalizer.Form.NFD);
                    char decodedChar = decoded != null && decoded.length() > 0 ? decoded.charAt(0) : FALLBACK_CHAR;
                    if (WinAnsiEncoding.INSTANCE.getCharacter(decodedChar) != null) {
                        sb.append(decodedChar);
                    } else {
                        sb.append(FALLBACK_CHAR);
                    }
                }
            }
        } catch (Exception e) {
            throw new PdfBoxHelperException("unexpected character decoding error for input " + original, e);
        }
        return sb.toString();
    }

    /**
     * Adds a string, no parsing
     *
     * @param stream     stream
     * @param textConfig text config
     * @param textX      starting X position of text
     * @param textY      stating Y position of text
     * @param text       text
     */
    public static void addTextSimple(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, String text) {
        try {
            stream.setFont(textConfig.getFont(), textConfig.getFontSize());
            stream.setNonStrokingColor(textConfig.getColor());
            stream.beginText();
            stream.moveTextPositionByAmount(textX, textY);
            stream.drawString(text);
            stream.endText();
        } catch (Exception e) {
            LOG.warn("Could not add text: " + e.getClass() + " - " + e.getMessage());
        }
    }

    private static List<Integer> getWrapableIndexes(String text) {
        List<Integer> list = new ArrayList<Integer>();
        Pattern pt = Pattern.compile("\\s+");
        Matcher m = pt.matcher(text);
        while (m.find()) {
            list.add(m.start());
        }
        list.add(text.length());
        return list;
    }

    public static String[] splitText(PDFont font, int fontSize, float allowedWidth, String text) {
        String endPart;
        String shortenedText = text;
        if (shortenedText.length() > MAX_CHARS_IN_LINE) {
            endPart = shortenedText.substring(MAX_CHARS_IN_LINE);
            shortenedText = shortenedText.substring(0, MAX_CHARS_IN_LINE);
        } else {
            endPart = "";
        }
        if (getTextWidth(font, fontSize, shortenedText) <= allowedWidth && shortenedText.indexOf((char) 13) == -1) {
            return new String[]{shortenedText, null};
        }

        boolean cleanSplit = true;
        List<Integer> indexes = getWrapableIndexes(shortenedText);
        int start = 0;
        int j = indexes.size() - 1;
        int end = indexes.get(j);

        int lineBreakPos = shortenedText.indexOf(10);
        if (lineBreakPos != -1 && getTextWidth(font, fontSize, shortenedText.substring(start, lineBreakPos)) <= allowedWidth) {
            end = lineBreakPos;
        } else {
            while (getTextWidth(font, fontSize, shortenedText.substring(start, end)) > allowedWidth) {
                if (j == 0) {
                    cleanSplit = false;
                    break;
                }
                end = indexes.get(--j);
            }
        }
        if (!cleanSplit) {
            //no good wrap point found
            end = shortenedText.length();
            while (getTextWidth(font, fontSize, shortenedText.substring(start, end)) > allowedWidth) {
                end--;
            }
        }
        String part1;
        String part2;
        if (cleanSplit) {
            part1 = shortenedText.substring(start, end).trim();
            part2 = shortenedText.substring(end + 1, shortenedText.length()).concat(endPart).trim();
        } else {
            part1 = shortenedText.substring(start, end - 1).concat("-").trim();
            part2 = shortenedText.substring(end - 1, shortenedText.length()).concat(endPart).trim();
        }
        return new String[]{part1, part2};
    }

    public static float getTextWidth(PDFont font, int fontSize, String text) {
        try {
            return font.getStringWidth(text) / 1000F * (float) fontSize;
        } catch (Exception e) {
            LOG.warn("Could not calculate string length: " + e.getClass() + " - " + e.getMessage());
            return 0;
        }
    }

    public static float nextLineY(int currentY, int fontSize, float lineHeightD) {
        return currentY - fontSize - lineHeightD;
    }
}

