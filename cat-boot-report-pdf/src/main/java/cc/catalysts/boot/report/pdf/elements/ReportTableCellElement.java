package cc.catalysts.boot.report.pdf.elements;

import java.awt.image.BufferedImage;

/**
 * Created by sfarcas on 7/18/2016.
 */
public class ReportTableCellElement {

    private String text;
    private BufferedImage img;
    private ReportTable table;

    public ReportTableCellElement(String text, BufferedImage img, ReportTable table){
        this.text = text;
        this.img = img;
        this.table = table;
    }

    public String getText() {
        return text;
    }

    public BufferedImage getImg() {
        return img;
    }

    public ReportTable getTable() {
        return table;
    }

}
