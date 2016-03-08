/**
 * @author Thomas Scheinecker, Catalysts GmbH.
 */

define([
    'angular',
    './cat-boot-enum-directive.js'
], function (angular,
             cbEnumDirective) {
    'use strict';

    return angular
        .module('cc.catalysts.boot.CbEnum', [
            cbEnumDirective
        ])
        .name;
});