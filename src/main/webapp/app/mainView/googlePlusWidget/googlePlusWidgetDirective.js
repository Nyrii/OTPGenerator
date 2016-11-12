/**
 * Created by wilmot_g on 07/11/16.
 */

'use strict';
angular.module('requirerisApp')

		.directive('googlePlusWidget', function() {

			return {
				restrict: 'AE',
				templateUrl: "app/mainView/googlePlusWidget/googlePlusWidget.html",
				controller: "googlePlusWidgetController",
				scope: {
					state: "@",
					authenticated: "="
				}

			}
		});
