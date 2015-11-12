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
/**
 * 
 */
package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Constraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * @author Kai Sauerwald <kai.sauerwald@uni-dortmund.de>
 *
 */
@Entity
public class RAHistoryEntry extends Model
{
	/**
	 * 
	 */
	public RAHistoryEntry()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public RAHistoryEntry(Slide s, Client c)
	{
		this.slide = s;
		this.client = c;
	}
	
	@Id
	public Long id;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public Date time;

	@ManyToOne
	@Constraints.Required
	public Slide slide;

	@ManyToOne
	@Constraints.Required
	public Client client;
}
