# IJ-Plugin_JACoP
Just Another Colocalisation Plugin

##Authors
Fabrice P. Cordelières, Bordeaux Imaging Center (France). Fabrice.Cordelieres at gmail dot com
Susanne Bolte, IFR 83, Paris (France). Susanne.Bolte at upmc.fr

#Features
JACoP allows:
_**Calculating a set of commonly used co-localization indicators:**_
* Pearson's coefficient
* Overlap coefficient
* k1 & k2 coefficients
* Manders' coefficient

_**Generating commonly used visualizations:**_
* Cytofluorogram

_**Having access to more recently published methods:**_
* Costes' automatic threshold
* Li's ICA
* Costes' randomization
* Objects based methods (2 methods: distances between centres and centre-particle coincidence)

**NB:** All methods are implemented to work on 3D datasets.

## Description
JACoP has been totaly re-written, based on user feedback. The interface has been re-designed to oﬀer full access to all the options, based on a unique Swing frame.
![JACoP v2.0: The new interface](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/blob/master/Docs/jacop_interface.jpg)
It includes a “Zoom/Reset button” which allows the user to set the two selected images side-by-side, automatically adapting the zoom. For each method selected, the user’s attention is drawn on options to set, by highlighting the appropriate tab by turning its caption to red.

## References/Citation
When using the current plugin for publication, please refer to our review [S. Bolte & F. P. Cordelières, A guided tour into subcellular colocalization analysis in light microscopy, Journal of Microscopy, Volume 224, Issue 3: 213-232.](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/blob/master/Docs/Bolte%2C%20Cordelie%CC%80res%20-%202006%20-%20A%20guided%20tour%20into%20subcellular%20colocalization%20analysis%20in%20light%20microscopy.pdf), to this webpage and of course to ImageJ. A copy of your paper being sent to both of our e-mail adresses would also be greatly appreciated !

JACoP v2.0 was released for the second ImageJ User and Developer Conference in November 2009. The conference proceedings related the plugin is [available here](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/blob/master/Docs/jacop_ijconf2008.pdf).

## Download
Navigate to the [latest release](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/latest), and download JACoP_.jar from the associated assets section.

## Installation
Copy the JACoP_.jar file to the Plugins folder of ImageJ, restart ImageJ and use the “JACoP” command from the Plugins menu.
Alternatively, drag and drop the plugin files to ImageJ's toolbar: a save dialog box should appear, pointing at your plugins folder: save it there and restart ImageJ.

## License
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.

## Changelog

### [JACoP v2.1.4 (21/02/03)](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/tag/v2.1.4)
* Corrected a bug preventing the GUI to be displayed when images were already opened

### [JACoP v2.1.3 (20/10/12)](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/tag/v2.1.3)
* Corrected a bug occuring when a RGB image is first opened, then splitted into its channels: the image list is now updated correctly, without displaying an error message
* Replaced the URL for the paper by reference to the DOI both in the link and the direct display on the GUI

### [JACoP v2.1.2 (20/03/27)](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/tag/v2.1.2)
* Modified the GUI: imported from NetBeans to Eclipse (made it compatible with WindowsBuilder).
* Modified the warning message to explicitly state the type of expected images.
* Changed the citation as J. of Microscopy changed our review from freely available to view-for-fees...
* Mavenized the project and changed the minimum required IJ version.

### [JACoP v2.1.1 (20/08/2010)](https://github.com/fabricecordelieres/IJ-Plugin_JACoP/releases/tag/v2.1.1)
* Fixed a bug about distance based co-localization when calling the function from a macro.

### JACoP v2.1.0 (01/04/2010)
Fixed a bug leading to an error in the Manders' coefficients calculation when applying a threshold to images.

### JACoP v2.0.0 (07/11/2008)

* New interface: one window presenting all options
* JACoP is now fully macro recordable
* Added the objects based method
* Added the “Zoom/Reset button” allowing to set the two selected images side-by-side, automatically adapting the zoom.
