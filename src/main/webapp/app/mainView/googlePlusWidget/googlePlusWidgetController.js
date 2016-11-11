/**
 * Created by wilmot_g on 07/11/16.
 */

'use strict';
angular.module('requirerisApp')
		.controller('googlePlusWidgetController', ["$scope", "$cookies", function ($scope, $cookies) {

			$(document).ready(function() {
				if ($cookies.get('Requireris')) {
					$scope.signedIn = true;
				} else {
					$scope.signedIn = false;
				}
			});

			$scope.signInGoogle = function () {
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
			};

			$scope.signOutGoogle = function () {
				$scope.signedIn = false;
				$cookies.remove('Requireris');
			};

			function getData(key) {
				$.ajax({
					url: "/api/getAuthorizeData" + "?_csrf=" + getCSRF(),
					type: "POST",
					data: {
						key: key
					},
					dataType: "json",
					success: function (data) {
						//TODO : data = error -> pas co. sinon co. (Y'a le nom dedans)
						if (data.error) {
							return;
							console.log(data.error);
						}

						var now = new Date(),
						exp = new Date(now.getFullYear(), now.getMonth() + 1, now.getDate());
						$cookies.put('Requireris', data.id, {
							expires: exp
						});

						$scope.$apply(function() {
							$scope.signedIn = true;
						});
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