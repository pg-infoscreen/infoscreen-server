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
import java.util.Date;

// Import play models
import models.Client;
import models.ClientCheckin;
import org.joda.time.DateTime;
import play.mvc.Controller;
// Import play classes
import play.mvc.Http;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public class Checkin extends Controller {

    public static Result checkin() {
        Client client = Checkin.auth(request());
        if (client != null) {
            ClientCheckin clientCheckin = new ClientCheckin();
            clientCheckin.ip = request().remoteAddress();
            clientCheckin.checkintime = new DateTime();

            JsonNode jsonNode = request().body().asJson();
            if(jsonNode == null){
                return badRequest("badRequest");
            }
            if(!jsonNode.has("hostname")){
                return badRequest("badRequest");
            }
            if(!jsonNode.has("sshport")){
                return badRequest("badRequest");
            }

            clientCheckin.hostname = jsonNode.get("hostname").asText();
            clientCheckin.sshport = jsonNode.get("sshport").asText();
            clientCheckin.client = client;
            clientCheckin.save();
            return ok("ok");
        }
        return unauthorized("unauthorized");
    }

    private static Client auth(Http.Request request){
        if(request.hasHeader("X-AUTHTOKEN")){
            String token = request.getHeader("X-AUTHTOKEN");
            return Client.find.where().eq("token", token).findUnique();
        }
        return null;
    }

}