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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Template extends Model {

    @Id
    public Integer template_id;

    @Constraints.Required
    public String name;

    @Column(columnDefinition="text")
    public String script;

    @Column(columnDefinition="text")
    public String css;

    @Column(columnDefinition="text")
    @Constraints.Required
    public String html;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<Slide> slide;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    public List<SlideError> slideErrors;
    
    @Column(columnDefinition="mediumtext")
    public String templateDescription;	

    public static Finder<Integer,Template> find = new Finder<Integer,Template>(
            Integer.class, Template.class
    );

    @Override
    public void update() {
        controllers.lib.SlideTools.modifiedSlidesForTemplate(this.template_id);
        super.update();
    }
}