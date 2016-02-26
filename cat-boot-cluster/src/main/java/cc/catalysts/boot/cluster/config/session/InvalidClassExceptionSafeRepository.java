package cc.catalysts.boot.cluster.config.session;

import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * Wrapper session-repository that deletes session objects which can no longer be deserialized
 * adapted from: https://github.com/spring-projects/spring-session/issues/280
 * </p>
 * <p>
 * This helps mitigate issues when by upgrading libraries (such as spring-security) the compatability of cluster
 * serialization gets broken.
 * In that case your users will simply be logged out but there won't be any more severe problems.
 * </p>
 *
 * @author Paul Klingelhuber
 */
public class InvalidClassExceptionSafeRepository<S extends ExpiringSession> implements SessionRepository<S> {
    private static final Logger LOG = getLogger(InvalidClassExceptionSafeRepository.class);
    private final SessionRepository<S> repository;
    private final RedisTemplate<String, ExpiringSession> sessionRedisTemplate;
    private final CounterService counterService;
    static final String BOUNDED_HASH_KEY_PREFIX = "spring:session:sessions:";

    public InvalidClassExceptionSafeRepository(SessionRepository<S> repository,
                                               RedisTemplate<String, ExpiringSession> sessionRedisTemplate,
                                               CounterService counterService) {
        this.repository = repository;
        this.sessionRedisTemplate = sessionRedisTemplate;
        this.counterService = counterService;
    }

    public S getSession(String id) {
        try {
            return repository.getSession(id);
        } catch (SerializationException e) {
            LOG.info("deleting non-deserializable session with key {}", id);
            // NOTE: deleting directly via redis instead of template since the repository.delete method would
            //  run into the same serializationissue again
            sessionRedisTemplate.delete(BOUNDED_HASH_KEY_PREFIX + id);
            counterService.increment("meter.cat.boot.session.deleteAfterDeserializationError");
            return null;
        }
    }

    public S createSession() {
        return repository.createSession();
    }

    public void save(S session) {
        repository.save(session);
    }

    public void delete(String id) {
        repository.delete(id);
    }
}
