package cc.catalysts.boot.report.pdf.config;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.awt.*;

/**
 * @author Klaus Lehner
 */
public abstract class PdfStyleSheet {

    /**
     * the vertical line distance
     */
    private float lineDistance = 1;

    /**
     * the padding after sections (see {@link cc.catalysts.boot.report.pdf.PdfReportBuilder#beginNewSection(String, boolean)}
     */
    private int sectionPadding = 10;

    /**
     * the padding after headings
     */
    private int headingPaddingAfter = 4;

    /**
     * the text style for heading1 (h1)
     */

    private final PDColor BLACK = new PDColor(new float[] {0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

    private PdfTextStyle heading1Text = new PdfTextStyle(20, PdfFont.HELVETICA, BLACK, "bold");

    /**
     * the text style for the body text
     */
    private PdfTextStyle bodyText = new PdfTextStyle(12, PdfFont.HELVETICA, BLACK, "regular");

    private PdfTextStyle tableTitleText = new PdfTextStyle(12, PdfFont.HELVETICA, BLACK, "bold");
    private PdfTextStyle tableBodyText = new PdfTextStyle(12, PdfFont.HELVETICA, BLACK, "regular");

    private PdfTextStyle footerText = new PdfTextStyle(7, PdfFont.HELVETICA, BLACK, "regular");

    public int getSectionPadding() {
        return sectionPadding;
    }

    public void setSectionPadding(int sectionPadding) {
        this.sectionPadding = sectionPadding;
    }

    public int getHeadingPaddingAfter() {
        return headingPaddingAfter;
    }

    public void setHeadingPaddingAfter(int headingPaddingAfter) {
        this.headingPaddingAfter = headingPaddingAfter;
    }

    public PdfTextStyle getHeading1Text() {
        return heading1Text;
    }

    public void setHeading1Text(PdfTextStyle heading1Text) {
        this.heading1Text = heading1Text;
    }

    public PdfTextStyle getBodyText() {
        return bodyText;
    }

    public void setBodyText(PdfTextStyle bodyText) {
        this.bodyText = bodyText;
    }

    public PdfTextStyle getTableTitleText() {
        return tableTitleText;
    }

    public void setTableTitleText(PdfTextStyle tableTitleText) {
        this.tableTitleText = tableTitleText;
    }

    public PdfTextStyle getTableBodyText() {
        return tableBodyText;
    }

    public void setTableBodyText(PdfTextStyle tableBodyText) {
        this.tableBodyText = tableBodyText;
    }

    public PdfTextStyle getFooterText() {
        return footerText;
    }

    public void setFooterText(PdfTextStyle footerText) {
        this.footerText = footerText;
    }

    public float getLineDistance() {
        return lineDistance;
    }

    public void setLineDistance(float lineDistance) {
        this.lineDistance = lineDistance;
    }
}
