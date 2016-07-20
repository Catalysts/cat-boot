package cc.catalysts.boot.report.pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Dominik Hurnaus
 */
@ConfigurationProperties(prefix = "cat-boot.report.pdf.stylesheet", ignoreUnknownFields = false)
public class DefaultPdfStyleSheet extends PdfStyleSheet {

}
