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
                url: "/api/generate/" + $scope.module + "?_csrf=" + $scope.csrf(),
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
	}

    ]);