package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.elements.ReportTable;
import cc.catalysts.boot.report.pdf.utils.ReportFooterOnPages;
import org.springframework.core.io.Resource;

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

    PdfReportBuilder withHeaderOnAllPages(ReportElement headerElement);

    PdfReportBuilder withHeaderOnPages(String left, String middle, String right, ReportFooterOnPages headerOnPages);

    PdfReportBuilder withHeaderOnPages(ReportElement headerElement, ReportFooterOnPages headerOnPages);

    PdfReportBuilder withFooterOnAllPages(String left, String middle, String right);

    PdfReportBuilder withFooterOnAllPages(ReportElement footerElement);

    PdfReportBuilder withFooterOnPages(String left, String middle, String right, ReportFooterOnPages footerOnPages);

    PdfReportBuilder withFooterOnPages(ReportElement footerElement, ReportFooterOnPages footerOnPages);

    PdfReportBuilder addPadding(float padding);

    ReportTableBuilder startTable();

    PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException;

    PdfReportBuilder addImage(Resource resource, float width, float height) throws IOException;
}
