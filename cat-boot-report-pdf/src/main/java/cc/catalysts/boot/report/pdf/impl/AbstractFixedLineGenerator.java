package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.elements.ReportElementStatic;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for static page decorators that add a single line (split in left, center, right parts) to a certain
 * y-position of the page (typically used for header and footer lines)
 *
 * @author Paul Klingelhuber
 */
public abstract class AbstractFixedLineGenerator {
    private final PdfStyleSheet pdfStyleSheet;
    private String leftText;
    private String centerText;
    private String rightText;

    public static final String PAGE_TEMPLATE_CURR = "%PAGE_NUMBER%";
    public static final String PAGE_TEMPLATE_TOTAL = "%TOTAL_PAGES%";

    public AbstractFixedLineGenerator(PdfStyleSheet pdfStyleSheet, String leftText, String centerText, String rightText) {
        this.pdfStyleSheet = pdfStyleSheet;
        this.leftText = leftText;
        this.centerText = centerText;
        this.rightText = rightText;
    }

    /**
     * adds a footer to all pages
     *
     * @param report     the report where the footer should be added.
     * @param pageConfig the page layout of the page
     */
    public void addFooterToAllPages(PdfReportStructure report, PdfPageLayout pageConfig) {
        float x = pageConfig.getStartX();
        float y = getVerticalPosition(pageConfig);
        float w = pageConfig.getUsableWidth();
        List<ReportElementStatic> staticElements = new ArrayList<ReportElementStatic>();
        if (!StringUtils.isEmpty(leftText)) {
            ReportTextBox footerElem = new ReportTextBox(pdfStyleSheet.getFooterText(), pdfStyleSheet.getLineDistance(), leftText);
            footerElem.setAlign(ReportAlignType.LEFT);
            staticElements.add(new ReportElementStatic(footerElem, 0, x, y, w));
        }
        if (!StringUtils.isEmpty(centerText)) {
            ReportTextBox footerElem = new ReportTextBox(pdfStyleSheet.getFooterText(), pdfStyleSheet.getLineDistance(), centerText);
            footerElem.setAlign(ReportAlignType.CENTER);
            staticElements.add(new ReportElementStatic(footerElem, 0, x, y, w));
        }
        if (!StringUtils.isEmpty(rightText)) {
            ReportTextBox footerElem = new ReportTextBox(pdfStyleSheet.getFooterText(), pdfStyleSheet.getLineDistance(), rightText);
            footerElem.setAlign(ReportAlignType.RIGHT);
            staticElements.add(new ReportElementStatic(footerElem, 0, x, y, w));
        }

        report.addStaticElementsForEachPage(staticElements.toArray(new ReportElementStatic[staticElements.size()]));
    }

    protected abstract float getVerticalPosition(PdfPageLayout pageConfig);

}
