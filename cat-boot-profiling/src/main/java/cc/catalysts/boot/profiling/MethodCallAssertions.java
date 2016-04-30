package cc.catalysts.boot.profiling;

import org.springframework.util.Assert;

import static cc.catalysts.boot.profiling.MethodCallStatistics.lastStatistics;

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
public final class MethodCallAssertions {
    public static void assertDuration(long millis) {
        Assert.notNull(lastStatistics);
        Assert.isTrue(lastStatistics.getDuration() < millis);
    }

    public static void assertDuration(String method, double millis) {
        Assert.notNull(lastStatistics);
        Assert.isTrue(lastStatistics.getDuration(method) < millis);
    }

    private MethodCallAssertions() {
    }
}
