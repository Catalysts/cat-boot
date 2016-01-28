package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PdfReportModule.class)
public class PdfServiceIntegrationTest {

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private DefaultPdfStyleSheet defaultConfig;

    @Test
    public void loadContext() {
        Assert.assertNotNull(pdfReportService);
        Assert.assertNotNull(defaultConfig);
        Assert.assertEquals(1, defaultConfig.getLineDistance(), 0.01);
    }
}
