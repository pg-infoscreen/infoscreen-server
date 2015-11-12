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
// Import play models
import models.Websocket;
import play.libs.Json;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

public class WebSocketActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, out);
    }

    private final ActorRef out;

    public WebSocketActor(ActorRef out) {
        this.out = out;
    }
    
    public void preStart() {
        Websocket websocket = new Websocket();
        websocket.path = getSelf().path().toString();
        websocket.warnings = 0;
        websocket.save();
    }
    
    // Doing
    public void onReceive(Object message) throws Exception {
        // Select Infos 
        if (message instanceof String) {
            // Select websocket
            Websocket websocket = Websocket.find.where().eq("path", getSelf().path().toString()).findUnique();
            // Parse message to JSON
            JsonNode messageJson = Json.parse(message.toString());
            // Event Selection 
            int eventID = Integer.parseInt(messageJson.get("event").toString().replaceAll("^\"|\"$", ""));
            switch(eventID){ 
                
                case 1: // Get Ping from Client
                    WebSocketAction.pingUpdateWebsocket(messageJson, websocket);
                    WebSocketAction.pingUpdateStatistic(messageJson, websocket);
                    WebSocketAction.pingUpdateStatisticDay(messageJson, websocket);
                    break; 
                    
                case 3: // Schedule one new News to client
                    WebSocketAction.scheduleNewNews(websocket);
                    break;
                    
                case 5: // Schedule one new News to client
                    WebSocketAction.scheduleNewSlide(websocket);
                    break;
                    
                case 7: // Save screen guid
                    WebSocketAction.saveGuid(messageJson, websocket);
                    break;
                    
                default: // Default Route 
                    out.tell(message, self());
            }
        }
    }

    public void postStop() throws Exception {
        // Delete websocket
        Websocket websocket = Websocket.find.where().eq("path",getSelf().path().toString()).findUnique();
        if(websocket != null) {
            websocket.delete();
        }
    }
}