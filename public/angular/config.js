
var adminApp = angular.module('adminApp', [
    'ngRoute',
    'adminControllers',
    'ngCookies',
    'ui.ace',
    'autofill-directive',
    'summernote',
    'pascalprecht.translate'
]);


adminApp.config(["$httpProvider",
    function($httpProvider) {
        var interceptor = ["$rootScope", "$q", "$timeout", function($rootScope, $q, $timeout) {
            return function(promise) {
                return promise.then(
                    function(response) {
                        return response;
                    },
                    function(response) {
                        if (response.status == 401) {
                            $rootScope.$broadcast("InvalidToken");
                            $rootScope.sessionExpired = true;
                            $timeout(function() {$rootScope.sessionExpired = false;}, 5000);
                        } else if (response.status == 403) {
                            $rootScope.$broadcast("InsufficientPrivileges");
                        } else {
                            // Here you could handle other status codes, e.g. retry a 404
                        }
                        return $q.reject(response);
                    }
                );
            };
        }];
        $httpProvider.interceptors.push(interceptor);
    }]).config( ['$compileProvider',
        function( $compileProvider ) {
            $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|ssh):/);
            // Angular before v1.2 uses $compileProvider.urlSanitizationWhitelist(...)
        }
    ]);