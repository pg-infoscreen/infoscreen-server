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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author Kai Sauerwald
 *
 */
public class RAAlgorithm
{

	/**
	 * @param slides
	 *            Eine Liste von Slides über die gescheduled werden soll
	 * @param q
	 *            length of time slot to be scheduled
	 * @param r
	 *            relax parameter
	 */
	public static ArrayList<RAProcess> computeSchedule(List<RAProcess> slideList, List<RAProcess> initialSlides, int q, int r)
	{
		Random rnd = new Random(java.time.Instant.now().toEpochMilli());

		ArrayList<RAProcess> schedule = new ArrayList<RAProcess>(q + r);
		List<RAProcess> slides = new ArrayList<RAProcess>(slideList);

		// history
		if (initialSlides == null || initialSlides.size() == 0) {
			initialSlides = new ArrayList<RAProcess>(slides);
			Collections.shuffle(initialSlides, rnd);
		}

//		System.out.println("Init Vektor: ");
//		for (RAProcess s : initialSlides) {
//			System.out.print(s.name);
//		}
//		System.out.println("");

		// setup lasttime field from history
		for (int i = initialSlides.size() - 1, lastTime = 0; i >= 0; i--) {
			lastTime -= initialSlides.get(i).time;
			initialSlides.get(i).lastTime = lastTime;
		}

		// Initialize Comparator
		RAProcess.RAProcessCompare cmp = new RAProcess.RAProcessCompare(q);
		PriorityQueue<RAProcess> minHeap = new PriorityQueue<RAProcess>(cmp);

		for (int t = 0; t < q;) {
			Collections.shuffle(slides, rnd); // Bissel Zufall
			minHeap.clear();
			cmp.t = t;
			minHeap.addAll(slides);
			RAProcess s = minHeap.poll();
			// Verhindere Gleiche Slides hintereinander.
			while ((t != 0 && s == schedule.get(t - 1)) || s.maxHCounter == 0) {
				s = minHeap.poll();
			}
			s.lastTime = t;
			for (int i = 0; i < s.time; i++) {
				schedule.add(s);
			}

			if (s.maxH != Integer.MAX_VALUE)
				s.maxHCounter -= 1;

			t += s.time;
		}
		
		System.out.println("Computed a schedule:");
		int i = 0;
		for (RAProcess rap : minHeap) {
			System.out.println("schedule[" + i+ "]= " + rap.slide.slide_id + " [" + rap.slide.name + "]");
			i++;
		}

		return schedule;
	}
}
