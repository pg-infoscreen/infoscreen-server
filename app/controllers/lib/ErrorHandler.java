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
import java.util.Date;

// Import play models
import models.SlideError;
import models.Slide;
import models.Template;
import org.joda.time.DateTime;
// import play classes

public class ErrorHandler {
    
    public static void putScreenError(Slide slide, Template template, String exception) {
        SlideError slideError = new SlideError();
        slideError.timestamp = new DateTime();
        slideError.slide = slide;
        slideError.template = template;
        slideError.error = exception;
        slideError.save();
        // Deactivate slide
        slide.active = false;
        slide.update();
    }

    /*public static Result putClientError(){
        ClientError clientError = new ClientError();
        clientError.timestamp = new Date();
        clientError.ip = request().remoteAddress();
        clientError.exception = request().body().asText();
        clientError.save();
        return ok();
    }*/
}