package cc.catalysts.boot.thymeleaf2.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
public class WebjarsLinkProcessor extends AbstractAttrProcessor {
    private final String attribute;

    public WebjarsLinkProcessor(String attribute) {
        super(attribute);
        this.attribute = attribute;
    }

    @Override
    protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
        String[] value = element.getAttributeValue(attributeName).split(":");
        String webjarName = value[0];
        String filePath = value[1];

        element.removeAttribute(attributeName);

        element.setAttribute("th:" + attribute, String.format("@{/{path}/%s(path=${#webjars['%s'].path})}", filePath, webjarName));
        // reevaluate th:src
        element.setRecomputeProcessorsImmediately(true);

        return ProcessorResult.OK;
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
