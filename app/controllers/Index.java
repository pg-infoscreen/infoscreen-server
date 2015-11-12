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

// Import Java classes
import java.util.ArrayList;
import java.util.Iterator;
// Import play classes
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.client.fallback;
// Import play views
import views.html.client.index;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.lib.ChannelTools;
import controllers.lib.ClientTools;
import controllers.lib.SlideTools;
import controllers.lib.WebSocketTools;

public class Index extends Controller {
    
    // Index loader
    public static Result index() {
        // Beim Reload Session aktualisieren (alte loeschen, neue erstellen)
        session().clear();
        // Sessions checken
        SlideTools.checkScreenSessions(request().remoteAddress());  
        // Load Additional JS Scripts
        String jsscripts = "";
        ArrayList<String> filePaths = controllers.lib.FileTools.getJSfiles(request().host());
        Iterator<String> filePathsIterator = filePaths.iterator();
        while(filePathsIterator.hasNext()){
            jsscripts += filePathsIterator.next();
        }
        // Return Screen       
        return ok(index.render("", jsscripts, true));
    }
    
    // Index loader -> URL with channel
    public static Result indexChannel(Integer id) {
        // Check Channel
        if(!ChannelTools.channelExists(id)) {
            return badRequest("No Channel");
        }
        // Beim Reload Session aktualisieren (alte loeschen, neue erstellen)
        session().clear();
        // Sessions checken
        SlideTools.checkScreenSessions(request().remoteAddress());    
        // Add Channel Parameter to Session
        session("type", "channel");
        session("id", id.toString()); 
        // Load Additional JS Scripts
        String jsscripts = "";
        ArrayList<String> filePaths = controllers.lib.FileTools.getJSfiles(request().host());
        Iterator<String> filePathsIterator = filePaths.iterator();
        while(filePathsIterator.hasNext()){
            jsscripts += filePathsIterator.next();
        }
        // Return Screen        
        return ok(index.render("", jsscripts, true));
    } 
    
    // Index loader -> URL with client
    public static Result indexClient(String token) {
        // Client Channel
        if(!ClientTools.clientExists(token)) {
            return badRequest("No Client");
        }
        // Beim Reload Session aktualisieren (alte loeschen, neue erstellen)
        session().clear();
        // Sessions checken
        SlideTools.checkScreenSessions(request().remoteAddress());   
        // Add Client Parameter to Session
        session("type", "client");
        session("id", token); 
        // Load Additional JS Scripts
        String jsscripts = "";
        ArrayList<String> filePaths = controllers.lib.FileTools.getJSfiles(request().host());
        Iterator<String> filePathsIterator = filePaths.iterator();
        while(filePathsIterator.hasNext()){
            jsscripts += filePathsIterator.next();
        }
        // Return Screen         
        return ok(index.render("", jsscripts, true));
    }  
    
    // Websocket Interface
    public static WebSocket<String> socket() {
        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            public Props apply(ActorRef out) throws Throwable {
                return controllers.lib.WebSocketActor.props(out);
            }
        });
    }
    
    // Header Interface for Websocket
    public static Result init(Integer guid) {
        // Parameter an Websocket: GUID, IP
        if(WebSocketTools.saveInitData(guid, lang().toLocale().getLanguage(), request().host(), session("type"), session("id"))) {
            return ok();
        } else {
            return badRequest();
        }
    }
    
    // Load Fallback Template
    public static Result fallbackTemplate() {
        return ok(fallback.render());
    }
}