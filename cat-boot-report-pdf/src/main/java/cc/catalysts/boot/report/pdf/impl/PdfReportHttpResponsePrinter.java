package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportPrinter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
public final class PdfReportHttpResponsePrinter implements PdfReportPrinter<HttpServletResponse> {

    public void print(PdfReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + report.getFileName());

        report.getDocument().save(response.getOutputStream());
        report.getDocument().close();
        response.getOutputStream().close();
    }

}
