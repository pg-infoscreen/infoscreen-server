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

/**
 * Legt fest wie die Slides in dem Channel "gescheduled" werden sollen
 * @author Kai Sauerwald
 */
public enum ChannelSchedulingType {
	/**
	 * Die Slides werden in der Reihenfolge angezeigt, wie in der Anordnung
	 */
    LINEAR,
    /**
     * Ein Approximationsalgorithmus versucht ein möglichst gutes Scheduling zu finden.
     */
    DYNAMIC,
    /**
     * Der Name ist Programm.
     */
    RANDOM
}