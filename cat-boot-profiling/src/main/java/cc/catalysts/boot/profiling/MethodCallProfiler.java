package cc.catalysts.boot.profiling;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 */
public class MethodCallProfiler {
    private static final String ELK_DURATION = "duration";


    protected Object traceMethodCall(ProceedingJoinPoint pjp) throws Throwable {
        return traceMethodCall(pjp, true, true, Integer.MAX_VALUE);
    }

    protected Object traceMethodCall(ProceedingJoinPoint pjp, boolean logCallStatistics, boolean logMethodCall, int maxToStringLength) throws Throwable {
        Logger logger = getLogger(pjp);
        String method = pjp.getSignature().toShortString();

        boolean initialCall = false;
        MethodCallStatistics.CallStatistics callStatistics = MethodCallStatistics.statistics.get();
        if (callStatistics == null) {
            initialCall = true;
            callStatistics = new MethodCallStatistics.CallStatistics();
            callStatistics.begin();
            MethodCallStatistics.statistics.set(callStatistics);
        }

        Object result = null;

        try {
            beginMethodCall(method);
            result = pjp.proceed();
            return result;
        } catch (Throwable t) {
            result = t;
            throw t;
        } finally {
            endMethodCall(method);

            if (initialCall) {
                callStatistics.end();

                if (logger.isTraceEnabled()) {
                    if (logMethodCall) {
                        // log duration in separate field so that we can calculate with it in ELK
                        MDC.put(ELK_DURATION, String.valueOf(callStatistics.getDuration()));
                        logMethodCall(logger, pjp.getSignature(), pjp.getArgs(), result, maxToStringLength);
                        // but only log if for the first call, not for second one as well where we have written descriptions
                        MDC.remove(ELK_DURATION);
                    }

                    if (logCallStatistics && logCallStatistics(pjp)) {
                        logger.trace(callStatistics.prettyPrint());
                    }

                }
                MethodCallStatistics.lastStatistics = callStatistics;

                MethodCallStatistics.statistics.remove();
            }
        }
    }

    protected boolean logCallStatistics(ProceedingJoinPoint pjp) {
        return true;
    }

    protected Logger getLogger(ProceedingJoinPoint pjp) {
        return LoggerFactory.getLogger(pjp.getTarget().getClass());
    }

    public static void beginMethodCall(String method) {
        MethodCallStatistics.beginMethodCall(method);
    }

    public static void endMethodCall(String method) {
        MethodCallStatistics.endMethodCall(method);
    }

    private void logMethodCall(Logger logger, Signature signature, Object[] args, Object result, int maxReturnLength) {
        MethodSignature methodSignature = (MethodSignature) signature;
        String method = methodSignature.getMethod().getDeclaringClass().getSimpleName() + "." + methodSignature.getMethod().getName();

        logger.trace("{}({}){}",
                method,
                formatArguments(args, maxReturnLength),
                formatResult(signature.getDeclaringType(), result, maxReturnLength)
        );
    }

    private String formatArguments(Object[] args, int maxToStringLength) {
        final StringBuilder sb = new StringBuilder();

        boolean hasElements = false;
        for (Object arg : args) {
            hasElements = true;
            sb
                    .append(formatObject(arg, maxToStringLength))
                    .append(", ");
        }

        if (hasElements) {
            sb.deleteCharAt(sb.length() - 2);
        }

        return sb.toString();
    }

    protected String formatResult(Class returnType, Object result, int maxToStringLength) {
        if (result == null) {
            if (returnType.equals(Void.TYPE)) {
                return "";
            } else {
                return " returns null";
            }
        } else if (result instanceof Exception) {
            return " caused a " + result.getClass().getSimpleName();
        } else {
            return " returns " + formatObject(result, maxToStringLength);
        }
    }

    protected String formatObject(Object result, int maxReturnLength) {
        if (result instanceof byte[]) {
            return "byte[" + ((byte[]) result).length + "]";
        }
        if (result instanceof Iterable) {
            StringBuilder sb = new StringBuilder();

            boolean hasElements = false;
            for (Object item : (Iterable) result) {
                hasElements = true;
                if (sb.length() > maxReturnLength) {
                    break;
                }
                sb
                        .append(toString(item))
                        .append(", ");
            }

            if (hasElements) {
                sb.deleteCharAt(sb.length() - 2);
            }

            sb.insert(0, '[');
            sb.append(']');

            return StringUtils.abbreviate(sb.toString(), maxReturnLength);
        } else {
            return StringUtils.abbreviate(toString(result), maxReturnLength);
        }
    }

    private String toString(Object obj) {
        if (obj == null) {
            return "null";
        }

        String s = obj.toString();
        if (s != null && s.startsWith(obj.getClass().getName() + "@")) {
            return ReflectionToStringBuilder.reflectionToString(obj, ToStringStyle.SHORT_PREFIX_STYLE);
        } else {
            return s;
        }
    }
}
