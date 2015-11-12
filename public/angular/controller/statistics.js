adminControllers.controller('StatisticsSlidesCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        
        $scope.earlyDate = new Date;
        
        $http.get('channels').success(function (data) {
            $scope.channels = data;
        }).error(function(data, status) {
            errorFactory.setError(status);
        });
        
        $http.get('clients').success(function (data) {
            $scope.clients = data;
        }).error(function(data, status) {
            errorFactory.setError(status);
        });
        
        $scope.dropdownLabel = "Alle Slides";
        
        $scope.getSlides = function() {
            $scope.loading = true;
            $scope.earlyDate.setHours($scope.earlyDate.getHours() - 23);
            $scope.earlyDate.setMinutes(0);
            $scope.earlyDate.setSeconds(0);
            $scope.selectedSlides.splice(0,$scope.selectedSlides.length);
            $http.get('statistic/slides').success(function (data) {
                $scope.statistics = data;
                $scope.orderProp = 'views';
                $scope.reverse = true;
                $scope.loading = false;
            }).error(function(data, status) {
                errorFactory.setError(status);
                $scope.loading = false;
            });
        }
        
        $scope.getChannelSlides = function(id) {
            $scope.loading = true;
            $scope.earlyDate.setHours($scope.earlyDate.getHours() - 23);
            $scope.earlyDate.setMinutes(0);
            $scope.earlyDate.setSeconds(0);
            $scope.selectedSlides.splice(0,$scope.selectedSlides.length);
            $http.get('statistic/channel/' + parseInt(id)).success(function (data) {
                $scope.statistics = data;
                $scope.orderProp = 'views';
                $scope.reverse = true;
                $scope.loading = false;
            }).error(function(data, status) {
                errorFactory.setError(status);
                $scope.loading = false;
            });
        }
        
        $scope.getClientSlides = function(id) {
            $scope.loading = true;
            $scope.earlyDate.setHours($scope.earlyDate.getHours() - 23);
            $scope.earlyDate.setMinutes(0);
            $scope.earlyDate.setSeconds(0);
            $scope.selectedSlides.splice(0,$scope.selectedSlides.length);
            $http.get('statistic/client/' + parseInt(id)).success(function (data) {
                $scope.statistics = data;
                $scope.orderProp = 'views';
                $scope.reverse = true;
                $scope.loading = false;
            }).error(function(data, status) {
                errorFactory.setError(status);
                $scope.loading = false;
            });
        }
        
        // Detail Stats Part
        $scope.selectedSlides = [];
        
        $scope.updateChart = function() {
            // Detail Stats Part
            $scope.labels = [];
            $scope.series = [];
            $scope.data = [];        
            $scope.options = {
                responsive: true
            };
            
            // Update labels
            var hour = moment().hour();
            for (var i = 24; i > 0; i--) {
                $scope.labels.push(((((hour+1-i)%24)+24)%24) + "h");
            }
            
            angular.forEach($scope.selectedSlides, function(item) {
                $http.get('/statistic/statisticDay/' + parseInt(item.id)).success(function (data) {
                    $scope.data.push(data);
                    $scope.series.push(item.name);

                }).error(function(data, status) {
                    $scope.data.push([]);
                });
            });
        };
        
        // Checkboxen functionality
        $scope.selectSlide = function(id, name) {
            var index = -1;
            for (i = 0; i < $scope.selectedSlides.length; i++) { 
                if($scope.selectedSlides[i].id == id) {
                    var index = i;
                }
            }         
            if(index==-1) {
                $scope.selectedSlides.push({id:id, name:name});
            } else {
                $scope.selectedSlides.splice(index, 1);  
            }
            $scope.updateChart();
        }
        
        // Default load Slides Tab
        $scope.getSlides();
    }
);