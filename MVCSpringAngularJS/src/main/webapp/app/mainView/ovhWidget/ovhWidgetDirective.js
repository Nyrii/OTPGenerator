/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('ovhWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/ovhWidget/ovhWidget.html",
            controller: "ovhWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
