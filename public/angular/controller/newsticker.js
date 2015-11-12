adminControllers.controller('NewstickersCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('newstickers').success(function (data) {
                $scope.newstickers = data;
                $scope.orderProp = 'newsticker_id';
                $scope.activeNewsticker_id = $routeParams.newsticker_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();
        $scope.update = function(newsticker) {
            this.newsticker.active = !this.newsticker.active;
            $http.put("newstickers", this.newsticker).success(function() {
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });

adminControllers.controller('NewNewstickerCtrl',
    function($scope, errorFactory, $http, $location) {
        errorFactory.resetError();
        $http.get('newstickers/new').success(function(data) {
            $scope.newsticker = data;
        });
        
        $scope.save = function() {
            $http.put('newstickers', $scope.newsticker).success(function(data) {
                $location.path("/newstickers/"+data);
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

adminControllers.controller('NewstickerCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
        $http.get('newstickers/' + $routeParams.newsticker_id).success(function(data) {
            $scope.newsticker = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.update = function() {
            $http.put("newstickers", $scope.newsticker).success(function(data) {
                $location.path("/newstickers/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.delete = function() {
            $http.delete("/newstickers/" + $scope.newsticker.newsticker_id).success(function() {
                $timeout(function(){$location.path( "/newstickers")}, 500);
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