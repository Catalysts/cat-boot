package cc.catalysts.boot.profiling;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Klaus Lehner
 */
public class MethodCallStatistics {

    static final ThreadLocal<CallStatistics> statistics = new ThreadLocal<CallStatistics>();

    static CallStatistics lastStatistics;

    public static void beginMethodCall(String method) {
        CallStatistics callStatistics = statistics.get();
        if (callStatistics != null) {
            callStatistics.beginMethodCall(method);
        }
    }

    public static void endMethodCall(String method) {
        CallStatistics callStatistics = statistics.get();
        if (callStatistics != null) {
            callStatistics.endMethodCall(method);
        }
    }

    static class CallStatistics {
        private final Map<String, Long> methodCount = new LinkedHashMap<String, Long>();
        private final Map<String, Long> methodTimeSpent = new HashMap<String, Long>();
        private final Map<String, Long> methodLastStart = new HashMap<String, Long>();
        private final Map<String, Integer> methodDepth = new HashMap<String, Integer>();

        private int depth;
        private long beginTimestamp;
        private long endTimestamp;

        public long getDuration() {
            return endTimestamp - beginTimestamp;
        }

        public double getDuration(String method) {
            Assert.isTrue(methodCount.containsKey(method));

            long count = methodCount.get(method);
            long timeSpend = methodTimeSpent.get(method);

            return (double) timeSpend / (double) count;
        }

        public String prettyPrint() {
            long totalSpent = getDuration();

            StringBuilder builder = new StringBuilder();

            builder.append('\n');
            builder.append("----------------------------------------------------------------------------------\n");
            builder.append("   calls     avg ms   total ms      %   Signature\n");
            builder.append("----------------------------------------------------------------------------------\n");

            for (Map.Entry<String, Long> entry : methodCount.entrySet()) {
                String name = entry.getKey();
                long count = entry.getValue();
                Long timeSpend = methodTimeSpent.get(entry.getKey());
                if (timeSpend == null) {

                }
                Integer depth = methodDepth.get(entry.getKey());

                builder.append(StringUtils.leftPad(String.format("%d", count), 8));
                builder.append(StringUtils.leftPad(String.format("%.2f", (double) timeSpend / (double) count), 11));
                builder.append(StringUtils.leftPad(String.format("%d", timeSpend), 11));
                builder.append(StringUtils.leftPad(String.format("%.2f", totalSpent > 0 ? (double) timeSpend / (double) totalSpent * 100.0 : 0.0), 7));
                builder.append(StringUtils.leftPad("", depth * 2 + 3));
                builder.append(name);
                builder.append('\n');
            }

            return builder.toString();
        }

        public void endMethodCall(String method) {
            Long timeSpent = methodTimeSpent.get(method);
            if (timeSpent == null) {
                timeSpent = 0L;
            }

            Long lastStart = methodLastStart.get(method);
            Assert.notNull(lastStart);

            timeSpent += System.currentTimeMillis() - lastStart;
            depth--;

            methodDepth.put(method, depth);
            methodTimeSpent.put(method, timeSpent);
        }

        public void beginMethodCall(String method) {
            Long count = methodCount.get(method);
            if (count == null) {
                count = 0L;
            }

            count++;
            depth++;

            methodCount.put(method, count);
            methodLastStart.put(method, System.currentTimeMillis());
        }

        public void begin() {
            beginTimestamp = System.currentTimeMillis();
        }

        public void end() {
            endTimestamp = System.currentTimeMillis();
        }
    }
}