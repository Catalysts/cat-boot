package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.elements.ReportCompositeElement;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.elements.ReportElementStatic;
import cc.catalysts.boot.report.pdf.elements.ReportImage;
import cc.catalysts.boot.report.pdf.elements.ReportTable;
import cc.catalysts.boot.report.pdf.exception.PdfReportGeneratorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Paul Klingelhuber
 */
class PdfReportGenerator {

    public PdfReportGenerator() {

    }

    public void printToStream(PdfPageLayout pageConfig, Resource templateResource, PdfReportStructure report, OutputStream stream, PDDocument document) throws IOException {
        final DocumentWithResources documentWithResources = generate(pageConfig, templateResource, report, document);
        documentWithResources.saveAndClose(stream);
    }

    public DocumentWithResources generate(PdfPageLayout pageConfig, Resource templateResource, PdfReportStructure report) throws IOException {
        return generate(pageConfig, templateResource, report, new PDDocument());
    }

    /**
     * @param pageConfig page config
     * @param report     the report to print
     * @return object that contains the printed PdfBox document and resources that need to be closed after finally writing the document
     * @throws java.io.IOException
     */
    public DocumentWithResources generate(PdfPageLayout pageConfig, Resource templateResource, PdfReportStructure report, PDDocument document) throws IOException {
        final DocumentWithResources documentWithResources = new DocumentWithResources(document);
        PrintData printData = new PrintData(templateResource, pageConfig);
        PrintCursor cursor = new PrintCursor();

        breakPage(documentWithResources, cursor, printData);
        float maxWidth = pageConfig.getUsableWidth();

        int reportElementIndex = 0, nrOfReportElements = report.getElements().size();
        ReportElement currentReportElement = report.getElements().isEmpty() ? null : report.getElements().get(reportElementIndex);
        ReportElement nextReportElement = null;

        boolean performedBreakPageForCurrentReportElement = false; // for each element max. one break page allowed
        while (currentReportElement != null) {
            boolean forceBreak = false;

            float height = currentReportElement.getHeight(maxWidth);
            if (cursor.yPos - height < pageConfig.getLastY(cursor.currentPageNumber)) {
                //out of bounds
                if (currentReportElement.isSplitable() && (cursor.yPos - currentReportElement.getFirstSegmentHeight(maxWidth))
                        >= pageConfig.getLastY(cursor.currentPageNumber)) {
                    if (currentReportElement instanceof ReportTable) {
                        //it's a Table out of bounds, so we also do a height split
                        ReportElement[] twoElements = currentReportElement.split(maxWidth, cursor.yPos -
                                pageConfig.getLastY(cursor.currentPageNumber));
                        if (twoElements.length != 2) {
                            throw new IllegalStateException("The split method should always two parts.");
                        }
                        currentReportElement = twoElements[0];
                        nextReportElement = twoElements[1];
                        if (((ReportTable) currentReportElement).getExtraSplitting()) {
                            forceBreak = true;
                        }
                    } else if (currentReportElement instanceof ReportCompositeElement) {
                        ReportElement[] twoElements = currentReportElement.split(maxWidth, cursor.yPos -
                                pageConfig.getLastY(cursor.currentPageNumber));
                        if (twoElements.length != 2) {
                            throw new IllegalStateException("The split method should always two parts.");
                        }
                        currentReportElement = twoElements[0];
                        nextReportElement = twoElements[1];
                    } else {
                        ReportElement[] twoElements = currentReportElement.split(maxWidth);
                        if (twoElements.length != 2) {
                            throw new IllegalStateException("The split method should always two parts.");
                        }
                        currentReportElement = twoElements[0];
                        nextReportElement = twoElements[1];
                    }
                } else if (!performedBreakPageForCurrentReportElement) {
                    if (lastNonHeightElement(reportElementIndex, nrOfReportElements, currentReportElement.getHeight(maxWidth))) {
                        break; // ignores the last padding if there is not enough space for it
                    } else {
                        breakPage(documentWithResources, cursor, printData);
                        performedBreakPageForCurrentReportElement = true;
                        continue;
                    }
                } else {
                    // cleanup all dependencies first (there would be no way for the caller to clean this up)
                    documentWithResources.close();

                    throw new PdfReportGeneratorException(String.format("Could not generate pdf! " +
                                    "Not enough space (required=%s) for element of type %s",
                            height, currentReportElement.getClass().getSimpleName()));
                }
            }

            // without this block pdfbox 2.0.2 does not render properly
            // TODO: find a more elegant solution
            cursor.currentStream.close();
            PDPageTree pageTree = document.getDocumentCatalog().getPages();
            PDPage currPage = pageTree.get(pageTree.getCount() - 1);
            cursor.currentStream = new PDPageContentStream(document, currPage, PDPageContentStream.AppendMode.APPEND, false);
            // ---

            float nextY = currentReportElement.print(document, cursor.currentStream, cursor.currentPageNumber, cursor.xPos, cursor.yPos, maxWidth);
            if (nextReportElement == null) {
                // only add artificial spacing when there isn't currently a split object being processed
                //  otherwise rows from split tables would suddenly have empty space between them
                nextY -= pageConfig.getLineDistance();
            }
            cursor.imageList.addAll(currentReportElement.getImageIntents());

            currentReportElement = nextReportElement;
            nextReportElement = null;
            if (currentReportElement == null && reportElementIndex + 1 < report.getElements().size()) {
                currentReportElement = report.getElements().get(++reportElementIndex);
            }
            performedBreakPageForCurrentReportElement = false;
            cursor.yPos = nextY;
            if (forceBreak) {
                breakPage(documentWithResources, cursor, printData);
            }
        }
        cursor.currentStream.close();

        report.expandPagesStaticElements(cursor.currentPageNumber + 1);

        for (ReportElementStatic staticElem : report.getStaticElements()) {
            staticElem.print(document, null, 0, 0, 0, 0);
        }

        printImages(document, cursor);

        return documentWithResources;
    }

    private boolean lastNonHeightElement(int reportElementIndex, int nrOfReportElements, float height) {
        final double epsilon = 0.000001;

        return (reportElementIndex == nrOfReportElements - 1) && (height < epsilon);
    }

    ReportElement[] specialSplitTable(ReportTable reportTable, float allowedHeight, float allowedWidth) {
        return reportTable.splitFirstCell(allowedHeight, allowedWidth);
    }

    private void breakPage(DocumentWithResources documentWithResources, PrintCursor cursor, PrintData printData) throws IOException {
        final PDDocument document = documentWithResources.getDocument();
        if (cursor.currentStream != null) {
            cursor.currentStream.close();
        }

        if (printData.templateResource == null) {
            document.addPage(new PDPage(printData.pageConfig.getPageSize()));
        } else {
            PDDocument templateDoc = PDDocument.load(printData.templateResource.getInputStream());
            cursor.cacheTempalte(templateDoc);
            PDPage templatePage = templateDoc.getDocumentCatalog().getPages().get(0);
            document.importPage(templatePage);
            // prevent warnings about unclosed resources from finalizers by tracking these dependencies
            documentWithResources.addResourceDependency(templateDoc);
        }
        PDPage currPage = document.getDocumentCatalog().getPages().get(++cursor.currentPageNumber);
        cursor.currentStream = new PDPageContentStream(document, currPage, PDPageContentStream.AppendMode.APPEND, false);
        cursor.yPos = printData.pageConfig.getStartY(cursor.currentPageNumber);
        cursor.xPos = printData.pageConfig.getStartX();
    }

    private void printImages(PDDocument document, PrintCursor cursor) throws IOException {
        for (ReportImage.ImagePrintIntent ipi : cursor.imageList) {
            ipi.getImg().printImage(document, ipi.getPage(), ipi.getX(), ipi.getY());
        }
    }

    private static class PrintData {
        private Resource templateResource;
        private PdfPageLayout pageConfig;

        public PrintData(Resource templateResource, PdfPageLayout pageConfig) {
            this.templateResource = templateResource;
            this.pageConfig = pageConfig;
        }
    }

    private static class PrintCursor {
        private int currentPageNumber = -1;
        private PDPageContentStream currentStream;
        private float yPos;
        private float xPos;
        private List<ReportImage.ImagePrintIntent> imageList = new ArrayList<>();
        private Queue<Object> templateCache = new LinkedList<>();

        /**
         * used to prevent GC from pdfBox templates
         */
        public void cacheTempalte(Object template) {
            templateCache.add(template);
        }
    }

}
