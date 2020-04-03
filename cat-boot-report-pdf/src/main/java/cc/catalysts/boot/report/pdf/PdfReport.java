package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.impl.DocumentWithResources;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Klaus Lehner
 */
public class PdfReport implements AutoCloseable {
    private static final Logger LOG = getLogger(PdfReport.class);

    private final String fileName;
    private final DocumentWithResources documentWithResources;

    public PdfReport(String fileName, DocumentWithResources documentWithResources) {
        this.fileName = fileName;
        this.documentWithResources = documentWithResources;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Prefer using saveAndClose whenever possible.
     * Alternatively call close() yourself to close any tracked resource dependencies.
     */
    public PDDocument getDocument() {
        return documentWithResources.getDocument();
    }

    public void saveAndClose(OutputStream outputStream) throws IOException {
        documentWithResources.saveAndClose(outputStream);
    }

    @Override
    public void close() {
        documentWithResources.close();
    }

}
