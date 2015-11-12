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
import java.util.Date;
import org.joda.time.DateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import play.libs.Json;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Watchdog {
        
    public static void runWatchdog() {
        
        Akka.system().scheduler().schedule(Duration.create(30, TimeUnit.SECONDS), Duration.create(30, TimeUnit.SECONDS), new Runnable() {
            @Override
            public void run() {
                try {
                    executeWatchdog();
                } catch(Exception e) {
                    System.out.println("Watchdog Error: " + e.getMessage());
                }
            }
        },Akka.system().dispatcher());
    }
    
    private static boolean executeWatchdog() throws Exception {
        List<models.Websocket> websockets = models.Websocket.find.all();
        Iterator<models.Websocket> websocketsiterator = websockets.iterator();
        ActorRef akkaActor;
        
        while(websocketsiterator.hasNext()) {
            models.Websocket websocket = websocketsiterator.next();
            
            // Define actorRef -> empty ref -> teminated
            akkaActor = Akka.system().actorFor(websocket.path);
            
            // First check is if websocket avaible in akka --- GarbageCollection :)
            
            if(akkaActor.isTerminated()) {               
                    websocket.delete(); 
                    continue;
            }
            
            // Date with duration and offset 15 sec
            try{
                DateTime slideTime = websocket.updateTime;
                slideTime = slideTime.plusSeconds(websocket.slide.duration).plusSeconds(10);
                DateTime nowTime = new DateTime();
                if(slideTime.isBefore(nowTime)) {
                    ++websocket.warnings;
                    websocket.update();
                }
            } catch(NullPointerException exception) {
                // Case updateTime oder slide is NULL
                ++websocket.warnings;
                websocket.update();
            }
            
            //System.out.println("Websocket  : " + websocket.websocket_id);
            //System.out.println("Slide      : " + websocket.slide.name);
            //System.out.println("NowTime    : " + nowTime);
            //System.out.println("SlideTime  : " + slideTime);
            //System.out.println("Problem    : " + (slideTime.before(nowTime)));
            //System.out.println("--------------------------------------");
            
            // Increment ErrorCount from websocket
            if(websocket.warnings > 0) {
                // What todo
                switch(websocket.warnings){ 
                    case 1:
                        // Send Mail 
                        break;
                        
                    case 2: case 3:
                        // Reload Client
                        ObjectNode message = Json.newObject();
                        message.put("event", 2);
                        message.put("data", "");
                        // Send Reload Event
                        akkaActor.tell(message.toString(), null);     
                        break; 
                        
                    case 4: case 5: 
                        // Kill Connection and establish new connection
                        akkaActor.tell(PoisonPill.getInstance(), null);
                        break;
                        
                    default: 
                        // Send Mail
                } 
            }
        }        
        return true;
    }  
}