# Java-Melody Integration

This module integrates JavaMelody into your Spring-Boot application. It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

The following configuration parameters are available (here with their default values):

```
// Set this property to true to entirely disable profiling with JavaMelody
javamelody.disabled = false
```