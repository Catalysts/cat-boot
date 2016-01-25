package cc.catalysts.boot.i18n.controller;

import cc.catalysts.boot.i18n.dto.I18nDto;
import cc.catalysts.boot.i18n.service.I18nService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Thomas Scheinecker
 */
@RestController
@RequestMapping(value = "api/i18n")
public class I18nController {
    private final I18nService i18NService;

    public I18nController(I18nService i18NService) {
        this.i18NService = i18NService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public I18nDto getI18nProperties() {
        return i18NService.getAllValues();
    }
}
