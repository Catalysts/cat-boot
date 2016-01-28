package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public interface PdfReportBuilder {

    PdfReportBuilder beginNewSection(String title, boolean startNewPage);

    PdfReportBuilder addHeading(String heading);

    PdfReportBuilder addText(String text);

    PdfReportBuilder addText(String text, PdfTextStyle textConfig);

    PdfReportBuilder addElement(ReportElement element);

    PdfReportBuilder withHeaderOnAllPages(String left, String middle, String right);

    PdfReportBuilder withFooterOnAllPages(String left, String middle, String right);

    ReportTableBuilder startTable();

    PdfReport buildReport(PdfPageLayout pageConfig);

    void printToFile(File outputFile, PdfPageLayout pageConfig, Resource templateResource) throws IOException;

    PDDocument printToPDDocument(PdfPageLayout pageConfig, Resource templateResource) throws IOException;

    void printToHttpServletResponse(HttpServletResponse response, String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException;
}
