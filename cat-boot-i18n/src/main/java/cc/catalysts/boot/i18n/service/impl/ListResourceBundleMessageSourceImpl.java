package cc.catalysts.boot.i18n.service.impl;

import cc.catalysts.boot.i18n.service.ListResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.*;

/**
 * @author Thomas Scheinecker
 */
public class ListResourceBundleMessageSourceImpl extends ResourceBundleMessageSource implements
        ListResourceBundleMessageSource {

    private String[] basenames;

    @Override
    public Map<String, String> getAllMessages(final Locale locale) {
        final HashMap<String, String> allMessages = new HashMap<>();
        for (String basename : basenames) {

            final ResourceBundle currentBundle = getResourceBundle(basename, locale);
            final Enumeration<String> keys = currentBundle.getKeys();

            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                final String value = currentBundle.getString(key);

                if (!allMessages.containsKey(key)) {
                    allMessages.put(key, value);
                }
            }
        }

        return allMessages;
    }

    @Override
    public void setBasenames(String... basenames) {
        super.setBasenames(basenames);
        if (basenames != null) {
            this.basenames = new String[basenames.length];
            for (int i = 0; i < basenames.length; i++) {
                String basename = basenames[i];
                this.basenames[i] = basename.trim();
            }
        } else {
            this.basenames = new String[0];
        }
    }
}
