/**
 * Created by wilmot_g on 07/11/16.
 */

'use strict';
angular.module('requirerisApp')
		.controller('googlePlusWidgetController', ["$scope", function ($scope) {

			$("a").click(function () {
				$.ajax({
					url: "/api/getAuthorize" + "?_csrf=" + getCSRF(),
					type: "GET",
					dataType: "text",
					success: function (data) {
						var w = 400;
						var h = 500;
						var left = (screen.width / 2) - (w / 2);
						var top = (screen.height / 2) - (h / 2);
						window.open(data, "Sign in", 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=' + w +', height='+h+', top='+top+', left='+left);
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
