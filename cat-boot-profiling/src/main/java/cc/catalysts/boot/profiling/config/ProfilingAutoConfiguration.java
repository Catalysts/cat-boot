package cc.catalysts.boot.profiling.config;

import cc.catalysts.boot.profiling.MethodCallInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Klaus Lehner
 */
@Configuration
@ConditionalOnProperty(name = "profiling.disabled", havingValue = "false", matchIfMissing = true)
public class ProfilingAutoConfiguration {

    @Bean
    MethodCallInterceptor methodCallInterceptor() {
        return new MethodCallInterceptor();
    }
}
