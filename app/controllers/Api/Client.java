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
import java.util.UUID;

// Import play classes
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

// Import play controllers
import controllers.lib.Role.SecuredController;

public class Client extends SecuredController {

    public static Result getAll() {
        List<models.Client> clients = models.Client.find.all();
        return ok(Json.toJson(clients));
    }

    public static Result get(Integer id) {
        models.Client client = models.Client.find.byId(id);
        if (client != null) {
            return ok(Json.toJson(client));
        }
        return badRequest("This client doesn't exists.");
    }

    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.Client> clientForm = Form.form(models.Client.class);
        //JSON-data an formular das model binden
        clientForm = clientForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (clientForm.hasGlobalErrors() || clientForm.hasErrors()) {
            return badRequest(clientForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.Client client = clientForm.get();
            try {
                //Prüfen ob slide_id gesetzt ist und somit eine Slide aktualisert werden soll
                if (client.client_id != null) {
                    //Slide in der Datenbank suchen
                    models.Client testClient = models.Client.find.byId(client.client_id);
                    //Prüfen ob Slide vorhanden und Owner mit dem akutellen Admin übereinstimmt
                    if (testClient != null) {
                        //Versuchen das model zu aktualiseren
                        client.update();
                    } else {
                        return status(403, "This client doesn't exists.");
                    }
                } else {
                    //Versuchen das model zu speichern
                    client.registertime = new DateTime();
                    do {
                        client.token = UUID.randomUUID().toString();
                    }while (!models.Client.find.where().eq("token", client.token).findList().isEmpty());
                    client.save();
                }
                return ok(client.client_id.toString());
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
        models.Client client = new models.Client();
        client.name = "";
        client.street = "Otto-Hahn-Str.";
        client.housenumber = "";
        client.token = "leer";
        client.zipcode = "44227";
        client.city = "Dortmund";
        return ok(Json.toJson(client));
    }

}