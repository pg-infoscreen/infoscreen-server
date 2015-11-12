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
package controllers.lib;

import java.sql.Timestamp;
import org.joda.time.DateTime;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Iterator;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import models.StatisticDay;
import models.Statistic;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Database {
        
    public static void runDatabaseUtils() {
        
        Akka.system().scheduler().schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(1, TimeUnit.MINUTES), new Runnable() {
            @Override
            public void run() {
                try {
                    executeCleanStatisticDay();
                    executeControlStatisticDay();
                    executeControlStatistic();
                } catch(Exception e) {
                    System.out.println("Database Utils Error: " + e.getMessage());
                }
            }
        },Akka.system().dispatcher());
    }
    
    private static boolean executeCleanStatisticDay() throws Exception {
        // Early Date
        //Date checkDate = new Date(System.currentTimeMillis() - 25 * 3600 * 1000);
        DateTime checkDate = new DateTime();
        checkDate = checkDate.minusHours(23).minusMinutes(checkDate.getMinuteOfHour()).minusSeconds(checkDate.getSecondOfMinute());
        
        List<StatisticDay> statisticDayList = StatisticDay.find.all();
        Iterator<StatisticDay> statisticDayIterator = statisticDayList.iterator();
        while(statisticDayIterator.hasNext()){
            StatisticDay statisticDay = statisticDayIterator.next();
            if(statisticDay.updateTime.isBefore(checkDate)) {
                statisticDay.delete();
            }
        }
        return true;
    }
    
    // Remove Stats from Statistics without slide binding
    private static boolean executeControlStatistic() throws Exception {
        List<Statistic> statisticsList = Statistic.find.where().eq("slide", null).findList();
        Iterator<Statistic> statisticsIterator = statisticsList.iterator();
        while(statisticsIterator.hasNext()){
            Statistic statistic = statisticsIterator.next();
            statistic.delete();
        }
        return true;
    }
    
    // Remove Stats from StatisticsDay without slide binding
    private static boolean executeControlStatisticDay() throws Exception {
        List<StatisticDay> statisticsDayList = StatisticDay.find.where().eq("slide", null).findList();
        Iterator<StatisticDay> statisticsDayIterator = statisticsDayList.iterator();
        while(statisticsDayIterator.hasNext()){
            StatisticDay statisticDay = statisticsDayIterator.next();
            statisticDay.delete();
        }
        return true;
    }
}