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
package controllers.lib.Role;

// Import java classes

import models.Access;
// Import play models
import models.User;

import org.apache.commons.codec.binary.Base64;

import org.joda.time.DateTime;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
// import play classes
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SecuredAction extends Action.Simple {

    public static final String AuthTokenHeader = "X-XSRF-TOKEN";
    public static final String AuthTokenCookieKey = "XSRF-TOKEN";
    //public String AuthTokenUrlKey = "auth";
    public static User admin;

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        //Prüfen ob Aufruf per AngularJS mit Authtoken getätigt wurde
        if(ctx.request().hasHeader(SecuredAction.AuthTokenHeader)){
            String token = ctx.request().getHeader(SecuredAction.AuthTokenHeader);
            //Logger.info("Hier" + token);
            if (!token.equals("")) {
                models.Session session = models.Session.find.where().eq("session_id", token).findUnique();
                //Prüfen ob session vorhanden und User aktiviert und Session nicht gekicked
                if (session != null && session.owner.enabled && !session.kicked) {
                    //aktiven Admin abspeichern
                    SecuredAction.admin = session.owner;
                    SecuredAction.logAccess(ctx.request(),session);
                    return delegate.call(ctx);
                }
            }
        //Prüfen ob Aufruf über REST-Api mit HTTP-Authorization erfolgte
        }else if(ctx.request().hasHeader("Authorization")) {
            //Authorization-Header holen und "Basic " abschneiden
            String auth2 = ctx.request().getHeader("Authorization").replace("Basic ", "");
            //Verbleibender Teil mit Base64 dekodieren
            byte[] decoded = Base64.decodeBase64(auth2);
            //String in username und password unterteilen
            String[] parts = new String(decoded, "UTF-8").split(":");
            User user = new User();
            if (parts.length == 2) {
                user.email = parts[0];
                user.password = parts[1];
                //User versuchen zu authentifizieren
                Integer id = user.authenticate();
                if (id != null) {
                    user = User.find.byId(id);
                    if(user.enabled){
                        //aktiven Admin abspeichern
                        SecuredAction.admin = user;
                        //SecuredAction.logAccess(ctx.request());
                        return delegate.call(ctx);
                    }
                }
            } else {
                //Header mit Authorization-Anforderung anhängen
                //TODO klappt noch nicht
                ctx.response().setHeader("WWW-Authenticate", "Basic realm=\"RealmName\"");
            }
        }
        Result result = unauthorized("unauthorized");
        return F.Promise.pure(result);
    }

    public static void logAccess(Http.Request request, models.Session session) {

        if (!request.uri().startsWith("/statistic")){
            Access access = new Access();
            access.uri = request.uri();
            access.timestamp = new DateTime();
            access.session = session;
            access.method = request.method();
            try {
                JsonNode js = request.body().asJson();
                if (js != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    access.body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(js);
                }
            } catch (JsonProcessingException e) {
                access.body = "Error: Processing has been failed.";
            }
            access.save();
        }
    }

}
