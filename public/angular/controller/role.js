adminControllers.controller('RolesCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('/super/roles').success(function (data) {
                $scope.roles = data;
                $scope.orderProp = 'role_id';
                $scope.activeRole_id = $routeParams.role_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
            $http.get('/super/users').success(function (data) {
                $scope.users = data;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
            
            $scope.getUserCount = function(role) {
                if(typeof $scope.users == 'undefined' || $scope.users == null)
                    return 0;
                
                var count = 0;
                for (var i = 0; i < $scope.users.length; i++)
                {
                    var user = $scope.users[i];
                    if(user.role != null && user.role.role_id == role.role_id)
                        count += 1;
                }
                
                return count;
            }
        };
        $scope.get();
    });

adminControllers.controller('NewRoleCtrl',
    function($scope, errorFactory, $http, $location) {
        errorFactory.resetError();
        $http.get('/super/roles/new').success(function(data) {
            $scope.role = data;
        });
        $scope.save = function() {
            $http.put('super/roles', $scope.role).success(function(data) {
                $location.path("/role/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });

adminControllers.controller('RoleCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
//        $http.get('channels').success(function(data) {
//            console.log(data);
//        }).error(function(data, status){
//            errorFactory.setError(status);
//        });
        $http.get('super/roles/' + $routeParams.role_id).success(function(data) {
            $scope.role = data;
            $scope.time = 0;
            $http.get('channels').success(function(data) {
                $scope.channels = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
            $http.get('super/users').success(function(data) {
                $scope.users = data;
                usersInRole = [];
                for (var i = 0; i < data.length; i++)
                {
                    var user = data[i];
                    for (var j = 0; j < user.roles.length; j++)
                    {
                        if(user.roles != null && user.roles[j].role_id == $scope.role.role_id){
                            usersInRole.push(user);
                        }
                    }
                    
                }
                $scope.usersInRole = usersInRole;
            }).error(function(data, status){
                errorFactory.setError(status);
            });


        }).error(function(data, status){
            errorFactory.setError(status);
        });
        
        $scope.onChange = function(channel) {
            $scope.role.channels.push(channel)
        }
        
        $scope.updateUser = function(){
            for (var i = 0; i < $scope.usersInRole.length; i++)
            {
                var user = $scope.usersInRole[i];

                if(!user.roles.some(function(role) {
                   return role.role_id == $scope.role.role_id; 
                })){
                    user.roles.push($scope.role);
                    $http.put("super/users", user).success(function(data) {                        
                    }).error(function(data, status){
                        errorFactory.setError(status);
                    });
                }
            }
        }

        $scope.update = function() {
            $http.put("super/roles", $scope.role).success(function(data) {
                $scope.updateUser();
                $location.path("super/roles");
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        $scope.saveRole = function() {
            $scope.updateUser();
            
            $http.put("super/roles", $scope.role).success(function(data) {
                $scope.updateUser();
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        $scope.delete = function() {
            $http.delete("super/roles/" + $scope.role.role_id).success(function() {
                $timeout(function(){$location.path( "/roles")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

    });