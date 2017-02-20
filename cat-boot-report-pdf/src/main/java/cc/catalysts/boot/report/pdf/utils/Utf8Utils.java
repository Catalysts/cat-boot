package cc.catalysts.boot.report.pdf.utils;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public class Utf8Utils {

    /**
     * Those characters cannot be printed by PDFBox as they have a zero length
     */
    public static String removeCharactersWithZeroLength(String string) {
        return string.replace("\u200B", "").replace("\u2009", "").replace("\u2010", "").replace("\u25FB", "").replace("\u0308", "");
    }
}
