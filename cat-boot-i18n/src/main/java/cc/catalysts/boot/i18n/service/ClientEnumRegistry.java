package cc.catalysts.boot.i18n.service;

/**
 * <p>Use this interface to register {@link Enum}s on the client so that they can be used e.g. in ComboBoxes for selections.</p>
 *
 * <p>You need to have the translations in your message files (provided by {@link org.springframework.context.MessageSource}
 * with the prefix {@code enum.<EnumName>}.
 * </p>
 *
 * <p>Example: If you have the following Enum:</p>
 *
 * <pre>
 * public enum Gender {
 *    M,
 *    F
 * }</pre>
 *
 * then you need to have in your <code>message.properties</code>:
 *
 * <pre>
 * enum.Gender.M = male
 * enum.Gender.F = female
 * </pre>
 *
 *
 * @author Klaus Lehner
 */
public interface ClientEnumRegistry {


    /**
     * Registriert ein Enum für den Client, als Name wird der nicht vollqualifizierte Name der Klasse genommen
     *
     * @param enumClazz die Enum-Klasse, die am Client verwendet werden soll
     */
    void registerClientEnum(Class<? extends Enum> enumClazz);

    /**
     * Registriert ein Enum für den Client
     *
     * @param name      der Name, unter dem das Enum registriert werden soll
     * @param enumClazz die Enum-Klasse, die am Client verwendet werden soll
     */
    void registerClientEnum(String name, Class<? extends Enum> enumClazz);
}
