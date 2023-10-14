/*  JACoP: "Just Another Colocalization Plugin..." v2.1.2, 27/03/20
    Fabrice P Cordelieres, fabrice.cordelieres@gmail.com
    Susanne Bolte, susanne.bolte@upmc.fr 
 
    Copyright (C) 2006-2020 Susanne Bolte & Fabrice P. Cordelieres
  
    License:
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 *
 *
*/

import ij.IJ;
import ij.Macro;
import ij.plugin.PlugIn;

public class JACoP_ implements PlugIn{

	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.52p")) return;
		
		GUI frame = new GUI();
		
        if (Macro.getOptions()==null){
        	frame.setVisible(true);;
        }else{
            frame.doZeJob(false);
            frame.dispose();
        }
		
	}

}
