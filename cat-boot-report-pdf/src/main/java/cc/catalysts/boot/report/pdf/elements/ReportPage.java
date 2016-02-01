package cc.catalysts.boot.report.pdf.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Bochis, Catalysts GmbH
 */
public class ReportPage {
    private List<ReportElement> pageElements;

    public ReportPage(List<ReportElement> elements) {
        pageElements = elements != null ? elements : new ArrayList<ReportElement>();
        pageElements.add(new ReportPageBreak());
    }

    public List<ReportElement> getPageElements() {
        return pageElements;
    }
}
