package cc.catalysts.boot.i18n.dto;

import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Scheinecker
 */
public class I18nDto {
    private final Locale locale;
    private final Map<String, String> messages;

    public I18nDto(final Locale locale, final Map<String, String> messages) {
        this.locale = locale;
        this.messages = messages;
    }

    public Locale getLocale() {
        return locale;
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}
