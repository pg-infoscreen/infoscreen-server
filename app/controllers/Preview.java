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
package controllers;

// Import java classes
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

// Import play models
import models.Slide;
import play.mvc.Controller;
import play.mvc.Result;
// Import play views
import views.html.client.index;
import controllers.Exceptions.NoSlidesAvailableException;
// Import play classes
// Import play controllers
import models.Websocket;

public class Preview extends Controller {

    public static Result preview(Integer id) {
        // Variables
        String htmlTemplate = "";
        String jsscripts = "";

        try{
            // Select data from slides model
            Slide slide = Slide.find.byId(id);

            // Exception NoSlidesStoredException
            if(slide == null)
                throw new NoSlidesAvailableException();

            //Protokoll und Host für Webpfad laden
            String protocol = "http://";
            String host = request().host();

            // Merging htmlCode with json content
            try{
                htmlTemplate = controllers.lib.SlideBuilder.build(slide, protocol, host, lang().toLocale());
                
                // Add JS Libs from Library
                ArrayList<String> filePaths = controllers.lib.FileTools.getJSfiles(request().host());
                Iterator<String> filePathsIterator = filePaths.iterator();
                while(filePathsIterator.hasNext()){
                    jsscripts += filePathsIterator.next();
                }

            } catch(IOException e) {
                //ErrorHandler.putScreenError(slide, slide.template, e.getMessage());
                htmlTemplate = e.getMessage();

            } catch(IllegalArgumentException e) {
                //ErrorHandler.putScreenError(slide, slide.template, e.getMessage());
                htmlTemplate = e.getMessage();

            } catch(freemarker.template.TemplateException e) {
                //ErrorHandler.putScreenError(slide, slide.template, e.getMessage());
                htmlTemplate = e.getMessage();
            }

        } catch(NoSlidesAvailableException e) {
            htmlTemplate = e.getMessage();
        }
        
        //return ok(index.render(htmlTemplate, false));
        return ok(index.render(htmlTemplate, jsscripts, false));
    }
}