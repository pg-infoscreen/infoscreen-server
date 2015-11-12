/**
Copyright 2015 Fabian Bock, Fabian Bruckner, Christine Dahn, Amin Nirazi, Matthäus Poloczek, Kai Sauerwald, Michael Schultz, Shabnam Tabatabaian, Tim Tegeler und Marvin Wepner

This file is part of pg-infoscreen.

pg-infoscreen is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

pg-infoscreen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with pg-infoscreen.  If not, see <http://www.gnu.org/licenses/>.
*/
package controllers.Api;

// Import java classes

import org.joda.time.DateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

// Import play controllers
import controllers.lib.Role.SecuredController;

// Import play classes
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.libs.Json;
import play.mvc.Result;

// Import play models

public class Statistic extends SecuredController {
    
    // Stats for Slides
    public static Result getSlideStats() {
        List<models.Statistic> statistics = models.Statistic.find.all();
        Iterator<models.Statistic> statisticsIterator = statistics.iterator();
        // Empty List
        List<models.Statistic> statisticsReturn = new ArrayList<models.Statistic>();
        while(statisticsIterator.hasNext()) {
            models.Statistic statisticObject = statisticsIterator.next();
            if(!statisticsReturn.contains(statisticObject)) {
                // Add Statistic Object
                statisticsReturn.add(statisticObject);
            } else {
                // Update Statistic Object
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).views += statisticObject.views;
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).duration += statisticObject.duration;
                                if(statisticObject.updateTime.isAfter(statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime)) {
                    statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime = statisticObject.updateTime;
                }
            }
        }
        return ok(Json.toJson(statisticsReturn));
    }
    
    // Stats for Channels
    public static Result getChannelStats(Integer id) {
        List<models.Statistic> statistics = models.Statistic.find.where().eq("channel.channel_id", id).findList();
        Iterator<models.Statistic> statisticsIterator = statistics.iterator();
        // Empty List
        List<models.Statistic> statisticsReturn = new ArrayList<models.Statistic>();
        while(statisticsIterator.hasNext()) {
            models.Statistic statisticObject = statisticsIterator.next();
            if(!statisticsReturn.contains(statisticObject)) {
                // Add Statistic Object
                statisticsReturn.add(statisticObject);
            } else {
                // Update Statistic Object
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).views += statisticObject.views;
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).duration += statisticObject.duration;
                                if(statisticObject.updateTime.isAfter(statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime)) {
                    statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime = statisticObject.updateTime;
                }
            }
        }
        return ok(Json.toJson(statisticsReturn));
    }
    
    // Stats for Clients
    public static Result getClientStats(Integer id) {
        List<models.Statistic> statistics = models.Statistic.find.where().eq("client.client_id", id).findList();
        Iterator<models.Statistic> statisticsIterator = statistics.iterator();
        // Empty List
        List<models.Statistic> statisticsReturn = new ArrayList<models.Statistic>();
        while(statisticsIterator.hasNext()) {
            models.Statistic statisticObject = statisticsIterator.next();
            if(!statisticsReturn.contains(statisticObject)) {
                // Add Statistic Object
                statisticsReturn.add(statisticObject);
            } else {
                // Update Statistic Object
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).views += statisticObject.views;
                statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).duration += statisticObject.duration;
                                if(statisticObject.updateTime.isAfter(statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime)) {
                    statisticsReturn.get(statisticsReturn.indexOf(statisticObject)).updateTime = statisticObject.updateTime;
                }
            }
        }
        return ok(Json.toJson(statisticsReturn));
    }
    
    // Stats for StatisticDay
    public static Result getStatisticDayStats(Integer id) {
        // Variables
        Integer index = 0, slideHour, yesterdayHour;
        DateTime slideDayTime, yesterdayDayTime;
        
        List<models.StatisticDay> statisticsDay = models.StatisticDay.find.where().eq("slide.slide_id", id).order().asc("updateTime").findList();
        Iterator<models.StatisticDay> statisticsDayIterator = statisticsDay.iterator();
        
        // Yesterday Time Calc
        yesterdayDayTime = (new DateTime()).withZone(DateTimeZone.forID(session("timezone")));
        yesterdayDayTime = yesterdayDayTime.minusHours(23);
        yesterdayHour = yesterdayDayTime.getHourOfDay();
        
        // 0 Liste initialisieren
        List<Integer> statisticsDayReturn = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        
        // Iterate List
        while(statisticsDayIterator.hasNext()) {
            models.StatisticDay statisticDayObject = statisticsDayIterator.next();
            
            // Slide Time Calc
            slideDayTime = statisticDayObject.updateTime;
            slideDayTime = slideDayTime.withZone(DateTimeZone.forID(session("timezone")));
            slideHour = slideDayTime.getHourOfDay();
            
            // Because of long to int exceptionhandling
            try {   
                if(slideHour == yesterdayHour) {
                    index = statisticsDayReturn.size() - 1;
                } else if(slideHour > yesterdayHour) {
                    index = (slideHour - yesterdayHour);
                } else if(slideHour < yesterdayHour){
                    index = (24 - (yesterdayHour - slideHour)) % 24;
                }
                statisticsDayReturn.set(index, statisticDayObject.views.intValue());
            } catch(Exception e) {
                return badRequest(e.getMessage());
            }
        }
        return ok(Json.toJson(statisticsDayReturn));
    }

    public static Result getAccess() {
        // Variables
        Integer newValue, accessHour, yesterdayHour, index = 0;
        DateTime yesterdayDayTime, accessTime;
           
        // Yesterday Time Calc
        yesterdayDayTime = (new DateTime()).withZone(DateTimeZone.forID(session("timezone")));
        //yesterdayDayTime = yesterdayDayTime.minusHours(24);
        yesterdayDayTime = yesterdayDayTime.minusHours(23).minusMinutes(yesterdayDayTime.getMinuteOfHour()).minusSeconds(yesterdayDayTime.getSecondOfMinute());
        yesterdayHour = yesterdayDayTime.getHourOfDay();
                
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        List<models.Access> accessDay = models.Access.find.where().ge("timestamp", fmt.print(yesterdayDayTime.toDateTime(DateTimeZone.UTC))).order().asc("timestamp").findList();
        Iterator<models.Access> accessDayIterator = accessDay.iterator();
        
        // 0 Liste initialisieren
        List<Integer> accessDayReturn = Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        
        // Iterate List
        while(accessDayIterator.hasNext()) {
            models.Access access = accessDayIterator.next();
            // Access Time Calc
            accessTime = access.timestamp;
            accessTime = accessTime.withZone(DateTimeZone.forID(session("timezone")));
            accessHour = accessTime.getHourOfDay();
            
            // Set value
            if(accessHour == yesterdayHour) {
                index = accessDayReturn.size() - 1;
            } else if(accessHour > yesterdayHour) {
                index = accessHour - yesterdayHour;
            } else if(accessHour < yesterdayHour){
                index = (24 - (yesterdayHour - accessHour)) % 24;
            }
            
            newValue = accessDayReturn.get(index)+1;
            accessDayReturn.set(index, newValue);

        }
        return ok(Json.toJson(accessDayReturn));
    }
}