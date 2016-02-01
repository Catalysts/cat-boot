package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.impl.PdfReportFilePrinter;
import cc.catalysts.boot.report.pdf.impl.PdfReportHttpResponsePrinter;
import cc.catalysts.boot.report.pdf.PdfReportPrinter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Klaus Lehner
 */
@Configuration
@ComponentScan(basePackageClasses = PdfReport.class)
@EnableConfigurationProperties(value = DefaultPdfStyleSheet.class)
public class PdfReportAutoConfiguration {

    @Bean
    PdfReportPrinter fileReportPrinter() {
        return new PdfReportFilePrinter();
    }

    @Bean
    @ConditionalOnClass(HttpServletResponse.class)
    PdfReportPrinter httpResponsePrinter() {
        return new PdfReportHttpResponsePrinter();
    }
}
