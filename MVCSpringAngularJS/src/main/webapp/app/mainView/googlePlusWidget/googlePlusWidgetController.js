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
					dataType: "json",
					xhrFields: {
						withCredentials: true
					},
					success: function (data, status, header) {
						var w = 400;
						var h = 500;
						var left = (screen.width / 2) - (w / 2);
						var top = (screen.height / 2) - (h / 2);
						var popup = window.open(data.url, "Sign in", 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=' + w +', height='+h+', top='+top+', left='+left);

						var int = setInterval(function() {
							if (popup.closed) {
								clearInterval(int);
								getData(data.key);
							}
						}, 1000)
					},
					error: function (data) {
						console.log(data);
					}
				});
			});

			function getData(key) {
				$.ajax({
					url: "/api/getAuthorizeData" + "?_csrf=" + getCSRF(),
					type: "POST",
					data: {
						key: key
					},
					dataType: "json",
					success: function (data) {
						console.log(data);
						//TODO : data = error -> pas co. sinon co. (Y'a le nom dedans)
					},
					error: function (data) {
						console.log(data);
					}
				});
			}

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
		}]);
