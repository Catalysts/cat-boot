/**
 * @author Thomas Scheinecker, Catalysts GmbH.
 */

define([
    'angular'
], function (angular) {
    'use strict';

    function CatBootEnumService(cbEnumEndpoint, cbEnums) {
        var that = this;
        var loadingPromise;
        var initialized = false;

        this.init = function () {
            if (initialized) {
                throw new Error('Already initialized!');
            }
            if (!!loadingPromise) {
                throw new Error('Initialization already in progress!');
            }

            loadingPromise = cbEnumEndpoint.list()
                .then(function (enumValues) {
                    cbEnums = enumValues;
                    initialized = true;
                    loadingPromise = undefined;
                });

            return loadingPromise;
        };

        this.list = function (name) {
            if (!initialized) {
                throw new Error('Not yet initialized!');
            }

            var enumValues = cbEnums[name];
            if (!enumValues) {
                throw new Error('No enum with name \'' + name + '\' was registered!');
            }
            return enumValues;
        };

        this.get = function (name, value) {
            var enumValues = that.list(name);
            for (var i = 0; i < enumValues.length; i++) {
                var enumValue = enumValues[i];
                if (value === enumValue.id) {
                    return enumValue;
                }
            }

            throw new Error('No enum for \'' + value + '\' of type \'' + name + '\'');
        };
    }

    function CbEnumApiEndpointService($http,
                                      CB_ENUM_API_PATH) {
        this.list = function () {
            return $http
                .get(CB_ENUM_API_PATH)
                .then(function (response) {
                    return response.data;
                });
        };
    }

    return angular
        .module('cc.catalysts.boot.CbEnumService', [])
        .value('CB_ENUM_API_PATH', 'api/enum')
        .value('cbEnums', {})
        .service('cbEnumApiEndpointService', [
            '$http',
            'CB_ENUM_API_PATH',
            CbEnumApiEndpointService
        ])
        .service('cbEnumService', [
            'cbEnumApiEndpointService',
            'cbEnums',
            CatBootEnumService
        ])
        .run(['cbEnumService', function (cbEnumService) {
            // force eager initialization
            cbEnumService.init();
        }])
        .name;
});