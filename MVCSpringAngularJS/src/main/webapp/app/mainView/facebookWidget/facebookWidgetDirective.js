/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('facebookWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/facebookWidget/facebookWidget.html",
            controller: "facebookWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
