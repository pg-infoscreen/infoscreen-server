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

// Import play classes
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
// Import play classes
import play.db.ebean.Model;

@Entity
public class StatisticDay extends Model {

    @Id
    public Integer statisticday_id;
    
    public Long views;
    
    public Long duration;
    


    public DateTime updateTime;

    @ManyToOne
    public Channel channel;

    @ManyToOne
    public Slide slide;
    
    @ManyToOne
    public Client client;

    public static Finder<Integer,StatisticDay> find = new Finder<Integer,StatisticDay>(
            Integer.class, StatisticDay.class
    );
    
    @Override
    public void save() {
        updateTime = new DateTime();
        super.save();
    }
}