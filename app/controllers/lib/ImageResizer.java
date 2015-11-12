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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageResizer {

    public static BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight) throws IOException {
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage resizeWidth(BufferedImage inputImage, int scaledWidth) throws IOException {
        float scaledFactor = (float) scaledWidth / (float) inputImage.getWidth();
        int scaledHeight = (int) Math.floor(inputImage.getHeight() * scaledFactor);
        return resize(inputImage, scaledWidth, scaledHeight);
    }

    public static BufferedImage resizeHeight(BufferedImage inputImage, int scaledHeight) throws IOException {
        float scaledFactor = (float) scaledHeight / (float) inputImage.getHeight();
        int scaledWidth = (int) Math.floor(inputImage.getHeight() * scaledFactor);
        return resize(inputImage, scaledWidth, scaledHeight);
    }

}