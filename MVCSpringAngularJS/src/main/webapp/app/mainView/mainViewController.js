/**
 * Created by noboud_n on 06/11/2016.
 */

'use strict';
angular.module('requirerisApp')
    .controller('mainViewController', function () {

        $("submit").click(function () {
            var form = $("#form").serializeArray();
            $("#code").val('');
            $("#otp").html(form[0].value);
        });

    });
