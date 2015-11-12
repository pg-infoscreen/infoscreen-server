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
package controllers;

// Import play classes
import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;

// Import play controllers
import controllers.lib.Role.SecuredAction;
import controllers.lib.Role.SecuredController;

public class AdminSecured extends SecuredController {

    public static Result ping() {
        //Es kann direkt auf den Header zugegriffen werden, da durch den SecuredController sichergestell ist, dass er vorhanden ist
        String token = request().getHeader(SecuredAction.AuthTokenHeader);
        models.Session session = models.Session.find.where().eq("session_id", token).findUnique();
        ObjectNode result = Json.newObject();
        result.put(SecuredAction.AuthTokenCookieKey, session("auth"));
        result.put("user_id", session.owner.user_id);
        response().setCookie(SecuredAction.AuthTokenCookieKey, token);
        return ok(result);
    }

    public static Result logout() {
        //XSRF-TOKEN aus dem header holen
        String[] authTokenHeaderValues = request().headers().get(SecuredAction.AuthTokenHeader);
        //Wenn authTokenHeaderValues den richtigen Werten entspricht
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            //Token auslesen
            String token = authTokenHeaderValues[0];
            if (token != null) {
                //Session löschen
                session().clear();
                //Cookie löschen
                response().discardCookie("XSRF-TOKEN");
            }
        }
        return ok("logout");
    }

}
