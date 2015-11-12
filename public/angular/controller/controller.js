adminControllers.controller('HeaderController',
    function($scope, errorFactory, $location){
        $scope.isActive = function (viewLocation) {
            return ($location.path().indexOf(viewLocation) == 0);
        };
    });

adminControllers.controller('error',
    function($scope, errorFactory) {
        $scope.errorFactory = errorFactory;

        $scope.resetError = function () {
            errorFactory.resetError();
        };

    }
);








