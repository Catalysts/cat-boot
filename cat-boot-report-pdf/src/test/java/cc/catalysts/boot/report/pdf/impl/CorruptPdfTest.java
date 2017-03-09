package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfFont;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.exception.PdfReportGeneratorException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.IOException;

public class CorruptPdfTest {

    @Test(expected = PdfReportGeneratorException.class)
    public void generateCorruptPdf() throws IOException {
        final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());

        DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
        styleSheet.setBodyText(new PdfTextStyle(10, PdfFont.HELVETICA, Color.BLACK, "regular"));

        // generate pdf with large, not splittable image
        pdfReportService.createBuilder(styleSheet)
                .addImage(new ClassPathResource("github_icon.png"), 100, 1000)
                .buildReport("corrupt.pdf", PdfPageLayout.getPortraitA4Page(), null);
    }
}
