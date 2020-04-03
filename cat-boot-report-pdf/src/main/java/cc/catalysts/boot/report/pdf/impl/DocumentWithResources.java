package cc.catalysts.boot.report.pdf.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DocumentWithResources implements AutoCloseable {
    private static final Logger LOG = getLogger(DocumentWithResources.class);

    private final PDDocument document;

    private boolean isClosed = false;

    /**
     * resources that need to be closed after the document has been written.
     */
    private final List<Closeable> resourceDependencies = new ArrayList<>();

    public DocumentWithResources(PDDocument document) {
        this.document = document;
    }

    public PDDocument getDocument() {
        return document;
    }

    public void addResourceDependency(Closeable resourceDependency) {
        this.resourceDependencies.add(resourceDependency);
    }

    public void saveAndClose(OutputStream outputStream) throws IOException {
        try {
            document.save(outputStream);
        } finally {
            this.close();
        }
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        try {
            document.close();
        } catch (final Exception e) {
            LOG.warn("Exception occured while trying to close the document: {}", e.getMessage(), e);
        }
        for (Closeable dependency : resourceDependencies) {
            try {
                dependency.close();
            } catch (Exception e) {
                LOG.warn("Exception occured while trying to close resource dependency: {}", e.getMessage(), e);
            }
        }
        isClosed = true;
    }
}
