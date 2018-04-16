package cc.catalysts.boot.report.pdf.utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public class Utf8Utils {

    private static String replacement = "";

    private static Set<String> specialCharacters = Stream.of(
            "\u200B", "\u2009", "\u2010", "\u25FB", "\u0308", "\u0009", "\u2192")
            .collect(Collectors.toSet());

    /**
     * Those characters cannot be printed by PDFBox as they have a zero length
     */
    public static String removeCharactersWithZeroLength(String string) {
        for (String specialCharacter: specialCharacters){
            string = string.replace(specialCharacter, replacement);
        }

        return string;
    }

    public static void addSpecialCharacter(String specialCharacter) {
        specialCharacters.add(specialCharacter);
    }

    public static void setReplacement(String customReplacement) {
        replacement = customReplacement;
    }
}
