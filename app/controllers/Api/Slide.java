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
import java.util.concurrent.TimeUnit;

import models.SlideStatus;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;


// Import play classes
import com.fasterxml.jackson.databind.JsonNode;

import controllers.lib.Role.SecuredAction;
import controllers.lib.Role.SecuredController;
import controllers.lib.SlideUpdater;

public class Slide extends SecuredController {

    public static Result getAll() {
        List<models.Slide> slides = models.Slide.find.all();
        return ok(Json.toJson(slides));
    }

    public static Result get(Integer id) {
        models.Slide slide = models.Slide.find.byId(id);
        if (slide != null) {
            return ok(Json.toJson(slide));
        }
        return badRequest("This slide doesn't exists.");
    }

    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.Slide> slideForm = Form.form(models.Slide.class);
        //JSON-data an formular das model binden
        slideForm = slideForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (slideForm.hasGlobalErrors() || slideForm.hasErrors()) {
        	System.out.println(slideForm.errorsAsJson());
            return badRequest(slideForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.Slide slide = slideForm.get();
            try {
                //Prüfen ob slide_id gesetzt ist und somit eine Slide aktualisert werden soll
                if (slide.slide_id != null) {
                    //Slide in der Datenbank suchen
                    models.Slide testSlide = models.Slide.find.byId(slide.slide_id);
                    //Prüfen ob Slide vorhanden und Owner mit dem akutellen Admin übereinstimmt
                    if (testSlide != null) {
                        //Versuchen das model zu aktualiseren
                    	System.out.println(slide.deathDate);
                        slide.update();
                        SlideUpdater.slide(slide);
                    } else {
                        return status(403, "This slide doesn't exists.");
                    }
                } else {
                    slide.owner = SecuredAction.admin;
                    //Versuchen das model zu speichern
                    slide.save();
                    SlideUpdater.slide(slide);
                }
                return ok(slide.slide_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    public static Result delete(Integer slideId) {
        if (slideId != null) {
            models.Slide slide = models.Slide.find.byId(slideId);
            if (slide != null) {
                try {
                    //Versuchen das model zu löschen
                    slide.delete();
                    return ok(slide.slide_id.toString());
                } catch (Exception e) {
                    return internalServerError(e.getMessage());
                }
            }
            return status(403, "This Slide or this Slide doesn't exists.");
        }
        return badRequest();
    }

    public static Result getNew(){
        models.Slide slide = new models.Slide();
        slide.name = "neu";
        slide.preview = "";
        slide.active = false;
        slide.status = SlideStatus.DYNAMIC;
        slide.content = "{}";
        slide.cronexpression = "* * * * * ? *"; //Default quartz-scheduler cron expression
        slide.duration = 10;
        slide.url = "http://data.pg-infoscreen.de";
        slide.updateTime = 1;
        slide.updateTimeUnit = TimeUnit.HOURS;
        return ok(Json.toJson(slide));
    }

}
