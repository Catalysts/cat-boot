# Redis session clustering autoconfiguration

This module integrates Spring-Session (http://projects.spring.io/spring-session/) and configures it with useful defaults.
It also integrates Spring-Boot-Actuator (https://spring.io/guides/gs/actuator-service/) since when you go to a clustering setup, you will sooner or later need this kind of visibility into your application.

## Features

### Safe Session Deserialization

By default deserialization errors of session objects will stop your users in their tracks. This may happen more often than you think and is a very sneaky problem to have.
This problem will typically hit you when you upgrade some dependency which exposes classes which are used within the session object. One example is spring-security.
Upgrading spring-security can lead to all your user sessions becoming unusable.
By itself this wouldn't be so bad, but in most configuratoins your users would end up seeing some error page, and maybe even be unable to log in unless they manage to destroy their session-cookie.

The autoconfiguration in this module takes care of this and will simply destroy these invalid sessions, all your users will see is that they have to login again.


### Cluster-Metrics

It configures metric-publishing to redis and aggregation of metrics. This means in a clustered setup you will see aggregate counts of your monitoring values.

## Integration

With Gradle

```
runtime('cc.catalysts.boot:cat-boot-cluster:' + catBootVersion)
```

## Configuration

It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

The following configuration parameters are available (here with their default values):

```ini
# Set this property to true to disable publishing metrics to redis.
clustermetrics.disabled = false

```

Furthermore take a look at the configuration options for spring-boot-actuator.
