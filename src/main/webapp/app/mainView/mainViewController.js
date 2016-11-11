/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')
    .controller('mainViewController', ["$scope", function ($scope) {

        var dict = ["#Google", "#Github", "#Snapchat", "#Facebook", "#Dropbox", "#OVH"];

        $(document).ready(function () {
            $scope.moduleName = "Google";
            $scope.module = {value: "google"};
        });

        $('.nav > li > a').click(function (event) {
            event.preventDefault(); // Stop browser to take action for clicked anchor

            // Get displaying tab content jQuery selector
            var old_id = $('.nav > li.active > a').attr('href');

            // Find actived navigation and remove 'active' css
            var actived_nav = $('.nav > li.active');
            actived_nav.removeClass('active');

            // Add 'active' css into clicked navigation
            $(this).parents('li').addClass('active');

            // Show target tab content
            var new_id = $(this).attr('href');

            $.each(dict, function (key, value) {
                if (value == new_id) {
                    // "Generate a new [value.substr(1)] password" : Google, Facebook...
                    $scope.$apply(function () {
                        $scope.moduleName = value.substr(1);
                    });
                    // Change id depending of the active li
                    $(old_id).attr('id', value.substr(1));
                    // Change module of the otpModule
                    // $(new_id).attr('module', value.substr(1).toLowerCase());
                    return true;
                }
            });

            $("#secretID").val('');
        });

        $scope.changeModule = function(new_module) {
            $scope.module.value = new_module;
        }

    }]);
