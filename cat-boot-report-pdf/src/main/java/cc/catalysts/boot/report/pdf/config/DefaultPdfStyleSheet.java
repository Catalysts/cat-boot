package cc.catalysts.boot.report.pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Dominik Hurnaus
 */
@ConfigurationProperties(prefix = "cat.reporting.pdf.default", locations = "classpath:pdfreport.yaml", ignoreUnknownFields = false)
public class DefaultPdfStyleSheet extends PdfStyleSheet {
}
