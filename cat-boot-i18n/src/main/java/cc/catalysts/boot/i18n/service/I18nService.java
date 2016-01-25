package cc.catalysts.boot.i18n.service;

import cc.catalysts.boot.i18n.dto.I18nDto;
import org.springframework.stereotype.Service;

/**
 * @author Thomas Scheinecker
 */
@Service
public interface I18nService {

    /**
     * Retrieves all messages available via the {@link org.springframework.context.MessageSource MessageSource}
     *
     * @return Key Value Pair -&gt; Key represents the label key, Value represents the label in the current locale
     */
    I18nDto getAllValues();
}
