package cc.catalysts.boot.i18n.controller;

import cc.catalysts.boot.dto.NamedDto;
import cc.catalysts.boot.exception.NotFoundException;
import cc.catalysts.boot.i18n.service.ClientEnumRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exposes all registered enums under the URL <code>/api/enum</code>
 *
 * @author Klaus Lehner
 */
@RestController
@RequestMapping(value = "/api/enum")
public class ClientEnumController implements ClientEnumRegistry {

    private final MessageSource messageSource;
    private final Map<Locale, Map<String, List<NamedDto<String>>>> clientEnums = new ConcurrentHashMap<>();

    private final Map<String, Class> nameToClassMap = new ConcurrentHashMap<>();

    @Autowired
    public ClientEnumController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public List<NamedDto<String>> getEnumValues(@PathVariable(value = "name") String name) {
        final Locale locale = LocaleContextHolder.getLocale();
        checkIfCurrentLocaleRegistered(locale);
        if (clientEnums.get(locale).containsKey(name)) {
            return clientEnums.get(locale).get(name);
        } else {
            throw new NotFoundException(name);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", ""})
    public Map<String, List<NamedDto<String>>> getAll() {
        final Locale locale = LocaleContextHolder.getLocale();
        checkIfCurrentLocaleRegistered(locale);
        return clientEnums.get(locale);
    }

    private void checkIfCurrentLocaleRegistered(Locale locale) {
        if (!clientEnums.containsKey(locale)) {
            for (Map.Entry<String, Class> stringClassEntry : nameToClassMap.entrySet()) {
                register(stringClassEntry.getKey(), stringClassEntry.getValue(), locale);
            }
        }
    }

    @Override
    public void registerClientEnum(String name, Class<? extends Enum> enumClazz) {
        if (nameToClassMap.containsKey(name)) {
            throw new IllegalArgumentException("Client-Enum '" + name + "' already exists");
        }
        nameToClassMap.put(name, enumClazz);
        final Locale defaultLocale = Locale.getDefault();
        register(name, enumClazz, defaultLocale);

        // invalidate cache for all other locales here
        for (Locale locale : clientEnums.keySet()) {
            if (!locale.equals(defaultLocale)) {
                clientEnums.remove(locale);
            }
        }
    }

    private void register(String name, Class<? extends Enum> enumClazz, Locale locale) {
        List<NamedDto<String>> list = new ArrayList<>();
        String messageKeyRoot = "enum." + enumClazz.getSimpleName() + ".";
        for (Enum anEnum : enumClazz.getEnumConstants()) {
            list.add(new NamedDto<>(anEnum.name(),
                    messageSource.getMessage(messageKeyRoot + anEnum.name(), new Object[0], locale)));
        }
        if (!clientEnums.containsKey(locale)) {
            clientEnums.put(locale, new ConcurrentHashMap<>());
        }
        Map<String, List<NamedDto<String>>> localeMap = clientEnums.get(locale);
        localeMap.put(name, Collections.unmodifiableList(list));
    }

    @Override
    public void registerClientEnum(Class<? extends Enum> enumClazz) {
        registerClientEnum(enumClazz.getSimpleName(), enumClazz);
    }
}