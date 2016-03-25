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

        this.init = function () {
            if (!!loadingPromise) {
                throw new Error('Initialization already started!');
            }

            loadingPromise = cbEnumEndpoint.list()
                .then(function (allEnumValues) {
                    cbEnums = allEnumValues;
                });

            return loadingPromise;
        };

        this.list = function (name) {
            if (!loadingPromise) {
                throw new Error('Initialization not yet started!');
            }

            return loadingPromise.then(function (allEnumValues) {
                var enumValues = cbEnums[name];
                if (!enumValues) {
                    throw new Error('No enum with name \'' + name + '\' was registered!');
                }
                return enumValues;
            });
        };

        this.get = function (name, value) {
            if (!loadingPromise) {
                throw new Error('Initialization not yet started!');
            }

            return loadingPromise.then(function (allEnumValues) {
                var enumValues = that.list(name);
                for (var i = 0; i < enumValues.length; i++) {
                    var enumValue = enumValues[i];
                    if (value === enumValue.id) {
                        return enumValue;
                    }
                }

                throw new Error('No enum for \'' + value + '\' of type \'' + name + '\'');
            });
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