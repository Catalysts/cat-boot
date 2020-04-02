package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.ReportTableBuilder;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportTable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 * @author Klaus Lehner
 */
public class LinebreakTest {

    private final PDColor BLACK = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

    @Test
    public void demo() throws Exception {
        ReportTable.setLayoutingAssertionsEnabled(true);
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        final PdfReportFilePrinter pdfReportFilePrinter = new PdfReportFilePrinter();

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        styleSheet.setBodyText(new PdfTextStyle(10, PdfFont.HELVETICA, BLACK, "regular"));

        final ReportTableBuilder tableBuilder = pdfReportService.createBuilder(styleSheet)
                .addHeading("Table Linebreak Test")
                .beginNewSection("Table", false)
                .addPadding(10)
                .startTable()
                .addColumn("COL1", 2).addColumn("COL2", 2).addColumn("COL3", 4);

        for (int i = 0; i < 100; i++) {
            tableBuilder.createRow().withValues(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    "sooooooooooooooommmmmmmmmmmmmmmeeeeeeeeeeeeeeeeeeeeVeeeeeeeeeeeeeeeerrrrrrrrrrrrrryyyyyyyyyyyyyLooooooooooooooooooonnnnnnnnnnnnngggggggggggSiiiiiinnnnnggggggllllllleeeeeeeWooooooooorrrrrrdddddd",
                    "1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9"
            );
        }

        final PdfPageLayout layout = PdfPageLayout.getPortraitA4Page();
        layout.setMarginBottom(85);

        final PdfReport pdfReport = tableBuilder.endTable()
                .buildReport("table-lineAndPageBreaks.pdf",
                        layout,
                        new ClassPathResource("demo-template.pdf"));

        final File target = new File("pdf-out");

        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }

        pdfReportFilePrinter.print(pdfReport, target);
    }
}