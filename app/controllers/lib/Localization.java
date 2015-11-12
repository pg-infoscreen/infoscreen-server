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
import java.net.InetAddress;
import java.util.TimeZone;

import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

// Import play classes
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Localization {

    //Standard Zeitzone
    private static TimeZone timezone = TimeZone.getTimeZone("Europe/Berlin");

    public static TimeZone locate(String ip){
            String timezoneId;
            try {
                //InetAddress-Objekt mit Hilfe der ip erstellen
                InetAddress inet = InetAddress.getByName(ip);
                //Prüfen ob die InetAddresse eine lokale Adresse ist
                if (!(inet.isAnyLocalAddress() || inet.isLoopbackAddress())) {
                    //Localization-Abfrage an API
                    F.Promise<JsonNode> jsonPromise = WS.url("http://www.telize.com/geoip/" + ip).get().map(
                            new F.Function<WSResponse, JsonNode>() {
                                public JsonNode apply(WSResponse response) {
                                    return response.asJson();

                                }
                            }
                    );
                    // Convert jsonPromise to Object
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(jsonPromise.get(5000).toString());
                    timezoneId = actualObj.get("timezone").asText();
                    Localization.setTimezoneFromId(timezoneId);
                }
            } catch (Exception e) {

            }
            return Localization.getTimezone();
    }

    public static TimeZone getTimezone(){
        return Localization.timezone;
    }

    public static TimeZone setTimezoneFromId(String timezoneId){
        Localization.timezone = TimeZone.getTimeZone(timezoneId);
        return Localization.getTimezone();
    }
}