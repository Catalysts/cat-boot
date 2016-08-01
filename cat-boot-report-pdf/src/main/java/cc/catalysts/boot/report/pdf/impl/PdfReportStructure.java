package cc.catalysts.boot.report.pdf.impl;

import cc.catalysts.boot.report.pdf.elements.*;
import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.utils.ReportStaticElementOnPages;

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
            for (int pageNo = 0; pageNo < totalPages; pageNo++) {
                ReportStaticElementOnPages config = elem.getFooterOnPages();
                if (pageNo == 0 &&
                        config == ReportStaticElementOnPages.ALL_BUT_FIRST) {
                    continue;
                } else if (pageNo == (totalPages - 1) &&
                        config == ReportStaticElementOnPages.ALL_BUT_LAST) {
                    continue;
                }

                addStaticElement(
                        new ReportElementStatic(getFooterElementWithPagesSet(elem.getBase(), pageNo, totalPages),
                                pageNo,
                                elem.getX(),
                                elem.getY(),
                                elem.getWidth(),
                                elem.getFooterOnPages())
                );
            }
        }
    }

    public ReportElement getFooterElementWithPagesSet(ReportElement baseElement, int pageNo, int totalPages) {
        if (baseElement instanceof ReportTextBox) {
            ReportTextBox oldFooterTextBox = (ReportTextBox) baseElement;
            String newText = oldFooterTextBox.getText().replaceAll("%PAGE_NUMBER%", pageNo + 1 + "").replaceAll("%TOTAL_PAGES%", totalPages + "");
            return new ReportTextBox(oldFooterTextBox, newText);
        } else if (baseElement instanceof ReportTable) {
            ReportTable baseTable = (ReportTable) baseElement;
            ReportElement[][] oldElements = baseTable.getElements();
            ReportElement[][] newElements = new ReportElement[oldElements.length][];
            for (int row = oldElements.length - 1; row >= 0; --row) {
                newElements[row] = new ReportElement[oldElements[row].length];
                for (int col = oldElements[row].length - 1; col >= 0; --col) {
                    newElements[row][col] = getFooterElementWithPagesSet(oldElements[row][col], pageNo, totalPages);
                }
            }
            return new ReportTable(baseTable.getPdfStyleSheet(), baseTable.getCellWidths(), newElements, baseTable.getTitle());
        } else {
            return baseElement;
        }
    }
}