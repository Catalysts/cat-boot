package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportRichTextBox;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public class RichTextTest {

    private PdfReportBuilder pdfReportBuilder;
    private PdfTextStyle textStyle;
    private PdfReportFilePrinter pdfReportFilePrinter;
    private File target;

    @Before
    public void before() {
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        pdfReportFilePrinter = new PdfReportFilePrinter();

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        textStyle = new PdfTextStyle(10, PdfFont.HELVETICA, Color.BLACK, "regular");
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
        String text = "This is some \u2009text with \u2010strange characters\u25FB which cannot \u200B be printed";
        pdfReportBuilder.addElement(new ReportRichTextBox(textStyle, 1.f, text));

        printReport("rt-specialCharacters.pdf");
    }


    private void printReport(String fileName) throws IOException {
        final PdfReport pdfReport = pdfReportBuilder.buildReport(fileName,
                PdfPageLayout.getPortraitA4Page(), null);

        pdfReportFilePrinter.print(pdfReport, target);
    }
}