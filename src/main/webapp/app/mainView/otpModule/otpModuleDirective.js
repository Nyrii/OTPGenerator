/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('otpModule', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/otpModule/otpModule.html",
            controller: "otpModuleController",
            scope: {
                state: "@",
                'module' : '='
            }
        }
    });