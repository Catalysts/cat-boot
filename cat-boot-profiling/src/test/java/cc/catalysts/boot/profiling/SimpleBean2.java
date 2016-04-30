package cc.catalysts.boot.profiling;

import cc.catalysts.boot.profiling.annotation.MethodProfiling;
import org.springframework.stereotype.Component;

/**
 * @author Klaus Lehner
 */
@MethodProfiling
@Component
public class SimpleBean2 {

    public String callMeAgain() {
        return "Test";
    }
}
