package cc.catalysts.boot.i18n;

import cc.catalysts.boot.i18n.dto.I18nDto;
import cc.catalysts.boot.i18n.service.I18nService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Klaus Lehner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = I18nIntegrationTest.Context.class)
public class I18nIntegrationTest {

    @Autowired
    I18nService i18nService;

    @Test
    public void fetchAllEntries() {
        final I18nDto allValues = i18nService.getAllValues();
        Assert.assertNotNull(allValues);
        Assert.assertEquals(3, allValues.getMessages().size());
        Assert.assertEquals("I'm a message", allValues.getMessages().get("sample.message"));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Context {
    }
}
