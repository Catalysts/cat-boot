package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.elements.ReportElementStatic;
import cc.catalysts.boot.report.pdf.utils.PositionOfStaticElements;


/**
 * Base for static page decorators that add a single line (split in left, center, right parts) to a certain
 * y-position of the page (typically used for header and footer lines)
 *
 * @author Paul Klingelhuber
 */
public abstract class AbstractFixedLineGenerator {
    private PositionOfStaticElements footerOnPages;
    private ReportElement footerElement;

    public static final String PAGE_TEMPLATE_CURR = "%PAGE_NUMBER%";
    public static final String PAGE_TEMPLATE_TOTAL = "%TOTAL_PAGES%";

    public AbstractFixedLineGenerator(ReportElement footerElement, PositionOfStaticElements footerOnPages) {
        this.footerElement = footerElement;
        this.footerOnPages = footerOnPages;
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
        report.addStaticElementsForEachPage(new ReportElementStatic(footerElement, 0, x, y, w, footerOnPages));
    }

    protected abstract float getVerticalPosition(PdfPageLayout pageConfig);

    public PositionOfStaticElements getFooterOnPages() {
        return footerOnPages;
    }

    public ReportElement getFooterElement() {
        return footerElement;
    }

}
