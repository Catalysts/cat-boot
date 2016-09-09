package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.elements.ReportElement;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public interface ReportTableRowBuilder {

    ReportTableRowBuilder addValue(String value);

    ReportTableRowBuilder addValue(ReportElement value);

    /**
     * sets the row values and finishes the row
     *
     * @param rowValues the values for the current row
     * @return the surrounding report builder
     */

    ReportTableBuilder withValues(String... rowValues);

    ReportTableBuilder withValues(ReportElement... rowValues);

    ReportTableBuilder endRow();
}
