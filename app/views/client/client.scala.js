// Angular App definieren
var app = angular.module("screen", ['ngWebsocket']);

// "Global"-data pool for app
app.factory('dataPool', function() {
    
    // Globale Speicher
    var slidesPool = [];
    var newsPool = [];
    var guid;
    var fallbackTemplate;
    var loaded = false;
    var presentationMode = false;
    
    return {
        slides : slidesPool,
        news : newsPool,
        guid : guid,
        fallbackTemplate : fallbackTemplate,
        loaded : loaded,
        pm : presentationMode
    };
});

// App Client-Server-Commuication by WebSocket
app.factory('myWebsocket', ['$log', '$timeout', '$websocket', 'dataPool', '$http', function($log, $timeout, $websocket, dataPool, $http) {
    // Open a WebSocket connection
    
    var ws = $websocket.$new({
        url: "@routes.Index.socket().webSocketURL(request)",
        reconnect: true, // it will reconnect after 1 seconds
        reconnectInterval: 1000
    });

    // Socket open
    ws.$on('$open', function () {
        // Websocket Handshake
        dataPool.guid = Math.floor(Math.random() * (32767 - 1)) + 1;
        ws.$emit('7', {
            guid: dataPool.guid
        });
        $timeout(function() {
            $http.get("/init/" + dataPool.guid).success(function (data) {
                dataPool.loaded = true;
            }).error(function (data, status, headers, config) {
                // Handshake error, handling
            });
        }, 1000);
        $log.info('Websocket OPEN');
    })
    
    // Socket close
    ws.$on('$close', function () {
        $log.info('Websocket CLOSE');
        dataPool.loaded = false;
    });
    
    // Socket error
    ws.$on('$error', function () {
        $log.info('Websocket ERROR');
        dataPool.loaded = false;
    });
    
    // ReloadEvent
    ws.$on('2', function() {
        location.reload();
    });
    
    // Save incomming news
    ws.$on('4', function(data) {
        if(!angular.equals("{}", data)) {
            dataPool.news.unshift(JSON.parse(data));
        }
    });
    
    // Save incomming slide
    ws.$on('6', function(data) {
        if(!angular.equals("{}", data)) {
            dataPool.slides.unshift(JSON.parse(data));
        }
    });
    
    return {
        state: function() {
            return ws.$status();
        },
        ready: function() {
            return ws.$ready();
        },
        send: function(event, data) {
            ws.$emit(event, data);
        }
    };
}]);

// Main Controller
app.controller('start', ['$scope', '$http', '$timeout', '$interval', '$sce', '$filter', '$log', 'myWebsocket', 'dataPool', function($scope, $http, $timeout, $interval, $sce, $filter, $log, myWebsocket, dataPool) {

    // News in Screen laden, den Code initialisieren
    function newsShowFunction(){
        // Set Time before the first loading of Newsticker, prevents false
        // calculations
        $scope.getDatetime = Date.now();
        var elem = document.querySelector(".marquee");
        elem.addEventListener("animationiteration",function(e){
            var newsSpanText = document.getElementById("newsSpanText");
            if (dataPool.loaded && dataPool.news.length > 0) {
                var data = dataPool.news.pop();

                $scope.news_source = data.source;
                $scope.news_headline = data.headline;
                // If connection lost refill dataPool 
                if(!myWebsocket.ready()) {
                    dataPool.news.unshift(data);
                }
            }
            else {
                $scope.news_source = "PG587";
                $scope.news_headline = "Willkommen beim InfoScreen";            
            }
            newsSpanText.innerHTML = list[rnd];
        }, false);

    }
    
    // News in den Puffer laden
    function newsLoadFunction(){
        // Puffer ist 3 News gross
        if(dataPool.loaded && dataPool.news.length < 3 && myWebsocket.ready()){
            // Send new news event
            myWebsocket.send('3', {});
            $timeout(function() {
                newsLoadFunction();
            }, 1000);
        }else{
            $timeout(function() {
                newsLoadFunction();
            }, 1000);
        }
    }

    
    function showNextSlideFunction()
    {
        // Try to Show next Slide
        if (dataPool.loaded && dataPool.slides.length > 0) {
            var data = dataPool.slides.pop();
            //console.log(pureData);
            if(myWebsocket.ready()) {
                myWebsocket.send('1', {
                    slideID: data.slideID,
                    duration: data.duration 
                });
            } else {
                dataPool.slides.unshift(data);
            }
            var preview = "<h1 style='text-align:center; padding-top:100px;'>" + data.preview + "</h1>";
            $scope.slide = $sce.trustAsHtml(preview);
            $timeout(function() {
                $scope.slideClass = "animation-outin";
                $timeout(function() {
                    $scope.slideClass = "";
                }, 1500);
                $timeout(function() {
                    $scope.slide = $sce.trustAsHtml(data.content);
                }, 700);
            }, 3000);
            return data.duration * 1000;
        }
        // If there is no Slide 
        $scope.slide = $sce.trustAsHtml(dataPool.fallbackTemplate);
        return 1000;
    }
    
    // Slides in Screen laden
    function slideShowFunction(){
        var timing = 1000;
        if(!dataPool.pm)        {
            timing = showNextSlideFunction();
        }
        $timeout(function() {
            slideShowFunction();
        }, timing);
        
    }
    
    // Slides in den Puffer laden
    function slideLoadFunction(){
        // Puffer ist 3 News gross
        if(dataPool.loaded && dataPool.slides.length < 5 && myWebsocket.ready()){
            // Send new news event
            myWebsocket.send('5', {});
            $timeout(function() {
                slideLoadFunction();
            }, 1000);
        }else{
            $timeout(function() {
                slideLoadFunction();
            }, 1000);
        }
    }
    
    // Timer Funktion
    function timerFunction() {
        $scope.getDatetime = Date.now();
        var seconds = $filter('date')($scope.getDatetime, "s", "de_DE"); //1 - 59
        var minutes = $filter('date')($scope.getDatetime, "m", "de_DE"); //1 - 59
        var hours = $filter('date')($scope.getDatetime, "H", "de_DE"); //0 - 23
        /*
        Ab hier zeigesteuerte Events anlegen 
        */
        // Taeglicher Reload um 23:55:55 Uhr
        if(seconds == "55" && minutes == "55" && hours == "23") {
            $log.info("Client reload")
            location.reload();
        }
    }

    function togglePresentationMode(e)
    {
        var pm = document.getElementById("presentationMode");
        if(!pm.classList.contains("inPresentationMode"))
        {
            $log.info("[PresentationMode] Activated");
            pm.style.display = "block";
            dataPool.pm = true;
            pm.classList.add("inPresentationMode");
        }
        else
        {
            $log.info("[PresentationMode] Deactivated");
            pm.style.display = "none";
            dataPool.pm = false;
            pm.classList.remove("inPresentationMode");
        }
    }

    function onKeyboardPress(e)
    {
        if(e.charCode == 80)
            togglePresentationMode();
        else if(dataPool.pm && (e.keyCode == 39 || e.keyCode == 34))
            showNextSlideFunction();
    }
    
    /************************************************************
    SCRIPT-STARTER
    ************************************************************/ 
    (function() {
        // Fallback Template
        $http.get("/fallbackTemplate").success(function (data) {
            dataPool.fallbackTemplate = data;
        }).error(function (data, status, headers, config) {
            // Handshake error, handling
        });
        window.addEventListener("keypress", onKeyboardPress, false);
        // Show content and footer
        $("div").removeClass("noJS");
        newsLoadFunction();
        slideLoadFunction();
        newsShowFunction();
        slideShowFunction();
        $interval(timerFunction, 1000);
        $log.info("AngularJS Client gestartet...");
    })();  
}]);
