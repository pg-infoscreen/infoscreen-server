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

// Import play classes
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ScreenError extends Model{

    @Id
    public Integer screenerror_id;

    public DateTime timestamp;

    @Constraints.Required
    public String ip;
    
    @ManyToOne
    public Slide slide;

    @Column(columnDefinition="text")
    public String error;

    public static Model.Finder<Integer,ScreenError> find = new Model.Finder<Integer,ScreenError>(
            Integer.class, ScreenError.class
    );

}
