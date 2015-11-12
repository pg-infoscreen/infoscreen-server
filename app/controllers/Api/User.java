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
package controllers.Api;

// Import java classes
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import controllers.lib.Role.Roles;
import controllers.lib.Role.SecuredController;
import models.Access;
import models.Session;

import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ArrayNode;

// Import play classes
import controllers.lib.Role.SecuredAction;

public class User extends SecuredController {

    public static Result kick(Integer id){
        if(id != SecuredAction.admin.user_id){
            models.Session session = models.Session.find.where().eq("owner_user_id", id).orderBy("timestamp DESC").setMaxRows(1).findUnique();
            if (session != null) {
                session.kicked = true;
                session.update();
                return ok();
            }
            return badRequest("A session for this user doesn't exists.");
        }
        return badRequest("You can't kick yourself.");
    }


    public static Result getAll() {
        List<models.User> users = models.User.find.all();
        Iterator<models.User> slidesIterator = users.iterator();
        models.User user;
        while(slidesIterator.hasNext()){
            user = slidesIterator.next();
            Session session = Session.find.where().eq("owner_user_id", user.user_id).orderBy("timestamp DESC").setMaxRows(1).findUnique();
            user.lastSession = session;
        }
        return ok(Json.toJson(users));
    }

    public static Result get(Integer id) {
        models.User user = models.User.find.byId(id);
        if (user != null) {
            Session session = Session.find.where().eq("owner_user_id", user.user_id).orderBy("timestamp DESC").setMaxRows(1).findUnique();
            Access access = Access.find.where().eq("session.owner.user_id", user.user_id).orderBy("access_id DESC").setMaxRows(1).findUnique();
            user.lastSession = session;
            user.lastAction = access;
            return ok(Json.toJson(user));
        }
        return badRequest("This user doesn't exists.");
    }

    @Roles({"SuperAdmin"})
    public static Result getAccess(Integer id) {
        List<Access> accesses = Access.find.where().eq("session.owner.user_id", id).eq("method", "GET").orderBy("access_id DESC").findList();
        return ok(Json.toJson(accesses));
    }

    @Roles({"SuperAdmin"})
    public static Result getEdit(Integer id) {
        List<Access> accesses = Access.find.where().eq("session.owner.user_id", id).ne("method", "GET").orderBy("access_id DESC").findList();
        return ok(Json.toJson(accesses));
    }

    @Roles({"SuperAdmin"})
    public static Result getSession(Integer id) {
        List<Session> sessions = Session.find.where().eq("owner.user_id", id).orderBy("timestamp DESC").findList();
        return ok(Json.toJson(sessions));
    }

    @Roles({"SuperAdmin"})
    public static Result put() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        
		// Bug von Ebean indem die Rollen nicht geupdatet werden, sondern
		// versucht komplett neu zu speichern... CLASH!
		// TODO: After upgrading EBEAN fix this code
		LinkedList<Integer> role_list = new LinkedList<Integer>();
		ArrayNode roles = (ArrayNode) json.get("roles");
		roles.forEach(r -> {
			role_list.add(r.get("role_id").asInt());
		});
        
        
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<models.User> userForm = Form.form(models.User.class);
        //JSON-data an formular das model binden
        userForm = userForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if (userForm.hasGlobalErrors() || userForm.hasErrors()) {
            return badRequest(userForm.errorsAsJson());
        } else {
            //Model mit Hilfe des formulars generieren
            models.User user = userForm.get();
            try {
                //Prüfen ob user_id gesetzt ist und somit ein User aktualisert werden soll
                if (user.user_id != null) {
                    //User in der Datenbank suchen
                    models.User testUser = models.User.find.byId(user.user_id);
                    //Prüfen ob User vorhanden
                    if (testUser != null) {
                        //Prüfen ob User gleich dem aktuellen User ist
                        if (user.equals(SecuredAction.admin)){
                            //Aktivierung des Benutzers vorsichtshalber zurückstellen, damit der Benutzer sich nicht selber deaktivieren kann
                            user.enabled = SecuredAction.admin.enabled;
                        }
                        // Bug von Ebean indem die Rollen nicht richtig geupdatet
						// werden, sondern versucht komplett neu zu speichern...
						// CLASH!
						// TODO: After upgrading EBEAN fix this code
                        user.roles.clear();
                        role_list.forEach(r -> {
							models.Role role = models.Role.find.byId(r);
							user.roles.add(role);
						});
                        //Versuchen das model zu aktualiseren
                        user.update();
                    } else {
                        return status(403, "This user doesn't exists.");
                    }
                } else {
                	// Bug von Ebean indem die Rollen nicht richtig geupdatet
					// werden, sondern versucht komplett neu zu speichern...
					// CLASH!
					// TODO: After upgrading EBEAN fix this code
                    user.roles.clear();
                    role_list.forEach(r -> {
						models.Role role = models.Role.find.byId(r);
						user.roles.add(role);
					});
                    //Versuchen das model zu speichern
                    user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
                    user.save();
                }
                return ok(user.user_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    @Roles({"SuperAdmin"})
    public static Result delete(Integer userId) {
        if (userId != null && userId != SecuredAction.admin.user_id) {
            models.User user = models.User.find.byId(userId);
            if (user != null) {
                try {
                    //Versuchen das model zu löschen
                    user.delete();
                    return ok(user.user_id.toString());
                } catch (Exception e) {
                    return internalServerError(e.getMessage());
                }
            }
            return status(403, "This user doesn't exists.");
        }
        return badRequest();
    }

    @Roles({"SuperAdmin"})
    public static Result getNew(){
        models.User user = new models.User();
        user.name = "";
        user.firstname = "";
        user.email = "";
        user.enabled = true;
        user.creationtime = new DateTime();
        return ok(Json.toJson(user));
    }

    @Roles({"SuperAdmin"})
    public static Result checkExists(String email){
        models.User user = models.User.find.where().eq("email", email).findUnique();
        return ok(Boolean.toString(user != null));
    }
}