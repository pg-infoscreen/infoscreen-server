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

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

// Import play classes
import com.fasterxml.jackson.databind.JsonNode;

import controllers.lib.Role.SecuredController;


public class Template extends SecuredController {

    public static Result getAll() {
        List<models.Template> templates = models.Template.find.all();
        return ok(Json.toJson(templates));
    }

    public static Result get(Integer id) {
        models.Template template = models.Template.find.byId(id);
        if (template != null) {
            return ok(Json.toJson(template));
        }
        return badRequest("This template doesn't exists.");
    }

    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.Template> templateForm = Form.form(models.Template.class);
        //JSON-data an formular das model binden
        templateForm = templateForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (templateForm.hasGlobalErrors() || templateForm.hasErrors()) {
            return badRequest(templateForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.Template template = templateForm.get();
            try {
                //Prüfen ob template_id gesetzt ist und somit eine Template aktualisert werden soll
                if (template.template_id != null) {
                    //Template in der Datenbank suchen
                    models.Template testTemplate = models.Template.find.byId(template.template_id);
                    //Prüfen ob Template vorhanden
                    if (testTemplate != null) {
                        //Versuchen das model zu aktualiseren
                        template.update();
                    } else {
                        return status(403, "You're not the owner of this Template or this Template doesn't exists.");
                    }
                } else {
                    //Versuchen das model zu speichern
                    template.save();
                }
                return ok(template.template_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    public static Result delete(Integer templateId) {
        if (templateId != null) {
            models.Template template = models.Template.find.byId(templateId);
            if (template != null) {
                try {
                    //Versuchen das model zu löschen
                    template.delete();
                    return ok(template.template_id.toString());
                } catch (Exception e) {
                    return internalServerError();
                }
            }
            return status(403, "You're not the owner of this Template or this Template doesn't exists.");
        }
        return badRequest();
    }

    public static Result getNew(){
        models.Template template = new models.Template();
        template.name = "neu";
        template.css = "";
        template.script = "";
        template.html = "";
        return ok(Json.toJson(template));
    }
}
