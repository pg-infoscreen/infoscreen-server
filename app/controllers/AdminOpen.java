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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.DateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// Import play models
import controllers.lib.StringUtils;
import models.Session;
import models.User;
import models.EmailSent;
import org.mindrot.jbcrypt.BCrypt;
import play.Play;
import play.cache.Cache;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
// Import play views
import views.html.admin.index;
import views.html.email.password;

// Import play classes
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Import play controllers
import controllers.lib.Role.SecuredAction;
import controllers.lib.SlideTools;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

import javax.persistence.OptimisticLockException;

public class AdminOpen extends Controller {

    public static Result index() {
        // Use Session Check for admin too
        SlideTools.checkScreenSessions(request().remoteAddress());
        return ok(index.render());
    }

    public static Result login() {
        //HTTP-request aufrufen und den body als JSON auslesen
        JsonNode json = request().body().asJson();
        //abstraktes HTML-formular aus dem model erstellen lassen
        Form<User> userForm = Form.form(User.class);
        //JSON-data an formular das model binden
        userForm = userForm.bind(json);
        //Prüfen ob beim Binden Fehler aufgetreten sind
        if(userForm.hasGlobalErrors() || userForm.hasErrors()) {
            return badRequest(userForm.errorsAsJson());
        }else{
            //Model mit Hilfe des formulars generieren
            User user = userForm.get();
            //Versuchen den user zu authentifizieren
            Integer id = user.authenticate();
            if (id == null) {
                return unauthorized();
            } else {
                //Zufallstoken für die session erstellen
                String token = UUID.randomUUID().toString();
                //User in der Datenbank finden
                user = User.find.byId(id);
                //Zufallstoken übegeben und eine neue Session abspeichern
                Session session = new Session();
                session.session_id = token;
                session.ip = request().remoteAddress();
                session.browser = request().getHeader("User-Agent");
                session.timestamp = new DateTime();
                session.owner = user;
                session.save();
                session("auth",token);
                //JSON-response bauen
                ObjectNode result = Json.newObject();
                result.put(SecuredAction.AuthTokenCookieKey, token);
                result.put("user_id", id);
                //Einen cookie mit dem generierten Zufallstoken setzten, der Bezeichner (SecuredAction.AuthTokenCookieKey) muss
                //genau dem String "XSRF-TOKEN" entsprechen. Diesen erwarten standardmäßig AngualarJs um eine session zu erzeugen.
                //AngularJS sendet anschließend genau diesen Token bei jedem request mit
                response().setCookie(SecuredAction.AuthTokenCookieKey, token);
                return ok(result);
            }
        }
    }

    public static Result requestPassword(String emailAddress){
        //Benutzer anhand der Emailadresse finden
        models.User user = models.User.find.where().eq("email", emailAddress).findUnique();
        //Date-Objekte zur Berechnung erzeugen
        DateTime now = new DateTime();
        DateTime nowMinus5Minutes = new DateTime();
        //5 Minuten in Millisekunden von "jetzt" abziehen
        nowMinus5Minutes = nowMinus5Minutes.minusMinutes(5);
        //Pürfen ob Benutzer wirklich existiert und ob noch kein PasswordRequest angefordert wurde oder der PasswordRequest länger als 5 Minuten her ist
        if(user != null && (user.lastPasswordRequest == null || user.lastPasswordRequest.isBefore(nowMinus5Minutes))) {
            //Altes Passwort sichern
            String oldPassword = user.password;
            //Passwortlänge aus der config lesen
            Integer passwordLength = new Integer(Play.application().configuration().getString("defaultPasswordLength"));
            //Neues Passwort generieren lassen
            String newPassword = StringUtils.generateRandomPassword(passwordLength);
            //Neues Password hashen
            user.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.lastPasswordRequest = now;
            //Benutzer aktualisieren
            user.update();
            try {
                //Email erzeugen
                Email email = new Email();
                //Betreff festlegen
                email.setSubject("Ihr neues Passwort.");
                //Absenderdaten aus der config lesen
                String senderName = Play.application().configuration().getString("email.from.name");
                String senderEmailAddress = Play.application().configuration().getString("email.from.emailaddress");
                //Absender festlegen
                email.setFrom(senderName + " <" + senderEmailAddress + ">");
                //Empfänger festlegen
                email.addTo(user.firstname + " " + user.name + " <" + user.email + ">");
                //HTML-Body aus Template generieren lassen
                email.setBodyHtml(password.render(newPassword, user).toString());
                //Email versenden
                String messageId = MailerPlugin.send(email);


                //Neues emailSent-Modell erzeugen
                EmailSent emailSent = new EmailSent();
                //Absendedatum setzen
                emailSent.timestamp = new DateTime();
                //MessageId setzen
                emailSent.messageid = messageId;
                //Betreff festlegen
                emailSent.subject = email.getSubject();
                //Absender festlegen
                emailSent.sender = email.getFrom();
                //Empfänger festlegen
                emailSent.receiver = email.getTo().get(0);
                //HTML-Body mit leerem Passwort aus Template generieren lassen
                emailSent.body = password.render("*********", user).toString();
                emailSent.save();
            }catch(OptimisticLockException e){
                //Do nothing when emailSent could not be saved.
            }catch(Exception e){
                //Altes Passwort zurücksetzen, wenn der Versand der Email nicht funktionierte
                user.password = oldPassword;
                //Benutzer aktualisieren
                user.update();
            }
        }
        //Immer einen Statuscode 200 zurückgeben
        return ok();
    }

    public static Result js() {
        //Im cache nach controllern suchen
        String c = (String) Cache.get("controllers");
        if(Play.application().configuration().getString("infoscreen.mode").equals("live") && c != null) {
            return ok(c);
        }else{
            String angularPath = "public/angular/";
            //Statische angular-controller laden
            File config = new File(angularPath+"config.js");
            File modul = new File(angularPath+"modul.js");
            File routes = new File(angularPath+"routes.js");
            File services = new File(angularPath+"services.js");
            File translation = new File(angularPath+"translation.js");
            //Alle weiteren angular-controller laden
            File controllerDirectory = new File(angularPath+"controller/");
            File[] controllerFiles = controllerDirectory.listFiles();
            List<File> files = new ArrayList<>(Arrays.asList(controllerFiles));
            //Alle weiteren angular-filter laden
            File filtersDirectory = new File(angularPath+"filters/");
            File[] filtersFiles = filtersDirectory.listFiles();
            List<File> filters = new ArrayList<>(Arrays.asList(filtersFiles));
            //Mergen der Controller
            files.addAll(filters);
            //Statische angular-controller in die Liste einsortieren
            files.add(0, services);
            files.add(0, modul);
            files.add(0, translation);
            files.add(0, config);
            files.add(files.size(), routes);
            Iterator<File> iterator = files.iterator();
            //String zur Ausgabe und Zwischenspeicherung
            String javaScriptLine;
            String controller = "";
            //Schleife über alle angular-controller
            while(iterator.hasNext()) {
                try {
                    //Reader zum Einlesen der einzelnen angular-controller/directives erzeugen
                    FileReader fr = new FileReader(iterator.next());
                    BufferedReader br = new BufferedReader(fr);
                    //Schleife über gesamten Dateiinhlat
                    while ((javaScriptLine = br.readLine()) != null) {
                        controller += (javaScriptLine + "\r\n");
                    }
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    return internalServerError();
                }
            }
            //Cache setzen
            Cache.set("controllers", controller, 60 * 15);
            return ok(controller);
        }
    }

}
