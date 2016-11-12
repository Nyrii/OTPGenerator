/**
 * Created by noboud_n on 12/11/2016.
 */

'use strict'
angular.module('requirerisApp')
    .directive('updateKey', function() {
        return  {
            restrict: 'AE',
            templateUrl: "app/mainView/updateKey/updateKey.html",
            controller: "updateKeyController",
            scope: {
                state: "@",
                authenticated: "=",
                module: "="
            }
        }
    });