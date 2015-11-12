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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name="slide_schedule_options")
public class SlideScheduleOptions extends Model
{
	/**
	 * 
	 */
	public SlideScheduleOptions()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public SlideScheduleOptions(Slide s)
	{
		this.slide = s;
//		this.client = c;
	}
	
	@Id
	public Long id;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public Date time;

	@ManyToOne
	@Constraints.Required
	public Slide slide;

	@Column(name="minH")
	public Integer  minH;

	@Column(name="maxH")
	public Integer  maxH;

	@Column(name="beginTime")
	public Integer  timeBegin;
	
	@Column(name="endTime")
	public Integer  timeEnd;

	
//	@ManyToOne
//	@Constraints.Required
//	public Client client;

}
