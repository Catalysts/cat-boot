package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.ReportTableBuilder;
import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfPageLayout;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.config.PdfTextStyle;
import cc.catalysts.boot.report.pdf.elements.*;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
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
    public PdfReportBuilder withHeaderOnAllPages(String left, String middle, String right, boolean excludeOnFirstPage) {
        PdfStyleSheet HeaderTableConfiguration = new DefaultPdfStyleSheet();
        HeaderTableConfiguration.setTableTitleText(configuration.getFooterText());
        ReportTable headerTable = new ReportTableBuilderImpl(HeaderTableConfiguration, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        headerTable.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        headerTable.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        headerTable.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        headerTable.setBorder(false);
        fixedLineGenerators.add(new PdfHeaderGenerator(headerTable, excludeOnFirstPage));
        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnAllPages(ReportElement headerElement, boolean excludeOnFirstPage) {
        fixedLineGenerators.add(new PdfHeaderGenerator(headerElement, excludeOnFirstPage));
        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnAllPages(String left, String middle, String right, boolean excludeOnFirstPage) {
        PdfStyleSheet footerTableConfiguration = new DefaultPdfStyleSheet();
        footerTableConfiguration.setTableTitleText(configuration.getFooterText());
        ReportTable footerTable = new ReportTableBuilderImpl(footerTableConfiguration, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        footerTable.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        footerTable.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        footerTable.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        footerTable.setBorder(false);
        fixedLineGenerators.add(new PdfHeaderGenerator(footerTable, excludeOnFirstPage));
        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnAllPages(ReportElement footerElement, boolean excludeOnFirstPage) {
        fixedLineGenerators.add(new PdfHeaderGenerator(footerElement, excludeOnFirstPage));
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

    public PdfReportStructure buildReport(PdfPageLayout pageConfig) {
        PdfReportStructure report = new PdfReportStructure(configuration);
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
    public PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException {
        PdfReportStructure report = this.buildReport(pageConfig);
        PDDocument document = new PdfReportGenerator().generate(pageConfig, templateResource, report);
        return new PdfReport(fileName, document);
    }

    @Override
    public PdfReportBuilder addImage(Resource resource, float width, float height) throws IOException {
        return addElement(new ReportImage(ImageIO.read(resource.getFile()), width, height));
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
        addPadding(configuration.getHeadingPaddingAfter());
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
