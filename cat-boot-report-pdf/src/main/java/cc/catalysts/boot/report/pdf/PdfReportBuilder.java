package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.utils.PositionOfStaticElements;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public interface PdfReportBuilder {

    PdfReportBuilder addElement(ReportElement element);

    PdfReportBuilder addHeading(String heading);

    PdfReportBuilder addImage(Resource resource, float width, float height) throws IOException;

    PdfReportBuilder addLink(String text, String link);

    PdfReportBuilder addPadding(float padding);

    PdfReportBuilder addText(String text);

    PdfReportBuilder addText(String text, PdfTextStyle textConfig);

    PdfReportBuilder beginNewSection(String title, boolean startNewPage);

    PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException;

    PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource, PDDocument document) throws IOException;

    void registerFont(Resource resource);

    ReportTableBuilder startTable();

    PdfReportBuilder withFooterOnAllPages(String left, String middle, String right);

    PdfReportBuilder withFooterOnAllPages(ReportElement footerElement);

    PdfReportBuilder withFooterOnPages(String left, String middle, String right, PositionOfStaticElements footerPosition);

    PdfReportBuilder withFooterOnPages(ReportElement footerElement, PositionOfStaticElements footerPosition);

    PdfReportBuilder withHeaderOnAllPages(String left, String middle, String right);

    PdfReportBuilder withHeaderOnAllPages(ReportElement headerElement);

    PdfReportBuilder withHeaderOnPages(String left, String middle, String right, PositionOfStaticElements headerPosition);

    PdfReportBuilder withHeaderOnPages(ReportElement headerElement, PositionOfStaticElements headerPosition);
}
