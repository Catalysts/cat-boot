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
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by sfarcas on 7/21/2016.
 */
public class TablePaddingTest {
    private static final Logger LOG = getLogger(TablePaddingTest.class);

    public static final int IMAGE_SIZE_TOO_BIG_FOR_CELL = 1000;
    public static final int IMAGE_SIZE_SMALL = 50;
    private final PDColor BLACK = new PDColor(new float[]{0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
    private final PDColor BLUE = new PDColor(new float[]{0.0f, 0.0f, 1.0f}, PDDeviceRGB.INSTANCE);

    PdfReportService pdfReportService;
    PdfReportFilePrinter pdfReportFilePrinter;

    @Before
    public void setup() {
        ReportTable.setLayoutingAssertionsEnabled(true);
        // these objects can also be injected via Spring:
        pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
        pdfReportFilePrinter = new PdfReportFilePrinter();
    }

    @Test
    public void demoSmallImages() throws Exception {
        final String reportFileName = "table-paddingTestSmallImages.pdf";

        generateTestReport(IMAGE_SIZE_SMALL, reportFileName);
    }

    @Test
    public void demoLargeShrinkingImages() throws Exception {
        final String reportFileName = "table-paddingTestLargeImages.pdf";

        generateTestReport(IMAGE_SIZE_TOO_BIG_FOR_CELL, reportFileName);
    }

    @Test
    public void threadSafetyTest() throws Exception {
        // increase locally for "harder" test - NOTE that the test will stop working if you go beyond
        //  the actual limit of threads that the executor will start in parallel because of the cyclicbarrier.
        final int parallelism = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
        final CyclicBarrier gate = new CyclicBarrier(parallelism + 1);
        for (int i = 0; i < parallelism; i++) {
            final int threadNumber = i;
            executorService.execute(() -> {
                try {
                    gate.await();
                    generateTestReport(1 + threadNumber, "parallel-report-" + threadNumber + ".pdf");
                } catch (Exception e) {
                    LOG.warn("Exception occured: {}", e.getMessage(), e);
                }
            });
        }

        // make all start at the same time
        gate.await();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    private void generateTestReport(float imageSize, String reportFileName) throws Exception {

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        styleSheet.setBodyText(new PdfTextStyle(10, PdfFont.HELVETICA, BLACK, "regular"));

        BufferedImage img = ImageIO.read(new ClassPathResource("/github_icon.png").getInputStream());

        ReportTable sampleTable = new ReportTableBuilderImpl(styleSheet, null)
                .addColumn("1", 1).addColumn("2", 3).createRow().withValues("z1", "z2").build();
        sampleTable.setBorder(true);
        sampleTable.setNoInnerBorders(true);

        final PdfReport pdfReport = pdfReportService.createBuilder(styleSheet)
                .addHeading("Dear Github users")
                .addText(
                        "In this simple showcase you see most of the cool features that you can do with cat-boot-report.pdf. "
                                +
                                "This framework is not a full-fledged reporting engine, but it should help you in printing simple reports "
                                +
                                "for your Java apps without digging into complicated reporting engines.")
                .beginNewSection("Tables", false)
                .addText("You can not only add text, but also tables:")
                .addPadding(10)
                .startTable()
                .addColumn("COL1", 2)
                .addColumn("Image", 2)
                .addColumn("COL3", 4)
                .createRow().withValues(textBox(styleSheet, "1"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "2"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "3"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "4"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "5"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "6"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .endTable((tableBuilder) -> {
                    final ReportTable table = tableBuilder.build(false, true);
                    table.setCellPaddingX(0);
                    table.setCellPaddingY(0);
                    return table;
                })
                .beginNewSection("Test 2:", true)
                .addText("Table with images, inner borders only, x and y padding")
                .startTable()
                .addColumn("COL1", 2)
                .addColumn("Image", 2)
                .addColumn("COL3", 4)
                .createRow().withValues(textBox(styleSheet, "1"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "2"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "3"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "4"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "5"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .createRow().withValues(textBox(styleSheet, "6"), new ReportImage(img, imageSize, imageSize), sampleTable)
                .endTable((tableBuilder) -> {
                    final ReportTable table = tableBuilder.build();
                    table.setCellPaddingX(5);
                    table.setCellPaddingY(5);
                    table.setNoBottomBorder(true);
                    table.setNoTopBorder(true);
                    table.setDrawInnerVertical(true);
                    table.setDrawOuterVertical(false);

                    return table;
                })
                .buildReport(reportFileName,
                        PdfPageLayout.getPortraitA4Page(),
                        new ClassPathResource("demo-template.pdf"));

        final File target = new File("pdf-out");

        if (!target.exists()) {
            Assert.assertTrue(target.mkdirs());
        }
        pdfReportFilePrinter.print(pdfReport, target);
    }

    private ReportTextBox textBox(DefaultPdfStyleSheet styleSheet, String x1) {
        return new ReportTextBox(styleSheet.getBodyText(), styleSheet.getLineDistance(), x1);
    }

}
