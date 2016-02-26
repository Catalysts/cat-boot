package cc.catalysts.boot.cluster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.aggregate.AggregateMetricReader;
import org.springframework.boot.actuate.metrics.export.MetricExportProperties;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Exports metrics to redis and even provides aggregate counts of all running instances.
 *
 * @author Paul Klingelhuber
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = "clustermetrics.disabled", havingValue = "false", matchIfMissing = true)
public class ClusterMetricsConfig {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private MetricExportProperties export;

    @Bean
    @ExportMetricWriter
    public MetricWriter metricWriter(MetricExportProperties export) {
        return new RedisMetricRepository(redisConnectionFactory,
                export.getRedis().getPrefix(), export.getRedis().getKey());
    }

    @Bean
    public PublicMetrics metricsAggregate() {
        return new MetricReaderPublicMetrics(aggregatesMetricReader());
    }

    private MetricReader globalMetricsForAggregation() {
        return new RedisMetricRepository(this.redisConnectionFactory,
                this.export.getRedis().getAggregatePrefix(), this.export.getRedis().getKey());
    }

    private MetricReader aggregatesMetricReader() {
        AggregateMetricReader repository = new AggregateMetricReader(globalMetricsForAggregation());
        return repository;
    }
}
