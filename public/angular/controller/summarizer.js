adminControllers.controller('SummarizerCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout, $translate) {
        errorFactory.resetError();

        $scope.sumy = {};
        $scope.sumy.summarizer = "lex_rank";
        $scope.sumy.language = "german";
        $translate('SUMMARIZERS.SUMMARIZER.TEXT.TWO').then(function (textone) {
            $scope.sumy.text = textone;
        });
        $translate('SUMMARIZERS.SUMMARIZER.TEXT.THREE').then(function (texttwo) {
            $scope.textoutput = texttwo;
        });
        $scope.sumy.targetlength = "1";

        $scope.summarize = function() {
            $http.post("summarizer", $scope.sumy).success(function(data) {
                $scope.textoutput = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });