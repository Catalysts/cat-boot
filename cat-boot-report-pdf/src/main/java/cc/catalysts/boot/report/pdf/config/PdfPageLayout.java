package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.utils.PositionOfStaticElements;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Layout for a PDF document. Provides static methods to reuse standard layouts like A4 portrait or landscape.
 *
 * @author Klaus Lehner
 */
public class PdfPageLayout {

    private float width;
    private float height;
    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private float marginBottom;
    private float lineDistance;
    private float header;
    private float footer;
    private PositionOfStaticElements footerPosition;
    private PositionOfStaticElements headerPosition;

    public static PdfPageLayout getPortraitA4Page() {
        return new PdfPageLayout(595.27563F, 841.8898F, 28.346457F, 10, 100, 20, 1);
    }

    public static PdfPageLayout getPortraitA4PageWithSmallTopMargin() {
        return new PdfPageLayout(595.27563F, 841.8898F, 28.346457F, 10, 20, 20, 1);
    }

    public static PdfPageLayout getPortraitA4PageWithDoubleMargins() {
        return new PdfPageLayout(595.27563F, 841.8898F, 56.6929F, 56.6929F, 100, 20, 1);
    }

    public static PdfPageLayout getLandscapeA4Page() {
        return new PdfPageLayout(841.8898F, 595.27563F, 28.346457F, 10, 100, 20, 1);
    }

    public static PdfPageLayout getLandscapeA4PageWithSmallTopMargin() {
        return new PdfPageLayout(841.8898F, 595.27563F, 28.346457F, 10, 20, 20, 1);
    }

    public PdfPageLayout(float width, float height, float marginLeft, float marginRight, float marginTop, float marginBottom, float lineDistance) {
        this.width = width;
        this.height = height;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.lineDistance = lineDistance;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public float getLineDistance() {
        return lineDistance;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setLineDistance(float lineDistance) {
        this.lineDistance = lineDistance;
    }

    public PositionOfStaticElements getFooterPosition() { return footerPosition; }

    public void setFooterPosition(PositionOfStaticElements footerPosition) { this.footerPosition = footerPosition; }

    public PositionOfStaticElements getHeaderPosition() { return headerPosition; }

    public void setHeaderPosition(PositionOfStaticElements headerPosition) { this.headerPosition = headerPosition; }

    public void setFooter(float footerSize) {
        this.footer = footerSize;
    }

    public void setHeader(float headerSize) { this.header = headerSize; }

    public float getUsableWidth() { return width - marginLeft - marginRight; }

    public PDRectangle getPageSize() { return new PDRectangle(width, height); }

    public float getStartY() { return height - marginTop - header; }

    public float getStartY(int pageNo) { return pageNo == 0 && headerPosition == PositionOfStaticElements.ON_ALL_PAGES_BUT_FIRST ? height - marginTop : getStartY(); }

    public float getStartX() { return marginLeft; }

    public float getLastY() { return marginBottom + footer; }

    public float getLastY(int pageNo) { return pageNo == 0 && footerPosition == PositionOfStaticElements.ON_ALL_PAGES_BUT_FIRST ? marginBottom : getLastY(); }

    public float getLastX() { return width - marginRight; }
}
