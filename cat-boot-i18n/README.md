# I18n Rest-Endpoint

This module gives you some predefined endpoints for web applications that need to access your message properties on the backend.

This module works best in combination with `cat-boot-i18n-angular` as this provides an AngularJS client-side implementation of
all these endpoints.

## Integration

With Gradle

```
runtime('cc.catalysts.boot:cat-boot-i18n:' + catBootVersion)
```

## Configuration

It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

At the moment no custom configuration properties are available.

## Message-API

This modules configures your Spring-Boot application with an Rest-Endpoint `/api/i18n` which will list all available properties 
configured in your `MessageSource`.
The endpoint is useful for reusing your translation files on the client side, for example within an angular application.

## Enums on the client

Did you ever the case to display comboboxes on the client showing all possible values of server-side enum, localized? This
module helps you in doing that:

Use the interface `ClientEnumRegistry` (it is registered automatically in your spring context) to register {@link Enum}s on the 
client so that they can be used e.g. in ComboBoxes for selections.
 
You need to have the translations in your message files (provided by `org.springframework.context.MessageSource`
with the prefix `enum.<EnumName>`.

Example: If you have the following Enum:
```java
public enum Gender {
    M,
    F
}

then you need to have in your `message.properties`:

```ini
enum.Gender.M = male
enum.Gender.F = female
```

To register the enum, call:

```java
clientEnumRegistry.register(Gender.class);
```

during application startup.

The cat-boot-i18n Plugin will then expose all possible values for this enum and provide it to the client.