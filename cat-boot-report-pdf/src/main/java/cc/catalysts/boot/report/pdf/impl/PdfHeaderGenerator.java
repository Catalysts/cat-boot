package cc.catalysts.boot.report.pdf.impl;


import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;


/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Paul Klingelhuber
 */
public class PdfHeaderGenerator extends AbstractFixedLineGenerator {


    public PdfHeaderGenerator(PdfStyleSheet configuration, String leftText, String centerText, String rightText, boolean excludeOnFirstPage) {
        super(configuration, leftText, centerText, rightText, excludeOnFirstPage);
    }

    @Override
    protected float getVerticalPosition(PdfPageLayout pageConfig) {
        return pageConfig.getHeight();
    }

}
