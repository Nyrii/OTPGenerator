/**
 * Created by noboud_n on 10/11/2016.
 */

'use strict';
angular.module('requirerisApp')
    .controller('otpModuleController', ["$scope", function ($scope) {

        $scope.sendData = function () {
            var form = $("#form").serializeArray();
            $("#secretID").val('');

            $.ajax({
                url: "/api/generate/" + $scope.module + "?_csrf=" + getCSRF(),
                type: "POST",
                data: {
                    key: form[0].value
                },
                dataType: "text",
                success: function (data) {
                    $('#otp').html(data);
                },
                error: function (data) {
                    $('#otp').html(data);
                }
            });
        };

        function getCSRF() {
            var name = 'CSRF-TOKEN=';
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') c = c.substring(1);
                if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
            }
            return '';
        }
    }

    ]);