package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.ReportTableBuilder;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.*;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Klaus Lehner
 */
class PdfReportBuilderImpl implements PdfReportBuilder {

    private final PdfStyleSheet configuration;
    private List<ReportElement> elements = new ArrayList<>();
    private List<AbstractFixedLineGenerator> fixedLineGenerators = new ArrayList<>();

    public PdfReportBuilderImpl(PdfStyleSheet configuration) {
        this.configuration = configuration;
    }

    public PdfReportBuilderImpl addElement(ReportElement element) {
        elements.add(element);
        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnAllPages(String left, String middle, String right) {
        fixedLineGenerators.add(new PdfHeaderGenerator(configuration, left, middle, right));
        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnAllPages(String left, String middle, String right) {
        fixedLineGenerators.add(new PdfFooterGenerator(configuration, left, middle, right));
        return this;
    }

    @Override
    public PdfReportBuilder addPadding(float padding) {
        elements.add(new ReportPadding(padding));
        return this;
    }

    @Override
    public ReportTableBuilder startTable() {
        return new ReportTableBuilderImpl(configuration, this);
    }

    public PdfReport buildReport(PdfPageLayout pageConfig) {
        PdfReport report = new PdfReport(configuration);
        for (ReportElement element : elements) {
            if (element instanceof ReportElementStatic) {
                report.addStaticElement((ReportElementStatic) element);
            } else {
                report.addElement(element);
            }
        }
        for (AbstractFixedLineGenerator generator : fixedLineGenerators) {
            generator.addFooterToAllPages(report, pageConfig);
        }
        return report;
    }

    @Override
    public void printToFile(File outputFile, PdfPageLayout pageConfig, Resource templateResource) throws IOException {
        try {
            PDDocument document = printToPDDocument(pageConfig, templateResource);
            document.save(outputFile);
            document.close();
        } catch (COSVisitorException e) {
            throw new IOException("Error on generating PDF", e);
        }
    }

    @Override
    public PDDocument printToPDDocument(PdfPageLayout pageConfig, Resource templateResource) throws IOException {
        PdfReport report = this.buildReport(pageConfig);
        PDDocument document = new PdfReportPrinter(configuration).print(pageConfig, templateResource, report);
        return document;
    }

    @Override
    public void printToHttpServletResponse(HttpServletResponse response, String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException {
        PdfReport report = this.buildReport(pageConfig);
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        try {
            new PdfReportPrinter(configuration).printToStream(pageConfig, null, report, response.getOutputStream());
        } catch (Exception e) {
            throw new IOException("could not write result pdf", e);
        }
    }

    @Override
    public PdfReportBuilder beginNewSection(String title, boolean startNewPage) {
        if (startNewPage && !elements.isEmpty()) {
            elements.add(new ReportPageBreak());
        }
        addElement(new ReportPadding(configuration.getSectionPadding()));
        addHeading(title);
        return this;
    }

    @Override
    public PdfReportBuilderImpl addHeading(String heading) {
        addElement(new ReportTextBox(configuration.getHeading1Text(), configuration.getLineDistance(), heading));
        addElement(new ReportPadding(configuration.getHeadingPaddingAfter()));
        return this;
    }

    @Override
    public PdfReportBuilderImpl addText(String text, PdfTextStyle textConfig) {
        addElement(new ReportTextBox(textConfig, configuration.getLineDistance(), text));
        return this;
    }

    @Override
    public PdfReportBuilderImpl addText(String text) {
        addElement(new ReportTextBox(configuration.getBodyText(), configuration.getLineDistance(), text));
        return this;
    }

    public List<ReportElement> getElements() {
        return elements;
    }

}
