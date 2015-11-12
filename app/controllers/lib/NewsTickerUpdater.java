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
package controllers.lib;

// Import java classes
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// Import play models
import models.NewsTicker;
import play.libs.Akka;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
// Import scala classes
import scala.concurrent.duration.Duration;
// Import play classes
import akka.actor.Cancellable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class NewsTickerUpdater {

    public static HashMap<Integer, Cancellable> akkas = new HashMap<Integer, Cancellable>();

    public static void newsTicker(final NewsTicker newsticker){
        if(NewsTickerUpdater.akkas.containsKey(newsticker.newsticker_id)){
            //Logger.info("Removing akka for Newsticker "+newsticker.newsticker_id.toString());
            NewsTickerUpdater.akkas.remove(newsticker.newsticker_id).cancel();
        }

        //Logger.info("" + i.toString());
        Cancellable newakka = Akka.system().scheduler().schedule(
                Duration.create(1, TimeUnit.SECONDS),
                Duration.create(newsticker.updateTime, newsticker.updateTimeUnit),
                new Runnable() {
                    @Override
                    public void run() {
                        //Logger.info("Update von "+slide.slide_id);
                        if(newsticker.dynamic) {
                            //Logger.info("Update von Newsticker "+newsticker.newsticker_id);
                            NewsTickerUpdater.updateNewsTicker(newsticker.newsticker_id);
                        }
                    }
                },
                Akka.system().dispatcher()
        );
        //Logger.info("Adding akka for "+slide.slide_id.toString());
        NewsTickerUpdater.akkas.put(newsticker.newsticker_id,newakka);

    }

    private static void updateNewsTicker(int newsticker_id){
        NewsTicker newsticker = NewsTicker.find.byId(newsticker_id);
        if(newsticker != null){
            try{
                if(newsticker.url.length() > 0) {
                    String newContent = getJsonFromUrl(newsticker.url);
                    //Mapper für PrettyPrinting erstellen
                    ObjectMapper mapper = new ObjectMapper();
                    //PrettyPrinting aktivieren
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    //Json-Objekt aus Json-String erstellen
                    Object json = mapper.readValue(newContent, Object.class);
                    //Mit Stringwriter das Json-Objekt als String repräsentieren
                    StringWriter sw = new StringWriter();
                    mapper.writeValue(sw, json);
                    //Den pretty Json-String abspeichern
                    newsticker.content = sw.toString();
                    newsticker.update();
                }
            }catch(Exception e){
                /*e.printStackTrace();
                StringWriter swe = new StringWriter();
                PrintWriter pwe = new PrintWriter(swe);
                e.printStackTrace(pwe);
                ContentUpdate contentUpdate = new ContentUpdate();
                contentUpdate.slide = slide;
                contentUpdate.timestamp = new Date();
                contentUpdate.uri = slide.url;
                contentUpdate.exception = e.getMessage();
                contentUpdate.save();*/
                // Vorerst deaktiviert lassen
                //newsticker.active = false;
                //newsticker.update();
            }
        }
    }

    public static String getJsonFromUrl(final String url) {
        // Get Json Data
        F.Promise<JsonNode> jsonPromise = WS.url(url).get().map(
                new F.Function<WSResponse, JsonNode>() {
                    public JsonNode apply(WSResponse response) {
                        return response.asJson();
                    }
                }
        );
        // Convert jsonPromise to String - timeout 5000ms
        return jsonPromise.get(5000).toString();
    }

}
