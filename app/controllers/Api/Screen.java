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
import java.util.List;

import models.Websocket;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.node.ObjectNode;

// Import play controllers
import controllers.lib.Role.SecuredController;
// Import play classes
// Import play models

public class Screen extends SecuredController {

    public static Result getAll() {
        List<Websocket> websockets = Websocket.find.all();
        return ok(Json.toJson(websockets));
    }
    
    public static Result refreshScreen(Integer id) {
        Websocket websocket = models.Websocket.find.byId(id);
        ObjectNode message = Json.newObject();
        message.put("event", 2);
        message.put("data", "");
        if(websocket != null) {
            ActorRef akkaActor = Akka.system().actorFor(websocket.path);
            akkaActor.tell(message.toString(), null);
            return ok();
        } else {
            return badRequest();
        }
    }
}