package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.exception.PdfBoxHelperException;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class PdfBoxHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PdfBoxHelper.class);
    private static final char FALLBACK_CHAR = '?';


    private PdfBoxHelper() {
    }

    /**
     * Calculates the position where a string needs to be drawn in order to conform to the alignment
     *
     * @param x the desired position, will be corrected as necessary
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

    private static float calculateAlignBlockPosition(float x, ReportAlignType align, float totalWidth, float allowedWidth) {
        switch (align) {
            case LEFT:
                return x;
            case RIGHT:
                return x + allowedWidth - totalWidth;
            case CENTER:
                float halfW = totalWidth / 2F;
                float absoluteCenter = allowedWidth / 2F + x;
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
     * @param allowedWidth max width of text (where to wrap)
     * @param lineHeightD  line height delta of text (line height will be: fontSize + this)
     * @param text         text
     * @param underline    true to underline the text
     * @return ending Y position of this line
     */
    public static float addText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text, boolean underline) {
        String fixedText = text;
        if (textConfig.getFont() == null || textConfig.getFont().getFontEncoding() instanceof WinAnsiEncoding) {
            // only necessary if the font doesn't support unicode
            fixedText = fixString(text);
        }

        float nextLineY = nextLineY((int) textY, textConfig.getFontSize(), lineHeightD);
        try {
            String[] split = splitText(textConfig.getFont(), textConfig.getFontSize(), allowedWidth, fixedText);
            float x = calculateAlignPosition(textX, align, textConfig, allowedWidth, split[0]);
            if (!underline) {
                addTextSimple(stream, textConfig, x, nextLineY, split[0]);
            } else {
                addTextSimpleUnderlined(stream, textConfig, x, nextLineY, split[0]);
            }
            if (!StringUtils.isEmpty(split[1])) {
                return addText(stream, textConfig, textX, nextLineY, allowedWidth, lineHeightD, align, split[1], underline);
            } else {
                return nextLineY;
            }

        } catch (Exception e) {
            LOG.warn("Could not add text: " + e.getClass() + " - " + e.getMessage());
            return textY;
        }
    }

    /**
     * Compatibility overload for {@link #addText}.
     */
    public static float addText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text) {
        return addText(stream, textConfig, textX, textY, allowedWidth, lineHeightD, align, text, false);
    }

    private static String generalizeLineSeparators(String text) {
        String generalizedString = text.replace("\r\n", "\n");
        if (System.lineSeparator().equals("\r\n")) {
            generalizedString = generalizedString.replace("\n", "\r\n");
        }
        return generalizedString;
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
        private boolean underlined;

        public TextSegment(String text, PdfTextStyle style) {
            this(text, style, false);
        }

        public TextSegment(String text, PdfTextStyle style, boolean underline) {
            this.text = text;
            this.style = style;
            this.underlined = underline;
        }

        public TextSegment clone() {
            return new TextSegment(text, style, underlined);
        }

        public float getTextWidth() {
            return PdfBoxHelper.getTextWidth(style.getFont(), style.getFontSize(), text);
        }

        public boolean isUnderlined() {
            return underlined;
        }

        public void setUnderlined(boolean underlined) {
            this.underlined = underlined;
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

    private static List<TextSegment> findTextSegments(PdfTextStyle bodyText, String str) {
        List<TextSegment> segments = new ArrayList<>();

        if (str.isEmpty()) {
            segments.add(new TextSegment("", bodyText));
            return segments;
        }

        List<Character> markdownChars = Arrays.asList('*', '+');
        List<Character> whiteSpaces = Arrays.asList(' ', '\r', '\n', '\t');

        //TODO: generalize bold font generation
        PdfTextStyle boldText = new PdfTextStyle(bodyText.getFontSize(), PDType1Font.HELVETICA_BOLD, bodyText.getColor());

        String temp = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // markdown character must not be followed by a whitespace character
            if (markdownChars.contains(c) && i < str.length() - 1 && !whiteSpaces.contains(str.charAt(i + 1))) {
                if (temp.length() > 0) {
                    segments.add(new TextSegment(temp, bodyText));
                }
                temp = "";

                int endIndex = -1;
                for (int end = i + 1; end < str.length(); end++) {
                    if (str.charAt(end) == c) {
                        if (whiteSpaces.contains(str.charAt(end - 1))) {
                            // for strings like '*not bold text *' we need to quit here
                            temp += c;
                            break;
                        }
                        endIndex = end;
                        break;
                    }
                }

                if (endIndex == -1) {
                    continue;
                }

                String stringSegment = str.substring(i + 1, endIndex);
                List<TextSegment> subSegments = findTextSegments(bodyText, stringSegment);
                segments.addAll(subSegments);

                // manipulate segments accordingly
                for (TextSegment segment : subSegments) {
                    switch (c) {
                        case '*':
                            segment.setStyle(boldText);
                            break;
                        case '+':
                            segment.setUnderlined(true);
                            break;
                    }
                }

                i = endIndex;
            } else {
                temp += c;
            }
        }
        if (temp.length() > 0) {
            segments.add(new TextSegment(temp, bodyText));
        }

        return segments;
    }

    public static float addRichText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text) {
        String[] lines = generalizeLineSeparators(text).split(System.lineSeparator());

        float currX = textX, currY = textY;
        for (String line : lines) {
            List<TextSegment> segments = findTextSegments(textConfig, replaceBulletPoints(line));

            List<TextSegment> row;
            float totalRowWidth;

            while (segments.size() > 0) {
                row = new ArrayList<>();
                totalRowWidth = 0;

                TextSegment seg;
                for (int i = 0; i < segments.size(); i++) {
                    seg = segments.get(i);

                    if (totalRowWidth + seg.getTextWidth() > allowedWidth) {
                        String[] splitted = splitText(seg.getStyle().getFont(), seg.getStyle().getFontSize(), allowedWidth - totalRowWidth, seg.getText());
                        row.add(new TextSegment(splitted[0], seg.getStyle(), seg.isUnderlined()));
                        totalRowWidth += row.get(row.size() - 1).getTextWidth();
                        seg.setText(splitted[1]);
                        break;
                    } else {
                        row.add(seg);
                        totalRowWidth += seg.getTextWidth();
                    }
                }
                segments.removeAll(row);

                currX = calculateAlignBlockPosition(currX, align, totalRowWidth, allowedWidth);
                for (TextSegment segment : row) {
                    addText(stream, segment.getStyle(), currX, currY, allowedWidth - (currX - textX), lineHeightD, ReportAlignType.LEFT, segment.getText(), segment.isUnderlined());
                    currX += segment.getTextWidth();
                }

                currY = nextLineY((int) currY, textConfig.getFontSize(), lineHeightD);
                currX = textX;
            }
        }

        return currY;
    }

    public static float addTextWithLineBreaks(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text) {
        String[] lines = generalizeLineSeparators(text).split(System.lineSeparator());

        float currY = textY;
        for (String line : lines) {
            currY = addText(stream, textConfig, textX, currY, allowedWidth, lineHeightD, align, line);
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

    public static void addTextSimpleUnderlined(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, String text) {
        addTextSimple(stream, textConfig, textX, textY, text);
        try {
            float lineOffset = textConfig.getFontSize() / 8F;
            stream.setStrokingColor(textConfig.getColor());
            stream.setLineWidth(0.5F);
            stream.drawLine(textX, textY - lineOffset, textX + getTextWidth(textConfig.getFont(), textConfig.getFontSize(), text), textY - lineOffset);
            stream.stroke();
        } catch (IOException e) {
            e.printStackTrace();
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
        String endPart = "";
        String shortenedText = text;

        // look for manual line breaks which have priority
        List<String> breakSplitted = Arrays.asList(shortenedText.split("(\\r\\n)|(\\n)|(\\n\\r)")).stream().collect(Collectors.toList());
        if (breakSplitted.size() > 1) {
            // be sure that there do not have to be some breaks before \n
            String[] splittedFirst = splitText(font, fontSize, allowedWidth, breakSplitted.get(0));
            // concat remaining strings incl. (removed) linebreaks
            StringBuilder remaining = new StringBuilder(splittedFirst[1] == null ? "" : splittedFirst[1] + "\n");
            breakSplitted.stream().skip(1).forEach(s -> remaining.append(s + "\n"));
            remaining.deleteCharAt(remaining.length() - 1);
            return new String[]{splittedFirst[0], remaining.toString()};
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
            part1 = shortenedText.substring(start, end).replaceAll("\\s+$", "");
            part2 = shortenedText.substring(end + 1, shortenedText.length()).concat(endPart).replaceAll("^\\s+", "");
        } else {
            part1 = shortenedText.substring(start, end - 1).concat("-").replaceAll("\\s+$", "");
            part2 = shortenedText.substring(end - 1, shortenedText.length()).concat(endPart).replaceAll("^\\s+", "");
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

