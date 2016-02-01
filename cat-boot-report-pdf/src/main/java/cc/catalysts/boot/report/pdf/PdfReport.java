package cc.catalysts.boot.report.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * @author Klaus Lehner
 */
public class PdfReport {
    private final String fileName;
    private final PDDocument document;

    public PdfReport(String fileName, PDDocument document) {
        this.fileName = fileName;
        this.document = document;
    }

    public String getFileName() {
        return fileName;
    }

    public PDDocument getDocument() {
        return document;
    }
}
