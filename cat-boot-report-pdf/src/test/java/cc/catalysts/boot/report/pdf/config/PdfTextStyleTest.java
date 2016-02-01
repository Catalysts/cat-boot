package cc.catalysts.boot.report.pdf.config;

import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

/**
 * @author Klaus Lehner
 */
public class PdfTextStyleTest {

    @Test
    public void fromConstructor() {
        PdfTextStyle config = new PdfTextStyle("10,Times-Roman,#000000");

        Assert.assertEquals(10, config.getFontSize());
        Assert.assertEquals("Times-Roman", config.getFont().getBaseFont());
        Assert.assertEquals(Color.black, config.getColor());
    }
}
