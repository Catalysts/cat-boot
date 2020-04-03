package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportPrinter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        report.saveAndClose(new BufferedOutputStream(new FileOutputStream(outputFile)));
        report.close();
    }

}
