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
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Time;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

@EntityConcurrencyMode(ConcurrencyMode.NONE)
@Entity
public class Slide extends Model {
    
    @Id
    public Integer slide_id;

    @Constraints.Required
    public String name;

    public String preview;

    @Constraints.Required
    public String cronexpression;

    @Constraints.Required
    public Integer duration;

    public String url;

    public Integer updateTime;

    @Enumerated(EnumType.STRING)
    public TimeUnit updateTimeUnit;

    @Column(columnDefinition="mediumtext")
    @Constraints.Required
    public String content;

    @Column(columnDefinition="mediumtext")
    public String editorContent;

    @Constraints.Required
    public Boolean active;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="active_in_channel")
    public List<Channel> activeInChannel;
    
    @Constraints.Required
    public SlideStatus status;

    public Boolean modified;

    @ManyToOne
    public Template template;

    @ManyToOne
    @JsonIgnore
    public User owner;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<ContentUpdate> contentUpdate;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<SlideError> slideErrors;

	@Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    public Date deathDate = new Date(0);

    public String validate() {
        //validate cronexpression
        if (!Time.CronExpression.isValidExpression(cronexpression)) {
            return "invalid cronexpression";
        }
        //validate content
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(content);
        }catch(Exception e){
            return "invalid content";
        }

        if( updateTimeUnit != TimeUnit.HOURS &&
            updateTimeUnit != TimeUnit.MINUTES &&
            updateTimeUnit != TimeUnit.DAYS){
            return "invalid updateTimeUnit";
        }
        return null;
    }

    public static Finder<Integer,Slide> find = new Finder<Integer,Slide>(
            Integer.class, Slide.class
    );

    @Override
    public void save() {
        this.modified = true;
        super.save();
    }
    
    public void saveSuper() {
        super.save();
    }

    @Override
    public void update() {
        this.modified = true;
        super.update();
    }
    
    public void updateSuper() {
        super.update();
    }
}