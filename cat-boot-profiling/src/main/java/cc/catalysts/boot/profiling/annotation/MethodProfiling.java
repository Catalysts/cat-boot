package cc.catalysts.boot.profiling.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you put this annotation on a spring bean (either on class or on method level) then all method calls are traced and logged
 * with with {@link cc.catalysts.boot.profiling.MethodCallProfiler} in case the log level of your target bean is set to <code>TRACE</code>.
 *
 * @author Klaus Lehner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MethodProfiling {

    /**
     * If this value is <code>true</code>, then we track call statistics and log them separately
     *
     * @return true, if call statistics should be printed
     */
    boolean logCallStatistics() default true;

    /**
     * If this value is <code>true</code>, we log the mehod call with all incoming and outgoing parameters
     *
     * @return
     */
    boolean logMethodCall() default true;

    /**
     * If {@link #logMethodCall()} is <code>true</code>, then we log all incoming and outgouing parameters. This value
     * limits the string-representation of each parameter to a maximum.
     *
     * @return die maximum length of the string-representation of an object that is logged
     */
    int maxToStringLength() default Integer.MAX_VALUE;


}
