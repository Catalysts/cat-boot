package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.elements.PdfBoxHelper;
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

        Assert.assertEquals(10.5, config.getFontSize(), delta);
        Assert.assertEquals("Times-Roman", config.getFont().getBasename());
        Assert.assertEquals(Color.black, config.getColor());
    }

    @Test
    public void testFloatSize() {
        float fontSize = 10.5f;
        PdfTextStyle config = new PdfTextStyle(fontSize, PdfFont.TIMES_ROMAN, Color.red, "regular");

        Assert.assertEquals(fontSize, config.getFontSize(), delta);
    }

    @Test
    public void testTextWidth() {
        float fontSize = 0.5f;
        PdfTextStyle config = new PdfTextStyle(fontSize, PdfFont.COURIER, Color.BLACK, "bold");

        Float textWidth = PdfBoxHelper.getTextWidth(config.getCurrentFontStyle(), config.getFontSize(), "Some text");

        Assert.assertTrue(textWidth > 0);
    }
}
