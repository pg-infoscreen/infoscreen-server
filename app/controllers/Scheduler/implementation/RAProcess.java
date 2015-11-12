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
package controllers.Scheduler.implementation;

import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import play.libs.Time.CronExpression;
import models.Slide;

/**
 * @author Kai Sauerwald
 *
 */
public class RAProcess
{
	/**
	 * Name des Prozess
	 */
	public String name = "";
	/**
	 * Name des Prozess
	 */
	public Slide slide = null;
	/**
	 * Minimale Anzahl an Anzeigen
	 */
	public int minH = 0;
	/**
	 * Maximale Anzahl an Anzeigen
	 */
	public int maxH = Integer.MAX_VALUE;

	public int maxHCounter = Integer.MAX_VALUE;
	/**
	 * Anzeige dauer (in Sec?)
	 */
	public int time = 1;
	/**
	 * Wann die Slide das letzte mal Angezeigt wurde.
	 */
	public int lastTime = 0;

	/**
	 * @param name 
	 * 		Name der Slide
	 * @param minH
	 * 		Minimale Anzahl, die die Slide pro Zeit ang
	 * @param j
	 * 		Maximale Anzahl, wie oft die Slide angezeigt wird
	 * @param k
	 * 		Anzeigedauer der Slide
	 */
	public RAProcess(Slide slide, int minH, int maxH, int time)
	{
		this.maxH = maxH;
		this.maxHCounter = maxH;
		this.minH = minH;
		this.time = time;
		this.name = slide.name;
		this.slide = slide;
	}
	
	/**
	 * @param name 
	 * 		Name der Slide
	 * @param minH
	 * 		Minimale Anzahl, die die Slide pro Zeit ang
	 * @param j
	 * 		Maximale Anzahl, wie oft die Slide angezeigt wird
	 * @param k
	 * 		Anzeigedauer der Slide
	 */
	public RAProcess(String name, int minH, int maxH, int time)
	{
		this.maxH = maxH;
		this.maxHCounter = maxH;
		this.minH = minH;
		this.time = time;
		this.name = name;
	}

	/**
	 * 
	 */
	public RAProcess()
	{
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return name + "(" + minH + "," + maxH + "," + time + ")";
	}

	public int getOptimalDist(int q, int t)
	{
		if (minH > 0)
			return q / minH;
		else
			return q - t;
	}

	public static List<RAProcess> mapSlideToProcess(List<Slide> slides)
	{
		Date now = new Date();
		for (Slide s : slides) {
			RAProcess p = new RAProcess();
			p.slide = s;
			CronExpression cron;
			try {
				cron = new CronExpression(s.cronexpression);
				Duration dur = Duration.ofNanos(cron.getNextInterval(now));
				System.out.println(dur);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			p.time = s.duration;
			p.name = s.name;
		}
		
		return null;
	}

	public static class RAProcessCompare implements Comparator<RAProcess>
	{

		public int t = 0;
		public int q = 0;

		/**
		 * @param q
		 */
		public RAProcessCompare(int q)
		{
			this.q = q;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(RAProcess o1, RAProcess o2)
		{
			return Integer.compare(Math.abs(o1.lastTime + o1.getOptimalDist(q, t) - t), Math.abs(o2.lastTime + o2.getOptimalDist(q, t) - t));
		}
	}
}
