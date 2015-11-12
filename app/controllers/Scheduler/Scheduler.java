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
package controllers.Scheduler;

// Import java classes
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import models.Channel;
import models.ChannelSchedulingType;
import models.Client;
import models.RAEntry;
import models.Slide;
import models.SlideScheduleOptions;
// Import play models
import models.Websocket;
import play.cache.Cache;
import play.db.ebean.Model;
import play.libs.Json;






// Import play classes
import com.fasterxml.jackson.databind.node.ObjectNode;






import controllers.Scheduler.implementation.RAAlgorithm;
import controllers.Scheduler.implementation.RAProcess;
// Import play controllers
import controllers.lib.ErrorHandler;

// Import play views

public class Scheduler
{

	protected static String buildSlide(Websocket websocket, models.Slide s)
	{
		String htmlTemplate = "";
		// Protokoll und Host für Webpfad laden
		String protocol = "http://";
		String host = websocket.host;
		Locale locale = new Locale(websocket.locale);
		try {
			htmlTemplate = controllers.lib.SlideBuilder.build(s, protocol, host, locale);
		} catch (IOException e) {
			ErrorHandler.putScreenError(s, s.template, e.getMessage());
		} catch (IllegalArgumentException e) {
			ErrorHandler.putScreenError(s, s.template, e.getMessage());
		} catch (freemarker.template.TemplateException e) {
			ErrorHandler.putScreenError(s, s.template, e.getMessage());
		}

		// Cache
		Cache.set("slide" + s.slide_id, htmlTemplate, 86400);

		// Set modified value to false
		s.modified = false;
		s.updateSuper();

		return htmlTemplate;
	}

	protected static String getContent(Websocket websocket, models.Slide s)
	{
		// Get html template from cache
		String htmlTemplate = (String) Cache.get("slide" + s.slide_id);

		// Caching check
		Boolean check_build = true;
		try {
			check_build = htmlTemplate == null || s.modified;
		} catch (NullPointerException e) {
			ErrorHandler.putScreenError(s, s.template, e.getMessage());
		}

		// If Cache old, build Slide new
		if (check_build) {
			htmlTemplate = buildSlide(websocket, s);
		}

		return htmlTemplate;
	}

	protected static String getPreview(models.Slide s)
	{
		String slidePreview;
		// Attach slide preview
		if (s.preview != null && s.preview.length() > 0) {
			slidePreview = s.preview;
		} else {
			slidePreview = s.name;
		}

		return slidePreview;
	}

	protected static Slide scheduleRandom(Websocket websocket, List<Slide> slides)
	{
		if(slides.isEmpty())
			return null;
		
		Random generator = new Random();
		int rnd = generator.nextInt(slides.size());

		return slides.get(rnd);
	}

	protected static Slide scheduleLinear(Websocket websocket, List<Slide> slides, Client client)
	{		
		final List<RAEntry> raList;
		final Model model;

		if (client != null && client.ressourceAllocation != null) {
			raList = client.ressourceAllocation;
			model = client;
		} else if (websocket.ressourceAllocation != null) {
			raList = websocket.ressourceAllocation;
			model = websocket;
		} else if (client == null) {
			raList = websocket.ressourceAllocation = new LinkedList<RAEntry>();
			model = websocket;
		} else {
			raList = client.ressourceAllocation = new LinkedList<RAEntry>();
			client.update();
			model = client;
		}
	
		
		// Füge einfach die Lineare Ordnung ein...
		slides.stream()
		.map(s -> new RAEntry(s))
		.sequential()
		.forEachOrdered(ra -> { raList.add(ra);});
		
		model.update();
		
		return null;
	}

	protected static Slide scheduleDynamic(Websocket websocket, List<Slide> slides, Client client)
	{
		final List<RAEntry> raList;
		final Model model;
		final Channel channel;

		if (client != null && client.ressourceAllocation != null) {
			raList = client.ressourceAllocation;
			model = client;
		} else if (websocket.ressourceAllocation != null) {
			raList = websocket.ressourceAllocation;
			model = websocket;
		} else if (client == null) {
			raList = websocket.ressourceAllocation = new LinkedList<RAEntry>();
			model = websocket;
		} else {
			client.ressourceAllocation = new LinkedList<RAEntry>();
			client.update();
			raList = client.ressourceAllocation;
			model = client;
		}
		

		if(client != null && client.channel != null)
			channel = client.channel;
		else if(websocket.channel != null)
			channel = websocket.channel;
		else 
			return scheduleRandom(websocket, slides);
		
		// TODO: Compute from Cron-Expression how often the should be shown.
		// 		 Check Time constraints....
		List<RAProcess> processes = slides.stream().map( slide -> {
			int minH = 0;
			int maxH = Integer.MAX_VALUE;
			if (channel != null && channel.slideOptions != null) {
				Optional<SlideScheduleOptions>  osso =channel.slideOptions.stream().filter(sso -> sso.slide == slide).findFirst();
				if(osso.isPresent()){
					if(osso.get().minH != null)
						minH = osso.get().minH;
					if(osso.get().maxH != null)
						maxH = osso.get().maxH;
				}
			}
			return new RAProcess(slide, minH, maxH, slide.duration);
		}).collect(Collectors.toList());
		
		System.out.println("Prozesse ("+ processes.size()+"):" +  processes);
		
	    List<RAProcess> schedule =	RAAlgorithm.computeSchedule(processes, null, 3600, 60);
	    for (RAProcess rap : schedule) {
			raList.add(new RAEntry(rap.slide));
		}
		model.update();

		return null;
	}

	/**
	 * Versucht preschedulded Slides zu finden. Wenn es diese gibt, dann werden
	 * die zurück geben.
	 * 
	 * @param websocket
	 * @return
	 */
	protected static Slide getFromPreScheduled(Websocket websocket)
	{
		Client client = websocket.client;
		Slide slide = null;
		
		
		List<RAEntry> raList = null;
		Model model = null;
		
		if(client != null && client.ressourceAllocation != null && !client.ressourceAllocation.isEmpty()){
			raList = client.ressourceAllocation;
			model = client;
		}
		else if(websocket.ressourceAllocation != null && !websocket.ressourceAllocation.isEmpty()){
			raList = websocket.ressourceAllocation;
			model = websocket;
		}
		

		if (raList != null) {
			System.out.println("Model: " + model);
			System.out.println("List with size: " + raList.size());
			// Hole solange Slides aus der Schedule Liste, bis eine
			// Funktioniert
			while (true) {
				try {
					RAEntry ra = raList.get(0);
					System.out.println("RaEntry:" + ra.slide);
					models.Slide s = ra.slide;

					slide = s;

					break;
				} finally {
					raList.remove(0);
					model.update();
				}
			}
		}

		return slide;
	}

	/**
	 * Decodiert den String in dem die Ordnung der Slides gespeichert wurde.
	 * @param slides
	 * @param slideOrder
	 * @return
	 */
	protected static List<Slide> decodeOrder(List<Slide> slides, String slideOrder)
	{
		if(slideOrder == null || slideOrder.trim().isEmpty())
			return new LinkedList<Slide>();
		String[] split = slideOrder.trim().split(" ");
		ArrayList<Slide> res = new ArrayList<Slide>(split.length);

		for (int i = 0; i < split.length; i++) {
			final int j = Integer.parseInt(split[i]);
			java.util.Optional<Slide> opt = slides.parallelStream().filter(s -> s.slide_id == j).findFirst();
			res.add(opt.get());
		}

		return res;
	}

	protected static List<Slide> getSlidesFromChannel(Channel channel)
	{
		List<Slide> decoded = decodeOrder(channel.slides, channel.slideOrder);
		
		
		//Falls es keine Vernüftige Ordnung auf den Slides gibt
		if(channel.slides.size() > decoded.size())
		return channel.slides.stream()
		.filter(s -> s.active) // Nur Slides die Global aktiviert sind
//		.filter(s -> s.activeInChannel.contains(channel)) // Nur Slides die auch in diesem Channel Aktive sind
		.collect(Collectors.toList());
				
		return decoded.stream()
		.filter(s -> s.active) // Nur Slides die Global aktiviert sind
//		.filter(s -> s.activeInChannel.contains(channel)) // Nur Slides die auch in diesem Channel Aktive sind
		.collect(Collectors.toList());
	}

	// Developer Method
	public static ObjectNode schedule(Websocket websocket)
	{
		if(websocket.channel == null)
			websocket.channel = models.Channel.find.byId(1);
		
		if(websocket.client != null && websocket.client.channel != null && websocket.client.channel != websocket.channel)
			websocket.channel = websocket.client.channel;
		
		// Variables
		Slide slide = null;
		List<models.Slide> slides;
		ObjectNode result = Json.newObject();
		ChannelSchedulingType scheduleType = ChannelSchedulingType.RANDOM;

		// Hole eine Menge von Slides & setze den passenden SchedulingType
		if (websocket.client != null && websocket.client.channel != null) {
			slides = getSlidesFromChannel(websocket.client.channel);
			scheduleType = websocket.client.channel.scheduleType;
		}
		else if (websocket.channel != null) {
			slides = getSlidesFromChannel(websocket.channel);
			scheduleType = websocket.channel.scheduleType;
		} 
		else
			slides = models.Slide.find.where().eq("active", 1).findList();
		
		// Keine Slide vorhanden, na dann gibt es auch nix...
		if (slides.isEmpty())
			return result;

		// Falls bereits ein Schedulde berechnet wurde, versuchen wir diese
		// Slides zu nehmen
		slide = getFromPreScheduled(websocket);
		

		// Baue scheduling, je nach Scheduling Type
		if (slide == null) {
			if (scheduleType == ChannelSchedulingType.RANDOM)
				slide = scheduleRandom(websocket, slides);
			else if (scheduleType == ChannelSchedulingType.LINEAR)
				slide = scheduleLinear(websocket, slides, websocket.client);
			else if (scheduleType == ChannelSchedulingType.DYNAMIC)
				slide = scheduleDynamic(websocket, slides, websocket.client);
		}
		// Keine Slide... schlecht...
		if (slide == null){
			System.out.println("Keine Slide...");
			return result;}

		String htmlTemplate = "", slidePreview = "";
		Integer duration = 0, slideID = 0;

		htmlTemplate = getContent(websocket, slide);
		slidePreview = getPreview(slide);
		duration = slide.duration;
		slideID = slide.slide_id;

		// Check here Slide sanity
		if (htmlTemplate == null || htmlTemplate.isEmpty()){
			System.out.println("Slide nicht okay...");
			return result;
		}

		String channelInfo = websocket.channel != null ? String.format(", Channel %d - %s", websocket.channel.channel_id,websocket.channel.name) : "";
		String clientInfo = websocket.client != null ? String.format(", Client %d - %s ", websocket.client.client_id,websocket.client.name) : "";
		
		System.out.println(String.format("Liefere Slide (id=%d) \'%s\' an Websocket %s%s %s aus, über %s-scheduling",
				slide.slide_id, slide.name, websocket.guid, channelInfo, clientInfo,scheduleType));

		// Deliver build template
		result.put("slideID", slideID);
		result.put("content", htmlTemplate);
		result.put("duration", duration);
		result.put("preview", slidePreview);
		return result;
	}
}