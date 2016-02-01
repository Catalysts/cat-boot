package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportPrinter;
import org.apache.pdfbox.exceptions.COSVisitorException;

import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public final class PdfReportFilePrinter implements PdfReportPrinter<File> {

    public void print(PdfReport report, File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is no directory ");
        }
        File outputFile = new File(directory, report.getFileName());
        try {
            report.getDocument().save(outputFile);
            report.getDocument().close();
        } catch (COSVisitorException e) {
            throw new IOException("Error on generating PDF", e);
        }
    }


}
