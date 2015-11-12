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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import play.Play;

import models.File;
import models.Websocket;

public class FileTools {
    
    public static ArrayList<String> getJSfiles(final String host) {
        // Select all autoload availble JS Files
        List<File> files = File.find.where().like("type", "%javascript").findList();
        
        Iterator<File> filesiterator = files.iterator();
        ArrayList<String> paths = new ArrayList<String>();
        String protocol = "http://";
        
        while(filesiterator.hasNext()){
            File file = filesiterator.next();
            paths.add("<script src=\"" + protocol + host + "/library/" + file.name + "." + file.extension + "\" type=\"text/javascript\"></script>");
        }
        
        return paths;
    } 
}