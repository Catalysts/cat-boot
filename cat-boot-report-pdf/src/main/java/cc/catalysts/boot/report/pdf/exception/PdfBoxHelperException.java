package cc.catalysts.boot.report.pdf.exception;

/**
 * @author Michael Nitsch, Catalysts GmbH
 */
public class PdfBoxHelperException extends RuntimeException {
    public PdfBoxHelperException(String message) {
        super(message);
    }

    public PdfBoxHelperException(String message, Throwable cause) {
        super(message, cause);
    }
}
