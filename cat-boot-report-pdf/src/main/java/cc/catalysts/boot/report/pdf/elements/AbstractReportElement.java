package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractReportElement implements ReportElement {

    @Override
    public boolean isSplitable() {
        return false;
    }

    @Override
    public float getFirstSegmentHeight(float allowedWidth) {
        throw new IllegalStateException("If element is splitable, this method has to be implemented");
    }

    @Override
    public AbstractReportElement[] split(float allowedWidth) {
        throw new IllegalStateException("If element is splitable, this method has to be implemented");
    }

    @Override
    public ReportElement[] split(float allowedWidth, float allowedHeight) {
        throw new IllegalStateException("If element is splitable, this method has to be implemented");
    }

    @Override
    public Collection<ReportImage.ImagePrintIntent> getImageIntents() {
        return Collections.emptyList();
    }

    @Override
    public void setFontLib(Map<ReportFontType, PDFont> fontLib) {
        //do nothing in most cases
    }
}
