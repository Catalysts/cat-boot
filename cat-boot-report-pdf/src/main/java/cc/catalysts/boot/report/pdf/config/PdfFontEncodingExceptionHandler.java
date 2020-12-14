package cc.catalysts.boot.report.pdf.config;

/**
 * Interface that allows custom handling of exceptions where some text cannot be rendered due to incompatible
 * characters and no supporting font.
 */
@FunctionalInterface
public interface PdfFontEncodingExceptionHandler {

    String handleFontEncodingException(String text, String codePointString, int start, int end);

}
