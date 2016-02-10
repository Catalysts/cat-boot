package cc.catalysts.boot.thymeleaf.config;

import cc.catalysts.boot.thymeleaf.webjars.WebjarRegistrar;
import cc.catalysts.boot.thymeleaf.webjars.WebjarsDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author Klaus Lehner
 */
@Configuration
public class CatBootThymeleafAutoConfiguration {

    @Autowired(required = false)
    Collection<WebjarRegistrar> webjarRegistrars;

    @Bean
    WebjarsDialect webjarsDialect() {
        return new WebjarsDialect(webjarRegistrars);
    }
}
