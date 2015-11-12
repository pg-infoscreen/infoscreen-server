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

// Import play classes
import models.Channel;
import models.Client;
// Import play models
import models.Websocket;

public class WebSocketTools {
    
    public static boolean saveInitData(Integer guid, String locale, String host, String type, String typeId) {
        // Select Websocket
        Websocket websocket = Websocket.find.where().eq("guid", guid).findUnique();
        if(websocket != null) {
            websocket.host = host;
            websocket.locale = locale;
            // Start Add Channel or Client Data
            if(type != null) {
                if(type.equals("client")) {
                    websocket.client = Client.find.where().eq("token", typeId).findUnique();
                } else if(type.equals("channel")) {
                    websocket.channel = Channel.find.byId(Integer.parseInt(typeId));
                }
            }
            // End Add Channel or Client Data
            websocket.update();
            return true;
        } else {
            return false;
        }
    }
}