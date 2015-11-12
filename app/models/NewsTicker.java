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
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;
// Import play classes

@Entity
public class NewsTicker extends Model{

    @Id
    public Integer newsticker_id;
    
    @Constraints.Required
    public String name;
    
    public String url;
    
    @Constraints.Required
    public Boolean active;
    
    @Constraints.Required
    public Boolean dynamic;
    
    @Column(columnDefinition="text")
    public String content;
    
    public Integer updateTime;

    @Enumerated(EnumType.STRING)
    public TimeUnit updateTimeUnit;

    @ManyToOne
    public User owner;

    public String validate() {
        if( updateTimeUnit != TimeUnit.HOURS &&
            updateTimeUnit != TimeUnit.MINUTES &&
            updateTimeUnit != TimeUnit.DAYS){
            return "invalid updateTimeUnit";
        }
        return null;
    }

    public static Model.Finder<Integer,NewsTicker> find = new Model.Finder<Integer,NewsTicker>(
            Integer.class, NewsTicker.class
    );
}