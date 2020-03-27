
import ij.ImageJ;
import java.io.File;

/*
 *  Test.java
 * 
 *  Created on 1 avr. 2010, 10:43:02
 * 
 *  Copyright (C) 2010 Fabrice P. Cordelieres
 * 
 *  License:
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * 

 */

/**
 *
 * @author fab
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.getProperties().setProperty("plugins.dir", System.getProperty("user.dir")+File.separator+"bin"+File.separator);
        ImageJ ij=new ImageJ();
        ij.exitWhenQuitting(true);
    }

}
