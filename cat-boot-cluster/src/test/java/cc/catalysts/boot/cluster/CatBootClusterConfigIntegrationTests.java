package cc.catalysts.boot.cluster;

import cc.catalysts.boot.cluster.config.CatBootClusterAutoconfiguration;
import cc.catalysts.boot.cluster.config.session.InvalidClassExceptionSafeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.export.MetricExportProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

/**
 * @author Paul Klingelhuber
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestPropertySource
@ContextConfiguration(classes = {CatBootClusterConfigIntegrationTests.Config.class, CatBootClusterAutoconfiguration.class})
public class CatBootClusterConfigIntegrationTests {

    @Autowired
    SessionRepository sessionRepository;

    @Test
    public void testSessionRepository() {
        Assert.isTrue(sessionRepository instanceof InvalidClassExceptionSafeRepository);
    }

    @Test
    public void testSessionTimeout() {
        RedisOperationsSessionRepository innerRepository = (RedisOperationsSessionRepository)ReflectionTestUtils
                .getField(sessionRepository, "repository");
        Integer sessionTimeout = (Integer) ReflectionTestUtils.getField(innerRepository, "defaultMaxInactiveInterval");
        Assert.isTrue(Integer.valueOf(12345).equals(sessionTimeout));
    }

    @Configuration
    public static class Config {
        @Bean
        public MetricExportProperties metricExportProperties() {
            MetricExportProperties metricExportProperties = new MetricExportProperties();
            metricExportProperties.setUpDefaults();
            return metricExportProperties;
        }

        @Bean
        public CounterService counterService() {
            return Mockito.mock(CounterService.class);
        }

        @Bean
        public JedisConnectionFactory jedisConnectionFactory() {
            JedisConnectionFactory mock = Mockito.mock(JedisConnectionFactory.class);
            Mockito.when(mock.getConnection()).thenReturn(Mockito.mock(JedisConnection.class));
            return mock;
        }

    }

}
