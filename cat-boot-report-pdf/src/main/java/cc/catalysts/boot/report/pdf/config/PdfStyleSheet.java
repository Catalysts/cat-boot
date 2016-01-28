package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

/**
 * @author Klaus Lehner
 */
public abstract class PdfStyleSheet {

    private float lineDistance = 1;

    private int sectionPadding = 10;
    private int headingPaddingAfter = 4;

    private PdfTextStyle heading1Text = new PdfTextStyle(20, PDType1Font.HELVETICA_BOLD, ReportFontType.BOLD, Color.BLACK);
    private PdfTextStyle bodyText = new PdfTextStyle(12, PDType1Font.HELVETICA, ReportFontType.NORMAL, Color.BLACK);

    private PdfTextStyle tableTitleText = new PdfTextStyle(12, PDType1Font.HELVETICA_BOLD, ReportFontType.BOLD, Color.BLACK);
    private PdfTextStyle tableBodyText = new PdfTextStyle(12, PDType1Font.HELVETICA, ReportFontType.NORMAL, Color.BLACK);

    private PdfTextStyle footerText = new PdfTextStyle(7, PDType1Font.HELVETICA, ReportFontType.NORMAL, Color.BLACK);

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
