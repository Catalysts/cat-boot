package cc.catalysts.boot.thymeleaf.config;

import cc.catalysts.boot.thymeleaf.webjars.WebjarRegistrar;
import cc.catalysts.boot.thymeleaf.webjars.WebjarsDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Klaus Lehner
 */
@Configuration
public class CatBootThymeleafAutoConfiguration {

    @Autowired(required = false)
    Collection<WebjarRegistrar> webjarRegistrars = Collections.emptySet();

    @Bean
    WebjarsDialect webjarsDialect() {
        return new WebjarsDialect(webjarRegistrars);
    }
}
