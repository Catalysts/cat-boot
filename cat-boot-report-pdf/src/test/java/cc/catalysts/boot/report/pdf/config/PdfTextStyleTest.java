package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.elements.PdfBoxHelper;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

/**
 * @author Klaus Lehner
 */
public class PdfTextStyleTest {

    private Float delta = 0.0001f;

    @Test
    public void fromConstructor() {
        PdfTextStyle config = new PdfTextStyle("10.5,Times-Roman,#000000");

        PDColor black = new PDColor(new float[] {0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);

        Assert.assertEquals(10.5, config.getFontSize(), delta);
        Assert.assertEquals("Times-Roman", config.getFont().getBasename());
        Assert.assertEquals(black.getColorSpace(), config.getColor().getColorSpace());
    }

    @Test
    public void testFloatSize() {
        float fontSize = 10.5f;
        PDColor red = new PDColor(new float[] {1.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
        PdfTextStyle config = new PdfTextStyle(fontSize, PdfFont.TIMES_ROMAN, red, "regular");

        Assert.assertEquals(fontSize, config.getFontSize(), delta);
    }

    @Test
    public void testTextWidth() {
        float fontSize = 0.5f;
        PDColor black = new PDColor(new float[] {0.0f, 0.0f, 0.0f}, PDDeviceRGB.INSTANCE);
        PdfTextStyle config = new PdfTextStyle(fontSize, PdfFont.COURIER, black, "bold");

        Float textWidth = PdfBoxHelper.getTextWidth(config.getCurrentFontStyle(), config.getFontSize(), "Some text");

        Assert.assertTrue(textWidth > 0);
    }
}
