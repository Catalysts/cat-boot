package cc.catalysts.boot.report.pdf.elements;

import cc.catalysts.boot.report.pdf.config.PdfStyleSheet;
import cc.catalysts.boot.report.pdf.utils.ReportAlignType;
import cc.catalysts.boot.report.pdf.utils.ReportVerticalAlignType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * <p><b>IMPORTANT:</b> Although this class is publicly visible, it is subject to change and may not be implemented by clients!</p>
 *
 * @author Klaus Lehner
 */
public class ReportTable implements ReportElement {
    private static boolean LAYOUTING_ASSERTIONS_ENABLED = false;

    private static final boolean DEFAULT_BORDER = false;
    private static final float DEFAULT_CELL_PADDING_LEFT_RIGHT = 2;
    private static final float DEFAULT_CELL_PADDING_TOP_BOTTOM = 2;
    private static final int BORDER_Y_DELTA = 0;
    private final PdfStyleSheet pdfStyleSheet;

    private float[] cellWidths;
    private ReportVerticalAlignType[] cellAligns;
    private ReportElement[][] elements;
    private ReportElement[] title;
    private boolean border = DEFAULT_BORDER;
    private boolean noBottomBorder;
    private boolean noTopBorder;
    private boolean drawInnerHorizontal = true;
    private boolean drawInnerVertical = true;
    private boolean drawOuterVertical = true;
    private boolean enableExtraSplitting;
    private boolean isSplitable = true;
    private Collection<ReportImage.ImagePrintIntent> intents = new LinkedList<ReportImage.ImagePrintIntent>();

    /**
     * left and right cell padding
     */
    private float cellPaddingX = DEFAULT_CELL_PADDING_LEFT_RIGHT;
    private float cellPaddingY = DEFAULT_CELL_PADDING_TOP_BOTTOM;

    /**
     * @param cellWidths    width of each column (the sum of elements must be 1)
     * @param elements      elements of each cell
     * @param pdfStyleSheet the stylesheet to be used for this table
     * @param title         the titles for the report (first row)
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
        this.cellAligns = new ReportVerticalAlignType[cellWidths.length];
        Arrays.fill(cellAligns, ReportVerticalAlignType.TOP);
        this.elements = elements;
        this.title = title;
    }

    public void setNoInnerBorders(boolean noInnerBorders) {
        this.drawInnerHorizontal = !noInnerBorders;
        this.drawInnerVertical = !noInnerBorders;
    }

    public void setDrawInnerVertical(boolean drawInnerVertical) {
        this.drawInnerVertical = drawInnerVertical;
    }

    public void setDrawInnerHorizontal(boolean drawInnerHorizontal) {
        this.drawInnerHorizontal = drawInnerHorizontal;
    }

    public void setNoBottomBorder(boolean border) {
        this.noBottomBorder = border;
    }

    public void setNoTopBorder(boolean border) {
        this.noTopBorder = border;
    }

    public void setDrawOuterVertical(boolean drawOuterVertical) {
        this.drawOuterVertical = drawOuterVertical;
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
    public float print(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float startY, float allowedWidth) throws IOException {
        if (title != null) {
            throw new IllegalStateException("title not implemented!");
        }
        float y = startY;
        float previousY = y;
        int lineIndex = 0;

        for (ReportElement[] line : elements) {
            float calculatedHeight = LAYOUTING_ASSERTIONS_ENABLED ? getLineHeight(line, allowedWidth) : -1;
            y = printLine(document, stream, pageNumber, startX, y, allowedWidth, line);
            float actualHeight = previousY - y;
            if (LAYOUTING_ASSERTIONS_ENABLED && calculatedHeight != actualHeight) {
                throw new RuntimeException(String.format("Layout algorithm bug: layouting height calculation reported "
                                + "different height (%s) than painting code (%s) in table with %s lines, current line index: %s",
                        calculatedHeight, actualHeight, elements.length, lineIndex));
            }
            boolean isFirstLine = lineIndex == 0;
            boolean isLastLine = lineIndex == elements.length - 1;
            placeBorders(stream, previousY, y, startX, allowedWidth, isFirstLine, isLastLine);
            previousY = y;
            lineIndex++;
        }
        return y;
    }

    private void placeBorders(PDPageContentStream stream, float startY, float endY, float x, float allowedWidth,
                              boolean isFirstLine, boolean isLastLine) throws IOException {
        if (!border) {
            return;
        }
        stream.setStrokingColor(0, 0, 0);
        stream.setLineWidth(0.3f);
        float y0 = startY;
        float y1 = endY;
        float x1 = x + allowedWidth;
        if (drawInnerHorizontal) {
            if (!noBottomBorder || noBottomBorder && !isLastLine) {
                drawLine(stream, x, x1, y1, y1);
            }
        }

        // top border
        if (!noTopBorder && isFirstLine) {
            drawLine(stream, x, x1, y0, y0);
        }
        // bottom border
        if (!noBottomBorder && isLastLine) {
            drawLine(stream, x, x1, y1, y1);
        }

        float currentX = x;
        for (int i = 0; i < cellWidths.length; i++) {
            float width = cellWidths[i];
            if (
                    (i == 0 && drawOuterVertical) || // left
                            (i > 0 && drawInnerVertical) // inner
            ) {
                drawLine(stream, currentX, currentX, y0, y1);
            }
            currentX += width * allowedWidth;
        }
        // draw last
        if (drawOuterVertical) {
            drawLine(stream, currentX, currentX, y0, y1);
        }
    }

    private void drawLine(PDPageContentStream stream, float x0, float x1, float y0, float y1) throws IOException {
        stream.moveTo(x0, y0);
        stream.lineTo(x1, y1);
        stream.stroke();
    }

    private float calculateVerticalAlignment(ReportElement[] line, int elementIndex, float y, float allowedWidth) {
        float yPos = 0;
        float lineHeight = getLineHeight(line, allowedWidth);
        switch (cellAligns[elementIndex]) {
            case TOP:
                yPos = y - cellPaddingY;
                break;
            case BOTTOM:
                yPos = y - cellPaddingY - lineHeight + line[elementIndex].getHeight(cellWidths[elementIndex] * allowedWidth - 2 * cellPaddingX);
                break;
            case MIDDLE:
                yPos = y - cellPaddingY - lineHeight / 2 + line[elementIndex].getHeight(cellWidths[elementIndex] * allowedWidth - 2 * cellPaddingX) / 2;
                break;
            default:
                throw new IllegalArgumentException("Vertical align type " + cellAligns[elementIndex] + " not implemented for tables");
        }
        return yPos;
    }

    /**
     * draws a line.
     *
     * @return the new y position of the bottom of the line just drawn
     */
    private float printLine(PDDocument document, PDPageContentStream stream, int pageNumber, float startX, float y, float allowedWidth, ReportElement[] line) throws IOException {
        float x = startX + cellPaddingX;
        // minY = furthest that any cell has expanded to the bottom (min since coordinate system starts at the bottom)
        float minY = y;
        for (int i = 0; i < cellWidths.length; i++) {
            if (line[i] != null) {
                float yi = 0;
                float yPos = calculateVerticalAlignment(line, i, y, allowedWidth);

                final float columnNetWidth = getAllowedNetColumnWidth(allowedWidth, i);
                if (line[i] instanceof ReportImage) {
                    ReportImage reportImage = (ReportImage) line[i];
                    autoShrinkExcessiveImage(columnNetWidth, reportImage);

                    yi = line[i].print(document, stream, pageNumber, x, yPos, columnNetWidth);
                    reportImage.printImage(document, pageNumber, x, yPos);
                } else {
                    yi = line[i].print(document, stream, pageNumber, x, yPos, columnNetWidth);
                }
                intents.addAll(line[i].getImageIntents());
                minY = Math.min(minY, yi);
            }
            x += cellWidths[i] * allowedWidth;
        }
        return minY - cellPaddingY;
    }

    private void autoShrinkExcessiveImage(float maxWidth, ReportImage reportImage) {
        float initialWidth = reportImage.getWidth();
        final float newHeight = reportImage.getHeight() * maxWidth / initialWidth;
        // only auto-shrink, don't auto-grow
        if (maxWidth <= reportImage.getWidth() || newHeight <= reportImage.getHeight()) {
            reportImage.setWidth(maxWidth);
            reportImage.setHeight(newHeight);
        }
    }

    @Override
    public float getHeight(float allowedWidth) {
        float[] maxes = new float[elements.length];
        for (int lineIndex = 0; lineIndex < elements.length; lineIndex++) {
            maxes[lineIndex] = getLineHeight(elements[lineIndex], allowedWidth);
        }
        float max = 0;
        for (float f : maxes) {
            max += f;
        }
        return max;
    }

    @Override
    public boolean isSplitable() {
        return isSplitable;
    }

    public void setSplitable(boolean isSplitable) {
        this.isSplitable = isSplitable;
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
            if (line[i] != null) {
                maxHeight = Math.max(maxHeight, line[i].getFirstSegmentHeight(cellWidths[i] * allowedWidth - cellPaddingX * 2));
            }
        }
        return maxHeight + 2 * cellPaddingY;
    }

    private float getLineHeight(ReportElement[] line, float allowedWidth) {
        float maxHeight = 0;
        float currentHeight;
        for (int columnIndex = 0; columnIndex < line.length; columnIndex++) {
            final float columnNetWidth = getAllowedNetColumnWidth(allowedWidth, columnIndex);
            if (line[columnIndex] != null) {
                if (line[columnIndex] instanceof ReportImage) {
                    ReportImage reportImage = (ReportImage) line[columnIndex];
                    autoShrinkExcessiveImage(columnNetWidth, reportImage);
                    currentHeight = reportImage.getHeight();
                } else {
                    currentHeight = line[columnIndex].getHeight(columnNetWidth);
                }
                maxHeight = Math.max(maxHeight, currentHeight);
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
    public float getHeightOfElementToSplit(float allowedWidth, float allowedHeight) {
        float currentHeight = 0f;
        int i = 0;
        while (i < elements.length && (currentHeight + getLineHeight(elements[i], allowedWidth)) < allowedHeight) {
            currentHeight += getLineHeight(elements[i], allowedWidth);
            i++;
        }

        return getLineHeight(elements[i], allowedWidth);
    }

    @Override
    public ReportElement[] split(float allowedWidth, float allowedHeight) {
        float currentHeight = 0f;
        int lineIndex = 0;
        while (lineIndex < elements.length && (currentHeight + getLineHeight(elements[lineIndex], allowedWidth)) < allowedHeight) {
            currentHeight += getLineHeight(elements[lineIndex], allowedWidth);
            lineIndex++;
        }

        if (lineIndex > 0) {
            //they all fit until i-1, inclusive
            //check if the last row can be split
            ReportElement[][] extraRows = new ReportElement[2][elements[0].length];
            boolean splittable = false;
            if (enableExtraSplitting) {
                splittable = true;
                for (int j = 0; j < elements[lineIndex].length; j++) {
                    if (!elements[lineIndex][j].isSplitable() || currentHeight + elements[lineIndex][j].getFirstSegmentHeight(cellWidths[j] * allowedWidth - cellPaddingX * 2) + 2 * cellPaddingY >= allowedHeight) {
                        splittable = false;
                    }
                }

                if (splittable) {
                    for (int j = 0; j < elements[lineIndex].length; j++) {
                        if (elements[lineIndex][j].getHeight(cellWidths[j] * allowedWidth - cellPaddingX * 2) + currentHeight < allowedHeight) {
                            extraRows[0][j] = elements[lineIndex][j];
                            extraRows[1][j] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");
                        } else {
                            ReportElement[] extraSplit = elements[lineIndex][j].split(cellWidths[j] * allowedWidth - cellPaddingX * 2, allowedHeight - currentHeight - 2 * cellPaddingY);
                            extraRows[0][j] = extraSplit[0];
                            extraRows[1][j] = extraSplit[1];
                        }
                    }
                }
            }

            ReportElement[][] first = new ReportElement[splittable ? lineIndex + 1 : lineIndex][elements[0].length];
            ReportElement[][] next = new ReportElement[elements.length - lineIndex][elements[0].length];
            for (int j = 0; j < elements.length; j++) {
                if (j < lineIndex)
                    first[j] = elements[j];
                else
                    next[j - lineIndex] = elements[j];
            }
            if (splittable) {
                first[lineIndex] = extraRows[0];
                next[0] = extraRows[1];
            }
            ReportTable firstLine = createNewTableWithClonedSettings(first);
            ReportTable nextLines = createNewTableWithClonedSettings(next);

            return new ReportElement[]{firstLine, nextLines};
        } else {
            //this means first row does not fit in the given height
            ReportElement[][] first = new ReportElement[1][elements[0].length];
            ReportElement[][] next = new ReportElement[elements.length][elements[0].length];
            for (lineIndex = 1; lineIndex < elements.length; lineIndex++)
                next[lineIndex] = elements[lineIndex];
            for (lineIndex = 0; lineIndex < elements[0].length; lineIndex++) {
                ReportElement[] splits = elements[0][lineIndex].split(cellWidths[lineIndex] * allowedWidth - cellPaddingX * 2, allowedHeight - 2 * cellPaddingY);
                if (splits[0] != null)
                    first[0][lineIndex] = splits[0];
                else
                    first[0][lineIndex] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");

                if (splits[1] != null)
                    next[0][lineIndex] = splits[1];
                else
                    next[0][lineIndex] = new ReportTextBox(pdfStyleSheet.getBodyText(), pdfStyleSheet.getLineDistance(), "");
            }
            ReportTable firstLine = createNewTableWithClonedSettings(first);
            ReportTable nextLines = createNewTableWithClonedSettings(next);

            return new ReportElement[]{firstLine, nextLines};
        }
    }

    /**
     * Gets the net column width (usable space for content, equals column width minus padding).
     */
    private float getAllowedNetColumnWidth(float allowedTableWidth, int columnIndex) {
        return getAllowedGrossColumnWidth(allowedTableWidth, columnIndex) - cellPaddingX * 2;
    }

    /**
     * Gets the gross column width (total width of the column).
     */
    private float getAllowedGrossColumnWidth(float allowedWidth, int columnIndex) {
        return cellWidths[columnIndex] * allowedWidth;
    }

    @Override
    public ReportElement[] split(float allowedWidth) {
        ReportElement[][] first = new ReportElement[][]{elements[0]};
        ReportElement[][] next = Arrays.copyOfRange(elements, 1, elements.length);

        ReportTable firstLine = createNewTableWithClonedSettings(first);
        ReportTable nextLines = createNewTableWithClonedSettings(next);

        return new ReportElement[]{firstLine, nextLines};
    }

    public void setTextAlignInColumn(int column, ReportAlignType alignType, boolean excludeHeader) {
        for (int i = excludeHeader ? 1 : 0; i < elements.length; i++) {
            ReportElement[] element = elements[i];
            if (element[column] instanceof ReportTextBox) {
                ((ReportTextBox) element[column]).setAlign(alignType);
            }
        }
    }

    public void setVerticalAlignInColumn(int column, ReportVerticalAlignType alignType) {
        cellAligns[column] = alignType;
    }

    public ReportElement[][] getElements() {
        return elements;
    }

    public ReportElement[] getTitle() {
        return title;
    }

    public float[] getCellWidths() {
        return cellWidths;
    }

    public PdfStyleSheet getPdfStyleSheet() {
        return pdfStyleSheet;
    }

    public static void setLayoutingAssertionsEnabled(boolean enabled) {
        LAYOUTING_ASSERTIONS_ENABLED = enabled;
    }

}
