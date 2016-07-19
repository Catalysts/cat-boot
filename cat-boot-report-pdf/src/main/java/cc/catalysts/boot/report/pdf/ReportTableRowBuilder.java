package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.elements.ReportTableCellElement;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public interface ReportTableRowBuilder {

    ReportTableRowBuilder addValue(ReportTableCellElement value);

    /**
     * sets the row values and finishes the row
     *
     * @param rowValues the values for the current row
     * @return the surrounding report builder
     */
    ReportTableBuilder withValues(ReportTableCellElement... rowValues);

    ReportTableBuilder endRow();
}
