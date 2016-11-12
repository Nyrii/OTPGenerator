/**
 * Created by wilmot_g on 07/11/16.
 */

'use strict';
angular.module('requirerisApp')
		.controller('googlePlusWidgetController', ["$scope", "$cookies", function ($scope, $cookies) {

			$(document).ready(function() {
				if ($cookies.get('Requireris')) {
					$scope.authenticated = true;
				} else {
					$scope.authenticated = false;
				}
			});

			$scope.signInGoogle = function () {
				$.ajax({
					url: "/api/getAuthorize" + "?_csrf=" + $scope.csrf(),
					type: "GET",
					dataType: "json",
					xhrFields: {
						withCredentials: true
					},
					success: function (data, status) {
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
				$('#otp').html("");
				$scope.authenticated = false;
				$cookies.remove('Requireris');
				$cookies.remove('RequirerisName');
			};

			function getData(key) {
				$.ajax({
					url: "/api/getAuthorizeData" + "?_csrf=" + $scope.csrf(),
					type: "POST",
					data: {
						key: key
					},
					dataType: "json",
					success: function (data) {
						if (data.error) {
							return;
							console.log(data.error);
						}

						var now = new Date(),
						exp = new Date(now.getFullYear(), now.getMonth() + 1, now.getDate());
						$cookies.put('Requireris', data.id, {
							expires: exp
						});
						$cookies.put('RequirerisName', data.name, {
							expires: exp
						});

						$scope.$apply(function() {
							$('#otp').html("Bonjour " + data.name);
							$scope.authenticated = true;
						});
					},
					error: function (data) {
						console.log(data);
					}
				});
			}
		}]);
