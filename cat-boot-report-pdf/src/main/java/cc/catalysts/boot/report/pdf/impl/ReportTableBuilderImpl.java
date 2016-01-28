package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.elements.ReportElement;
import cc.catalysts.boot.report.pdf.ReportTableBuilder;
import cc.catalysts.boot.report.pdf.ReportTableRowBuilder;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.elements.ReportTable;
import cc.catalysts.boot.report.pdf.elements.ReportTextBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Paul Klingelhuber
 */
public class ReportTableBuilderImpl implements ReportTableBuilder {

    private final PdfStyleSheet pdfStyleSheet;
    private List<String> columnNames = new ArrayList<>();
    private List<Float> columnWeights = new ArrayList<>();
    private List<List<String>> tableValues = new ArrayList<>();
    private PdfReportBuilder reportBuilder;

    /**
     * init with default style
     */
    public ReportTableBuilderImpl(PdfStyleSheet pdfStyleSheet, PdfReportBuilder reportBuilder) {
        this.pdfStyleSheet = pdfStyleSheet;
        this.reportBuilder = reportBuilder;
    }

    public ReportTableBuilderImpl addColumn(String name) {
        addColumn(name, 1);
        return this;
    }

    /**
     * @param weight a weight, will be evaluated for column width, relative to all other passed values
     *               e.g. passing the same value for each will give evenly spaced. passing 2, 2 and 4 will produce widths of 25%, 25% and 50%
     */
    public ReportTableBuilderImpl addColumn(String name, float weight) {
        columnNames.add(name);
        columnWeights.add(weight);
        return this;
    }

    public ReportTableBuilderImpl setColumns(String... names) {
        columnNames.addAll(Arrays.asList(names));
        for (int i = 0; i < names.length; i++) {
            columnWeights.add(1f);
        }
        return this;
    }

    public ReportTableRowBuilderImpl createRow() {
        return new ReportTableRowBuilderImpl(this);
    }

    void addRow(List<String> values) {
        if (values.size() != columnNames.size()) {
            throw new IllegalArgumentException("invalid value count, must match column count: " + columnNames.size());
        }
        tableValues.add(values);
    }

    /**
     * build table taking column weights into account
     */
    public ReportTable build() {
        float[] widths = new float[columnNames.size()];
        float sum = 0;
        for (Float weight : columnWeights) {
            sum += weight;
        }
        float singlePartWidth = 1.0f / sum;
        for (int i = 0; i < widths.length; i++) {
            widths[i] = singlePartWidth * columnWeights.get(i);
        }
        return buildTableWithWidths(widths);
    }

    public ReportTable buildTableWithWidths(float[] widths) {
        ReportTable reportTable = new ReportTable(pdfStyleSheet, widths, toArray(), null);
        reportTable.setBorder(true);
        return reportTable;
    }

    private ReportElement[][] toArray() {
        // +1 for header
        ReportElement[][] result = new ReportElement[tableValues.size() + 1][columnNames.size()];
        int row = 0;
        int col = 0;
        // header
        for (col = 0; col < columnNames.size(); col++) {
            result[row][col] = new ReportTextBox(pdfStyleSheet.getTableTitleText(), pdfStyleSheet.getLineDistance(), columnNames.get(col));
        }
        row++;
        // body
        for (List<String> rowValues : tableValues) {
            col = 0;
            for (String value : rowValues) {
                result[row][col] = new ReportTextBox(pdfStyleSheet.getTableBodyText(), pdfStyleSheet.getLineDistance(), value);
                col++;
            }
            row++;
        }
        return result;
    }

    @Override
    public PdfReportBuilder endTable() {
        reportBuilder.addElement(this.build());
        return reportBuilder;
    }

    public static class ReportTableRowBuilderImpl implements ReportTableRowBuilder {
        private final ReportTableBuilderImpl parent;

        private List<String> values = new ArrayList<>();

        ReportTableRowBuilderImpl(ReportTableBuilderImpl parent) {
            this.parent = parent;
        }

        public ReportTableRowBuilderImpl addValue(String value) {
            values.add(value);
            return this;
        }


        public ReportTableBuilderImpl withValues(String... rowValues) {
            values.addAll(Arrays.asList(rowValues));
            return endRow();
        }

        public ReportTableBuilderImpl endRow() {
            parent.addRow(values);
            return parent;
        }
    }

}
