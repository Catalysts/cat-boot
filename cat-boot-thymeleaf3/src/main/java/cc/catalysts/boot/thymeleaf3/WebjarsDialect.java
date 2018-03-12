package cc.catalysts.boot.thymeleaf3;

import cc.catalysts.boot.thymeleaf.webjars.WebjarRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.*;

public class WebjarsDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {
    private final Collection<WebjarRegistrar> webjarRegistrars;

    @Autowired
    public WebjarsDialect(Collection<WebjarRegistrar> webjarRegistrars) {
        super("webjars", "webjars", 0);
        this.webjarRegistrars = webjarRegistrars;
    }


    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return new HashSet<>(Arrays.asList(
                new AbstractAttributeTagProcessor(TemplateMode.HTML, "webjars", null, false, "href", true, 0, true) {
                    @Override
                    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
                        String[] value = attributeValue.split(":");
                        String webjarName = value[0];
                        String filePath = value[1];

                        structureHandler.replaceAttribute(attributeName, "th:href", String.format("@{/{path}/%s(path=${#webjars['%s'].path})}", filePath, webjarName));
                    }
                },
                new AbstractAttributeTagProcessor(TemplateMode.HTML, "webjars", null, false, "src", true, 0, true) {
                    @Override
                    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
                        String[] value = attributeValue.split(":");
                        String webjarName = value[0];
                        String filePath = value[1];

                        structureHandler.replaceAttribute(attributeName, "th:src", String.format("@{/{path}/%s(path=${#webjars['%s'].path})}", filePath, webjarName));
                    }
                }
        ));
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return new HashSet<>(Arrays.asList("webjars"));
            }

            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                if (Objects.equals(expressionObjectName, "webjars")) {
                    Map<String, Object> webjars = new HashMap<>();
                    for (WebjarRegistrar webjarRegistrar : webjarRegistrars) {
                        webjars.putAll(webjarRegistrar.getWebjarMap());
                    }
                    return webjars;
                }

                return null;
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return true;
            }
        };
    }
}
