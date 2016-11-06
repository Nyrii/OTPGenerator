/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')
    .controller('googleWidgetController', ["$scope", function ($scope) {

        $("submit").click(function () {
            var form = $("#form").serializeArray();
            $("#secretID").val('');


            // $http.post("/api/" + $scope.module, {msg:"key=" + form[0].value})
            // .success(function () {
            //     console.log("msg sent");
            // })
            // .error(function () {
            //     console.log("msg failed");
            // });
            // $("#otp").html(form[0].value);

            $ .ajax({
                url: "/api/" + $scope.module + "?_csrf=" + getCSRF(),
                type: "POST",
                data: {
                    key: form[0].value
                },
                dataType: "text",
                success: function (data) {
                    console.log(data);
                    console.log("Password generated.");
                },
                error: function (data) {
                    console.log(data);
                }
            });
        });

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
