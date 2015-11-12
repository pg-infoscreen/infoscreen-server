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

import org.mindrot.jbcrypt.BCrypt;

import play.data.Form;
// Import play classes
import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.lib.Role.SecuredAction;
// Import play controllers
import controllers.lib.Role.SecuredController;

public class Me extends SecuredController {

    public static Result getAll() {
        List<models.User> users = models.User.find.all();
        return ok(Json.toJson(users));
    }

    public static Result get() {
        models.User user = models.User.find.byId(SecuredAction.admin.user_id);
        return ok(Json.toJson(user));
    }
    
    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.User> userForm = Form.form(models.User.class);
        //JSON-data an formular das model binden
        userForm = userForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (userForm.hasGlobalErrors() || userForm.hasErrors()) {
            return badRequest(userForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.User user = userForm.get();
            try {
                //Prüfen ob user_id gesetzt ist und somit ein User aktualisert werden soll
                if (user.user_id != null) {
                    //User in der Datenbank suchen
                    models.User testUser = models.User.find.byId(user.user_id);
                    //Prüfen ob User vorhanden und der User dem eingeloggten User entspricht
                    if (testUser != null && testUser.equals(SecuredAction.admin)) {
                        user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
                        //Versuchen das model zu aktualiseren
                        user.update();
                    } else {
                        return status(403, "You're not the user or this user doesn't exists.");
                    }
                } else {
                    //Versuchen das model zu speichern
                    return status(403, "No user id given.");
                }
                return ok(user.user_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }
}