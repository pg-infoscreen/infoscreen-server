adminControllers.controller('ChannelsCtrl',
    function($scope, errorFactory, $routeParams, $http) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('channels').success(function (data) {
                $scope.channels = data;
                $scope.orderProp = 'user_id';
                $scope.activeChannel_id = $routeParams.channel_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();
    });

adminControllers.controller('NewChannelCtrl',
    function($scope, errorFactory, $http, $location) {
        errorFactory.resetError();
        $http.get('channels/new').success(function(data) {
            $scope.channel = data;
        });
        $scope.save = function() {
            $http.put('channels', $scope.channel).success(function(data) {
                $location.path("/channels/"+data);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });

adminControllers.controller('ChannelCtrl',
    function($scope, errorFactory, $routeParams, $http, $location, $timeout) {
        errorFactory.resetError();
        $http.get('channels/' + $routeParams.channel_id).success(function(data) {
            $scope.channel = data;
            $scope.time = 0;
            $http.get('slides').success(function(data) {
                $scope.slides = data;
                /* This is no longer necessary, we dont remove slides on the right side
                var slidesIntersect = [];
                var foundSlideInSlides = false;
                for (var i = 0; i < $scope.slides.length; i++) {
                    for (var k = 0; k < $scope.channel.slides.length; k++) {
                        if($scope.slides[i].slide_id == $scope.channel.slides[k].slide_id) {
                            foundSlideInSlides = true;
                        }
                    }
                    if(!foundSlideInSlides){
                        slidesIntersect.push($scope.slides[i]);
                    }
                    foundSlideInSlides = false;
                }
                $scope.slides = slidesIntersect;*/
                // Reconstruct SlideOrder, if there is a slide order                
                if($scope.channel.slideOrder == null || $scope.channel.slideOrder == "" )
                    $scope.slideOrder = $scope.channel.slides;
                else {
                    $scope.slideOrder = $scope.channel.slideOrder.split(' ').map(Number);
                    if($scope.slideOrder.length > 0){
                        list = []
                        for (var i = 0; i < $scope.slideOrder.length; i++) {
                            for (var j = 0; j < $scope.slides.length; j++) {
                                if($scope.slideOrder[i] == $scope.slides[j].slide_id)
                                    list.push($scope.slides[j]);
                                    
                            }
                        }
                        $scope.channel.slides = list;
                    }
                    else
                        $scope.slideOrder = $scope.channel.slides;
                }
                for (var k = 0; k < $scope.channel.slides.length; k++) {
                    $scope.time += $scope.channel.slides[k].duration;
                }

                if($scope.channel.scheduleType != 'LINEAR'){
                    // Remove duplicates
                    a = [];
                    for ( i = 0; i <  $scope.channel.slides.length; i++ ) {
                        var current =  $scope.channel.slides[i];
                        if (a.indexOf(current) < 0) a.push(current);
                    }
                }
                
                if ($scope.channel.scheduleType == 'DYNAMIC') {
                    $scope.buildDynamicOptions();
                }
                
            }).error(function(data, status){
                errorFactory.setError(status);
            });

        }).error(function(data, status){
            errorFactory.setError(status);
        });
        
        $scope.Precompute = function (){
            //Necassary because of seconds in quartz-scheduler
            $scope.channel.cronexpression = "* " + $scope.channel.cronexpression;
            
            // Konstruiere String für die Liste
            list = ""
            for(var i = 0; i < $scope.channel.slides.length; i++)            {
                slide = $scope.channel.slides[i];
                list = list + (i == 0 ? "" : " ") + slide.slide_id
            }
            $scope.channel.slideOrder = list
            
            // Lösche doppelte einträge
            $scope.channel.slides = $scope.channel.slides.filter(function(item, pos) {
                return $scope.channel.slides.indexOf(item) == pos;
            })
            
            console.log($scope.slideOptions)
            
            if(typeof $scope.slideOptions != "undefined"){
                console.log("save options")
                $scope.channel.slideOptions = []
                for(key in $scope.slideOptions)
                    $scope.channel.slideOptions.push($scope.slideOptions[key]);
            }
            console.log($scope.channel.slideOptions)
        }

        $scope.update = function() {
            $scope.Precompute();
            
            $http.put("channels", $scope.channel).success(function(data) {
                $location.path("/channels/"+data);
            }).error(function(data, status){
                //Necassary because of seconds in quartz-scheduler
                $scope.channel.cronexpression = $scope.channel.cronexpression.substring(2);
                errorFactory.setError(status);
            });
        };
        
        $scope.saveChannel = function() {
            $scope.Precompute();
            
            console.log($scope.channel)
            
            $http.put("channels", $scope.channel).success(function(data) {
            }).error(function(data, status){
                //Necassary because of seconds in quartz-scheduler
                $scope.channel.cronexpression = $scope.channel.cronexpression.substring(2);
                errorFactory.setError(status);
            });
            
            //Danach müsste man die Liste wiederherstellen
        };

        $scope.delete = function() {
            $http.delete("/channels/" + $scope.channel.channel_id).success(function() {
                $timeout(function(){$location.path( "/channels")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        $scope.buildDynamicOptions = function () {
            // Building a liste of Options, but only if not allready build
            if(typeof $scope.slideOptions == "undefined"){
                $scope.slideOptions = {};

                for ( i = 0; i <  $scope.channel.slideOptions.length; i++ ) {
                    var current =  $scope.channel.slideOptions[i];
                    console.log(current)
                    console.log(current.slide)
                    if(typeof $scope.slideOptions[current.slide.slide_id] == 'undefined')
                        $scope.slideOptions[current.slide.slide_id] = current;
                }
                for ( i = 0; i <  $scope.channel.slides.length; i++ ) {
                    var current =  $scope.channel.slides[i];
                    if(typeof $scope.slideOptions[current.slide_id] == 'undefined')
                        $scope.slideOptions[current.slide_id] = 
                        { time: null, slide: current, minH: null, maxH: null, timeBegin: null, timeEnd: null };
    
                }
            }
        }
        
        $scope.changeToDynamic = function() {
            // Remove duplicates
            a = [];
            for ( i = 0; i <  $scope.channel.slides.length; i++ ) {
                var current =  $scope.channel.slides[i];
                if (a.indexOf(current) < 0) a.push(current);
            }
            $scope.channel.slides = a;
            
            $scope.buildDynamicOptions();
        };
        
        $scope.changeToLinear = function() {
            console.log("Change 2 linear")
        };
        
        $scope.changeToRandom = function() {
            // Remove duplicates
            a = [];
            for ( i = 0; i <  $scope.channel.slides.length; i++ ) {
                var current =  $scope.channel.slides[i];
                if (a.indexOf(current) < 0) a.push(current);
            }
            $scope.channel.slides = a;
        };
        
        $scope.updateSlideStatus = function() {
            // Disabled, because of write conflict...
            this.slide.active = !this.slide.active;
            $http.put("slides", this.slide).success(function() {
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };

        $scope.addSlide = function() {
            $scope.channel.slides.push(this.slide);
            $scope.time += this.slide.duration;
        };

        $scope.deleteSlide = function() {
            var newChannelSlides = [];
            for (var i = 0; i < $scope.channel.slides.length; i++) {
                if ($scope.channel.slides[i] != this.slide) {
                    newChannelSlides.push($scope.channel.slides[i]);
                }
            }
            $scope.channel.slides = newChannelSlides;
            $scope.time -= this.slide.duration;
            /*$scope.slides.push(this.slide);*/
        };
        
        $scope.moveSlideUp = function() {
            index = $scope.channel.slides.indexOf(this.slide);
            slides = $scope.channel.slides;
            $scope.channel.slides[index] = $scope.channel.slides[(index+slides.length-1)%slides.length]
            $scope.channel.slides[(index+slides.length-1)%slides.length] = this.slide
        };
        
        $scope.moveSlideDown = function() {
            index = $scope.channel.slides.indexOf(this.slide);
            slides = $scope.channel.slides;
            $scope.channel.slides[index] = $scope.channel.slides[(index+slides.length+1)%slides.length]
            $scope.channel.slides[(index+slides.length+1)%slides.length] = this.slide
        };

        $scope.deleteAllSlides = function() {
            $scope.channel.slides = [];
            $scope.time = 0;
        };

        $scope.addAllSlides = function() {
            $scope.channel.slides = $scope.channel.slides.concat($scope.slides);
            $scope.time = 0;
            for (var k = 0; k < $scope.channel.slides.length; k++) {
                $scope.time += $scope.channel.slides[k].duration;
            }
        };

    });