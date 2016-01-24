# Java-Melody Integration

This module integrates JavaMelody (https://github.com/javamelody/javamelody/wiki) into your Spring-Boot application. 

## Integration

With Gradle

```
runtime('cc.catalysts.boot:cat-boot-javamelody:' + catBootVersion)
```

## Configuration

It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

The following configuration parameters are available (here with their default values):

```ini
# Set this property to true to entirely disable profiling with JavaMelody
javamelody.disabled = false

# The path under width JavaMelody should be available
javamelody.monitoringPath = /monitoring

# A list of URLs that should not be profiled with JavaMelody
javamelody.urlExcludePattern = (/webjars/.*|/css/.*|/images/.*|/fonts/.*|/ui/.*|/js/.*|/views/.*|/monitoring/.*|/lesscss/.*|/favicon.ico)
```
