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

import java.util.Date;
import java.util.LinkedList;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.joda.time.DateTime;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.joda.time.DateTime;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.Where;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Client extends Model {

    @Id
    public Integer client_id;

    @Constraints.Required
    public String token;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String location;

    @Constraints.Required
    public String street;

    @Constraints.Required
    public String housenumber;

    @Constraints.Required
    public String zipcode;

    @Constraints.Required
    public String city;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    public List<RAHistoryEntry> raHistory = new LinkedList<RAHistoryEntry>();

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    public List<RAEntry> ressourceAllocation = new LinkedList<RAEntry>();
    
    @ManyToOne
    public Channel channel;

    public DateTime registertime;

    @OneToMany(cascade = CascadeType.ALL)
    public List<ClientCheckin> clientcheckin;
    
    public static Finder<Integer, Client> find = new Finder<Integer, Client>(
            Integer.class, Client.class
    );

}
