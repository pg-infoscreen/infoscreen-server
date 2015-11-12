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

import javax.persistence.*;

import org.joda.time.DateTime;
import play.data.format.Formats;
import play.data.validation.Constraints;
// Import play classes
import play.db.ebean.Model;


@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"name", "extension"})
)
@Entity
public class File extends Model {

    @Id
    public Integer file_id;

    public DateTime creationtime;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String extension;

    public String type;

    @Transient
    public String content;

    @Transient
    public long size;

    @ManyToOne
    public User owner;

    public static Finder<Integer,File> find = new Finder<Integer,File>(
            Integer.class, File.class
    );

    public String getFullName(){
        return name + "." + extension;
    }

}