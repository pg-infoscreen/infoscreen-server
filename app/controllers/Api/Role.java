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

import controllers.lib.Role.Roles;
import controllers.lib.Role.SecuredController;
import play.data.Form;
import play.db.ebean.EbeanPlugin;
import play.libs.Json;
import play.mvc.Result;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

// Import play classes

@Roles({"SuperAdmin"})
public class Role extends SecuredController {

    public static Result getAll() {
        List<models.Role> roles = models.Role.find.all();
        return ok(Json.toJson(roles));
    }

    public static Result get(Integer id) {
        models.Role role = models.Role.find.byId(id);
        if (role != null) {
            return ok(Json.toJson(role));
        }
        return badRequest("This user doesn't exists.");
    }
    
	public static Result put()
	{
		// HTTP-request aufrufen und den body als JSON auslesen
		JsonNode json = request().body().asJson();

		// Bug von Ebean indem die Channels nicht geupdatet werden, sondern
		// versucht komplett neu zu speichern... CLASH!
		// TODO: After upgrading EBEAN fix this code
		LinkedList<Integer> channel_list = new LinkedList<Integer>();
		ArrayNode channels = (ArrayNode) json.get("channels");
		channels.forEach(c -> {
			channel_list.add(c.get("channel_id").asInt());
		});

		// abstraktes HTML-formular aus dem model erstellen lassen
		Form<models.Role> roleForm = Form.form(models.Role.class);
		// JSON-data an formular das model binden
		roleForm = roleForm.bind(json);
		// Prüfen ob beim Binden Fehler aufgetreten sind
		if (roleForm.hasGlobalErrors() || roleForm.hasErrors()) {
			return badRequest(roleForm.errorsAsJson());
		} else {
			// Model mit Hilfe des formulars generieren
			models.Role role = roleForm.get();
			try {
//				System.out.println("-------------\nName:" + role.name);
//				System.out.println("ID:" + role.role_id);
//				System.out.println("Channels:" + role.channels.size());
				// Prüfen ob role_id gesetzt ist und somit eine Role aktualisert
				// werden soll
				if (role.role_id != null) {
					// Role in der Datenbank suchen
					models.Role testRole = models.Role.find.byId(role.role_id);
					// Prüfen ob Role vorhanden und Owner mit dem akutellen
					// Admin übereinstimmt
					if (testRole != null) {
						// Bug von Ebean indem die Channels nicht geupdatet
						// werden, sondern versucht komplett neu zu speichern...
						// CLASH!
						// TODO: After upgrading EBEAN fix this code
						role.channels.clear();
						channel_list.forEach(c -> {
							models.Channel chan = models.Channel.find.byId(c);
							role.channels.add(chan);
						});
						// Versuchen das model zu aktualiseren
						role.update();
					} else {
						return status(403, "This role doesn't exists.");
					}
				} else {
					System.out.println("Versuche Modell zu speichern");
					// Versuchen das model zu speichern
					role.save();
				}
				return ok(role.role_id.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return internalServerError();
			}
		}
	}

    public static Result delete(Integer clientId) {
        if (clientId != null) {
            models.Client client = models.Client.find.byId(clientId);
            if (client != null) {
                try {
                    //Versuchen das model zu löschen
                    client.delete();
                    return ok(client.client_id.toString());
                } catch (Exception e) {
                    return internalServerError();
                }
            }
            return status(403, "This Client doesn't exists.");
        }
        return badRequest();
    }

    public static Result getNew(){
        models.Role role = new models.Role();
        role.name = "New Role";
        return ok(Json.toJson(role));
    }

}