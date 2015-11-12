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

// Import play classes
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import models.Channel;
import models.Client;
import models.Slide;
import models.Statistic;
import models.StatisticDay;
// Import play models
import models.Websocket;
import org.joda.time.DateTime;
import play.libs.Akka;
import play.libs.Json;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WebSocketAction {
    
    public static boolean pingUpdateWebsocket(JsonNode message, Websocket websocket) {
        // Only execute if websocket connection is available
        if(!Akka.system().actorFor(websocket.path).isTerminated()) {
            int slideID = Integer.parseInt(message.get("data").get("slideID").toString());
            websocket.slide = Slide.find.byId(slideID);
            websocket.warnings = 0;
            websocket.updateTime = new DateTime();
            websocket.update();
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean pingUpdateStatistic(JsonNode message, Websocket websocket) {
        // Only execute if websocket connection is available
        if(!Akka.system().actorFor(websocket.path).isTerminated()) {
            int slideID = Integer.parseInt(message.get("data").get("slideID").toString());
            int duration = Integer.parseInt(message.get("data").get("duration").toString());
            Slide slide = Slide.find.byId(slideID);
            // Temps
            Statistic statistic;
            Channel channel;
            Client client;
            
            // Websocket with Client (Channel) connection
            if(websocket.client != null && websocket.client.channel != null) {
                statistic = Statistic.find.where().eq("slide.slide_id", slideID).eq("client.client_id", websocket.client.client_id).eq("client.channel.channel_id", websocket.client.channel.channel_id).findUnique();
                client = websocket.client;
                channel = websocket.client.channel;
            // Websocket with Client (no Channel) connection    
            } else if(websocket.client != null && websocket.client.channel == null) {
                statistic = Statistic.find.where().eq("slide.slide_id", slideID).eq("client.client_id", websocket.client.client_id).eq("channel", null).findUnique();
                client = websocket.client;
                channel = null;
            // Websocket with Channel connection
            } else if(websocket.channel != null) {
                statistic = Statistic.find.where().eq("slide.slide_id", slideID).eq("channel.channel_id", websocket.channel.channel_id).eq("client", null).findUnique();
                client = null;
                channel = websocket.channel;
            // Websocket with no connection
            } else {
                statistic = Statistic.find.where().eq("slide.slide_id", slideID).eq("client", null).eq("channel", null).findUnique();
                client = null;
                channel = null;
            }
            
            if(statistic != null) {
                statistic.channel = channel;
                statistic.client = client;
                ++statistic.views;
                statistic.duration += Long.valueOf(duration);
                statistic.update();
            } else {
                statistic = new Statistic();
                statistic.channel = channel;
                statistic.client = client;
                statistic.slide = slide;
                statistic.views = Long.valueOf(1);
                statistic.duration = Long.valueOf(duration);
                statistic.save();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean pingUpdateStatisticDay(JsonNode message, Websocket websocket) {
        // Only execute if websocket connection is available
        if(!Akka.system().actorFor(websocket.path).isTerminated()) {
            int slideID = Integer.parseInt(message.get("data").get("slideID").toString());
            int duration = Integer.parseInt(message.get("data").get("duration").toString());
            Slide slide = Slide.find.byId(slideID);
            //Date now = new Date;
            // Temps
            List<StatisticDay> statisticDayList;
            StatisticDay statisticDay = null;
            statisticDayList = StatisticDay.find.where().eq("slide.slide_id", slideID).order().desc("statisticday_id").findList();
            if(statisticDayList.size() > 0) {
                statisticDay = statisticDayList.get(0);
            }
            
            if(statisticDay != null && statisticDay.updateTime.getHourOfDay() == (new DateTime()).getHourOfDay()) {
                statisticDay.channel = null;
                statisticDay.client = null;
                ++statisticDay.views;
                statisticDay.duration += Long.valueOf(duration);
                statisticDay.update();
            } else {
                statisticDay = new StatisticDay();
                statisticDay.channel = null;
                statisticDay.client = null;
                statisticDay.slide = slide;
                statisticDay.views = Long.valueOf(1);
                statisticDay.duration = Long.valueOf(duration);
                statisticDay.save();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean scheduleNewNews(Websocket websocket) {
        ObjectNode message = Json.newObject();
        message.put("event", 4);
        message.put("data", controllers.Scheduler.NewsTicker.schedule().toString());
        if(websocket != null) {
            ActorRef akkaActor = Akka.system().actorFor(websocket.path);
            // Only execute if websocket connection is available
            if(!akkaActor.isTerminated()) {
                akkaActor.tell(message.toString(), null);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static boolean scheduleNewSlide(Websocket websocket) {
        ObjectNode message = Json.newObject();
        message.put("event", 6);
        message.put("data", controllers.Scheduler.Scheduler.schedule(websocket).toString());
        if(websocket != null) {
            ActorRef akkaActor = Akka.system().actorFor(websocket.path);
            // Only execute if websocket connection is available
            if(!akkaActor.isTerminated()) {
                akkaActor.tell(message.toString(), null);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static boolean saveGuid(JsonNode message, Websocket websocket) {
        if(websocket != null) {
            websocket.guid = Integer.parseInt(message.get("data").get("guid").toString());
            websocket.update();
            return true;
        } else {
            return false;
        }
    }
}