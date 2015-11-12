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
package controllers.lib;

// Import java classes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Import play models
import models.Slide;
import models.SlideStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import play.cache.Cache;

// Import play views
import views.html.client.template;

// Freemarker java classes
import freemarker.template.Configuration;
import freemarker.template.Template;

// Import external lib
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
    
public class SlideBuilder {
    
    public static String freemarkerParser(String html, Slide slide, String url, Locale locale) throws IOException, IllegalArgumentException, freemarker.template.TemplateException {


        //Datenmodell erzeugen
        Map<String, Object> data = new HashMap<>();
        //Daten aus der slide an das Datenmodell übergeben
        data.put("name", slide.name);
        data.put("duration", slide.duration);
        String freemarker = "";
        //Überprüfen ob die slide nicht vom Typ EDITOR ist
        if(slide.status != SlideStatus.EDITOR){
            //Return while content empty
            if(slide.content.equals("{}") || slide.content.equals("[]"))
                return (html);
            data.put("content", slide.content);
            data.put("contentstring", StringEscapeUtils.escapeEcmaScript(slide.content));
            //Prüfen ob FreemarkerBibliothek noch im Cache ist
            if(Cache.get("freemarker") != null){
                freemarker = (String) Cache.get("freemarker");
            }else{
                //Freemarkerfile laden und Inhalt auslesen
                File freemarkerFile = new File("app/freemarker.ftl");
                if(freemarkerFile.exists()) {

                    FileReader fr = new FileReader(freemarkerFile);
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    freemarker = "";
                    //Prüfen ob Dateityp ein image ist.
                    while ((line = br.readLine()) != null) {
                        freemarker += line;
                    }
                    Cache.set("freemarker",freemarker);
                    fr.close();
                    br.close();
                }
            }
        }else{
            //Daten aus der editorContent-Spalte an das Datenmodell übergeben
            data.put("editorContent", slide.editorContent);
        }
        data.put("url", url+"/");
        data.put("library", url+"/library/");
        FreemarkerWrapper fmw = new FreemarkerWrapper();
        data.put("fmw", fmw);
        //Stringwriter für das Übergeben des geparsten Inhalts erzeugen
        StringWriter sw = new StringWriter();
        //Template um die Evaluierung des JSON-String erweitern
        html = "<#setting locale='"+ locale.getLanguage() +"'>" + freemarker + html;
        //Freemarker configuration object
        Configuration cfg = new Configuration(Configuration.getVersion());
        //Template mit Hilfe der Configuration erzeugen
        Template template = new Template("name", new StringReader(html), cfg);
        //Template mit dem Datenmodell parsen
        template.process(data, sw);
        //Fertigen templateString zurückgeben.
        return sw.toString();
    }
    
    public static String build (Slide slideData, String protocol, String host, Locale locale) throws IOException, IllegalArgumentException, freemarker.template.TemplateException {
    
        // Variables
        String htmlCode;

        //Überprüfen ob die slide nicht vom Typ EDITOR ist
        if(slideData.status != SlideStatus.EDITOR){
            // Get template data
            models.Template templateData = slideData.template;
            // Adding css, javascript, html to htmlCode
            htmlCode = template.render(templateData.script, templateData.css, templateData.html).toString();
        }else{

            htmlCode = template.render("", "", "${editorContent}").toString();
        }

        // Merging htmlCode with json content
        String jsonHtmlCode = freemarkerParser(htmlCode, slideData, protocol + host, locale);
        
        // return compress HTML Code for Perfomance
        HtmlCompressor compressor = new HtmlCompressor();

        compressor.setEnabled(true);                   //if false all compression is off 
        compressor.setRemoveComments(true);            //if false keeps HTML comments 
        compressor.setRemoveMultiSpaces(true);         //if false keeps multiple whitespaces
        compressor.setRemoveIntertagSpaces(true);      //removes iter-tag whitespace characters
        compressor.setRemoveQuotes(false);             //removes unnecessary tag attribute quotes
        compressor.setSimpleDoctype(true);             //simplify existing doctype
        compressor.setRemoveScriptAttributes(true);    //remove optional attributes from script tags
        compressor.setRemoveStyleAttributes(true);     //remove optional attributes from style tags
        compressor.setRemoveLinkAttributes(true);      //remove optional attributes from link tags
        compressor.setRemoveFormAttributes(true);      //remove optional attributes from form tags
        compressor.setRemoveInputAttributes(true);     //remove optional attributes from input tags
        compressor.setSimpleBooleanAttributes(true);   //remove values from boolean tag attributes
        compressor.setRemoveJavaScriptProtocol(true);  //remove "javascript:" from inline events
        compressor.setRemoveHttpProtocol(false);       //replace "http://" with "//" inside tags
        compressor.setRemoveHttpsProtocol(false);      //replace "https://" with "//" inside tags 
        compressor.setPreserveLineBreaks(true);        //preserves original line breaks
        //compressor.setRemoveSurroundingSpaces("br,p"); //remove spaces around provided tags
        compressor.setCompressCss(true);               //compress inline css 
        compressor.setCompressJavaScript(true);        //compress inline javascript
        
        // TODO ErrorHandling verfeinern
        try{
            return compressor.compress(jsonHtmlCode);
        } catch(Exception e) {
            return jsonHtmlCode;
        }
    }
}