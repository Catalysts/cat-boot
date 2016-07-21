package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.elements.ReportImage;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import cc.catalysts.boot.report.pdf.utils.ReportFooterOnPages;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public class DemoTest {

    @Test
    public void demo() throws Exception {
        // these objects can also be injected via Spring:
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        final PdfReportFilePrinter pdfReportFilePrinter = new PdfReportFilePrinter();

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        styleSheet.setBodyText(new PdfTextStyle(10, PDType1Font.HELVETICA, Color.BLACK));

        BufferedImage img = null;
        try{
            img = ImageIO.read(new ClassPathResource("/github_icon.png").getInputStream());
        }catch(IOException e){

        }

        final PdfReport pdfReport = pdfReportService.createBuilder(styleSheet)
                .addHeading("Dear Github users")
                .addText("In this simple showcase you see most of the cool features that you can do with cat-boot-report.pdf. " +
                        "This framework is not a full-fledged reporting engine, but it should help you in printing simple reports " +
                        "for your Java apps without digging into complicated reporting engines.")
                .beginNewSection("Table", false)
                .addText("You can not only add text, but also tables:")
                .addPadding(10)
                .startTable()
                .addColumn("COL1", 2).addColumn("COL2", 2).addColumn("COL3", 4)
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x1"), new ReportImage(img, img.getWidth(), img.getHeight()), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "x3"))
                .createRow().withValues(new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y1"), new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), "y3"), new ReportTableBuilderImpl(styleSheet, null)
                        .addColumn("1", 1).addColumn("2", 3).createRow().withValues("z1", "z2").build())
                .endTable()
                .beginNewSection("Formatting", false)
                .addText("You can also format text as you can see here.", new PdfTextStyle(13, PDType1Font.TIMES_BOLD_ITALIC, Color.BLUE))
                .beginNewSection("Images", false)
                .addText("Images are also supported out-of-the-box:")
                .addPadding(10)
                .addImage(new ClassPathResource("github_icon.png"), 100, 100)
                .withFooterOnAllPages("Demo-PDF", "cat-boot-report-pdf", PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                        + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL)
                .withHeaderOnPages("Demo-PDF", "cat-boot-report-pdf", PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                        + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL, ReportFooterOnPages.ALL_BUT_FIRST)
                .buildReport("demo.pdf",
                        PdfPageLayout.getPortraitA4Page(),
                        new ClassPathResource("demo-template.pdf"));


        pdfReport.getDocument().save("demo.pdf");

        final File target = new File("pdf-out");
        Assert.assertTrue(target.mkdirs());
        pdfReportFilePrinter.print(pdfReport, target);

    }
}