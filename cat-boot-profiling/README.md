# Profiling

Simple built-in profiling without any extra tools 

## Integration

With Gradle

```
runtime('cc.catalysts.boot:cat-boot-profiling:' + catBootVersion)
```

## Configuration

This module comes with an AutoConfiguration.

## Usage

Use the annotation @MethodProfling on any Spring Bean to log method calls. Nested calls to multiple beans that have that
annotation are accumulated. As an example, if you have those two beans:

```java
@MethodProfiling
@Component
public class SimpleBean {

    @Autowired
    SimpleBean2 simpleBean2;

    public String callMe() {
        return simpleBean2.callMeAgain();
    }
}

@MethodProfiling
@Component
public class SimpleBean2 {

    public String callMeAgain() {
        return "Test";
    }
}
```

And you then perform a call to simpleBean.callMe(), then you might run into log output like this:

```
[2016-04-30 12:53:52.524] TRACE [main] --- c.c.b.p.SimpleBean: 
----------------------------------------------------------------------------------
   calls     avg ms   total ms      %   Signature
----------------------------------------------------------------------------------
       1      59,00         59  98,33   SimpleBean.callMe()
       1      29,00         29  48,33     SimpleBean2.callMeAgain()
```

Don't forget to set the log level of your target bean (the bean that holds the annotation) to TRACE.