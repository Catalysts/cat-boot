package cc.catalysts.boot.javamelody;

import net.bull.javamelody.Parameter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JavaMelodyConfigurationTest.Context.class)
public class JavaMelodyConfigurationTest {

    @Autowired
    FilterRegistrationBean javaMelody;

    @Value("${javamelody.monitoring-path}")
    String monitoringPath;

    @Test
    public void context() {
        Assert.assertNotNull(javaMelody);
        Assert.assertEquals("/profiling", monitoringPath);
        Assert.assertEquals("/profiling", javaMelody.getInitParameters().get(Parameter.MONITORING_PATH.getCode()));
    }

    @EnableAutoConfiguration
    @PropertySource("classpath:application.properties")
    @Configuration
    public static class Context {
    }
}
