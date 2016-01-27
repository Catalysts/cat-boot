define([
    'angular'
], function (angular) {
    'use strict';

    return angular
        .module('cb.i18n.cbI18nService', [])
        .provider('cbI18nService', function () {
            var messages = {};
            var locale;
            var resolvePromise;

            function storeMessages(locale, map) {
                if (locale) {
                    if (angular.isUndefined(messages[locale])) {
                        messages[locale] = {};
                    }

                    angular.extend(messages[locale], map);
                }
            }

            function I18nService($http, $q, $log) {
                var that = this;

                function loadMessages(module) {
                    var params = {};
                    if (locale) {
                        params.locale = locale;
                    }
                    return $http
                        .get('api/i18n', {
                            params: params
                        })
                        .then(function (response) {
                            return response.data;
                        })
                        .then(function (res) {
                            if (!locale) {
                                locale = res.locale;
                            }
                            storeMessages(locale, res.messages);
                            return res;
                        }, function (reason) {
                            $log.warn('I18n couldn\'t be loaded: i18n module ' + module, reason);
                            return $q.reject(reason);
                        });
                }


                /**
                 * Get/Set the locale
                 * @param {string} [newLocale] when defined set as new locale and calls resolve
                 * @returns {String|Promise} the current locale when used as a getter, the result of resolve oterwise
                 */
                this.locale = function (newLocale) {
                    if (newLocale) {
                        if (locale !== newLocale) {
                            locale = newLocale;
                            return that.resolve();
                        }
                    } else {
                        return locale;
                    }
                };

                /**
                 * Resolve a message key to a value
                 * @param {string} key the key of the message to be resolved
                 * @param {string} [fallbackMessage] the default message to use if no message is defined for the given key
                 * @returns {Promise} a promise resolving to the message for the given key.
                 */
                this.get = function (key, fallbackMessage) {
                    return that.resolve()
                        .then(function (data) {
                            var messages = data.messages;
                            var message = messages[key] || fallbackMessage;
                            if (!message) {
                                throw new Error('No message for key\'' + key + '\' available and no default was provided!');
                            }
                            return message;
                        });
                };

                /**
                 * Resolve a message key to a value using the loaded vocabulary and the message key
                 * @param {string} key the key of the message to be resolved
                 * @param {string} [fallbackMessage] the default message to use if no message is defined for the given key
                 * @returns {String} the resolved message for the given key
                 * @throws {Error} if the key is not defined in the current language or the message bundle for the locale wasn't yet loaded
                 */
                this.getImmediate = function (key, fallbackMessage) {
                    if (!messages || !messages[locale]) {
                        throw new Error('Messges for locale \'' + locale + '\' not yet loaded!');
                    }

                    var message = messages[locale][key] || fallbackMessage;
                    if (!message) {
                        throw new Error('No message for key\'' + key + '\' available and no default was provided!');
                    }

                    return message;
                };

                /**
                 * Load the messages for the current locale
                 * @returns {Promise} a promise resolving to an object containg a local
                 */
                this.resolve = function () {
                    if (!resolvePromise) {
                        resolvePromise = loadMessages();
                    }
                    return resolvePromise;
                };
            }

            this.$get = [
                '$http',
                '$q',
                '$injector',
                function ($http, $q, $injector) {
                    var i18nService = new I18nService($http, $q, $injector);
                    // load messages for default locale upon instantiation
                    i18nService.resolve();
                    return i18nService;
                }
            ];
        })
        .name;
});
