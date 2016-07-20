package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.elements.*;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;

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
            for (int j = 0; j < totalPages; j++) {
                setFooterPageNumbers(baseElement, j, totalPages);
                if (baseElement instanceof ReportTable) {
                    ReportTable baseTable = (ReportTable) baseElement;
                    for (int k = baseTable.getElements().length - 1; k >= 0 ; --k){
                        for (int l = baseTable.getElements()[k].length - 1; l >= 0; --l) {
                            System.out.println(l);
                            setFooterPageNumbers(baseTable.getElements()[k][l], j, totalPages);
                        }
                    }
                }
                if (j != 0 || !elem.isExcludeOnFirstPage())
                    addStaticElement(new ReportElementStatic(baseElement, j, elem.getX(), elem.getY(), elem.getWidth(), elem.isExcludeOnFirstPage()));
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
