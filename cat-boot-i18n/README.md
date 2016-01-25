# I18n Rest-Endpoint

This modules configures your Spring-Boot application with an Rest-Endpoint `/api/i18n` which will list all available properties configured via `spring.messages.basename`.
The endpoint is usefull for reusing your translation files on the client side, for example within an angular application.

## Integration

With Gradle

```
runtime('cc.catalysts.boot:cat-boot-i18n:' + catBootVersion)
```

## Configuration

It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

At the moment no custom configuration properties are available.