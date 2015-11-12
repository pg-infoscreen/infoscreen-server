
adminControllers.controller('LibraryCtrl',
    function($scope, errorFactory, $http) {
        errorFactory.resetError();
        $scope.get = function() {
            $http.get('files').success(function (data) {
                $scope.files = data;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();
        $scope.clipboard = function() {
            window.prompt("Copy to clipboard: Ctrl+C, Enter", "${library}" + this.file.name);
        };

    });

adminControllers.controller('NewLibraryCtrl',
    function($scope, errorFactory, FileUploader) {
        var xsrf = getCookie("XSRF-TOKEN")
        var uploader = $scope.uploader = new FileUploader({
            url: 'files',
            headers : {
                'X-XSRF-TOKEN': xsrf
            }
        });
// FILTERS
        uploader.filters.push({
            name: 'customFilter',
            fn: function(item /*{File|FileLikeObject}*/, options) {
                return this.queue.length < 10;
            }
        });
// CALLBACKS
        uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
            console.info('onWhenAddingFileFailed', item, filter, options);
        };
        uploader.onAfterAddingFile = function(fileItem) {
            console.info('onAfterAddingFile', fileItem);
        };
        uploader.onAfterAddingAll = function(addedFileItems) {
            console.info('onAfterAddingAll', addedFileItems);
        };
        uploader.onBeforeUploadItem = function(item) {
            console.info('onBeforeUploadItem', item);
        };
        uploader.onProgressItem = function(fileItem, progress) {
            console.info('onProgressItem', fileItem, progress);
        };
        uploader.onProgressAll = function(progress) {
            console.info('onProgressAll', progress);
        };
        uploader.onSuccessItem = function(fileItem, response, status, headers) {
            console.info('onSuccessItem', fileItem, response, status, headers);
        };
        uploader.onErrorItem = function(fileItem, response, status, headers) {
            console.info('onErrorItem', fileItem, response, status, headers);
        };
        uploader.onCancelItem = function(fileItem, response, status, headers) {
            console.info('onCancelItem', fileItem, response, status, headers);
        };
        uploader.onCompleteItem = function(fileItem, response, status, headers) {
            console.info('onCompleteItem', fileItem, response, status, headers);
        };
        uploader.onCompleteAll = function() {
            console.info('onCompleteAll');
        };
        console.info('uploader', uploader);
    });

adminControllers.controller('FileCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
        $http.get('files/' + $routeParams.id).success(function(data) {
            $scope.file = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.clipboard = function() {
            window.prompt("Copy to clipboard: Ctrl+C, Enter", "${library}" + this.file.name);
        };

        $scope.delete = function() {
            $http.delete("files/" + $scope.file.file_id).success(function() {
                $timeout(function(){$location.path( "/files")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });