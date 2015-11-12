adminControllers.controller('ScreensCtrl',
    function($rootScope, $scope, $routeParams, $http, $timeout, $interval, errorFactory) {
        errorFactory.resetError();
        
        // Refresh Function
        var updateFunction = function() {
            $http.get('screens').success(function(data) {
                $scope.screens = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        }
        // Initial Update
        updateFunction();
        // AutoUpdate data from REST every second
        var update = $interval(function () {
            updateFunction();
        }, 1000);
        // Stop Autoupdate if route change
        $rootScope.$on("$locationChangeStart", function() { 
            $interval.cancel(update);
        });
                
        $scope.refresh = function(id) {
            $http.get("/refreshScreen/"+id).success(function() {
                
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
});