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

// Import play classes
import play.mvc.Controller;
import play.mvc.Result;

public class File extends Controller {
    
    public static Result getRawFileFromLibrary(String name){
        java.io.File file2 = new java.io.File("upload/" + name);
        //Prüfen ob Datei schon vorhanden ist
        if (file2.exists()) {
            return ok(file2);
        }
        return badRequest();
    }  
}