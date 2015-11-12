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

// Import java classes
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

// Import play classes
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ContentUpdate extends Model{

    @Id
    public Integer contentupdate_id;

    public DateTime timestamp;

    @Constraints.Required
    public String uri;

    @Column(columnDefinition="text")
    public String exception;

    @ManyToOne
    @JsonIgnore
    public Slide slide;

    public static Model.Finder<Integer,ContentUpdate> find = new Model.Finder<Integer,ContentUpdate>(
            Integer.class, ContentUpdate.class
    );

}
