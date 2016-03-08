/**
 * @author Thomas Scheinecker, Catalysts GmbH.
 */

define([
    'angular',
    './cat-boot-enum-service.js'
], function (angular,
             cbEnumServiceModule) {
    'use strict';

    /**
     * @param {$compile} $compile
     * @param {CatBootEnumService} cbEnumService
     * @returns {{restrict: string, require: string, scope: boolean, link: link}}
     */
    function cbEnum($compile, cbEnumService) {
        return {
            restrict: 'A',
            require: '?ngModel',
            scope: false,
            link: function ($scope, $element, $attrs, ngModel) {
                if (!$element.is('select')) {
                    throw new Error('cb-enum can only be used with <select> elements!');
                }

                var enumName = $attrs.cbEnum;
                var enumValues = cbEnumService.list(enumName);

                for (var i = 0; i < enumValues.length; i++) {
                    var optionScope = $scope.$new(true);
                    var enumValue = enumValues[i];
                    optionScope.value = enumValue.id;
                    optionScope.name = enumValue.name;
                    $element.append($compile('<option value="{{::value}}">{{name}}</option>')(optionScope));
                }
            }
        };
    }

    return angular
        .module('cc.catalysts.boot.CbEnumDirective', [
            cbEnumServiceModule
        ])
        .directive('cbEnum', [
            '$compile',
            'cbEnumService',
            cbEnum
        ])
        .name;
});