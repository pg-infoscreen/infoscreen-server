adminControllers.controller('LogsCtrl',
    function($scope, errorFactory, $http) {
        errorFactory.resetError();
        $http.get('/log').success(function(data) {
            $scope.logs = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.metrics = function() {
            $http.get("/admin/metrics").success(function (response) {
                // Token valid, fetch user data
                $scope.metrics = response;

            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };

        $scope.between = function( value, min,  max) {
            if(value >= min && value <= max){
                return 1;
            }else{
                return 0;
            }
        };

        $scope.prozentsatz = function( prozentwert,  grundwert) {
            return (prozentwert / grundwert) * 100;
        };

        $scope.Math = window.Math;

        $scope.metrics();

    });

adminControllers.controller('LogSlideCtrl',
        function($scope, errorFactory, $routeParams, $http) {
            errorFactory.resetError();
            $http.get('slides/' + $routeParams.id).success(function(data) {
                $scope.slide = data;
                $http.get('log/slide/' + $routeParams.id).success(function(data) {
                    $scope.logs = data;
                }).error(function(data, status){
                    errorFactory.setError(status);
                });
            }).error(function(data, status){
                errorFactory.setError(status);
            });

        });