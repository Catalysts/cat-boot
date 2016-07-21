package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.elements.*;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.utils.ReportFooterOnPages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfReportStructure {

    private final PdfStyleSheet configuration;
    private List<ReportElement> elements = new ArrayList<ReportElement>();
    private List<ReportElementStatic> staticElements = new ArrayList<ReportElementStatic>();
    private List<ReportElementStatic> staticElementsForEachPage = new ArrayList<ReportElementStatic>();

    public PdfReportStructure(PdfStyleSheet configuration) {
        this.configuration = configuration;
    }

    public PdfStyleSheet getConfiguration() {
        return configuration;
    }

    public void addElement(ReportElement elem) {
        elements.add(elem);
    }

    public List<ReportElement> getElements() {
        return elements;
    }

    public void addPage(ReportPage page) {
        for (ReportElement element : page.getPageElements()) {
            addElement(element);
        }
    }

    public void addStaticElement(ReportElementStatic elem) {
        staticElements.add(elem);
    }

    public List<ReportElementStatic> getStaticElements() {
        return staticElements;
    }

    public void addStaticElementsForEachPage(ReportElementStatic... elements) {
        staticElementsForEachPage.addAll(Arrays.asList(elements));
    }

    public void expandPagesStaticElements(int totalPages) {
        for (int i = staticElementsForEachPage.size() - 1; i >= 0; --i) {
            ReportElementStatic elem = staticElementsForEachPage.get(i);
            ReportElement baseElement = elem.getBase();
            for (int pageNo = 0; pageNo < totalPages; pageNo++) {
                setFooterPageNumbers(baseElement, pageNo, totalPages);
                if (baseElement instanceof ReportTable) {
                    ReportTable baseTable = (ReportTable) baseElement;
                    for (int row = baseTable.getElements().length - 1; row >= 0 ; --row){
                        for (int col = baseTable.getElements()[row].length - 1; col >= 0; --col) {
                            setFooterPageNumbers(baseTable.getElements()[row][col], pageNo, totalPages);
                        }
                    }
                }
                if ((elem.getFooterOnPages() == ReportFooterOnPages.ALL) ||
                        (elem.getFooterOnPages() == ReportFooterOnPages.ALL_BUT_FIRST && pageNo != 0)
                        || (elem.getFooterOnPages() == ReportFooterOnPages.ALL_BUT_LAST && pageNo != (totalPages - 1)))
                    addStaticElement(new ReportElementStatic(baseElement, pageNo, elem.getX(), elem.getY(), elem.getWidth(), elem.getFooterOnPages()));
            }
        }
    }

    public void setFooterPageNumbers(ReportElement footerElement, int pageNo, int totalPages){
        if (footerElement instanceof ReportTextBox) {
            ReportTextBox footerTextBox = (ReportTextBox) footerElement;
            footerTextBox.setText(footerTextBox.getText().replaceAll("%PAGE_NUMBER%", pageNo + 1 + "").replaceAll("%TOTAL_PAGES%", totalPages + ""));
        }
    }

}
