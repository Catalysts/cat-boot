package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.*;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public class PdfReportStructureServiceTest {

    private PdfReportService pdfReportService;
    private static File outDirectory = new File("pdf-out");
    private final PDColor BLACK = new PDColor(new float[] {0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

    @BeforeClass
    public static void cleanupDirectory() throws IOException {
        if (outDirectory.exists()) {
            FileUtils.forceMkdir(outDirectory);
        }
        FileUtils.forceMkdir(outDirectory);
    }

    @Before
    public void before() {
        pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
    }

    @Test
    public void buildReport() throws IOException {
        PdfReportBuilder builder = pdfReportService.createBuilder();
        final PdfReport pdfReport = builder.beginNewSection("test", true)
                .beginNewSection("foo", true)
                .beginNewSection("bar", true)
                .buildReport("example.pdf", PdfPageLayout.getLandscapeA4Page(), null);
        new PdfReportFilePrinter().print(pdfReport, outDirectory);
        Assert.assertTrue(new File(outDirectory, "example.pdf").exists());
    }

    @Test
    public void generateAndSavePlainExample() throws Exception {
        final PdfReport pdfReport = createTestReport().buildReport("example-plain.pdf", PdfPageLayout.getPortraitA4Page(), null);

        new PdfReportFilePrinter().print(pdfReport, outDirectory);
    }

    @Test
    public void specialCharactersExample() throws Exception {
        final PdfReport pdfReport = pdfReportService.createBuilder()
                .addHeading("special chars test")
                .addText("start 1€ foo@bar.at öäü !\"§$%&%&//()=?`îôâ Ružomberok " + (char) 8220 + "123456789" + (char) 8222 + " - end")
                .buildReport("example-special.pdf", PdfPageLayout.getPortraitA4Page(), null);

        new PdfReportFilePrinter().print(pdfReport, outDirectory);
    }

    @Test
    public void generateAndSaveTemplateExample() throws Exception {
        Resource template = new ClassPathResource("template.pdf");
        final PdfReport pdfReport = createTestReport().buildReport("example-template.pdf", PdfPageLayout.getPortraitA4Page(), template);

        new PdfReportFilePrinter().print(pdfReport, outDirectory);
    }

    @Test
    public void generateAndSaveHeaderFooterSmallMarginExample() throws Exception {
        PdfReportBuilder report = createTestReport()
                .withHeaderOnAllPages("one", "two", "three")
                .withFooterOnAllPages("left", "center", "right: " + PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                        + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL);

        final PdfReport pdfReport = report.buildReport("example-header-footer.pdf", PdfPageLayout.getPortraitA4Page(), null);
        new PdfReportFilePrinter().print(pdfReport, outDirectory);
    }

    PdfReportBuilder createTestReport() {

        PdfStyleSheet styleSheet = new DefaultPdfStyleSheet();

        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            longText.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        }

        PdfTextStyle otherConfig = new PdfTextStyle(12, PdfFont.HELVETICA, BLACK, "bold");

        return pdfReportService.createBuilder()
                .beginNewSection("Test 1, Table", true)
                .startTable()
                .addColumn("COL1", 2).addColumn("COL2", 2).addColumn("COL3", 4)
                .createRow()
                .addValue("val1").addValue("val2").addValue("val3").endRow()
                .createRow().withValues("x1", "x2", "x3")
                .createRow().withValues("y1", "y2", "y3")
                .endTable()
                .beginNewSection("Test 2, Long text", true)
                .addElement(new ReportTextBox(otherConfig, 1, longText.toString()))
                .addText("testing default text")
                .addText(longText.toString(), otherConfig)
                .beginNewSection("Test 3, Sections without pagebreak", false)
                .addText("section text")
                .beginNewSection("Test 3, Sections without pagebreak", false)
                .addText("section text")
                .beginNewSection("Test 3, Sections without pagebreak", false)
                .addText("section text")
                .beginNewSection("Test 3, Sections without pagebreak", false)
                .addText("section text");
    }

}
