/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('snapchatWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/snapchatWidget/snapchatWidget.html",
            controller: "snapchatWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
