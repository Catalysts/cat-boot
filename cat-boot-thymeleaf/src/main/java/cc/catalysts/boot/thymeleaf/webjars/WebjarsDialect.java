package cc.catalysts.boot.thymeleaf.webjars;

import cc.catalysts.boot.thymeleaf.webjars.processor.WebjarsLinkProcessor;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.*;

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
public class WebjarsDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    private final Collection<WebjarRegistrar> webjarRegistrars;

    public WebjarsDialect(Collection<WebjarRegistrar> webjarRegistrars) {
        this.webjarRegistrars = webjarRegistrars;
    }

    @Override
    public Set<IProcessor> getProcessors() {
        Set<IProcessor> processors = new HashSet<>();

        processors.add(new WebjarsLinkProcessor("src"));
        processors.add(new WebjarsLinkProcessor("href"));

        return Collections.unmodifiableSet(processors);
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(IProcessingContext processingContext) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> webjars = new HashMap<>();
        if (webjarRegistrars != null) {
            for (WebjarRegistrar webjarRegistrar : webjarRegistrars) {
                webjars.putAll(webjarRegistrar.getWebjarMap());
            }
        }
        map.put("webjars", webjars);
        return map;
    }

    @Override
    public String getPrefix() {
        return "webjars";
    }
}
