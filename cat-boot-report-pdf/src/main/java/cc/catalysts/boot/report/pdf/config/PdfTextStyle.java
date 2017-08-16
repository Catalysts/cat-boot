package cc.catalysts.boot.report.pdf.config;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.springframework.util.Assert;

import java.awt.*;

public class PdfTextStyle {


    private float fontSize;
    private PdfFont font;
    private PDColor color;
    private String style;

    public PdfTextStyle(float fontSize, PdfFont defaultFont, PDColor color, String style) {
        this.fontSize = fontSize;
        this.font = defaultFont;
        this.color = color;
        this.style = style;
    }

    /**
     * This constructor is used by spring when creating a font from properties.
     *
     * @param config e.g. 10,Times-Roman,#000000
     */
    public PdfTextStyle(String config) {
        Assert.hasText(config);
        String[] split = config.split(",");
        Assert.isTrue(split.length == 3, "config must look like: 10,Times-Roman,#000000");
        fontSize = Float.parseFloat(split[0]);
        font = PdfFont.getFont(split[1]);
        Color tempColor = new Color(Integer.valueOf(split[2].substring(1), 16));
        float[] components = {tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue()};
        color = new PDColor(components, PDDeviceRGB.INSTANCE);
    }

    public float getFontSize() {
        return fontSize;
    }

    public PdfFont getFont() {
        return font;
    }

    public PDColor getColor() {
        return color;
    }

    public PDFont getCurrentFontStyle() {
        return font.getStyle(style);
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
