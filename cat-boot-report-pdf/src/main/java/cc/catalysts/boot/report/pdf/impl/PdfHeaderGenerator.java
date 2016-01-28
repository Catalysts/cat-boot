package cc.catalysts.boot.report.pdf.impl;


import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;

/**
 * @author Paul Klingelhuber
 */
public class PdfHeaderGenerator extends AbstractFixedLineGenerator {


    public PdfHeaderGenerator(PdfStyleSheet configuration, String leftText, String centerText, String rightText) {
        super(configuration, leftText, centerText, rightText);
    }

    @Override
    float getVerticalPosition(PdfPageLayout pageConfig) {
        return pageConfig.getHeight();
    }

}
