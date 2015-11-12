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
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by tim on 14.04.15.
 */
public class FreemarkerWrapper {

    public int add(int a, int b) {
        return a + b;
    }

    public int mul(int a, int b) {
        return a * b;
    }

    public String reduceText(String text, int wantedLength) {
        return sumy(null, "german", String.valueOf(wantedLength), text);
    }

    public String sumy(String summarizer, String language, String targetlength, String text) {
    /*
                # arguments of sumyapi.py call
                # 1: chosen summarizer [edmundson|lex_rank|lsa|luhn|random|text_rank], default lsa
                # 2: language, default [german]
                # 3: target summary length in sentences, default [3]
                # 4: text to summarize
                */

        if (summarizer == null) {
            summarizer = "lsa";
        }
        // erlaubte werte: edmundson, lex_rank, lsa, luhn, random, text_rank


        if (language == null) {
            language = "english";
        }


        if (targetlength == null) {
            targetlength = "3";
        }

        StringBuilder sb = new StringBuilder();
        int results = 0;
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "./public/python_script/sumyapi.py", summarizer, language, targetlength, text);
            //System.out.println("FreemarkerWrapper.java passes text to python: ["+text+"]");

            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8")); // python returns utf8 bytestreams
            BufferedReader errin = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));

            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
                sb.append(" ");
                results++;
            }

            if (results == 0) {
                while ((s = errin.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sb.append(e);
            sb.append(" - ");
        }
        if (results == 0) {
            sb.append("Python error, reduced text returned empty. Input was: [");
            sb.append(text);
            sb.append("]");
        }
        String resultString = sb.toString();
        //System.out.println("FreemarkerWrapper.java received text from python: ["+resultString+"]");
        return resultString;
    }
}
