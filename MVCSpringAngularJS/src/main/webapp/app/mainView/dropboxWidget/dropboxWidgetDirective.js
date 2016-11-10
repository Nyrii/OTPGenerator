/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('dropboxWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/dropboxWidget/dropboxWidget.html",
            controller: "dropboxWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
