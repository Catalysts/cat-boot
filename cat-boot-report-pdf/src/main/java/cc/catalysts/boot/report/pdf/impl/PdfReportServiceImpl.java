package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.PdfReportService;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author Klaus Lehner
 */
@Service
public class PdfReportServiceImpl implements PdfReportService {

    private final PdfStyleSheet defaultConfiguration;

    @Autowired
    public PdfReportServiceImpl(DefaultPdfStyleSheet defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    @Override
    public PdfReportBuilder createBuilder() {
        return new PdfReportBuilderImpl(defaultConfiguration);
    }

    @Override
    public PdfReportBuilder createBuilder(PdfStyleSheet config) {
        return new PdfReportBuilderImpl(config);
    }

}
