package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.elements.ReportElement;

import java.awt.image.BufferedImage;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public interface ReportTableRowBuilder {

    ReportTableRowBuilder addValue(ReportElement value);

    /**
     * sets the row values and finishes the row
     *
     * @param rowValues the values for the current row
     * @return the surrounding report builder
     */
    ReportTableBuilder withValues(ReportElement... rowValues);

    ReportTableBuilder endRow();
}
