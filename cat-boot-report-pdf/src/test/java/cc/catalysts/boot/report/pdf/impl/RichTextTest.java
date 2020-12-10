package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportLink;
import cc.catalysts.boot.report.pdf.elements.ReportRichTextBox;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import cc.catalysts.boot.report.pdf.utils.PdfFontContext;
import com.google.common.collect.Lists;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public class RichTextTest {

    DefaultPdfStyleSheet styleSheet;
    private PdfReportBuilder pdfReportBuilder;
    private PdfTextStyle textStyle;
    private PdfReportFilePrinter pdfReportFilePrinter;
    private File target;
    private final PDColor BLACK = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

    @Before
    public void before() {
        styleSheet = new DefaultPdfStyleSheet();
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(styleSheet);
        pdfReportFilePrinter = new PdfReportFilePrinter();

        textStyle = new PdfTextStyle(10, PdfFont.HELVETICA, BLACK, "regular");
        styleSheet.setBodyText(textStyle);

        pdfReportBuilder = pdfReportService.createBuilder(styleSheet);

        target = new File("pdf-out");
        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }
    }

    @Test
    public void simpleText() throws Exception {
        String text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores" +
                " et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" +
                "+Lorem+ ipsum dolor *sit amet*, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores " +
                "et ea rebum. Stet clita kasd +gubergren+, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-simpleText.pdf");
    }

    @Test
    public void boldText() throws Exception {
        String text = "Lorem ipsum dolor sit amet, consetetur *sadipscing* elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores" +
                " et ea rebum. Stet clita kasd gubergren, no *sea* takimata sanctus est Lorem ipsum dolor sit amet. " +
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores " +
                "et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-boldText.pdf");
    }

    @Test
    public void underlineText() throws Exception {
        String text = "Lorem ipsum dolor sit amet, consetetur +sadipscing+ elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores" +
                " et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. " +
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores " +
                "et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-underlineText.pdf");
    }

    @Test
    public void specialText() throws Exception {
        String text = "Lorem ipsum dolor sit amet, consetetur +sadipscing+ elitr, sed diam nonumy eirmod *tempor invidunt*, ut " +
                "*laboreetdoloremagna*, magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores" +
                " et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. " +
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut " +
                "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores " +
                "et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-specialText.pdf");
    }

    @Test
    public void longText() throws Exception {
        String text = "Loremipsumdolorsitametconsetetursadipscingelitrseddiamnonumyeirmodtemporinviduntutlaboreetdoloremagnamagnaaliquyameratseddiamvoluptua.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-longText.pdf");
    }

    @Test
    public void longText2() throws Exception {
        String text = "*bold* Loremipsumdolorsitametconsetetursadipscingelitrseddiamnonumyeirmodtemporinviduntutlaboreetdoloremagnamagnaaliquyameratseddiamvoluptua.";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-longText2.pdf");
    }

    @Test
    public void specialCharacters() throws IOException {
        String text = "This is some \u2009text with \u2010strange \u2192 characters " +
                "\u25FB which cannot be \u0009 printed \u0308 " +
                " this tests removal via cc.catalysts.boot.report.pdf.utils.Utf8Utils";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-specialCharacters.pdf");
    }

    @Test
    public void unicodeCharactersEmojisChinese() throws IOException {
        final PdfFontContext context = pdfReportBuilder.getFontContext();
        context.setFallbackFonts(Lists.newArrayList(
                context.getInternalFont("Symbola"),
                context.getInternalFont("HanaMinA")
        ));

        String text = getUnicodeTestText();

        pdfReportBuilder.addElement(new ReportTextBox(textStyle, 1.f, text));

        printReport("rt-emojiAndUnicodeChars.pdf");
    }

    @Test
    public void unicodeCharactersViaExceptionHandler() throws IOException {
        pdfReportBuilder.getFontContext().registerFontEncodingExceptionHandler((text, codepoint, start, end) ->
                text.replace(codepoint, "_")
        );

        String text = getUnicodeTestText();
        pdfReportBuilder.addElement(new ReportTextBox(textStyle, 1.f, text));

        printReport("rt-emojiAndUnicodeChars-fallbackHandled.pdf");
    }

    private String getUnicodeTestText() {
        String woman = "\uD83D\uDC69";
        String modifier = "\uD83C\uDFFD";
        String zeroJoin = "\u200D";
        String computer = "\uD83D\uDCBB";

        return "---=== EMOJI TEST ===---\n" +
                "Simple emoji: \uD83D\uDE0B\n" +
                "Other emoji: \uD83D\uDC6B\n" +
                "More emoji: " + woman + "\n" +
                "With modifier: " + woman + modifier + "\n" +
                "Modifier with joiner: " + woman + zeroJoin + modifier + "\n" +
                "Two emojis with joiner: " + woman + zeroJoin + computer + "\n" +
                "Emoji with modifier and other joined emoji: " + woman + modifier + zeroJoin + computer + "\n" +
                "And some other special characters: 鸡汤" +
                "";
    }

    @Test
    public void textStyles() throws Exception {
        String text = "*bold* _italic_ +underline+";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-textStyles.pdf");
    }

    @Test
    public void notTextStyles() throws Exception {
        String text = "*notBold not_italic notUnderline+";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-notTextStyles.pdf");
    }

    @Test
    public void textWithLinks() throws IOException {
        pdfReportBuilder.addElement(new ReportLink("LinkText", "http://www.pdfbox.org", textStyle, 1.f));

        printReport("rt-linkText.pdf");
    }

    private void printReport(String fileName) throws IOException {
        final PdfReport pdfReport = pdfReportBuilder.buildReport(fileName,
                PdfPageLayout.getPortraitA4Page(), null);

        pdfReportFilePrinter.print(pdfReport, target);
    }
}