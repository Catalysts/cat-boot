package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.exception.PdfBoxHelperException;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
     * Calculates the position whee a string needs to be drawn in order to conform to the alignment
     *
     * @param x     the desired position, will be corrected as necessary
     * @return the corrected position
     */
    private static float calculateAlignPosition(float x, ReportAlignType align, PdfTextStyle textConfig, float allowedWidth, String text) {
        switch (align) {
            case LEFT:
                return x;
            case RIGHT:
                float w = getTextWidth(textConfig.getFont(), textConfig.getFontSize(), text);
                return x + allowedWidth - w;
            case CENTER:
                float halfW = getTextWidth(textConfig.getFont(), textConfig.getFontSize(), text) / 2;
                float absoluteCenter = allowedWidth / 2 + x;
                return absoluteCenter - halfW;
            default:
                throw new IllegalArgumentException("Align type " + align + " not implemented for text");
        }
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
            float x = calculateAlignPosition(textX, align, textConfig, allowedWidth, text);
            addTextSimple(stream, textConfig, x, nextLineY, split[0]);
            if (!StringUtils.isEmpty(split[1])) {
                return addText(stream, textConfig, textX, nextLineY, allowedWidth, lineHeightD, align, split[1]);
            } else {
                return nextLineY;
            }

        } catch (Exception e)  {
            LOG.warn("Could not add text: " + e.getClass() + " - " + e.getMessage());
            return textY;
        }
    }


    private static String replaceBulletPoints(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '-') {
                return str.substring(0, i) + "•" + str.substring(i + 1);
            }

            if (str.charAt(i) != ' ' && str.charAt(i) != '\t') {
                break;
            }
        }
        return str;
    }

    private static class TextSegment {
        private String text;
        private PdfTextStyle style;

        public TextSegment(String text, PdfTextStyle style) {
            this.text = text;
            this.style = style;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public PdfTextStyle getStyle() {
            return style;
        }

        public void setStyle(PdfTextStyle style) {
            this.style = style;
        }
    }

    private static ArrayList<TextSegment> findTextSegments(PdfTextStyle bodyText, String str) {
        ArrayList<TextSegment> segments = new ArrayList<>();

        if(str.isEmpty()) {
            segments.add(new TextSegment("", bodyText));
            return segments;
        }

        //TODO: generalize bold font generation
        PdfTextStyle boldText = new PdfTextStyle(bodyText.getFontSize(), PDType1Font.HELVETICA_BOLD, bodyText.getColor());

        String temp = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '*' && i < str.length() - 1 && str.charAt(i + 1) != ' ') {
                // greedily search for closing asterisk
                int index = -1;

                for (int j = i + 1; j < str.length(); j++) {
                    if (str.charAt(j) == '*') {
                        if (str.charAt(j - 1) == ' ') {
                            index = -1;
                            break;
                        }
                        index = j;
                    }
                }

                if (index != -1) {
                    if (!temp.isEmpty()) {
                        segments.add(new TextSegment(temp, bodyText));
                        temp = "";
                    }
                    segments.add(new TextSegment(str.substring(i + 1, index), boldText));
                    i = index;
                } else {
                    temp += c;
                }
            } else {
                temp += c;
            }
        }

        if (!temp.isEmpty()) {
            segments.add(new TextSegment(temp, bodyText));
        }

        return segments;
    }

    public static float addRichText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text) {
        String[] lines = text.split(System.lineSeparator());

        float currX = textX, currY = textY;
        for(String line : lines) {
            ArrayList<TextSegment> segments = findTextSegments(textConfig, replaceBulletPoints(line));
            float totalLineWidth = 0;
            for(TextSegment seg : segments) {
                addText(stream, seg.getStyle(), currX, currY, allowedWidth, lineHeightD, align, seg.getText());
                float segWidth =getTextWidth(seg.getStyle().getFont(), seg.getStyle().getFontSize(), seg.getText());
                currX += segWidth;
                totalLineWidth += segWidth;
            }

            // HACK
            int extraLineBreaks = (int)(totalLineWidth / allowedWidth);

            currY = nextLineY((int)currY, textConfig.getFontSize(), lineHeightD);
            while(extraLineBreaks --> 0) {
                currY = nextLineY((int)currY, textConfig.getFontSize(), lineHeightD);
            }

            currX = textX;
        }

        return currY;
    }

    private static String fixString(final String original) {
        StringBuilder sb = new StringBuilder();
        try {
            for (char ch : original.toCharArray()) {
                if (WinAnsiEncoding.INSTANCE.hasNameForCode(ch)) {
                    sb.append(ch);
                } else {
                    switch (ch) {
                        case (char) 8220:
                        case (char) 8222:
                            sb.append('"');
                            break;
                        case (char) 8230:
                            sb.append("...");
                            break;
                        case (char) 8364: // euro sign
                            sb.append((char) 128); // see http://stackoverflow.com/questions/22260344/pdfbox-encode-symbol-currency-euro
                            break;
                        case (char) 8226: // bullet point
                            sb.append((char) 149);
                            break;
                        case (char) 8211: // endash
                            sb.append((char) 150);
                            break;
                        default:
                            String decoded = Normalizer.normalize(String.valueOf(ch), Normalizer.Form.NFD);
                            char decodedChar = decoded != null && decoded.length() > 0 ? decoded.charAt(0) : FALLBACK_CHAR;
                            if (WinAnsiEncoding.INSTANCE.getCharacter(decodedChar) != null) {
                                sb.append(decodedChar);
                            } else {
                                sb.append(FALLBACK_CHAR);
                            }
                            break;
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

