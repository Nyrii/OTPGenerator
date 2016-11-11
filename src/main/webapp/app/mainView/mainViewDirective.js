/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')

    .directive('mainView', function() {

        return {
            restrict: 'AE',
            templateUrl: "app/mainView/mainView.html",
            controller: "mainViewController",
            scope: {
                state: "@"
            },

        }
    });
