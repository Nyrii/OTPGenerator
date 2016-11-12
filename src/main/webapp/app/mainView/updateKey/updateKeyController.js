/**
 * Created by noboud_n on 12/11/2016.
 */

angular.module('requirerisApp')
    .controller('updateKeyController', ['$scope', function($scope) {

        $scope.updateKey = function() {
			var form = $("#form").serializeArray();
			$("#secretID").val('');

			$.ajax({
				url: "/api/updateKey/" + $scope.module + "?_csrf=" + $scope.getCSRF(),
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
        }
	}]);