package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;

/**
 * @author Klaus Lehner
 */
public interface PdfReportService {

    PdfReportBuilder createBuilder();

    PdfReportBuilder createBuilder(PdfStyleSheet config);
}
