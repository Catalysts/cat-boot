package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import cc.catalysts.boot.report.pdf.utils.PdfFontContext;
import com.google.common.collect.Lists;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public class ExportReusingStylesheetTest {

    private final DefaultPdfStyleSheet sharedStyleSheet = new DefaultPdfStyleSheet();
    private PdfReportBuilder pdfReportBuilder;
    private PdfTextStyle textStyle;
    private PdfReportFilePrinter pdfReportFilePrinter;
    private File target;
    private final PDColor BLACK = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

    private void setup() {
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(sharedStyleSheet);
        pdfReportFilePrinter = new PdfReportFilePrinter();

        textStyle = new PdfTextStyle(10, PdfFont.HELVETICA, BLACK, "regular");
        sharedStyleSheet.setBodyText(textStyle);

        pdfReportBuilder = pdfReportService.createBuilder(sharedStyleSheet);
        PdfFontContext fontContext = pdfReportBuilder.getFontContext();
        fontContext.setFallbackFonts(Lists.newArrayList(
                fontContext.getInternalFont("HanaMinA")
        ));

        target = new File("pdf-out");
        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }
    }

    @Test
    public void testMultipleExportsFromSameThread() throws IOException {
        // export 1
        this.setup();

        pdfReportBuilder.addElement(new ReportTextBox(textStyle, 1.f, "And some other special characters: 鸡汤"));
        printReport("export-fontembed-1.pdf");

        // export 2
        this.setup();

        pdfReportBuilder.addElement(new ReportTextBox(textStyle, 1.f, "And some other special characters: 鸡汤"));
        printReport("export-fontembed-2.pdf");
    }

    private void printReport(String fileName) throws IOException {
        final PdfReport pdfReport = pdfReportBuilder.buildReport(fileName,
                PdfPageLayout.getPortraitA4Page(), null);

        pdfReportFilePrinter.print(pdfReport, target);
    }
}