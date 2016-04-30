package cc.catalysts.boot.profiling;

import cc.catalysts.boot.profiling.annotation.MethodProfiling;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Interceptes call calls which are annotated with {@link MethodProfiling}.
 *
 * @author Klaus Lehner
 */
@Aspect
@Component
public class MethodCallInterceptor extends MethodCallProfiler {
    @Pointcut("within(@cc.catalysts.boot.profiling.annotation.MethodProfiling *) || execution(@cc.catalysts.boot.profiling.annotation.MethodProfiling * * (..)))")
    public void monitored() {
    }

    @Around("monitored()")
    public Object requestMapping(final ProceedingJoinPoint pcp) throws Throwable {
        Signature signature = pcp.getSignature();
        if (signature instanceof MethodSignature) {
            Method method = ((MethodSignature) signature).getMethod();

            MethodProfiling annotation = AnnotationUtils.findAnnotation(method, MethodProfiling.class);
            if (annotation == null) {
                annotation = AnnotationUtils.findAnnotation(pcp.getTarget().getClass(), MethodProfiling.class);
            }
            if (annotation != null) {
                return traceMethodCall(pcp, annotation.logCallStatistics(), annotation.logMethodCall(), annotation.maxToStringLength());
            }
        }
        return traceMethodCall(pcp);
    }
}
