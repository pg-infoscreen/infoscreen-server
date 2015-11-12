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
package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.joda.time.DateTime;

import java.util.List;

import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Role extends Model {

    @Id
    @GeneratedValue
    public Integer role_id;

    public String name;
    
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Channel> channels;

    public static Finder<Integer,Role> find = new Finder<>(
            Integer.class, Role.class
    );

}