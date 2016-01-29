package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import cc.catalysts.boot.report.pdf.utils.ReportFontType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Klaus Lehner
 */
public class ReportTable implements ReportElement {

    private static final boolean DEFAULT_BORDER = false;
    private static final float DEFAULT_CELL_PADDING_LEFT_RIGHT = 2;
    private static final float DEFAULT_CELL_PADDING_TOP_BOTTOM = 0;
    private static final int BORDER_Y_DELTA = 1;
    private final PdfStyleSheet pdfStyleSheet;

    private float[] cellWidths;
    private ReportElement[][] elements;
    private ReportElement[] title;
    private boolean border = DEFAULT_BORDER;
    private boolean noBottomBorder;
    private boolean noTopBorder;
    private boolean placeLastBorder = true;
    private boolean enableExtraSplitting;
    private Collection<ReportImage.ImagePrintIntent> intents = new LinkedList<ReportImage.ImagePrintIntent>();

    /**
     * left and right cell padding
     */
    private float cellPaddingX = DEFAULT_CELL_PADDING_LEFT_RIGHT;
    private float cellPaddingY = DEFAULT_CELL_PADDING_TOP_BOTTOM;

    /**
     * @param cellWidths width of each column (the sum of elements must be 1)
     * @param elements   elements of each cell
     */
    public ReportTable(PdfStyleSheet pdfStyleSheet, float[] cellWidths, ReportElement[][] elements, ReportElement[] title) {
        this.pdfStyleSheet = pdfStyleSheet;
        if (elements == null || cellWidths == null) {
            throw new IllegalArgumentException("Arguments cant be null");
        }
        if (elements.length > 0 && cellWidths.length != elements[0].length) {
            throw new IllegalArgumentException("The cell widths must have the same number of elements as 'elements'");
        }
        if (title != null && title.length != cellWidths.length) {
            throw new IllegalArgumentException("Title must be null, or the same size as elements");
        }
        this.cellWidths = cellWidths;
        this.elements = elements;
        this.title = title;
    }

    public void setNoBottomBorder(boolean border) {
        this.noBottomBorder = border;
    }

    public void setNoTopBorder(boolean border) {
        this.noTopBorder = border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public void setExtraSplitting(boolean enableExtraSplitting) {
        this.enableExtraSplitting = enableExtraSplitting;
    }

    /**
     * @param cellPaddingX for left and right
     */
    public void setCellPaddingX(float cellPaddingX) {
        this.cellPaddingX = cellPaddingX;
    }

    /**
     * @param cellPaddingY for top and bottom
     */
    public void setCellPaddingY(float cellPaddingY) {
        this.cellPaddingY = cellPaddingY;
    }

    public boolean getExtraSplitting() {
        return enableExtraSplitting;
    }

    @Override
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth, Map<ReportFontType, PDFont> fontLibrary) throws IOException {
        if (title != null) {
            throw new IllegalStateException("title not implemented!");
        }
        float y = startY;
        int i = 0;
        for (ReportElement[] line : elements) {
            y = printLine(document, stream, pageNumber, startX, y, allowedWidth, line, fontLibrary);
            if (i == elements.length - 1 && noBottomBorder) {
                placeLastBorder = false;
            }
            placeBorders(stream, startY, y, startX, allowedWidth);
            i++;
        }
        return y;
    }

    private void placeBorders(PDPageContentStream stream, float startY, float endY, float x, float allowedWidth) throws IOException {
        if (border) {
            stream.setStrokingColor(0, 0, 0);
            stream.setLineWidth(0.3f);
            float y0 = startY - BORDER_Y_DELTA;
            float y1 = endY - (BORDER_Y_DELTA + 1);
            if (!noTopBorder) {
                stream.drawLine(x, y0, x + allowedWidth, y0);
            }
            if (placeLastBorder) {
                stream.drawLine(x, y1, x + allowedWidth, y1);
            }

            float currX = x;
            for (float width : cellWidths) {
                stream.drawLine(currX, y0, currX, y1);
                currX += width * allowedWidth;
            }
            stream.drawLine(currX, y0, currX, y1);
        }
    }

    private float printLine(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float y, float allowedWidth, ReportElement[] line, Map<ReportFontType, PDFont> fontLibrary) throws IOException {
        float x = startX + cellPaddingX;
        float minY = y;
        for (int i = 0; i < cellWidths.length; i++) {
            if (line[i] != null) {
                float yi = line[i].print(document, stream, pageNumber, x, y - cellPaddingY, cellWidths[i] * allowedWidth - cellPaddingX * 2, fontLibrary);
                intents.addAll(line[i].getImageIntents());
                minY = Math.min(minY, yi);
            }
            x += cellWidths[i] * allowedWidth;
        }
        return minY - cellPaddingY;
    }

    @Override
    public float getHeight(float allowedWidth) {
        float[] maxes = new float[elements.length];
        for (int i = 0; i < elements.length; i++) {
            maxes[i] = getLineHeight(elements[i], allowedWidth);
        }
        float max = 0;
        for (float f : maxes) {
            max += f;
        }
        return max;
    }

    @Override
    public boolean isSplitable() {
        return true;
    }

    @Override
    public float getFirstSegmentHeight(float allowedWidth) {
        if (elements != null && elements.length > 0) {
            return border ? getFirstSegmentHeightFromLine(elements[0], allowedWidth) + BORDER_Y_DELTA : getFirstSegmentHeightFromLine(elements[0], allowedWidth);
        } else {
            return 0;
        }
    }

    private float getFirstSegmentHeightFromLine(ReportElement[] line, float allowedWidth) {
        float maxHeight = 0f;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != null)
                maxHeight = Math.max(maxHeight, line[i].getFirstSegmentHeight(cellWidths[i] * allowedWidth - cellPaddingX * 2));
        }
        return maxHeight + 2 * cellPaddingY;
    }

    private float getLineHeight(ReportElement[] line, float allowedWidth) {
        float maxHeight = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != null) {
                maxHeight = Math.max(maxHeight, line[i].getHeight(cellWidths[i] * allowedWidth - cellPaddingX * 2));
            }
        }
        return maxHeight + 2 * cellPaddingY;
    }


    private ReportTable createNewTableWithClonedSettings(ReportElement[][] data) {
        ReportTable newTable = new ReportTable(pdfStyleSheet, cellWidths, data, title);
        newTable.setBorder(border);
        newTable.setCellPaddingX(cellPaddingX);
        newTable.setCellPaddingY(cellPaddingY);
        newTable.setExtraSplitting(enableExtraSplitting);
        return newTable;
    }

    @Override
    public Collection<ReportImage.ImagePrintIntent> getImageIntents() {
        return intents;
    }

    public ReportElement[] splitFirstCell(float allowedHeight, float allowedWidth) {
        ReportElement[] firstLineA = new ReportElement[elements[0].length];
        ReportElement[] firstLineB = new ReportElement[elements[0].length];
        boolean hasSecondPart = false;
        for (int i = 0; i < elements[0].length; i++) {
            ReportElement elem = elements[0][i];
            float width = cellWidths[i] * allowedWidth - 2 * cellPaddingX;
            if (elem != null && elem.isSplitable()) {
                ReportElement[] split = elem.split(width, allowedHeight);
                firstLineA[i] = split[0];
                firstLineB[i] = split[1];
                if (firstLineB[i] != null) {
                    hasSecondPart = true;
                }
            } else {
                firstLineA[i] = elem;
            }
        }

        if (hasSecondPart) {
            ReportElement[][] newMatrix = new ReportElement[elements.length][elements[0].length];
            newMatrix[0] = firstLineB;
            for (int i = 1; i < elements.length; i++) {
                newMatrix[i] = elements[i];
            }

            ReportTable firstLine = createNewTableWithClonedSettings(new ReportElement[][]{firstLineA});
            ReportTable nextLines = createNewTableWithClonedSettings(newMatrix);

            return new ReportElement[]{firstLine, nextLines};
        } else {
            return new ReportElement[]{this, null};
        }
    }

    @Override
    public ReportElement[] split(float allowedWidth, float allowedHeight) {
        float currentHeight = 0f;
        int i = 0;
        while (i < elements.length && (currentHeight + getLineHeight(elements[i], allowedWidth)) < allowedHeight) {
            currentHeight += getLineHeight(elements[i], allowedWidth);
            i++;
        }

        if (i > 0) {
            //they all fit until i-1, inclusive
            //check if the last row can be split
            ReportElement[][] extraRows = new ReportElement[2][elements[0].length];
            boolean splittable = false;
            if (enableExtraSplitting) {
                splittable = true;
                for (int j = 0; j < elements[i].length; j++) {
                    if (!elements[i][j].isSplitable() || currentHeight + elements[i][j].getFirstSegmentHeight(cellWidths[j] * allowedWidth - cellPaddingX * 2) + 2 * cellPaddingY >= allowedHeight) {
                        splittable = false;
                    }
                }

                if (splittable) {
                    for (int j = 0; j < elements[i].length; j++) {
                        if (elements[i][j].getHeight(cellWidths[j] * allowedWidth - cellPaddingX * 2) + currentHeight < allowedHeight) {
                            extraRows[0][j] = elements[i][j];
                            extraRows[1][j] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");
                        } else {
                            ReportElement[] extraSplit = elements[i][j].split(cellWidths[j] * allowedWidth - cellPaddingX * 2, allowedHeight - currentHeight - 2 * cellPaddingY);
                            extraRows[0][j] = extraSplit[0];
                            extraRows[1][j] = extraSplit[1];
                        }
                    }
                }
            }

            ReportElement[][] first = new ReportElement[splittable ? i + 1 : i][elements[0].length];
            ReportElement[][] next = new ReportElement[elements.length - i][elements[0].length];
            for (int j = 0; j < elements.length; j++) {
                if (j < i)
                    first[j] = elements[j];
                else
                    next[j - i] = elements[j];
            }
            if (splittable) {
                first[i] = extraRows[0];
                next[0] = extraRows[1];
            }
            ReportTable firstLine = createNewTableWithClonedSettings(first);
            ReportTable nextLines = createNewTableWithClonedSettings(next);

            return new ReportElement[]{firstLine, nextLines};
        } else {
            //this means first row does not fit in the given height
            ReportElement[][] first = new ReportElement[1][elements[0].length];
            ReportElement[][] next = new ReportElement[elements.length][elements[0].length];
            for (i = 1; i < elements.length; i++)
                next[i] = elements[i];
            for (i = 0; i < elements[0].length; i++) {
                ReportElement[] splits = elements[0][i].split(cellWidths[i] * allowedWidth - cellPaddingX * 2, allowedHeight - 2 * cellPaddingY);
                if (splits[0] != null)
                    first[0][i] = splits[0];
                else
                    first[0][i] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");

                if (splits[1] != null)
                    next[0][i] = splits[1];
                else
                    next[0][i] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");
            }
            ReportTable firstLine = createNewTableWithClonedSettings(first);
            ReportTable nextLines = createNewTableWithClonedSettings(next);

            return new ReportElement[]{firstLine, nextLines};
        }
    }

    @Override
    public ReportElement[] split(float allowedWidth) {
        ReportElement[][] first = new ReportElement[][]{elements[0]};
        ReportElement[][] next = Arrays.copyOfRange(elements, 1, elements.length);

        ReportTable firstLine = createNewTableWithClonedSettings(first);
        ReportTable nextLines = createNewTableWithClonedSettings(next);

        return new ReportElement[]{firstLine, nextLines};
    }

    @Override
    public void setFontLib(Map<ReportFontType, PDFont> fontLib) {
        for (ReportElement[] elementLine : elements) {
            for (ReportElement reportElement : elementLine) {
                if (reportElement != null) {
                    reportElement.setFontLib(fontLib);
                }
            }
        }
    }

    public void setTextAlignInColumn(int column, ReportAlignType alignType, boolean excludeHeader) {
        for (int i = excludeHeader ? 1 : 0; i < elements.length; i++) {
            ReportElement[] element = elements[i];
            if (element[column] instanceof ReportTextBox) {
                ((ReportTextBox) element[column]).setAlign(alignType);
            }
        }
    }
}
