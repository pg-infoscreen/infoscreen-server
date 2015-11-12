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
package controllers.Scheduler;

// Import java classes
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import play.libs.Json;

// Import play classes
import com.fasterxml.jackson.databind.JsonNode;

public class NewsTicker {
        
    public static JsonNode schedule() {
        //TODO Freemarker einsetzen spaeter
        List<models.NewsTicker> newstickers = models.NewsTicker.find.all();
        Iterator<models.NewsTicker> newstickersiterator = newstickers.iterator();
        
        /*if(session("newstickerIndex1") == null) {
            session("newstickerIndex1", new Integer(0).toString());
        }*/
        
        while(newstickersiterator.hasNext()){
            models.NewsTicker newsticker = newstickersiterator.next();
            if(newsticker.active) {
                //TODO Exaktes Errorhandling implementieren
                try {
                    JsonNode news = Json.parse(newsticker.content);
                    if(news.size() > 0) {
                        // Index tooling
                        /*Integer newstickerIndex = Integer.parseInt(session("newstickerIndex1"));
                        Integer newstickerIndex = 1;
                        if(newstickerIndex >= news.size())
                            newstickerIndex = 0;
                        session("newstickerIndex1", new Integer((newstickerIndex + 1) % news.size()).toString());*/
                        // News Auswahl zufaellig, solange Sessions nicht funktionieren
                        Random generator = new Random();
                        int newstickerIndex = generator.nextInt(news.size() - 1);
                        // Headline zurueckgeben
                        return(news.get(newstickerIndex));
                    } else {
                        //System.out.println("No news in newsticker.");
                    }
                } catch(Exception e) {
                    //System.out.println("Newsticker error." + e.getMessage());
                }
            }
        }
        //System.out.println("No newsticker exists.");
        return(Json.parse("{}"));
    }
}