adminControllers.controller('ClientsCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function(){
            $http.get('clients').success(function(data) {
                $scope.clients = data;
                $scope.activeClient_id = $routeParams.client_id;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
            $http.get('channels').success(function (data) {
                $scope.channels = data;
                $scope.orderProp = 'user_id';
                $scope.activeChannel_id = $routeParams.channel_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();

        $scope.update = function() {
            $http.put("clients", this.client).success(function(data) {
                $scope.success = true;
            }).error(function(data, status){
                $scope.success = false;
            });
        };

        $scope.delete = function(client_id) {
            $http.delete("/clients/"+client_id).success(function() {
                var clients = [];
                angular.forEach($scope.clients, function(item){
                    if(item.client_id != client_id)
                    this.push(item);
                },clients);
                $scope.clients = clients;
                $scope.success = true;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

    });

adminControllers.controller('NewClientCtrl',
    function($scope, errorFactory, $http, $location) {
        errorFactory.resetError();
        $http.get('clients/new').success(function(data) {
            $scope.client = data;
        });

        $scope.save = function() {
            $http.put('clients', $scope.client).success(function(data) {
                $location.path("/clients/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

    });

adminControllers.controller('ClientCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
        $http.get('clients/' + $routeParams.client_id).success(function(data) {
            $scope.client = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.update = function() {
            $http.put("clients", $scope.client).success(function(data) {
                $location.path("/clients/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.delete = function() {
            $http.delete("/clients/" + $scope.client.client_id).success(function() {
                $timeout(function(){$location.path( "/clients")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

    });