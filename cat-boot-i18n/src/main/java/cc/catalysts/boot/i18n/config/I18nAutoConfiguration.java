package cc.catalysts.boot.i18n.config;

import cc.catalysts.boot.i18n.controller.ClientEnumController;
import cc.catalysts.boot.i18n.controller.I18nController;
import cc.catalysts.boot.i18n.service.I18nService;
import cc.catalysts.boot.i18n.service.ListResourceBundleMessageSource;
import cc.catalysts.boot.i18n.service.impl.I18nServiceImpl;
import cc.catalysts.boot.i18n.service.impl.ListResourceBundleMessageSourceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * @author Thomas Scheinecker
 */
@Configuration
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@Conditional(I18nAutoConfiguration.CatResourceBundleCondition.class)
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
@ConfigurationProperties(prefix = "spring.messages")
public class I18nAutoConfiguration extends MessageSourceAutoConfiguration {

    protected static class CatResourceBundleCondition extends ResourceBundleCondition {}

    private String[] getBasenames() {
        String basename = getBasename();
        if (StringUtils.hasText(basename)) {
            return StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename));
        }

        return null;
    }

    @Bean
    @Override
    public ListResourceBundleMessageSourceImpl messageSource() {
        ListResourceBundleMessageSourceImpl messageSource = new ListResourceBundleMessageSourceImpl();

        String[] basenames = getBasenames();
        if (basenames != null) {
            messageSource.setBasenames(basenames);
        }

        Charset encoding = getEncoding();
        if (encoding != null) {
            messageSource.setDefaultEncoding(encoding.name());
        }
        messageSource.setFallbackToSystemLocale(isFallbackToSystemLocale());
        messageSource.setCacheSeconds(getCacheSeconds());

        return messageSource;
    }

    @Bean
    I18nService i18nApi(ListResourceBundleMessageSource messageSource) {
        return new I18nServiceImpl(messageSource);
    }

    @Bean
    I18nController i18nController(I18nService i18NService) {
        return new I18nController(i18NService);
    }

    @Bean
    ClientEnumController clientEnumController(MessageSource messageSource) {
        return new ClientEnumController(messageSource);
    }
}
