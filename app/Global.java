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
// Import java classes
import java.util.Iterator;
import java.util.List;

import models.NewsTicker;
// Import play models
import models.Slide;
import models.Websocket;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;

// Import play classes
import play.cache.Cache;
import play.filters.gzip.GzipFilter;

// Import plugins
import com.kenshoo.play.metrics.JavaMetricsFilter;

// Import play controllers
import controllers.lib.SlideUpdater;
import controllers.lib.Watchdog;
import controllers.lib.Database;
import controllers.lib.NewsTickerUpdater;

public class Global extends GlobalSettings {

    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class, JavaMetricsFilter.class};
    }  
    
    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");
        
        // Default Channel anlegen (immer ID1 fuer Defaults!), wenn Channel Tabelle leer.
        if (models.Channel.find.findRowCount() == 0) {
            models.Channel defaultChannel = new models.Channel();
            defaultChannel.name = "Default-Channel";
            defaultChannel.owner = null;   
            defaultChannel.save();
        }
        
        //Websockets leeren
        List<Websocket> websockets = Websocket.find.all();
        Iterator<Websocket> websocketsiterator = websockets.iterator();
        while(websocketsiterator.hasNext()){
            Websocket websocket = websocketsiterator.next();
            websocket.delete();
        }
        
        // Akka-Scheduler für das Löschen veralteter Slides
        SlideUpdater.slideDeathCheckInit();
        
        //Akka-Scheduler für alle Slides anlegen
        List<Slide> slides = Slide.find.all();
        Iterator<Slide> slidesiterator = slides.iterator();
        while(slidesiterator.hasNext()){
            Slide slide = slidesiterator.next();
            SlideUpdater.slide(slide);
        }
        
        //Akka-Scheduler für alle Newsticker anlegen
        List<NewsTicker> newstickers = NewsTicker.find.all();
        Iterator<NewsTicker> newstickersiterator = newstickers.iterator();
        while(newstickersiterator.hasNext()){
            NewsTicker newsticker = newstickersiterator.next();
            NewsTickerUpdater.newsTicker(newsticker);
        }

        //Templatecache zurücksetzen
        slides = Slide.find.all();
        slidesiterator = slides.iterator();
        while(slidesiterator.hasNext()){
            Slide slide = slidesiterator.next();
            Cache.remove("slide" + slide.slide_id);
            slide.modified = true;
            slide.update();
        }
        
        //Start database trigger
        Database.runDatabaseUtils();
        
        //Start watchdog at least
        Watchdog.runWatchdog();
    }

    @Override
    public void onStop(Application app) {
        //this.cancellable.cancel();
        Logger.info("Application shutdown");
        
        //Akka System sauber beenden
        //Akka.system().shutdown();
    }

}