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
import java.text.ParseException;
import java.util.Date;
// Import java classes
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

// Import play classes
import play.libs.Time.CronExpression;
import play.mvc.Controller;

// TODO keine controller vereerbung
public class SlideTools extends Controller {
    
    public static void modifiedSlidesForTemplate(Integer templateID) {
        // Find all slides with this template
        List<models.Slide> slides = models.Slide.find.where().ilike("template", "" + templateID).findList();
        // Iterate over slides
        Iterator<models.Slide> slidesIterator = slides.iterator();
        while(slidesIterator.hasNext()){
            models.Slide slide = slidesIterator.next();
            slide.modified = true;
            slide.update();
        }
    }
    
    public static Boolean timeDateAwareness (String expression, TimeZone timeZone) throws ParseException {
        CronExpression expr = new CronExpression(expression);
        expr.setTimeZone(timeZone);
        return (expr.isSatisfiedBy(new Date()));
    }
    
    public static void checkScreenSessions(String ip) {
        if(session("timezone") == null) {
            //String ip = request().remoteAddress();
            Localization.locate(ip);
            session("timezone", Localization.getTimezone().getID());
        }else{
            Localization.setTimezoneFromId(session("timezone"));
        }
    }
}