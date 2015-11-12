var app=angular.module("screen",["ngWebsocket"]);app.factory("dataPool",function(){var e,n,t=[],o=[],i=!1;return{slides:t,news:o,guid:e,fallbackTemplate:n,loaded:i}}),app.factory("myWebsocket",["$log","$timeout","$websocket","dataPool","$http",function(e,n,t,o,i){var a=t.$new({url:"@routes.Index.socket().webSocketURL(request)",reconnect:!0,reconnectInterval:1e3});return a.$on("$open",function(){o.guid=Math.floor(32766*Math.random())+1,a.$emit("7",{guid:o.guid}),n(function(){i.get("/init/"+o.guid).success(function(e){o.loaded=!0}).error(function(e,n,t,o){})},1e3),e.info("Websocket OPEN")}),a.$on("$close",function(){e.info("Websocket CLOSE"),o.loaded=!1}),a.$on("$error",function(){e.info("Websocket ERROR"),o.loaded=!1}),a.$on("2",function(){location.reload()}),a.$on("4",function(e){angular.equals("{}",e)||o.news.unshift(JSON.parse(e))}),a.$on("6",function(e){angular.equals("{}",e)||o.slides.unshift(JSON.parse(e))}),{state:function(){return a.$status()},ready:function(){return a.$ready()},send:function(e,n){a.$emit(e,n)}}}]),app.controller("start",["$scope","$http","$timeout","$interval","$sce","$filter","$log","myWebsocket","dataPool",function(e,n,t,o,i,a,s,l,u){function c(){if(u.loaded&&u.news.length>0){var n=u.news.pop();e.news_source=n.source,e.news_headline=n.headline,l.ready()||u.news.unshift(n),t(function(){c()},7e3)}else e.news_source="PG587",e.news_headline="Willkommen beim InfoScreen",t(function(){c()},1e3)}function r(){u.loaded&&u.news.length<3&&l.ready()?(l.send("3",{}),t(function(){r()},1e3)):t(function(){r()},1e3)}function d(){if(u.loaded&&u.slides.length>0){var n=u.slides.pop();l.ready()?l.send("1",{slideID:n.slideID,duration:n.duration}):u.slides.unshift(n);var o="<h1 style='text-align:center; padding-top:100px;'>"+n.preview+"</h1>";e.slide=i.trustAsHtml(o),t(function(){e.slideClass="animation-outin",t(function(){e.slideClass=""},1500),t(function(){e.slide=i.trustAsHtml(n.content),t(function(){d()},1e3*n.duration)},700)},3e3)}else e.slide=i.trustAsHtml(u.fallbackTemplate),t(function(){d()},1e3)}function f(){u.loaded&&u.slides.length<5&&l.ready()?(l.send("5",{}),t(function(){f()},1e3)):t(function(){f()},1e3)}function p(){e.getDatetime=Date.now();var n=a("date")(e.getDatetime,"s","de_DE"),t=a("date")(e.getDatetime,"m","de_DE"),o=a("date")(e.getDatetime,"H","de_DE");"55"==n&&"55"==t&&"23"==o&&(s.info("Client reload"),location.reload())}!function(){n.get("/fallbackTemplate").success(function(e){u.fallbackTemplate=e}).error(function(e,n,t,o){}),r(),f(),c(),d(),o(p,1e3),s.info("AngularJS Client gestartet...")}()}]);