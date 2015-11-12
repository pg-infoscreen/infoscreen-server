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
import java.util.concurrent.TimeUnit;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

// Import play classes
import com.fasterxml.jackson.databind.JsonNode;

import controllers.lib.NewsTickerUpdater;
import controllers.lib.Role.SecuredAction;
import controllers.lib.Role.SecuredController;

public class NewsTicker extends SecuredController {

    public static Result getAll() {
        List<models.NewsTicker> newstickers = models.NewsTicker.find.all();
        return ok(Json.toJson(newstickers));
    }

    public static Result get(Integer id) {
        models.NewsTicker newsticker = models.NewsTicker.find.byId(id);
        if (newsticker != null) {
            return ok(Json.toJson(newsticker));
        }
        return badRequest("This newsticker doesn't exists.");
    }

    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.NewsTicker> newstickerForm = Form.form(models.NewsTicker.class);
        //JSON-data an formular das model binden
        newstickerForm = newstickerForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (newstickerForm.hasGlobalErrors() || newstickerForm.hasErrors()) {
            return badRequest(newstickerForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.NewsTicker newsticker = newstickerForm.get();
            try {
                //Prüfen ob slide_id gesetzt ist und somit eine Slide aktualisert werden soll
                if (newsticker.newsticker_id != null) {
                    //Slide in der Datenbank suchen
                    models.NewsTicker testNewsticker = models.NewsTicker.find.byId(newsticker.newsticker_id);
                    //Prüfen ob Slide vorhanden und Owner mit dem akutellen Admin übereinstimmt
                    if (testNewsticker != null) {
                        //Versuchen das model zu aktualiseren
                        newsticker.update();
                        NewsTickerUpdater.newsTicker(newsticker);
                    } else {
                        return status(403, "You're not the owner of this Slide or this Slide doesn't exists.");
                    }
                } else {
                    newsticker.owner = SecuredAction.admin;
                    //Versuchen das model zu speichern
                    newsticker.save();
                    NewsTickerUpdater.newsTicker(newsticker);
                }
                return ok(newsticker.newsticker_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    /*public static Result putContent(Integer slideId) {
        if (slideId != null) {
            models.Slide slide = models.Slide.find.byId(slideId);
            //TODO: Adminüberprüfung rausgenommen
            if (slide != null) {
                //if(slide != null && slide.owner.equals(SecuredAction.admin)) {
                slide.content = request().body().asText();
                ;
                try {
                    slide.update();
                    return ok(slide.slide_id.toString());
                } catch (Exception e) {
                    return internalServerError();
                }
            }
            return status(403, "You're not the owner of this Slide or this Slide doesn't exists.");
        }
        return badRequest();
    }*/

    public static Result delete(Integer newstickerId) {
        if (newstickerId != null) {
            models.NewsTicker newsticker = models.NewsTicker.find.byId(newstickerId);
            //TODO: Adminüberprüfung rausgenommen
            if (newsticker != null) {
                //if(slide != null && slide.owner.equals(SecuredAction.admin)) {
                try {
                    //Versuchen das model zu löschen
                    newsticker.delete();
                    return ok(newsticker.newsticker_id.toString());
                } catch (Exception e) {
                    return internalServerError();
                }
            }
            return status(403, "You're not the owner of this Newsticker or this Newsticker doesn't exists.");
        }
        return badRequest();
    }

    public static Result getNew(){
        models.NewsTicker newsticker = new models.NewsTicker();
        newsticker.name = "neu";
        newsticker.active = false;
        newsticker.dynamic = true;
        newsticker.content = "[{}]";
        newsticker.url = "http://data.pg-infoscreen.de";
        newsticker.updateTime = 1;
        newsticker.updateTimeUnit = TimeUnit.HOURS;
        return ok(Json.toJson(newsticker));
    }

}
