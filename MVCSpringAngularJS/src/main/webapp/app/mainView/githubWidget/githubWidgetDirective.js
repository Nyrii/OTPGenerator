/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('githubWidget', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/githubWidget/githubWidget.html",
            controller: "githubWidgetController",
            scope: {
                state: "@",
                'module' : '@',
            },

        }
    });
