package cc.catalysts.boot.i18n.service;

import org.springframework.context.HierarchicalMessageSource;

import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Scheinecker
 */
public interface ListResourceBundleMessageSource extends HierarchicalMessageSource {

    /**
     * Returns a map of key/value pairs of all configured message properties
     * (across all configured resource bundles).
     * Be aware that if 2 bundles specify the same key, the value of the first bundle
     * specifying a value for it, will be used, all later values will be ignored silently.
     *
     * @param locale current locale
     * @return key value pars
     */
    Map<String, String> getAllMessages(final Locale locale);
}
