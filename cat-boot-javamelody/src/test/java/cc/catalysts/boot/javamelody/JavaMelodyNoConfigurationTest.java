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
@ContextConfiguration(classes = JavaMelodyNoConfigurationTest.Context.class)
public class JavaMelodyNoConfigurationTest {

    @Autowired(required = false)
    FilterRegistrationBean javaMelody;

    @Test
    public void context() {
        Assert.assertNull(javaMelody);
    }

    @EnableAutoConfiguration
    @PropertySource("classpath:application-nojm.properties")
    @Configuration
    public static class Context {
    }
}
