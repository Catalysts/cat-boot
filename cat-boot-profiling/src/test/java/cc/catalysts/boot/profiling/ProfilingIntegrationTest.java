package cc.catalysts.boot.profiling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProfiledApplication.class})
public class ProfilingIntegrationTest {

    @Autowired
    SimpleBean simpleBean;

    @Test
    public void contextStarts() {
        simpleBean.callMe();
        Assert.assertNotNull(MethodCallStatistics.lastStatistics);
        Assert.assertTrue(MethodCallStatistics.lastStatistics.prettyPrint().contains("SimpleBean.callMe"));
        Assert.assertTrue(MethodCallStatistics.lastStatistics.prettyPrint().contains("SimpleBean2.callMeAgain"));
    }
}
