package cc.catalysts.boot.cluster.config;


import cc.catalysts.boot.cluster.config.session.InvalidClassExceptionSafeRepository;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;

/**
 * Session clustering support via redis. Configures the redis session timeout via the
 * <code>server.session-timeout</code> value.
 *
 * @author Paul Klingelhuber
 */
@Configuration
@EnableRedisHttpSession
public class SpringSessionConfig {

    /**
     * reconfigure session-timeout in clustered mode so it matches the session-timeout of non-clustered mode
     */
    @Primary
    @Bean
    public SessionRepository sessionRepository(RedisTemplate<String, ExpiringSession> sessionRedisTemplate,
                                               CounterService counterService, ServerConfig serverConfig) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate);
        sessionRepository.setDefaultMaxInactiveInterval(serverConfig.getSessionTimeout());
        // to prevent issues when upgrading spring-security versions (incompatible SecurityContext serialization)
        //  wrap the sessionRepository again
        return new InvalidClassExceptionSafeRepository(sessionRepository, sessionRedisTemplate, counterService);
    }

    @ConditionalOnMissingBean(JedisConnectionFactory.class)
    @Bean
    public JedisConnectionFactory connectionFactory(RedisConfig config) {
        if (StringUtils.isEmpty(config.getHost()) || StringUtils.isEmpty(config.getPort())) {
            throw new IllegalStateException("missing required redis configuration properties " +
                    "(spring.redis.host or spring.redis.port)");
        }
        JedisShardInfo shardInfo = new JedisShardInfo(config.host, config.port, "redisServer");
        shardInfo.setPassword(config.password);
        return new JedisConnectionFactory(shardInfo);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        return new StringRedisTemplate(jedisConnectionFactory);
    }

    @Component
    @ConfigurationProperties(prefix = "server")
    static class ServerConfig {
        private static final int DEFAULT_SESSION_TIMEOUT = 3600;

        private Integer sessionTimeout = DEFAULT_SESSION_TIMEOUT;

        public Integer getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }

    /**
     * simple configuration for sessions
     */
    @Component
    @ConfigurationProperties(prefix = "spring.redis")
    static class RedisConfig {
        private String host = "localhost";
        private int port = Protocol.DEFAULT_PORT;
        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
