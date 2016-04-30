package cc.catalysts.boot.profiling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ProfiledApplication.class})
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
