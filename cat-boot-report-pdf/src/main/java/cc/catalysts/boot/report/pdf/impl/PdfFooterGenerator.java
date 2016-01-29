package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;

/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Paul Klingelhuber
 */
public class PdfFooterGenerator extends AbstractFixedLineGenerator {

    public PdfFooterGenerator(PdfStyleSheet pdfStyleSheet, String leftText, String centerText, String rightText) {
        super(pdfStyleSheet, leftText, centerText, rightText);
    }

    @Override
    float getVerticalPosition(PdfPageLayout pageConfig) {
        return pageConfig.getLastY();
    }

}
