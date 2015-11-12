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

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.ContentUpdate;
// Import play models
import models.Slide;
import models.SlideStatus;

import org.joda.time.DateTime;

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

public class SlideUpdater {

    public static HashMap<Integer, Cancellable> akkas = new HashMap<>();
    
    /**
     * Fügt einen Schdeule bei Akka ein, der jede Minute Prüft ob slides gelöscht werden sollen
     * @author Kai Sauerwald
     */
    public static void slideDeathCheckInit(){
    	Akka.system().scheduler().schedule(
                Duration.create(1, TimeUnit.SECONDS),
                Duration.create(1, TimeUnit.MINUTES),
                new Runnable() {
                    @Override
                    public void run() {
                    	Date now = new Date();
                    	
                    	List<Slide> slides = Slide.find.all();
                        Iterator<Slide> slidesiterator = slides.iterator();
                        while(slidesiterator.hasNext()){
                            Slide slide = slidesiterator.next();
                            if(slide.deathDate == null)
                            	continue;                            
                            if(slide.deathDate.getTime() != 0 && slide.deathDate.after(now))
                            {
                            	slide.delete();
                            }
                        }
                    }
                },
                Akka.system().dispatcher()
        );
    }

    public static void slide(final Slide slide){
        if(SlideUpdater.akkas.containsKey(slide.slide_id)){
            //Logger.info("Removing akka for Slide "+slide.slide_id.toString());
            SlideUpdater.akkas.remove(slide.slide_id).cancel();
        }

        //Logger.info("" + i.toString());
        Cancellable newakka = Akka.system().scheduler().schedule(
                Duration.create(1, TimeUnit.SECONDS),
                Duration.create(slide.updateTime, slide.updateTimeUnit),
                new Runnable() {
                    @Override
                    public void run() {
                        //Logger.info("Update von "+slide.slide_id);
                        if(slide.status == SlideStatus.DYNAMIC) {
                            //Logger.info("Update von Slide "+slide.slide_id);
                            SlideUpdater.updateSlide(slide.slide_id);
                        }
                    }
                },
                Akka.system().dispatcher()
        );
        //Logger.info("Adding akka for "+slide.slide_id.toString());
        SlideUpdater.akkas.put(slide.slide_id,newakka);

    }

    private static void updateSlide(int slide_id){
        Slide slide = Slide.find.byId(slide_id);
        if(slide != null){
            try{
                String newContent = getJsonFromUrl(slide.url);
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
                slide.content = sw.toString();
                slide.update();
            }catch(Exception e){
                //e.printStackTrace();
                /*StringWriter swe = new StringWriter();
                PrintWriter pwe = new PrintWriter(swe);
                e.printStackTrace(pwe);*/
                ContentUpdate contentUpdate = new ContentUpdate();
                contentUpdate.slide = slide;
                contentUpdate.timestamp = new DateTime();
                contentUpdate.uri = slide.url;
                contentUpdate.exception = e.getMessage();
                contentUpdate.save();
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
