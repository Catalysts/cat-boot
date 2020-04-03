package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportImage;
import cc.catalysts.boot.report.pdf.elements.ReportTable;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import cc.catalysts.boot.report.pdf.utils.PositionOfStaticElements;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sfarcas on 7/21/2016.
 */
public class Demo2Test {

    private final PDColor BLACK = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
    private final PDColor BLUE = new PDColor(new float[]{0.0f, 0.0f, 1.0f}, PDDeviceRGB.INSTANCE);

    @Test
    public void demo() throws Exception {
        ReportTable.setLayoutingAssertionsEnabled(true);
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        final PdfReportFilePrinter pdfReportFilePrinter = new PdfReportFilePrinter();

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();

        final PdfReportBuilder builder = pdfReportService.createBuilder(styleSheet);

        // test custom fonts from resources folder
        styleSheet.setBodyText(new PdfTextStyle(10, PdfFont.getFont("Noto Sans"), BLACK, "regular"));
        styleSheet.setTableBodyText(new PdfTextStyle(10, PdfFont.getFont("Noto Sans"), BLACK, "regular"));

        BufferedImage img = ImageIO.read(new ClassPathResource("/github_icon.png").getInputStream());

        ReportTable sampleTable = new ReportTableBuilderImpl(styleSheet, null)
                .addColumn("1", 1).addColumn("2", 3).createRow().withValues("z1", "z2").build();
        sampleTable.setBorder(true);
        sampleTable.setNoInnerBorders(true);

        final PdfReport pdfReport = builder
                .addHeading("Dear Github users")
                .addText("In this simple showcase you see most of the cool features that you can do with cat-boot-report.pdf. " +
                        "This framework is not a full-fledged reporting engine, but it should help you in printing simple reports " +
                        "for your Java apps without digging into complicated reporting engines.")
                .beginNewSection("Tables", false)
                .addText("You can not only add text, but also tables:")
                .addPadding(10)
                .startTable()
                .addColumn("COL1", 2).addColumn("Image", 2).addColumn("COL3", 4)
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x1"), new ReportImage(img, 1000, 1000), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "image on the left should be auto-shrunk to fit"))
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x1"), new ReportImage(img, 50, 50), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "image on the left should not get enlarged to fill the cell (should be smaller than the above image)"))
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y1"), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y3"), sampleTable)
                .endTable()
                .addText("\n\nTable with inner horizontal borders only:")
                .startTable()
                .addColumn("COL1", 1)
                .addColumn("COL2", 1)
                .createRow().withValues("R1-C1", "R1-C2")
                .createRow().withValues("R2-C1", "R2-C2")
                .createRow().withValues("R3-C1", "R3-C2")
                .endTable((tableBuilder) -> {
                    final ReportTable table = tableBuilder.build();
                    table.setNoBottomBorder(true);
                    table.setNoTopBorder(true);
                    table.setDrawInnerHorizontal(true);
                    table.setDrawInnerVertical(false);
                    table.setDrawOuterVertical(false);
                    table.setCellPaddingX(5);
                    table.setCellPaddingY(5);

                    return table;
                })
                .addText("\n\nTable with inner vertical borders only:")
                .startTable()
                .addColumn("COL1", 1)
                .addColumn("COL2", 1)
                .addColumn("COL3", 1)
                .createRow().withValues("R1-C1", "R1-C2", "R1-C3")
                .createRow().withValues("R2-C1", "R2-C2", "R2-C3")
                .endTable((tableBuilder) -> {
                    final ReportTable table = tableBuilder.build();
                    table.setNoBottomBorder(true);
                    table.setNoTopBorder(true);
                    table.setDrawInnerHorizontal(false);
                    table.setDrawInnerVertical(true);
                    table.setDrawOuterVertical(false);
                    table.setCellPaddingX(5);
                    table.setCellPaddingY(5);

                    return table;
                })
                .beginNewSection("Formatting", false)
                .addText("You can also format text as you can see here.", new PdfTextStyle(13, PdfFont.TIMES_ROMAN, BLUE, "boldItalic"))
                .withFooterOnAllPages("Demo-PDF", "cat-boot-report-pdf", PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                        + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL)
                .withHeaderOnPages("Demo-PDF", "cat-boot-report-pdf", "not include me on first pageq", PositionOfStaticElements.ON_ALL_PAGES_BUT_FIRST)
                .buildReport("demo2.pdf",
                        PdfPageLayout.getPortraitA4Page(),
                        new ClassPathResource("demo-template.pdf"));

        final File target = new File("pdf-out");

        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }
        pdfReportFilePrinter.print(pdfReport, target);
    }

}
