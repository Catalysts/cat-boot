package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.exception.PdfBoxHelperException;
import cc.catalysts.boot.report.pdf.utils.PdfFontContext;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import cc.catalysts.boot.report.pdf.utils.Utf8Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PdfBoxHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PdfBoxHelper.class);

    private static final char BOLD_MARKDOWN = '*';
    private static final char ITALIC_MARKDOWN = '_';
    private static final char UNDERLINED_MARKDOWN = '+';

    private static LoadingCache<PDFont, Map<Character, Float>> fontSizeMapCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<PDFont, Map<Character, Float>>() {

                @Override
                public Map<Character, Float> load(PDFont font) throws Exception {
                    return new HashMap<>();
                }
            });

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
                float w = getTextWidth(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), text);
                return x + allowedWidth - w;
            case CENTER:
                float halfW = getTextWidth(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), text) / 2;
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

        float nextLineY = nextLineY(textY, textConfig.getFontSize(), lineHeightD);

        if (text.equals("")) {
            addTextSimple(stream, textConfig, textX, nextLineY, "");
            return nextLineY;
        }

        try {
            String[] split = splitText(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), allowedWidth, Utf8Utils.removeCharactersWithZeroLength(text));
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
            return PdfBoxHelper.getTextWidth(style.getCurrentFontStyle(), style.getFontSize(), text);
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

    private static List<TextSegment> findTextSegments(PdfTextStyle bodyText, String str, String boldFontStyle, String italicFontStyle) {
        List<TextSegment> segments = new ArrayList<>();

        if (str.isEmpty()) {
            segments.add(new TextSegment("", bodyText));
            return segments;
        }

        List<Character> markdownChars = Arrays.asList(BOLD_MARKDOWN, ITALIC_MARKDOWN, UNDERLINED_MARKDOWN);
        List<Character> whiteSpaces = Arrays.asList(' ', '\r', '\n', '\t');

        PdfTextStyle boldText = new PdfTextStyle(bodyText.getFontSize(), bodyText.getFont(), bodyText.getColor(), boldFontStyle);
        PdfTextStyle italicText = new PdfTextStyle(bodyText.getFontSize(), bodyText.getFont(), bodyText.getColor(), italicFontStyle);

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
                    // there is no end markdown char, so the start markdown char should be treated as a normal char
                    temp += c;
                    continue;
                }

                String stringSegment = str.substring(i + 1, endIndex);
                List<TextSegment> subSegments = findTextSegments(bodyText, stringSegment, boldFontStyle, italicFontStyle);
                segments.addAll(subSegments);

                // manipulate segments accordingly
                for (TextSegment segment : subSegments) {
                    switch (c) {
                        case BOLD_MARKDOWN:
                            segment.setStyle(boldText);
                            break;
                        case UNDERLINED_MARKDOWN:
                            segment.setUnderlined(true);
                            break;
                        case ITALIC_MARKDOWN:
                            segment.setStyle(italicText);
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
        return addRichText(stream, textConfig, textX, textY, allowedWidth, lineHeightD, align, text, "bold", "italic");
    }

    public static float addRichText(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, float allowedWidth, float lineHeightD, ReportAlignType align, String text, String boldFontStyle, String italicFontStyle) {
        String[] lines = generalizeLineSeparators(Utf8Utils.removeCharactersWithZeroLength(text)).split(System.lineSeparator());

        float currX = textX, currY = textY;
        for (String line : lines) {
            List<TextSegment> segments = findTextSegments(textConfig, replaceBulletPoints(line), boldFontStyle, italicFontStyle);

            List<TextSegment> row;
            float totalRowWidth;

            while (segments.size() > 0) {
                row = new ArrayList<>();
                totalRowWidth = 0;

                TextSegment seg;
                for (int i = 0; i < segments.size(); i++) {
                    seg = segments.get(i);

                    if (totalRowWidth + seg.getTextWidth() > allowedWidth) {
                        String[] splitted = splitText(seg.getStyle().getCurrentFontStyle(), seg.getStyle().getFontSize(), allowedWidth - totalRowWidth, seg.getText());
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
        List<PDFont> fonts = getPossibleFonts(textConfig.getCurrentFontStyle());
        final List<TextSegmentWithFont> segments = splitTextWithFallbackFonts(fonts, text);
        for (TextSegmentWithFont segment : segments) {
            try {
                stream.setFont(segment.font, textConfig.getFontSize());
                stream.setNonStrokingColor(textConfig.getColor());
                stream.beginText();
                stream.setTextMatrix(new Matrix(1, 0, 0, 1, textX, textY));
                stream.showText(segment.text);
                textX += fontMeasurementToPosition(segment.font.getStringWidth(segment.text), textConfig.getFontSize());
            } catch (Exception e) {
                LOG.warn("Could not add text: " + e.getClass() + " - " + e.getMessage());
            } finally {
                try {
                    stream.endText();
                } catch (IOException e) {
                    LOG.error("Error while generating text {}: {}", text, e.getMessage(), e);
                }
            }
        }
    }

    public static void addTextSimpleUnderlined(PDPageContentStream stream, PdfTextStyle textConfig, float textX, float textY, String text) {
        addTextSimple(stream, textConfig, textX, textY, text);
        try {
            float lineOffset = textConfig.getFontSize() / 8F;
            stream.setStrokingColor(textConfig.getColor());
            stream.setLineWidth(0.5F);
            stream.moveTo(textX, textY - lineOffset);
            stream.lineTo(textX + getTextWidth(textConfig.getCurrentFontStyle(), textConfig.getFontSize(), text), textY - lineOffset);
            stream.stroke();
        } catch (IOException e) {
            LOG.error("Error while underlining text {}: {}", text, e.getMessage(), e);
        }
    }

    private static List<Integer> getWrapableIndexes(String text) {
        List<Integer> list = new ArrayList<>();
        Pattern pt = Pattern.compile("\\s+");
        Matcher m = pt.matcher(text);
        while (m.find()) {
            list.add(m.start());
        }
        list.add(text.length());
        return list;
    }

    public static String[] splitText(PDFont font, float fontSize, float allowedWidth, String text) {
        String endPart = "";

        text = Utf8Utils.removeCharactersWithZeroLength(text);
        text = text.replaceAll("\\r", "");

        // look for manual line breaks which have priority
        List<String> breakSplitted = Arrays.asList(text.split("\\n"));
        if (breakSplitted.size() > 1) {
            // be sure that there do not have to be some breaks before \n
            String[] splittedFirst = splitText(font, fontSize, allowedWidth, breakSplitted.get(0));
            // concat remaining strings incl. (removed) linebreaks
            StringBuilder remaining = new StringBuilder(splittedFirst[1] == null ? "" : splittedFirst[1] + "\n");
            breakSplitted.stream().skip(1).forEach(s -> remaining.append(s + "\n"));
            remaining.deleteCharAt(remaining.length() - 1);
            return new String[]{splittedFirst[0], remaining.toString()};
        }

        if (text.indexOf(10) == -1 && getTextWidth(font, fontSize, text) <= allowedWidth) {
            return new String[]{text, null};
        }

        boolean cleanSplit = true;
        List<Integer> indexes = getWrapableIndexes(text);
        int start = 0;
        int j = indexes.size() - 1;
        int end = indexes.get(j);

        int lineBreakPos = text.indexOf(10);
        if (lineBreakPos != -1 && getTextWidth(font, fontSize, text.substring(start, lineBreakPos)) <= allowedWidth) {
            end = lineBreakPos;
        } else {
            while (getTextWidth(font, fontSize, text.substring(start, end)) > allowedWidth) {
                if (j == 0) {
                    cleanSplit = false;
                    break;
                }
                end = indexes.get(--j);
            }
        }

        String part1 = "";
        String part2 = text;
        if (cleanSplit) {
            part1 = text.substring(start, end).replaceAll("\\s+$", "");
            part2 = text.substring(end + 1).concat(endPart).replaceAll("^\\s+", "");
        } else {
            final String separator = "-";
            allowedWidth -= getTextWidth(font, fontSize, separator);

            //no good wrap point found
            end = text.length();
            while (end > 0 && getTextWidth(font, fontSize, text.substring(start, end)) > allowedWidth) {
                end--;
            }

            boolean splittable = end >= 2;
            if (splittable) {
                part1 = text.substring(start, end - 1).concat(separator).replaceAll("\\s+$", "");
                part2 = text.substring(end - 1).concat(endPart).replaceAll("^\\s+", "");
            }

            if (!splittable) {
                return new String[]{"", text};
            }
        }

        return new String[]{part1, part2};
    }

    private static Map<Character, Float> getFontSizeCache(PDFont font) {
        try {
            return fontSizeMapCache.get(font);
        } catch (ExecutionException e) {
            LOG.error("Error while getting font size cache data for {}: {}", font, e.getMessage(), e);
        }
        return new HashMap<>();
    }

    public static float getTextWidth(PDFont font, float fontSize, String text) {
        List<PDFont> fonts = getPossibleFonts(font);

        float maxSum = 0F;
        for (String line : text.split("\\n")) {
            final List<TextSegmentWithFont> segments = splitTextWithFallbackFonts(fonts, line);
            float sum = 0F;
            for (TextSegmentWithFont segment : segments) {
                float value = 0F;
                try {
                    value = segment.font.getStringWidth(segment.text);
                } catch (Exception e) {
                    LOG.warn("Could not calculate string length: " + e.getClass() + " - " + e.getMessage());
                    return 0;
                }
                sum += value;
            }

            maxSum = Math.max(maxSum, sum);
        }
        return fontMeasurementToPosition(maxSum, fontSize);
    }

    private static float fontMeasurementToPosition(float value, float fontSize) {
        return value / 1000F * fontSize;
    }

    private static List<PDFont> getPossibleFonts(PDFont font) {
        final PdfFontContext fontContext = PdfFontContext.current();
        List<PDFont> fonts;
        if (fontContext != null) {
            fonts = fontContext.getPossibleFonts(font);
        } else {
            fonts = new ArrayList<>();
            fonts.add(font);
        }
        return fonts;
    }

    private static List<TextSegmentWithFont> splitTextWithFallbackFonts(List<PDFont> fonts, String text) {
        List<TextSegmentWithFont> result = new ArrayList<>();
        if (text.length() > 0) {
            PDFont currentFont = null;
            int start = 0;
            for (int i = 0; i < text.length(); ) {
                int codePoint = text.codePointAt(i);
                int codeChars = Character.charCount(codePoint);
                String codePointString = text.substring(i, i + codeChars);
                boolean compatibleFontFound = false;
                for (PDFont font : fonts) {
                    try {
                        font.encode(codePointString);
                        compatibleFontFound = true;
                        if (font != currentFont) {
                            if (currentFont != null) {
                                result.add(new TextSegmentWithFont(currentFont, text.substring(start, i)));
                            }
                            currentFont = font;
                            start = i;
                        }
                        break;
                    } catch (Exception ioe) {
                        // font cannot encode codepoint
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("{} not found in font {}", codePointString, font);
                        }
                    }
                }
                if (!compatibleFontFound) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("None of these fonts: {} can encode: '{}'",
                                fonts.stream()
                                        .map(it -> it.getName())
                                        .collect(Collectors.joining(",")),
                                codePointString
                        );
                    }
                    text = callFontEncodingExceptionHandler(text, codePointString, start, i);
                } else {
                    i += codeChars;
                }
            }
            result.add(new TextSegmentWithFont(currentFont, text.substring(start)));
        }
        return result;
    }

    private static String callFontEncodingExceptionHandler(String text, String codePointString, int start, int end) {
        final PdfFontContext context = PdfFontContext.current();
        if (context != null) {
            final String newText = context.handleFontEncodingException(text, codePointString, start, end);
            if (newText.length() > text.length()) {
                throw new IllegalStateException("Exceptionhandler must not enlarge string as this causes layout bugs.\n" +
                        "Transformed:\n" +
                        text + "\n" +
                        "to:\n" +
                        newText);
            }
            return newText;
        }
        throw new PdfBoxHelperException("Cannot encode '" + codePointString + "'.");
    }

    private static class TextSegmentWithFont {
        final PDFont font;
        final String text;

        TextSegmentWithFont(PDFont font, String text) {
            this.font = font;
            this.text = text;
        }

    }

    public static float nextLineY(float currentY, float fontSize, float lineHeightD) {
        return currentY - fontSize - lineHeightD;
    }
}

