package cc.catalysts.boot.i18n.controller;

import cc.catalysts.boot.dto.NamedDto;
import cc.catalysts.boot.exception.NotFoundException;
import cc.catalysts.boot.i18n.service.ClientEnumRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Exposes all registered enums under the URL <code>/api/enum</code>
 *
 * @author Klaus Lehner
 */
@RestController
@RequestMapping(value = "/api/enum")
public class ClientEnumController implements ClientEnumRegistry {

    private final MessageSource messageSource;
    private final Map<String, List<NamedDto<String>>> clientEnums = new HashMap<>();

    @Autowired
    public ClientEnumController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public List<NamedDto<String>> getEnumValues(@PathVariable(value = "name") String name) {
        if (clientEnums.containsKey(name)) {
            return clientEnums.get(name);
        } else {
            throw new NotFoundException(name);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", ""})
    public Map<String, List<NamedDto<String>>> getAll() {
        return clientEnums;
    }

    @Override
    public void registerClientEnum(String name, Class<? extends Enum> enumClazz) {
        if (clientEnums.containsKey(name)) {
            throw new IllegalArgumentException("Client-Enum '" + name + "' already exists");
        }
        List<NamedDto<String>> list = new ArrayList<>();
        String messageKeyRoot = "enum." + enumClazz.getSimpleName() + ".";
        for (Enum anEnum : enumClazz.getEnumConstants()) {
            list.add(new NamedDto<>(anEnum.name(),
                    messageSource.getMessage(messageKeyRoot + anEnum.name(), new Object[0], Locale.getDefault())));
        }
        clientEnums.put(name, Collections.unmodifiableList(list));
    }

    @Override
    public void registerClientEnum(Class<? extends Enum> enumClazz) {
        registerClientEnum(enumClazz.getSimpleName(), enumClazz);
    }
}
