# JACoP: Just Another Colocalisation Plugin

**This plug-in is a compilation of co-localization tools**

## Authors
Fabrice P. Cordelières, Bordeaux Imaging Center (France). Fabrice.Cordelieres at gmail dot com

Susanne Bolte, IFR 83, Paris (France). Susanne.Bolte at upmc.fr


## Features
JACoP allows:

* *Calculating a set of commonly used co-localization indicators:*
  * Pearson's coefficient
  * Overlap coefficient
  * k1 & k2 coefficients
  * Manders' coefficient
* *Generating commonly used visualizations:*
  * Cytofluorogram
* *Having access to more recently published methods:*
  * Costes' automatic threshold
  * Li's ICA
  * Costes' randomization
  * Objects based methods (2 methods: distances between centres and centre-particle coincidence)

*All methods are implemented to work on 3D datasets.*


## Description
JACoP has been totaly re-written, based on user feedback. The interface has been re-designed to oﬀer full access to all the options, based on a unique Swing frame.

*JACoP v2.0: The new interface*
![JACoP v2.0: The new interface](img/Jacop_interface.jpg?raw=true)
It includes a “Zoom/Reset button” which allows the user to set the two selected images side-by-side, automatically adapting the zoom. For each method selected, the user’s attention is drawn on options to set, by highlighting the appropriate tab by turning its caption to red.


## References/Citation
When using the current plugin for publication, please refer to our review **[Bolte & F. P. Cordelières, A guided tour into subcellular colocalization analysis in light microscopy, Journal of Microscopy, Volume 224, Issue 3: 213-232.)](docs/Bolte_Cordelieres_JMicrosc_2006.pdf)**, to this webpage and of course to ImageJ. A copy of your paper being sent to both of our e-mail adresses would also be greatly appreciated !

JACoP v2.0 was released for the second ImageJ User and Developer Conference in November 2008. The conference proceedings related the plugin is [available here](docs/Cordelieres_Bolte_IJ-Conf_2008.pdf).


## Installation
Simply download [JACoP_.jar](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/latest) to the Plugins folder of ImageJ, restart ImageJ and use the "JACoP" command in the Plugins menu.


## Download
Plugin available here: [JACoP_.jar](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/latest)


## License
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
Copyright (C) 2006 Susanne Bolte & Fabrice P. Cordelières


## Changelog
* __2.0 (07/11/2008):__
  * New interface: one window presenting all options
  * JACoP is now fully macro recordable
  * Added the objects based method
  * Added the "Zoom/Reset button” allowing to set the two selected images side-by-side, automatically adapting the zoom.

* __2.1 (01/04/2010):__
  * Fixed a bug leading to an error in the Manders' coefficients calculation when applying a threshold to images.

* __2.1.1 (20/08/2010):__
  * Fixed a bug about distance based co-localization when calling the function from a macro.

* __2.1.2 (27/03/2020):__
  * Modified the GUI: imported from NetBeans to Eclipse (made it compatible with WindowsBuilder).
  * Modified the warning message to explicitly state the type of expected images.
  * Changed the citation as J. of Microscopy changed our review from freely available to view-for-fees...
  * Mavenized the project and changed the minimum required IJ version.

