adminControllers.controller('SlidesCtrl',
    function($scope, errorFactory, $routeParams, $http, errorFactory) {
        errorFactory.resetError();
        $scope.date = new Date();
        $scope.get = function() {
            $http.get('slides').success(function (data) {
                $scope.slides = data;
                $scope.orderProp = 'user_id';
                $scope.activeSlide_id = $routeParams.slide_id;
            }).error(function(data, status) {
                errorFactory.setError(status);
            });
        };
        $scope.get();

        $scope.update = function() {
            this.slide.active = !this.slide.active;
            $http.put("slides", this.slide).success(function() {
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
    });

adminControllers.controller('NewSlideCtrl',
    function($scope, errorFactory, $http, $location, errorFactory) {
        errorFactory.resetError();
        $http.get('slides/new').success(function(data) {
            $scope.slide = data;
            //Necassary because of seconds in quartz-scheduler
            $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
        });
        $http.get('templates').success(function(data) {
            $scope.templates = data;
        }).error(function(data, status){
            errorFactory.setError(status);
        });
        $scope.save = function() {
            //Necassary because of seconds in quartz-scheduler
            $scope.slide.cronexpression = "* " + $scope.slide.cronexpression;
            $http.put('slides', $scope.slide).success(function(data) {
                $location.path("/slides/"+data);
            }).error(function(data, status){
                //Necassary because of seconds in quartz-scheduler
                $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
                errorFactory.setError(status);
            });
        };

        $scope.openModal = function() {
            $http.get('files/mime/image').success(function(data) {
                $scope.images = data;
                $('#myModal').modal('toggle');
                $(".summernote").summernote("focus");
            });
        };

        $scope.addImage = function(image) {
            $(".summernote").summernote("insertImage", "library/" + image.name + "." + image.extension, image.name);
        };

        // Watch for croneexpression field
        //TODO javacript error beheben
        $scope.$watch("slide.cronexpression", function(newValue, oldValue) {
            $scope.greeting = prettyCron.toString($scope.slide.cronexpression);
            if($scope.greeting === "Every minute")
                $scope.greeting = "No restriction"
        });
        
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

adminControllers.controller('SlideCtrl',
    function($scope, $routeParams,$dateParser, $http, $location, $timeout, errorFactory,$filter) {
        errorFactory.resetError();
        console.log("muh")
        $http.get('slides/' + $routeParams.slide_id).success(function(data) {
            $scope.slide = data;
            $scope.templ = angular.fromJson(data.template.templateDescription);
            $scope.complexMode = $scope.templ == null;
            $scope.staticData = {}
            // Necassary for recursion
            $scope.dodeep = function(json, owner) {
                for ( var d of json)
                {
                    d.owner = owner;
                    if(d.type=="List"){
                        owner[d.name] = [];
                    }
                    if(d.type=="Complex"){
                        owner[d.name] = {};
                        $scope.dodeep(d.complexDesc, owner[d.name]);
                    }   
                    if(d.type=="Bool"){
                        owner[d.name] = true;
                    }
                    if(d.type=="Int"){
                        owner[d.name] = 0;
                    }
                    if(d.type=="String"){
                        owner[d.name] = "Test";
                    }
                }
            }
            $scope.dodeep($scope.templ,$scope.staticData );
            console.log($scope.templ)
            console.log($scope.staticData)
            $scope.deathDate = new Date(0)
            $scope.enableDeathDate = false;
            if(data.deathDate != null){
                $scope.deathDate = $dateParser(data.deathDate, "dd.MM.yyyy HH:mm");
                $scope.enableDeathDate = true;
            }
            //Necassary because of seconds in quartz-scheduler
            $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
            $http.get('templates').success(function(data) {
                $scope.templates = data;
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        }).error(function(data, status){
            errorFactory.setError(status);
        });

        $scope.update = function() {
            // TODO: Gedoppleten Code auslagern, Benutzer feedback.
            $scope.slide.deathDate = $scope.deathDate!= null ? $filter('date')($scope.deathDate, "dd.MM.yyyy HH:mm", "UTC") : null;
            //Necassary because of seconds in quartz-scheduler
            $scope.slide.cronexpression = "* " + $scope.slide.cronexpression;
            $http.put("slides", $scope.slide).success(function(data) {
                $location.path("/slides/"+data);
                $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
            }).error(function(data, status){
                errorFactory.setError(status);
                //Necassary because of seconds in quartz-scheduler
                $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
            });

        };
        
        $scope.saveSlide = function() {
            // TODO: Gedoppleten Code auslagern, Benutzer feedback.
            $scope.slide.deathDate = $scope.deathDate!= null ? $filter('date')($scope.deathDate, "dd.MM.yyyy HH:mm", "UTC") : null;
            //Necassary because of seconds in quartz-scheduler
            $scope.slide.cronexpression = "* " + $scope.slide.cronexpression;
            $http.put("slides", $scope.slide).success(function(data) {
                //Necassary because of seconds in quartz-scheduler
                $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
            }).error(function(data, status){
                errorFactory.setError(status);
                //Necassary because of seconds in quartz-scheduler
                $scope.slide.cronexpression = $scope.slide.cronexpression.substring(2);
                
            });

        };
        
        $scope.updateDeathDate = function(sadfg) {
            // Old state, this  event is fired bevor the change is happend
            /*if(!$scope.enableDeathDate && $scope.deathDate == null)
                $scope.deathDate = new Date(0);
            if($scope.enableDeathDate)
                $scope.deathDate = null;*/
            $scope.deathDate = sadfg
            console.log($scope.deathDate)
        };

        $scope.delete = function() {
            $http.delete("/slides/" + $scope.slide.slide_id).success(function() {
                $timeout(function(){$location.path( "/slides")}, 500);
            }).error(function(data, status){
                errorFactory.setError(status);
            });
        };
        
        $scope.openModal = function() {
            $http.get('files/mime/image').success(function(data) {
                $scope.images = data;
                $('#myModal').modal('toggle');
                $(".summernote").summernote("focus");
            });
        };
        
        $scope.isJSONValid = function(json, desc){
            var ob = angular.fromJson(json);
            var recursion = function(obj, des){
                if( Object.prototype.toString.call( des ) === '[object Array]' ) {
                    for(var d of des)
                    {
                        if(!obj[d.name] == null)
                            return false;
                        if(!recursion(obj[d.name],d))
                            return false;
                    }
                    return true;
                }
                if(des.type==="String"){
                    return (typeof obj == "string");
                }
                else if(des.type==="Bool"){
                    return (typeof obj == "boolean");
                }
                else if(des.type==="Int"){
                    return (typeof obj == "number");
                }
                else if(des.type=="Complex"){
                    return recursion(obj,des.complexDesc);
                }
                else if(des.type=="List"){
                    if( Object.prototype.toString.call( obj ) !== '[object Array]' )
                        return false;
                    
                    for (var i = 0; i < obj.length; i++)
                    {
                        if(!recursion(obj[i],des.complexDesc))
                            return false;
                    }
                    return true;
                }

                console.log("Recuresion========")
                console.log(obj)
                console.log(des)
                console.log(typeof obj)
                
                return false;
            }

            console.log("Recuresion========")
            var res = recursion(ob,desc);
            console.log(res)
            return res;
        }
        
        $scope.listAdd = function(desc) {
            var elem = {}
            var typeDesc=angular.copy(desc.complexDesc);
            $scope.dodeep(typeDesc,elem);
            elem.$$complexDesc = typeDesc; // toJson ignores Properies that start with $$. See https://docs.angularjs.org/api/ng/function/angular.toJson
            console.log(elem)
            desc.owner[desc.name].push(elem);
            $scope.onStaticChange()
        };   
        
        $scope.onStaticChange = function() {
            console.log($scope.staticData);
            $scope.slide.content = angular.toJson($scope.staticData,true,4);
        }
        
        $scope.updateIndex = function(idx, d) {
        }

        $scope.addImage = function(image) {
            $(".summernote").summernote("insertImage", "library/" + image.name + "." + image.extension, image.name);
        };

        // Watch for croneexpression field
        //TODO javacript error beheben
        $scope.$watch("slide.cronexpression", function(newValue, oldValue) {
            $scope.greeting = prettyCron.toString($scope.slide.cronexpression);
            if($scope.greeting === "Every minute")
                $scope.greeting = "No restriction"
        });
        
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

adminControllers.controller('SlidePreviewCtrl',
    function($scope, $routeParams, $http, $sce, errorFactory) {
        errorFactory.resetError();
        $http.get('slides/' + $routeParams.slide_id).success(function(data) {
            $scope.slide = data;
            $scope.previewUrl = $sce.trustAsResourceUrl("preview/" + data.slide_id);
        }).error(function(data, status){
            errorFactory.setError(status);
        });

    });