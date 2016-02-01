package cc.catalysts.boot.report.pdf;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public interface ReportTableRowBuilder {

    ReportTableRowBuilder addValue(String value);

    /**
     * sets the row values and finishes the row
     *
     * @param rowValues the values for the current row
     * @return the surrounding report builder
     */
    ReportTableBuilder withValues(String... rowValues);

    ReportTableBuilder endRow();
}
