package cc.catalysts.boot.thymeleaf.config;

import cc.catalysts.boot.thymeleaf3.WebjarConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Klaus Lehner
 */
@Configuration
public class CatBootThymeleafAutoConfiguration {

    @ConditionalOnClass(name = "org.thymeleaf.templatemode.TemplateMode")
    @Import(WebjarConfig.class)
    public static class Thymeleaf3Config {
    }


    @ConditionalOnMissingClass("org.thymeleaf.templatemode.TemplateMode")
    @Import(cc.catalysts.boot.thymeleaf2.WebjarConfig.class)
    public static class Thymeleaf2Config {
    }
}
