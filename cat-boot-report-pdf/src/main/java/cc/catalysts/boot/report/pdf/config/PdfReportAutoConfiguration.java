package cc.catalysts.boot.report.pdf.config;

import cc.catalysts.boot.report.pdf.PdfReport;
import cc.catalysts.boot.report.pdf.PdfReportPrinter;
import cc.catalysts.boot.report.pdf.impl.PdfReportFilePrinter;
import cc.catalysts.boot.report.pdf.impl.PdfReportHttpResponsePrinter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author Klaus Lehner
 */
@Configuration
@ComponentScan(basePackageClasses = PdfReport.class)
@EnableConfigurationProperties(value = DefaultPdfStyleSheet.class)
public class PdfReportAutoConfiguration {

    @Bean
    PdfReportPrinter<File> fileReportPrinter() {
        return new PdfReportFilePrinter();
    }


    @Configuration
    @ConditionalOnClass(HttpServletResponse.class)
    static class ServletConfiguration {
        @Bean
        PdfReportPrinter<HttpServletResponse> httpResponsePrinter() {
            return new PdfReportHttpResponsePrinter();
        }

    }
}
