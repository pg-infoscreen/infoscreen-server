// AngularJS filter
adminControllers.filter('filterSecondsToTime', function() {
    return function(seconds) {
        // From seconds to milliseconds
        var duration = moment.duration(seconds*1000);
        return duration.days()+" Tage - "+duration.hours()+" Std - "+duration.minutes()+" Min - "+duration.seconds()+" Sek";
    }
});