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
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import controllers.lib.ImageResizer;
import org.joda.time.DateTime;
import play.Play;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;


// Import play classes
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.lib.Role.SecuredAction;
import controllers.lib.Role.SecuredController;

import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

public class File extends SecuredController {

    public static Result getAll() {
        //Alle Dateien aus der Datenbank laden
        List<models.File> files = models.File.find.all();
        //Iterator über alle Dateien erstellen
        Iterator<models.File> iterator = files.iterator();
        //Über alle Dateien iterieren
        while (iterator.hasNext()){
            models.File fileInDatabase = iterator.next();
            java.io.File fileOnDisk = new java.io.File(Play.application().configuration().getString("library.upload.path") + "/"  + fileInDatabase.getFullName());
            //Pürfen ob Datei nicht nur in der Datenbank sondern auch auf der Festplatte liegt
            if (fileOnDisk.exists()) {
                //Dateigröße setzen
                fileInDatabase.size = fileOnDisk.length();
            }else{
                iterator.remove();
            }
        }
        return ok(Json.toJson(files));
    }

    public static Result get(Integer id) {
        //Datei mit Hilfe der Id aus der Datenbank lesen und Prüfen ob Datei wirklich vorhanden ist
        models.File fileInDatabase = models.File.find.byId(id);
        if (fileInDatabase != null) {
            try {
                //Datei von der Festplatte laden und Prüfen ob Datei wirklich vorhanden ist
                java.io.File fileOnDisk = new java.io.File(Play.application().configuration().getString("library.upload.path") + "/"  + fileInDatabase.getFullName());
                if(fileOnDisk.exists()) {
                    FileReader fr = new FileReader(fileOnDisk);
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    String content = "";
                    //Prüfen ob Dateityp ein image ist.
                    if (!fileInDatabase.type.contains("image")) {
                        while ((line = br.readLine()) != null) {
                            content += line+"\n";
                        }
                        fileInDatabase.content = content;
                    }
                    fr.close();
                    br.close();
                    //Dateigröße setzen
                    fileInDatabase.size = fileOnDisk.length();
                    return ok(Json.toJson(fileInDatabase));
                }
            } catch (IOException e) {
                return internalServerError();
            }
        }
        return badRequest("This file doesn't exists.");
    }

    public static Result getByMime(String mime) {
        //Alle Dateien aus der Datenbank laden
        List<models.File> files = models.File.find.where().contains("type", mime).findList();
        //Iterator über alle Dateien erstellen
        Iterator<models.File> iterator = files.iterator();
        //Über alle Dateien iterieren
        while (iterator.hasNext()){
            models.File fileInDatabase = iterator.next();
            java.io.File fileOnDisk = new java.io.File(Play.application().configuration().getString("library.upload.path") + "/"  + fileInDatabase.getFullName());
            //Pürfen ob Datei nicht nur in der Datenbank sondern auch auf der Festplatte liegt
            if (fileOnDisk.exists()) {
                //Dateigröße setzen
                fileInDatabase.size = fileOnDisk.length();
            }else{
                iterator.remove();
            }
        }
        return ok(Json.toJson(files));
    }

    public static Result delete(Integer id) {
        //Datei mit Hilfe der Id aus der Datenbank lesen und Prüfen ob Datei wirklich vorhanden ist
        models.File fileInDatabase = models.File.find.byId(id);
        ArrayNode templatesContainingName = searchTemplatesForFile(fileInDatabase.name);
        if(templatesContainingName.size() == 0){
            if (fileInDatabase != null) {
                fileInDatabase.delete();
                //Datei von der Festplatte laden und Prüfen ob Datei wirklich vorhanden ist
                java.io.File fileOnDisk = new java.io.File(Play.application().configuration().getString("library.upload.path") + "/" +fileInDatabase.name);
                if (fileOnDisk.exists()) {

                        if (fileOnDisk.delete()) {
                            return ok();
                        }
                        return internalServerError();
                    }

            }
            return badRequest("This file doesn't exists.");
        }
        return badRequest(templatesContainingName);
    }

    public static Result put() {
        //Body als MultipartFormDate auslesen
        Http.MultipartFormData body = request().body().asMultipartFormData();
        //Eine Liste der Dateien erstellen
        List<Http.MultipartFormData.FilePart> list = body.getFiles();
        //Einen Iterator für die Datien erstellen
        Iterator<Http.MultipartFormData.FilePart> iterator = list.iterator();
        //Prüfen ob überhaupt Dateien geschickt wurden
        if (iterator.hasNext()) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            //Über alle Dateien iterieren
            while (iterator.hasNext()) {
                //Nächste Datei auslesen
                Http.MultipartFormData.FilePart filePart = iterator.next();
                String fileName;
                try
                {
                    fileName = URLEncoder.encode(filePart.getFilename(), "UTF8");
                }catch (UnsupportedEncodingException e1){
                    e1.printStackTrace();
                    return internalServerError();
                }
                String fileBaseName = FilenameUtils.getBaseName(fileName);
                String fileExtension = FilenameUtils.getExtension(fileName);
                String contentType = filePart.getContentType();
                java.io.File file = filePart.getFile();
                //Prüfen ob Datei schon vorhanden ist
                models.File fileInDatabase = models.File.find.where().eq("name", fileBaseName).eq("extension", fileExtension).findUnique();
                if(fileInDatabase != null){
                    //Daten aktualiseren
                    fileInDatabase.creationtime = new DateTime();
                    fileInDatabase.type = contentType;
                    fileInDatabase.owner = SecuredAction.admin;
                    fileInDatabase.update();
                    ids.add(fileInDatabase.file_id);
                }else{
                    //Daten neu schreiben
                    models.File newfileInDatabase = new models.File();
                    newfileInDatabase.type = contentType;
                    newfileInDatabase.name = fileBaseName;
                    newfileInDatabase.extension = fileExtension;
                    newfileInDatabase.owner = SecuredAction.admin;
                    newfileInDatabase.creationtime = new DateTime();
                    newfileInDatabase.save();
                    ids.add(newfileInDatabase.file_id);
                }
                try {
                    java.io.File newFile = new java.io.File(Play.application().configuration().getString("library.upload.path") + "/"  + fileName);
                    if(contentType.contains("image")) {
                        BufferedImage outputImage = ImageResizer.resizeWidth(ImageIO.read(file), 64);
                        ImageIO.write(outputImage, fileExtension, new java.io.File(Play.application().configuration().getString("library.upload.path") + "/" + fileBaseName + "_small." + fileExtension));
                    }
                    //Temporäre Datei über einen Buffer kopieren
                    InputStream is = null;
                    OutputStream os = null;
                    is = new FileInputStream(file);
                    os = new FileOutputStream(newFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    is.close();
                    os.close();
                } catch (IOException e) {
                    return internalServerError();
                }
                return ok(Json.toJson(ids));
            }
        }
        return badRequest();
    }

    private static ArrayNode searchTemplatesForFile(String name){
        name = "${library}"+name;
        List<models.Template> templates = models.Template.find.all();
        Iterator<models.Template> iterator = templates.iterator();
        models.Template template;
        ArrayNode templatesContainingName = Json.newObject().putArray("templates");
        while(iterator.hasNext()){
            template = iterator.next();
            if((template.css != null && template.css.contains(name)) ||
                (template.script != null && template.script.contains(name)) ||
                (template.html != null && template.html.contains(name))){
                ObjectNode templateJson = Json.newObject();
                templateJson.put("id", template.template_id);
                templateJson.put("name", template.name);
                templatesContainingName.add(templateJson);
            }
        }
        return templatesContainingName;
    }

}
