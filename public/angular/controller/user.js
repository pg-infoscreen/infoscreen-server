adminControllers.controller('UsersCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        $scope.error = 1;

        $scope.errorMessage = errorFactory.error;
        $scope.date = new Date();

        $scope.updateChart = function() {
            // Detail Stats Part
            $scope.labels = [];
            $scope.series = ['Zugriffe'];
            $scope.data = [];
            $scope.options = {
                responsive: true
            };

            $http.get('/statistic/access').success(function (data) {
                $scope.data = [];
                $scope.data.push(data);

                // Update data
                $scope.labels = [];
                var hour = moment().hour();
                for (var i = 24; i > 0; i--) {
                    $scope.labels.push(((((hour+1-i)%24)+24)%24) + "h");
                }

            }).error(function(data, status) {
                $scope.data.push([]);
            });


        };
        $scope.updateChart();


        $scope.get = function() {
            $http.get('super/users').success(function (data) {
                $scope.users = data;
                $scope.activeUser_id = $routeParams.user_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();

        $scope.switchEnable = function() {
            var editedUser = this.user;
            editedUser.enabled = !editedUser.enabled;
            $http.put("super/users", this.user).success(function(data) {
                this.user = editedUser;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.kick = function() {
            this.user.lastSession.kicked = !this.user.lastSession.kicked;
            $http.get("super/users/kick/" + this.user.user_id).success(function () {
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        
    });

adminControllers.controller('NewUserCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout, PasswordGenerator) {
        errorFactory.resetError();
        $http.get('super/users/new').success(function(data) {
            $scope.newUser = data;
            $scope.generatePassword();
            $scope.roles();
        }).error(function(data, status){
            errorFactory.setError(status);
        });
        
        $scope.roles = function() {
            $http.get("super/roles").success(function(data) {
                $scope.rolelist = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.update = function() {
            console.log($scope.newUser)
            $http.put("super/users", $scope.newUser).success(function(data) {
                $location.path("users/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.checkEmail = function() {
            if($scope.newUser.email.length > 0){
            $http.get("super/users/check/" + $scope.newUser.email).success(function(data) {
                $scope.emailExists = (data == 'true');
                $scope.myForm.email.$valid  = $scope.myForm.email.$valid && (data == 'false');
            }).error(function(data, status){
                errorFactory.setError(status);
            });
            }
        };

        $scope.addUpper = PasswordGenerator.addUpper;
        $scope.addNumbers = PasswordGenerator.addNumbers;
        $scope.addSymbols = PasswordGenerator.addSymbols;
        $scope.passwordLength = PasswordGenerator.passwordLength;
        $scope.generatePassword = function(){
            $scope.newUser.password = PasswordGenerator.generate($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
            $scope.passwordStrength = PasswordGenerator.strength($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
        }

    });

adminControllers.controller('UserCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $http.get('super/users/' + $routeParams.user_id).success(function(data) {
            $scope.user = data;
            $scope.roles();
            $scope.session();
            $scope.access();
            $scope.edit();
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.roles = function() {
            $http.get("super/roles").success(function(data) {
                $scope.rolelist = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.access = function() {
            $http.get("super/users/access/"+$scope.user.user_id).success(function(data) {
                $scope.accesses = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.edit = function() {
            $http.get("super/users/edit/"+$scope.user.user_id).success(function(data) {
                $scope.edits = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.session = function() {
            $http.get("super/users/session/"+$scope.user.user_id).success(function(data) {
                $scope.sessions = data;
                $scope.status = "session";
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.body = function() {
            if(this.edit.showBody == null){
                this.edit.showBody = true;
            }else{
                this.edit.showBody = !this.edit.showBody;
            }
        };

    });

adminControllers.controller('EditUserCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, PasswordGenerator) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('super/users/' + $routeParams.user_id).success(function (data) {
                $scope.editUser = data;
                $scope.originalEmail = $scope.editUser.email;
                $scope.roles();
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        }
        $scope.get();

        $scope.checkEmail = function() {
            if($scope.editUser.email.length > 0){
                if($scope.editUser.email == $scope.originalEmail){
                    $scope.emailExists = false;
                }else{
                    $http.get("super/users/check/" + $scope.editUser.email).success(function(data) {
                        $scope.emailExists = (data == 'true');
                        $scope.myForm.email.$valid  = $scope.myForm.email.$valid && (data == 'false');
                    }).error(function(data, status){
                        errorFactory.setError(status);
                    });
                }
            }
        };

        $scope.update = function() {
            $http.put("super/users", $scope.editUser).success(function(data) {
                $location.path("users/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.delete = function() {
            $http.delete("/users/" + $scope.editUser.user_id).success(function() {
                $timeout(function(){$location.path( "/users")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.roles = function() {
            $http.get("super/roles").success(function(data) {
                $scope.rolelist = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.addUpper = PasswordGenerator.addUpper;;
        $scope.addNumbers = PasswordGenerator.addNumbers;
        $scope.addSymbols = PasswordGenerator.addSymbols;
        $scope.passwordLength = PasswordGenerator.passwordLength;
        $scope.generatePassword = function(){
            $scope.newUser.password = PasswordGenerator.generate($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
            $scope.passwordStrength = PasswordGenerator.strength($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
        }

    });

adminControllers.controller('MeCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout, PasswordGenerator) {
        errorFactory.resetError();
        $http.get('users/me').success(function(data) {
            $scope.user = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.update = function() {
            $http.put("users", $scope.user).success(function(data) {
                $location.path("/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.addUpper = PasswordGenerator.addUpper;;
        $scope.addNumbers = PasswordGenerator.addNumbers;
        $scope.addSymbols = PasswordGenerator.addSymbols;
        $scope.passwordLength = PasswordGenerator.passwordLength;
        $scope.generatePassword = function(){
            $scope.user.password = PasswordGenerator.generate($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
            $scope.user.repeatPassword = $scope.user.password;
            $scope.passwordStrength = PasswordGenerator.strength($scope.addUpper, $scope.addNumbers, $scope.addSymbols, $scope.passwordLength);
        }
    });
