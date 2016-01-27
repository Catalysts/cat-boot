define([
    'angular',
    './i18n-service.js'
], function (angular,
             i18nServiceModule) {
    'use strict';

    /**
     * @ngdoc
     * @directive
     * @name cbI18n
     *
     * @restrict AE
     * @param {string} key the key to be translated
     * @param {string} [default] the default text to show
     * @param {string} cat-boot-i18n an alias for 'key' when the directive is used in attribute form
     */
    return angular
        .module('cb.i18n.cbI18nDirective', [
            i18nServiceModule
        ])
        .directive('cbI18n', [
            'cbI18nService',
            function (i18nService) {
                return {
                    restrict: 'AE',
                    link: function (scope, element, attrs) {
                        var key;
                        if (attrs.hasOwnProperty('cbI18n')) {
                            key = attrs.cbI18n;
                        } else {
                            key = attrs.key;
                        }

                        i18nService
                            .get(key, attrs.default)
                            .then(function (message) {
                                element.text(message);
                            });
                    }
                };
            }])
        .name;
});
