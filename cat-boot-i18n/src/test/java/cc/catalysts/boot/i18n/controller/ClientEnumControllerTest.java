package cc.catalysts.boot.i18n.controller;

import cc.catalysts.boot.dto.NamedDto;
import cc.catalysts.boot.exception.NotFoundException;
import cc.catalysts.boot.i18n.Gender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Klaus Lehner
 */
public class ClientEnumControllerTest {
    private ClientEnumController clientEnumController;

    @Before
    public void before() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF8");
        clientEnumController = new ClientEnumController(messageSource);
        clientEnumController.registerClientEnum(Gender.class);
    }

    @Test
    public void getAllEnglish() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        final Map<String, List<NamedDto<String>>> all = clientEnumController.getAll();
        Assert.assertEquals(1, all.size());
        final List<NamedDto<String>> gender = all.get("Gender");
        Assert.assertEquals(2, gender.size());
        Assert.assertEquals("female", gender.get(1).getName());
        Assert.assertEquals("F", gender.get(1).getId());
    }

    @Test
    public void getAllGerman() {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        final Map<String, List<NamedDto<String>>> all = clientEnumController.getAll();
        Assert.assertEquals(1, all.size());
        final List<NamedDto<String>> gender = all.get("Gender");
        Assert.assertEquals(2, gender.size());
        Assert.assertEquals("weiblich", gender.get(1).getName());
        Assert.assertEquals("F", gender.get(1).getId());
    }

    @Test
    public void getValuesOfEnumGerman() {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        final List<NamedDto<String>> gender = clientEnumController.getEnumValues("Gender");
        Assert.assertEquals(2, gender.size());
        Assert.assertEquals("weiblich", gender.get(1).getName());
        Assert.assertEquals("F", gender.get(1).getId());

    }

    @Test
    public void getValuesOfEnumEnglish() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        final List<NamedDto<String>> gender = clientEnumController.getEnumValues("Gender");
        Assert.assertEquals(2, gender.size());
        Assert.assertEquals("female", gender.get(1).getName());
        Assert.assertEquals("F", gender.get(1).getId());
    }

    @Test(expected = NotFoundException.class)
    public void unknownEnum() {
        clientEnumController.getEnumValues("Type");
    }

}
