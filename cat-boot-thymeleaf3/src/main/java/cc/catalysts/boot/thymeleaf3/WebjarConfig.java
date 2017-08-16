package cc.catalysts.boot.thymeleaf3;

import cc.catalysts.boot.thymeleaf.webjars.WebjarRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class WebjarConfig {

    @Autowired(required = false)
    Collection<WebjarRegistrar> webjarRegistrars = Collections.emptySet();

    @Bean
    WebjarsDialect thymeleaf3WebjarsDialect() {
        return new WebjarsDialect(webjarRegistrars);
    }
}
