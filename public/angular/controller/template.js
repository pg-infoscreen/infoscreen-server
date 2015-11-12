adminControllers.controller('TemplatesCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('templates').success(function (data) {
                $scope.templates = data;
                $scope.orderProp = 'user_id';
                $scope.activeTemplate_id = $routeParams.template_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();
    });

adminControllers.controller('NewTemplateCtrl',
    function($scope, errorFactory, $http, $location) {
        errorFactory.resetError();
        $http.get('templates/new').success(function(data) {
            $scope.template = data;
        });

        $scope.save = function() {
            $http.put('templates', $scope.template).success(function(data) {
                $location.path("/templates/" + data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        // Ace Editor Global Options
        $scope.aceLoaded = function(_editor) {
            // Options
            _editor.setTheme("ace/theme/dreamweaver");
            _editor.setAutoScrollEditorIntoView(true);
            _editor.setOptions({
                showGutter: true,
                showPrintMargin: false,
                fontSize: '12px',
                maxLines: '30',
                minLines: '10'
            });
        };
    });

adminControllers.controller('TemplateCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
        $http.get('templates/' + $routeParams.template_id).success(function(data) {
            $scope.template = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });


        $scope.update = function() {
            $http.put("templates", $scope.template).success(function() {
                $location.path( "/templates/" + $scope.template.template_id);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        $scope.saveTemplate = function() {
            // TODO: Gedoppleten Code auslagern, Benutzer feedback.
            $http.put("templates", $scope.template).success(function() {
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.delete = function() {
            $http.delete("/templates/" + $scope.template.template_id).success(function() {
                $timeout(function(){$location.path( "/templates")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        // Ace Editor Global Options
        $scope.aceLoaded = function(_editor) {
            // Options
            _editor.setTheme("ace/theme/dreamweaver");
            _editor.setAutoScrollEditorIntoView(true);
            _editor.setOptions({
                showGutter: true,
                showPrintMargin: false,
                fontSize: '12px',
                maxLines: '30',
                minLines: '10'
            });
        };
    });
