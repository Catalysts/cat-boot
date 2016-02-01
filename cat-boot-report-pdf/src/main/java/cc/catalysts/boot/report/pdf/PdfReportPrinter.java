package cc.catalysts.boot.report.pdf;

import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public interface PdfReportPrinter<T> {
    void print(PdfReport report, T destination) throws IOException;
}
