/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('googleWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/googleWidget/googleWidget.html",
            controller: "googleWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
