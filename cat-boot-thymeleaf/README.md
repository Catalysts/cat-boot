# Thymeleaf extensions

This modules enhances `spring-boot-starter-thymeleaf` by some useful dialects and processors.  

## Integration

With Gradle

```groovy
runtime('cc.catalysts.boot:cat-boot-thymeleaf:' + catBootVersion)
```

## Configuration

It comes with with an AutoConfiguration,
so if you are using Spring Boot's @EnableAutoConfiguration, then the configuration will be picked up automatically.

## WebJars-Support

WebJars are are a cool thing. You can simply add them like as JARs in your web apps, and they include all client-side resources
that you require for your web page (CSS, JS, LESS,...).

Adding WebJars to your application is simple as well, all you need to do is to add the JAR to your classpath, i.e. with Gradle:

```groovy
dependenciey {
   compile 'org.webjars.bower:bootstrap:3.3.6'
}
```

In your Thymeleaf-Templates you can then refer to the artifacts in that classpath like that:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8"/>
    <title>Dummy Title</title>
    <link rel="stylesheet" th:href="@{/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css}"/>
</head>
<body>

<script th:src="@{/webjars/jquery/3.3.6/dist/js/bootstrap.min.js}"></script>

</body>
</html>
```

So, you already might see the problem here: We have to enter the version number (3.3.6) on multiple places. If you want
to upgrade the bootstrap webjar, you need to be careful to not forget it in all templates.

And even worse, if your webjar is updated due to a transitive dependency, then you even won't recognize that you have
to update your templates.

The module cat-boot-thymeleaf helps you there in connection with the cat-boot-webjar Gradle plugin. This plugin generates
the following class for you as part of the build process: (see the official docu for more information https://github.com/Catalysts/cat-gradle-plugins#webjars)

```java
public class Webjars {
    public static class Webjar {
        public final String group;
        public final String name;
        public final String version;
        public final String path;

        private Webjar(String group, String name, String version) {
            this.group = group;
            this.name = name;
            this.version = version;
            this.path = "webjars/" + name + "/" + version;
        }
    }

    public static final Map<String, Webjar> webjars;

    static {
        Map<String, Webjar> webjarsMap = new HashMap<String, Webjar>();
        webjarsMap.put("bootstrap", new Webjar("org.webjars.bower", "bootstrap", "3.3.5"));
        webjars = Collections.unmodifiableMap(webjarsMap);
    }
}
```

What it does in behind is to analyze your dependency graph and collect all WebJars that your application will finally consist of.

All you need to do now with cat-boot-thymeleaf is to register this class in your ApplicationContext:

```java
@Bean
public WebjarRegistrar wpcWebjars() {
    return () -> Webjars.webjars;
}
```

Only by doing this, you can then modify your Thymeleaf template as follows:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8"/>
    <title>Dummy Title</title>
    <link rel="stylesheet" webjars:href="bootstrap:dist/css/bootstrap.min.css"/>
</head>
<body>

<script webjars:src="jquery:dist/jquery.min.js"></script>
<script webjars:src="bootstrap:dist/js/bootstrap.min.js"></script>

</body>
</html>
```

Spot the difference? No need to worry anymore about versioning of the client artifacts anymore, cat-boot-thymeleaf
will automatically transform the webjars-URLs to the versioned URLs that you know from above.