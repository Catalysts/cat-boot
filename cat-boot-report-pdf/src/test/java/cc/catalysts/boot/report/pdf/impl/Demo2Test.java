package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sfarcas on 7/21/2016.
 */
public class Demo2Test {

    private final PDColor BLACK = new PDColor(new float[] {0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
    private final PDColor BLUE = new PDColor(new float[] {0.0f, 0.0f, 1.0f}, PDDeviceRGB.INSTANCE);

    @Test
    public void demo() throws Exception {
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        final PdfReportFilePrinter pdfReportFilePrinter = new PdfReportFilePrinter();

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        styleSheet.setBodyText(new PdfTextStyle(10, PdfFont.HELVETICA, BLACK, "regular"));

        BufferedImage img = null;
        try {
            img = ImageIO.read(new ClassPathResource("/github_icon.png").getInputStream());
        } catch (IOException e) {

        }

        ReportTable sampleTable = new ReportTableBuilderImpl(styleSheet, null)
                .addColumn("1", 1).addColumn("2", 3).createRow().withValues("z1", "z2").build();
        sampleTable.setBorder(true);
        sampleTable.setNoInnerBorders(true);

        final PdfReport pdfReport = pdfReportService.createBuilder(styleSheet)
                .addHeading("Dear Github users")
                .addText("In this simple showcase you see most of the cool features that you can do with cat-boot-report.pdf. " +
                        "This framework is not a full-fledged reporting engine, but it should help you in printing simple reports " +
                        "for your Java apps without digging into complicated reporting engines.")
                .beginNewSection("Table", false)
                .addText("You can not only add text, but also tables:")
                .addPadding(10)
                .startTable()
                .addColumn("COL1", 2).addColumn("Image", 2).addColumn("COL3", 4)
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x1"), new ReportImage(img, img.getWidth(), img.getHeight()), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x3"))
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y1"), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y3"), sampleTable)
                .endTable()
                .beginNewSection("Formatting", false)
                .addText("You can also format text as you can see here.", new PdfTextStyle(13, PdfFont.TIMES_ROMAN, BLUE, "boldItalic"))
                .withFooterOnAllPages("Demo-PDF", "cat-boot-report-pdf", PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                        + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL)
                .withHeaderOnPages("Demo-PDF", "cat-boot-report-pdf", "not include me on first pageq", PositionOfStaticElements.ON_ALL_PAGES_BUT_FIRST)
                .buildReport("demo2.pdf",
                        PdfPageLayout.getPortraitA4Page(),
                        new ClassPathResource("demo-template.pdf"));


        pdfReport.getDocument().save("demo2.pdf");

        final File target = new File("pdf-out2");

        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }
        pdfReportFilePrinter.print(pdfReport, target);

    }

}
