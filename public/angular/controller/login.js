
adminControllers.controller('LoginCtrl',
    function($scope, errorFactory, $routeParams, $cookies, $http, $q, $timeout, $translate)  {
        // This is only for demo purposes
        $scope.credentials = {
            email: "",
            password: ""
        };

        $scope.template = "<div id=\"headline\">\r\n\t${content.title}\r\n</div>\r\n<#if content.title??>\r\n\t<#if content.title?size gt 0>\r\n\t\t<div id=\"headlinestyle\">\r\n\t\t\t${content.title[0]}\r\n\t\t</div>\r\n\t</#if>\r\n</#if>";
        $scope.slide="{\r\n\t\"id\" : 1432,\r\n\t\"title\" : \"The Sake of Argument\",\r\n\t\"alt\" : \"'It's not actually ... it's a DEVICE for EXPLORING a PLAUSIBLE REALITY that's not the one we're in, to gain a broader understanding about it.' 'oh, like a boat!' '...' 'Just for the sake of argument, we should get a boat! You can invite the Devil, too, if you want.'\",\r\n\t\"img\" : \"iVBORw0KGgoAAAANSUhEUgAAAuQAAADSCAAA...\"\r\n}";

        // Ace Editor Global Options
        $scope.aceLoaded = function(_editor) {
            // Options
            _editor.setTheme("ace/theme/monokai");
            _editor.setAutoScrollEditorIntoView(true);
            _editor.setOptions({
                showGutter: true,
                showPrintMargin: false,
                fontSize: '12px',
                maxLines: '30',
                minLines: '12'
            });
        };

        // Check token cookie and try to authenticate
        // Otherwise the user has to log in
        var token = $cookies["XSRF-TOKEN"];
        if (token) {
            $http.get("/ping")
                .then(
                function(response) {
                    // Token valid, fetch user data
                    return $http.get("/users/" + response.data.user_id);
                },
                function(response) {
                    token = undefined;
                    $cookies["XSRF-TOKEN"] = undefined;
                    return $q.reject("Token invalid");
                }
            ).then(
                function(response) {
                    $scope.user = response.data;
                }, function(response) {
                    // Token invalid or fetching the user failed
                }
            );
        }

        /**
         * Login using the given credentials as (email,password).
         * The server adds the XSRF-TOKEN cookie which is then picked up by Play.
         */
        $scope.login = function(credentials) {
            $http.post("/admin/login", credentials)
                .then(
                function(response) { // success
                    token = response.data.token;
                    var user_id = response.data.user_id;
                    return $http.get("users/" + user_id); // return another promise to chain `then`
                }, function(response) { // error
                    $scope.error = "true";
                    // return 'empty' promise so the right `then` function is called
                    return $q.reject("Login failed");
                }
            )
                .then(
                function(response) {
                    $scope.user = response.data;
                },
                function(response) {
                    //console.log(response);
                }
            );
        };

        /**
         * Invalidate the token on the server.
         */
        $scope.logout = function() {
            $http.post("/admin/logout").success(function() {
                $scope.user = undefined;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        /**
         * Pings the server. When the request is not authorized, the $http interceptor should
         * log out the current user.
         * When using routes, this is not necessary.
         */
        $scope.ping = function() {
            $http.get("/ping").success(function() {
                $scope.ok = true;
                $timeout(function() {$scope.ok = false;}, 3000);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.refresh = function(id) {
            $scope.loading = true;
            $http.get("/refreshScreens/"+id).success(function() {
                $timeout(function(){$scope.screens(); $scope.loading = false;}, 1000);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.refreshAll = function() {
            $scope.loading = true;
            $http.get("/refreshScreens").success(function() {
                $timeout(function(){$scope.screens();  $scope.loading = false}, 1000);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.emergency = function() {
            $http.get("/emergency").success(function(response) {
                $scope.ok = (response.data === "1");
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.requestPassword = function() {
            $http.get("admin/requestPassword/"+$scope.credentials.email).success(function(response) {
                $scope.passwordRequested = true;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        /** Subscribe to the logout event emitted by the $http interceptor. */
        $scope.$on("InvalidToken", function() {
            console.log("InvalidToken!");
            $scope.user = undefined;
        });
        
        // Runtime Translation
        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

    });