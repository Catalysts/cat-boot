package cc.catalysts.boot.i18n.service.impl;

import cc.catalysts.boot.i18n.dto.I18nDto;
import cc.catalysts.boot.i18n.service.I18nService;
import cc.catalysts.boot.i18n.service.ListResourceBundleMessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author Thomas Scheinecker
 */
public class I18nServiceImpl implements I18nService {

    private final ListResourceBundleMessageSource messageSource;

    public I18nServiceImpl(ListResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public I18nDto getAllValues() {
        Locale locale = LocaleContextHolder.getLocale();

        return new I18nDto(locale, messageSource.getAllMessages(locale));
    }
}
