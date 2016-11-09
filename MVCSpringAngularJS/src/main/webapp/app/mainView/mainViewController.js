/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')
    .controller('mainViewController', ["$cookies", "$scope", "LoginService", "Auth",
        function ($cookies, $scope, $loginService, $auth) {

        $(document).ready(function() {
            var active_tab_selector = $('.nav > li.active > a').attr('href');
            var dict = ["#Google", "#Github"];

            $.each(dict, function(key, value) {
                if (value == active_tab_selector) {
                    $('#module').html(value.substr(1));
                    $(active_tab_selector).css('display', '');
                    return true;
                }
            });
        });

        $('.nav > li > a').click(function (event) {
            event.preventDefault(); // Stop browser to take action for clicked anchor

            // Get displaying tab content jQuery selector
            var active_tab_selector = $('.nav > li.active > a').attr('href');

            // Find actived navigation and remove 'active' css
            var actived_nav = $('.nav > li.active');
            actived_nav.removeClass('active');

            // Add 'active' css into clicked navigation
            $(this).parents('li').addClass('active');

            // Hide displaying tab content
            $(active_tab_selector).css('display', 'none');

            // Show target tab content
            var target_tab_selector = $(this).attr('href');
            $(target_tab_selector).css('display', '');

            var dict = ["#Google", "#Github"];

            $.each(dict, function(key, value) {
                if (value == target_tab_selector) {
                    $('#module').html(value.substr(1));
                    return true;
                }
            });

            $("#secretID").val('');
        });

    }

]);
