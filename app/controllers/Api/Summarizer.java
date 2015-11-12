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
package controllers.Api;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import controllers.lib.FreemarkerWrapper;

public class Summarizer extends Controller
{

    public static Result requestSumy()
    {
        JsonNode json = request().body().asJson();
        if (json == null)
        {
            return badRequest("Expecting Json data in POST request body");
        }
        else
        {
            String text = json.findPath("text").textValue();
            if (text == null)
            {
                return badRequest("Missing json field [text]");
            }
            else
            {                
                /*
                # arguments of sumyapi.py call
                # 1: chosen summarizer [edmundson|lex_rank|lsa|luhn|random|text_rank], default lsa
                # 2: language, default [german]
                # 3: target summary length in sentences, default [3]
                # 4: text to summarize
                */

                String summarizer = json.findPath("summarizer").textValue();

                String language = json.findPath("language").textValue();

                String targetlength = json.findPath("targetlength").textValue();

                FreemarkerWrapper fmw = new FreemarkerWrapper();

                //System.out.println("Summarizer.java received text: ["+text+"]");
                String result = fmw.sumy(summarizer,language,targetlength,text);
                //System.out.println("Summarizer.java is sending result: ["+result+"]");
                return ok(result);
            }
        }
    }

}
