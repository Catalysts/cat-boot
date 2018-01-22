package cc.catalysts.boot.report.pdf.elements;

public class ReportPaddingWithHeight extends ReportPadding {

    public ReportPaddingWithHeight(float padding) {
        super(padding);
    }

    @Override
    public float getHeight(float allowedWidth) {
        return getPadding();
    }
}
