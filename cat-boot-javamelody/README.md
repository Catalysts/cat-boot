# Java-Melody Integration

This module integrates JavaMelody into your Spring-Boot application. It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

The following configuration parameters are available (here with their default values):

```ini
# Set this property to true to entirely disable profiling with JavaMelody
javamelody.disabled = false

# The path under width JavaMelody should be available
monitoringPath = /monitoring

# A list of URLs that should not be profiled with JavaMelody
urlExcludePattern = (/webjars/.*|/css/.*|/images/.*|/fonts/.*|/ui/.*|/js/.*|/views/.*|/monitoring/.*|/lesscss/.*|/favicon.ico)
```
