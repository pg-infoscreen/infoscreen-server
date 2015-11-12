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

import java.util.Iterator;
import java.util.List;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.lib.Role.Roles;
import controllers.lib.Role.SecuredAction;
import controllers.lib.Role.SecuredController;

// Import play classes

public class Channel extends SecuredController {

    public static Result getAll() {
        List<models.Channel> channels = models.Channel.find.all();
        return ok(Json.toJson(channels));
    }

    public static Result get(Integer id) {
        models.Channel channel = models.Channel.find.byId(id);
        if (channel != null) {
            Iterator<models.Slide> iterator2 = channel.slides.iterator();
            while(iterator2.hasNext()){
                iterator2.next().template = null;
            }
            return ok(Json.toJson(channel));
        }
        return badRequest("This channel doesn't exists.");
    }


    @Roles({"Channel"})
    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.Channel> channelForm = Form.form(models.Channel.class);
        //JSON-data an formular das model binden
        channelForm = channelForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (channelForm.hasGlobalErrors() || channelForm.hasErrors()) {
            return badRequest(channelForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.Channel channel = channelForm.get();
            try {
                //Prüfen ob channel_id gesetzt ist und somit eine Channel aktualisert werden soll
                if (channel.channel_id != null) {
                    //Channel in der Datenbank suchen
                    models.Channel testChannel = models.Channel.find.byId(channel.channel_id);
                    //Prüfen ob Channel vorhanden und Owner mit dem akutellen Admin übereinstimmt
                    if (testChannel != null) {
                        //Versuchen das model zu aktualiseren
                        channel.update();
                    } else {
                        return status(403, "This channel doesn't exists.");
                    }
                } else {
                    channel.owner = SecuredAction.admin;
                    //Versuchen das model zu speichern
                    channel.save();
                }
                return ok(channel.channel_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    public static Result delete(Integer channelId) {
    	if(channelId == 1)
    		return forbidden("Channel one should not be delted");
        if (channelId != null) {
            models.Channel channel = models.Channel.find.byId(channelId);
            if (channel != null) {
                try {
                    //Versuchen das model zu löschen
                    channel.delete();
                    return ok(channel.channel_id.toString());
                } catch (Exception e) {
                    return internalServerError(e.getMessage());
                }
            }
            return status(403, "This Channel or this Channel doesn't exists.");
        }
        return badRequest();
    }

    public static Result getNew(){
        models.Channel channel = new models.Channel();
        channel.name = "neu";
        return ok(Json.toJson(channel));
    }

}
