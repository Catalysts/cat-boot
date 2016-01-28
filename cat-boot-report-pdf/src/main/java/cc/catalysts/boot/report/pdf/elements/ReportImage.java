package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ReportImage extends AbstractReportElement implements ReportElement {

    private BufferedImage img;
    private ImagePrintIntent intent;
    private float width;
    private float height;
    private ReportAlignType align = ReportAlignType.LEFT;

    /**
     * The image will be placed as is. Please provide the greatest size as possible.
     *
     * @param img    Buffered image
     * @param width  width of image on report
     * @param height height of image on report
     */
    public ReportImage(BufferedImage img, float width, float height) {
        this.img = img;
        this.width = width;
        this.height = height;
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float leftX, float startY, float allowedWidth, Map<ReportFontType, PDFont> fontLibrary) throws IOException {
        intent = new ImagePrintIntent(this, pageNumber, calcStartX(leftX, allowedWidth, width), startY);
        return startY - height;
    }

    @Override
    public float getHeight(float allowedWidth) {
        return height;
    }

    /**
     * <p>Call this method to print images. <b>Make sure that the streams are closed before calling this method </b></p>
     * <p>Normal print method doesn't work since: http://stackoverflow.com/questions/9326245/how-to-exactly-position-an-image-inside-an-existing-pdf-page-using-pdfbox</p>
     *
     * @param document   the pdDocument.
     * @param pageNumber page of image
     * @param x          location of image
     * @param y          location of image
     * @throws java.io.IOException
     */
    public void printImage(PDDocument document, int pageNumber, float x, float y) throws IOException {
        PDJpeg obj = new PDJpeg(document, img);
        PDPageContentStream currentStream = new PDPageContentStream(document, (PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber), true, false);
        currentStream.drawXObject(obj, x, y - height, width, height);
        currentStream.close();
    }

    private float calcStartX(float leftX, float allowedWidth, float imgWidth) {
        switch (getAlign()) {
            case LEFT:
                return leftX;
            case CENTER:
                return (allowedWidth - imgWidth) / 2 + leftX;
            default:
                throw new IllegalStateException("align type not implemented");
        }
    }

    @Override
    public Collection<ImagePrintIntent> getImageIntents() {
        if (intent == null) {
            throw new IllegalStateException("print must be called before getImage intents");
        }
        return Collections.singletonList(intent);
    }

    public void setAlign(ReportAlignType align) {
        this.align = align;
    }

    public ReportAlignType getAlign() {
        return align;
    }

    /**
     * This class is required, since pdf image printing has a bug. The img element must be created before the pdf box stream.
     */
    public static class ImagePrintIntent {
        private ReportImage img;
        private int page;
        private float x;
        private float y;

        public ImagePrintIntent(ReportImage img, int page, float x, float y) {
            this.img = img;
            this.page = page;
            this.x = x;
            this.y = y;
        }

        public ReportImage getImg() {
            return img;
        }

        public int getPage() {
            return page;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}
