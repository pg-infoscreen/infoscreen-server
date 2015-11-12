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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import play.libs.Json;
import play.mvc.Result;

// Import play classes
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.lib.Role.SecuredController;

public class Log extends SecuredController {

    public static Result get() {
        try {
            //Log-Dateie einesen
            FileReader fr = new FileReader("logs/application.log");
            BufferedReader br = new BufferedReader(fr);
            String log = "";
            //ArrayListe für die Zeilenobjekte erzeugen
            ArrayList<ObjectNode> a = new ArrayList<ObjectNode>();
            int i = 1;
            //Schliefe über die gesamte Datei
            while ((log = br.readLine()) != null) {
                //Nur nicht leeere Zeilen einlesen
                if (!log.equals("")) {
                    ObjectNode line = Json.newObject();
                    line.put("id", i);
                    line.put("line", log);
                    a.add(line);
                    i++;
                }
            }
            br.close();
            fr.close();
            return ok(Json.toJson(a));
        } catch (FileNotFoundException e) {
            return internalServerError();
        } catch (IOException e) {
            return internalServerError();
        }
    }

    public static Result getContetUpdateForSlide(Integer id) {
        models.Slide slide = models.Slide.find.byId(id);
        if (slide != null) {
            return ok(Json.toJson(slide.contentUpdate));
        }
        return badRequest("This slide doesn't exists.");
    }

}
