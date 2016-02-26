package cc.catalysts.boot.cluster.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Paul Klingelhuber
 */
@Configuration
@ConditionalOnProperty(name = "catBootCluster.disabled", havingValue = "false", matchIfMissing = true)
@ComponentScan(basePackageClasses = CatBootClusterAutoconfiguration.class)
public class CatBootClusterAutoconfiguration {
}
