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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Channel extends Model {

    @Id
    public Integer channel_id;

    @Constraints.Required
    public String name;

    @ManyToOne
    @JsonIgnore
    public User owner;

    @ManyToMany(cascade =  CascadeType.ALL)
    @JoinTable(name="channel_slide")
    @OrderColumn(name="order_index") // Funktioniert anscheinend mit EBEAN nicht.
    public List<Slide> slides;

    /**
     * Speichert die Ordnung der Slides, die im Admininterface festgelegt wurde.
     * Blöder Hack. Der Objekt-Relationale-Mapper kann weder Listen (d.h. duplikate und Ordnung bleibt erhalten) noch direkt auf Arraytypen von Datenbanken abbilden.
     * Daher kodieren wir unsere Ordnung in einen String.
     */
    @Lob
    @Column(name="slideOrder", length=2048)
    public String slideOrder = "";
    
    /**
     * Speichert die Schedulingmethode für den Channel
     */
    @Constraints.Required
    public ChannelSchedulingType scheduleType = ChannelSchedulingType.RANDOM;
    
    @ManyToMany(cascade = CascadeType.ALL)
    public List<SlideScheduleOptions> slideOptions;

    public static Finder<Integer,Channel> find = new Finder<Integer,Channel>(
            Integer.class, Channel.class
    );

}
