package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.elements.ReportTable;

/**
 * @author Klaus Lehner
 */
public interface ReportTableBuilder {

    ReportTableBuilder addColumn(String name, float weight);

    ReportTableRowBuilder createRow();

    PdfReportBuilder endTable();

    ReportTable build();
}
