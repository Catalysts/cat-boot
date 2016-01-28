package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface ReportElement {

    /**
     * @param document     PdfBox document
     * @param stream       PdfBox stream
     * @param pageNumber   current page number (0 based)
     * @param startX       starting x
     * @param startY       starting y
     * @param allowedWidth maximal width of segment
     * @return the Y position of the next line
     * @throws java.io.IOException
     */
    float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth, Map<ReportFontType, PDFont> fontLibrary) throws IOException;

    /**
     * Height of this segment
     *
     * @param allowedWidth allowed with of the segment
     * @return height of the segment
     */
    float getHeight(float allowedWidth);

    /**
     * In case the element may be split over pages, this method should return true
     *
     * @return may the element be split over multiple pages
     */
    boolean isSplitable();

    /**
     * in case isSplitable is true, this method will be called.
     *
     * @return the height of the first nonSplitable segment
     */
    float getFirstSegmentHeight(float allowedWidth);

    /**
     * If the element my be split this method should return two elements. The first unsplitable elements, and the rest.
     * The second arrays element may be null
     *
     * @param allowedWidth allowed with of the segment
     * @return Arrays with <b>two</b> report elements
     */
    ReportElement[] split(float allowedWidth);

    /**
     * Will split the report element, so the height of the first segment will the maximum value less or equal to the allowed height value.
     *
     * @param allowedWidth  width of report element
     * @param allowedHeight max height of first segment.
     * @return two report elements
     */
    ReportElement[] split(float allowedWidth, float allowedHeight);

    /**
     * Returns all the images that should have been printed by this element
     *
     * @return collection, can't be null, migth be empty
     */
    Collection<ReportImage.ImagePrintIntent> getImageIntents();

    /**
     * Will be called before element is used
     *
     * @param fontLib map with all the usable fonts
     */
    void setFontLib(Map<ReportFontType, PDFont> fontLib);
}
