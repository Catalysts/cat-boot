package cc.catalysts.boot.profiling;

import cc.catalysts.boot.profiling.annotation.MethodProfiling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Klaus Lehner
 */
@MethodProfiling
@Component
public class SimpleBean {

    @Autowired
    SimpleBean2 simpleBean2;

    public String callMe() {
        return simpleBean2.callMeAgain();
    }
}
