package cc.catalysts.boot.i18n.dto;

import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Scheinecker
 */
public class I18nDto {
    private final Locale locale;
    private final Map<String, String> i18n;

    public I18nDto(final Locale locale, final Map<String, String> i18n) {
        this.locale = locale;
        this.i18n = i18n;
    }

    public Locale getLocale() {
        return locale;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }
}
