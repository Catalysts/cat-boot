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

# Enable monitoring of service methods of classes annotated with the spring stereotype annotation @Service
javamelody.enableSpringServiceMonitoring = false

# Enable monitoring of controller methods of classes annotated with the spring stereotype annotation @Controller
javamelody.enableSpringControllerMonitoring = false

```

If you run multiple applications using this module on the same machine, you can run into problems with concurrent access to the storage folder.
To solve this by giving them separate javamelody storage folders, simply set the following configuration option:

```ini
javamelody.storageDirectory = /tmp/javamelody-${server.port:0000}
```
