package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportBuilder;
import cc.catalysts.boot.report.pdf.ReportTableBuilder;
import cc.catalysts.boot.report.pdf.config.*;
import cc.catalysts.boot.report.pdf.elements.*;
import cc.catalysts.boot.report.pdf.utils.PositionOfStaticElements;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Klaus Lehner
 */
class PdfReportBuilderImpl implements PdfReportBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(PdfReportBuilderImpl.class);

    private final PdfStyleSheet configuration;
    private final PDDocument document;

    private List<ReportElement> elements = new ArrayList<>();
    private List<AbstractFixedLineGenerator> fixedLineGenerators = new ArrayList<>();

    public PdfReportBuilderImpl(PdfStyleSheet configuration) {
        this.configuration = configuration;
        this.document = new PDDocument();

        loadResourceFonts();
    }

    public List<ReportElement> getElements() {
        return elements;
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
            if (generator instanceof PdfFooterGenerator) {
                pageConfig.setFooterPosition(generator.getFooterOnPages());
                pageConfig.setFooter(generator.getFooterElement().getHeight(pageConfig.getUsableWidth()) + configuration.getLineDistance());
            } else {
                pageConfig.setHeaderPosition(generator.getFooterOnPages());
                pageConfig.setHeader(generator.getFooterElement().getHeight(pageConfig.getUsableWidth()) + configuration.getLineDistance());
            }
        }

        return report;
    }

    private void loadResourceFonts() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

            for (Resource font : resourcePatternResolver.getResources("classpath*:fonts/*.ttf")) {
                registerFont(font);
            }
        } catch (IOException e) {
            LOG.error("Failed to get files!", e);
        }
    }

    public PdfReportBuilderImpl addElement(ReportElement element) {
        elements.add(element);

        return this;
    }

    @Override
    public PdfReportBuilderImpl addHeading(String heading) {
        addElement(new ReportTextBox(configuration.getHeading1Text(), configuration.getLineDistance(), heading));
        addPadding(configuration.getHeadingPaddingAfter());

        return this;
    }

    @Override
    public PdfReportBuilder addImage(Resource resource, float width, float height) throws IOException {
        return addElement(new ReportImage(ImageIO.read(resource.getFile()), width, height));
    }

    @Override
    public PdfReportBuilder addLink(String text, String link) {
        return addElement(new ReportLink(text, link, configuration.getBodyText(), configuration.getLineDistance()));
    }

    @Override
    public PdfReportBuilder addPadding(float padding) {
        elements.add(new ReportPadding(padding));

        return this;
    }

    @Override
    public PdfReportBuilderImpl addText(String text) {
        addElement(new ReportTextBox(configuration.getBodyText(), configuration.getLineDistance(), text));

        return this;
    }

    @Override
    public PdfReportBuilderImpl addText(String text, PdfTextStyle textConfig) {
        addElement(new ReportTextBox(textConfig, configuration.getLineDistance(), text));

        return this;
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
    public PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource) throws IOException {
        return buildReport(fileName, pageConfig, templateResource, new PDDocument());
    }

    public PdfReport buildReport(String fileName, PdfPageLayout pageConfig, Resource templateResource, PDDocument document) throws IOException {
        PdfReportStructure report = this.buildReport(pageConfig);
        new PdfReportGenerator().generate(pageConfig, templateResource, report, document);
        return new PdfReport(fileName, document);
    }

    @Override
    public void registerFont(Resource resource) {
        try {
            LOG.info("Loading Font {}", resource);
            PdfFont.registerFont(PDType0Font.load(document, resource.getInputStream()));
        } catch (IOException e) {
            LOG.error("Failed to register font!", e);
        }
    }

    @Override
    public ReportTableBuilder startTable() {
        return new ReportTableBuilderImpl(configuration, this);
    }

    @Override
    public PdfReportBuilder withFooterOnAllPages(String left, String middle, String right) {
        PdfStyleSheet sheet = new DefaultPdfStyleSheet();
        sheet.setTableTitleText(configuration.getFooterText());

        ReportTable table = new ReportTableBuilderImpl(sheet, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        table.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        table.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        table.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        table.setBorder(false);

        fixedLineGenerators.add(new PdfFooterGenerator(table, PositionOfStaticElements.ON_ALL_PAGES));

        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnAllPages(ReportElement footerElement) {
        fixedLineGenerators.add(new PdfFooterGenerator(footerElement, PositionOfStaticElements.ON_ALL_PAGES));

        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnPages(String left, String middle, String right, PositionOfStaticElements footerPosition) {
        PdfStyleSheet sheet = new DefaultPdfStyleSheet();
        sheet.setTableTitleText(configuration.getFooterText());

        ReportTable table = new ReportTableBuilderImpl(sheet, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        table.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        table.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        table.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        table.setBorder(false);

        fixedLineGenerators.add(new PdfFooterGenerator(table, footerPosition));

        return this;
    }

    @Override
    public PdfReportBuilder withFooterOnPages(ReportElement footerElement, PositionOfStaticElements footerPosition) {
        fixedLineGenerators.add(new PdfFooterGenerator(footerElement, footerPosition));

        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnAllPages(String left, String middle, String right) {
        PdfStyleSheet sheet = new DefaultPdfStyleSheet();
        sheet.setTableTitleText(configuration.getFooterText());

        ReportTable table = new ReportTableBuilderImpl(sheet, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        table.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        table.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        table.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        table.setBorder(false);

        fixedLineGenerators.add(new PdfHeaderGenerator(table, PositionOfStaticElements.ON_ALL_PAGES));

        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnAllPages(ReportElement headerElement) {
        fixedLineGenerators.add(new PdfHeaderGenerator(headerElement, PositionOfStaticElements.ON_ALL_PAGES));

        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnPages(String left, String middle, String right, PositionOfStaticElements headerPosition) {
        PdfStyleSheet sheet = new DefaultPdfStyleSheet();
        sheet.setTableTitleText(configuration.getFooterText());

        ReportTable table = new ReportTableBuilderImpl(sheet, this).addColumn(left, 1).addColumn(middle, 1).addColumn(right, 1).build();
        table.setTextAlignInColumn(0, ReportAlignType.LEFT, false);
        table.setTextAlignInColumn(1, ReportAlignType.CENTER, false);
        table.setTextAlignInColumn(2, ReportAlignType.RIGHT, false);
        table.setBorder(false);

        fixedLineGenerators.add(new PdfHeaderGenerator(table, headerPosition));

        return this;
    }

    @Override
    public PdfReportBuilder withHeaderOnPages(ReportElement headerElement, PositionOfStaticElements headerPosition) {
        fixedLineGenerators.add(new PdfHeaderGenerator(headerElement, headerPosition));

        return this;
    }
}
