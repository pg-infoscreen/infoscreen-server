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
import java.util.List;

// Import play models
import models.User;
// Import play classes
import play.libs.Json;
import play.mvc.Result;
// Import play controllers
import controllers.lib.Role.SecuredController;


public class Api extends SecuredController {

    public static Result users() {
        List<User> users = User.find.all();
        return ok(Json.toJson(users));
    }

    public static Result user(Integer id) {
        User user = User.find.byId(id);
        return ok(Json.toJson(user));
    }

}