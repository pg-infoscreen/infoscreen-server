/* App Module */
adminApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/admin', {
                templateUrl: 'assets/angular/views/index.html',
                controller: 'LoginCtrl'/*,
                 resolve:{
                 authorize:function($http) {
                 return $http.get("/ping"); // $http.get liefert eine Promise zurück
                 }
                 }*/
            }).
            when('/users/', {
                templateUrl: 'assets/angular/views/user/users.html',
                controller: 'UsersCtrl'
            }).
            when('/users/new', {
                templateUrl: 'assets/angular/views/user/new.html',
                controller: 'NewUserCtrl'
            }).
            when('/users/:user_id', {
                templateUrl: 'assets/angular/views/user/users.html',
                controller: 'UsersCtrl'
            }).
            when('/user/me', {
                templateUrl: 'assets/angular/views/user/me.html',
                controller: 'MeCtrl'
            }).
            when('/user/edit/:user_id', {
                templateUrl: 'assets/angular/views/user/edit.html',
                controller: 'EditUserCtrl'
            }).
            when('/user/:user_id', {
                templateUrl: 'assets/angular/views/user/user.html',
                controller: 'UserCtrl'
            }).
            when('/channels', {
                templateUrl: 'assets/angular/views/channel/channels.html',
                controller: 'ChannelsCtrl'
            }).
            when('/channels/new', {
                templateUrl: 'assets/angular/views/channel/new.html',
                controller: 'NewChannelCtrl'
            }).
            when('/channels/:channel_id', {
                templateUrl: 'assets/angular/views/channel/channels.html',
                controller: 'ChannelsCtrl'
            }).
            when('/channel/:channel_id', {
                templateUrl: 'assets/angular/views/channel/channel.html',
                controller: 'ChannelCtrl'
            }).
            when('/slides', {
                templateUrl: 'assets/angular/views/slide/slides.html',
                controller: 'SlidesCtrl'
            }).
            when('/slides/new', {
                templateUrl: 'assets/angular/views/slide/new.html',
                controller: 'NewSlideCtrl'
            }).
            when('/slides/:slide_id', {
                templateUrl: 'assets/angular/views/slide/slides.html',
                controller: 'SlidesCtrl'
            }).
            when('/slide/preview/:slide_id', {
                templateUrl: 'assets/angular/views/slide/preview.html',
                controller: 'SlidePreviewCtrl'
            }).
            when('/slide/:slide_id', {
                templateUrl: 'assets/angular/views/slide/slide.html',
                controller: 'SlideCtrl'
            }).
            when('/roles', {
                templateUrl: 'assets/angular/views/role/roles.html',
                controller: 'RolesCtrl'
            }).
            when('/roles/new', {
                templateUrl: 'assets/angular/views/role/new.html',
                controller: 'NewRoleCtrl'
            }).
            when('/roles/:role_id', {
                templateUrl: 'assets/angular/views/role/roles.html',
                controller: 'RolesCtrl'
            }).
            when('/role/:role_id', {
                templateUrl: 'assets/angular/views/role/role.html',
                controller: 'RoleCtrl'
            }).
            when('/templates', {
                templateUrl: 'assets/angular/views/template/templates.html',
                controller: 'TemplatesCtrl'
            }).
            when('/templates/new', {
                templateUrl: 'assets/angular/views/template/new.html',
                controller: 'NewTemplateCtrl'
            }).
            when('/templates/:template_id', {
                templateUrl: 'assets/angular/views/template/templates.html',
                controller: 'TemplatesCtrl'
            }).
            when('/template/:template_id', {
                templateUrl: 'assets/angular/views/template/template.html',
                controller: 'TemplateCtrl'
            }).
            when('/files', {
                templateUrl: 'assets/angular/views/file/files.html',
                controller: 'LibraryCtrl'
            }).
            when('/files/new', {
                templateUrl: 'assets/angular/views/file/new.html',
                controller: 'NewLibraryCtrl'
            }).
            when('/files/:id', {
                templateUrl: 'assets/angular/views/file/file.html',
                controller: 'FileCtrl'
            }).
            when('/log/slide/:id', {
                templateUrl: 'assets/angular/views/log/slide/slide.html',
                controller: 'LogSlideCtrl'
            }).
            when('/log', {
                templateUrl: 'assets/angular/views/log/log.html',
                controller: 'LogsCtrl'
            }).
            when('/newstickers', {
                templateUrl: 'assets/angular/views/newsticker/newstickers.html',
                controller: 'NewstickersCtrl'
            }).
            when('/newstickers/new', {
                templateUrl: 'assets/angular/views/newsticker/new.html',
                controller: 'NewNewstickerCtrl'
            }).
            when('/newstickers/:newsticker_id', {
                templateUrl: 'assets/angular/views/newsticker/newstickers.html',
                controller: 'NewstickersCtrl'
            }).
            when('/newsticker/:newsticker_id', {
                templateUrl: 'assets/angular/views/newsticker/newsticker.html',
                controller: 'NewstickerCtrl'
            }).
            when('/clients', {
                templateUrl: 'assets/angular/views/client/clients.html',
                controller: 'ClientsCtrl'
            }).
            when('/clients/new', {
                templateUrl: 'assets/angular/views/client/new.html',
                controller: 'NewClientCtrl'
            }).
            when('/clients/:client_id', {
                templateUrl: 'assets/angular/views/client/clients.html',
                controller: 'ClientsCtrl'
            }).
            when('/client/:client_id', {
                templateUrl: 'assets/angular/views/client/client.html',
                controller: 'ClientCtrl'
            }).
            when('/screens', {
                templateUrl: 'assets/angular/views/screen/screens.html',
                controller: 'ScreensCtrl'
            }).
            when('/statistics', {
                templateUrl: 'assets/angular/views/statistic/statistics.html',
                controller: 'StatisticsSlidesCtrl'
            }).
            when('/summarizer', {
                templateUrl: 'assets/angular/views/summarizer/summarizer.html',
                controller: 'SummarizerCtrl'
            }).
            otherwise({
                redirectTo: '/admin'
            });
    }]);