package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.elements.ReportTable;

import java.util.function.Function;

/**
 * @author Klaus Lehner
 */
public interface ReportTableBuilder {

    ReportTableBuilder addColumn(String name, float weight);

    ReportTableRowBuilder createRow();

    PdfReportBuilder endTable();

    /**
     * Finish building the table and run some code that manipulates it before adding it to the document.
     */
    PdfReportBuilder endTable(Function<ReportTableBuilder, ReportTable> tableConsumer);

    ReportTable build();

    ReportTable build(boolean withHeader, boolean withBorder);
}
