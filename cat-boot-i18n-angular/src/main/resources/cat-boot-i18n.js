define([
    'angular',
    './cat-boot-i18n/i18n-directive.js',
    './cat-boot-i18n/i18n-service.js'
], function (angular,
             i18nDirective,
             i18nServie) {
    'use strict';

    return angular
        .module('cb.i18n', [
            i18nDirective,
            i18nServie
        ])
        .name;
});
