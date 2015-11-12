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

import org.joda.time.DateTime;

import java.util.List;

import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;

import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User extends Model {

    @Id
    //@Constraints.Min(10)
    public Integer user_id;

    public String firstname;

    public String name;

    public String email;

    @JsonIgnore
    public String password;

    public DateTime creationtime;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<Slide> slides;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<Session> session;

    public Boolean enabled = true;

    @Transient
    public Session lastSession;

    @Transient
    public Access lastAction;

    public DateTime lastPasswordRequest;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_role")
    public List<Role> roles;

    public static Finder<Integer,User> find = new Finder<Integer,User>(
            Integer.class, User.class
    );

    public Integer authenticate() {
        User user = find.where().eq("email", email).findUnique();
        if(user != null && BCrypt.checkpw(this.password, user.password)){
            return user.user_id;
        }else{
            return null;
        }
    }

}