package cc.catalysts.boot.report.pdf.impl;


import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.utils.ReportStaticElementOnPages;


/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Paul Klingelhuber
 */
public class PdfHeaderGenerator extends AbstractFixedLineGenerator {


    public PdfHeaderGenerator(ReportElement headerElement, ReportStaticElementOnPages headerOnPages) {
        super(headerElement, headerOnPages);
    }

    @Override
    protected float getVerticalPosition(PdfPageLayout pageConfig) {
        return pageConfig.getStartY();
    }

}
