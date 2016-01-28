package cc.catalysts.boot.report.pdf;

import cc.catalysts.boot.report.pdf.config.DefaultPdfStyleSheet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Klaus Lehner
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(value = DefaultPdfStyleSheet.class)
public class PdfReportModule {
}
