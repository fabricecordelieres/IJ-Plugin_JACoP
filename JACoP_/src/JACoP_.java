/*JACoP: "Just Another Colocalization Plugin..." v1, 13/02/06
    Fabrice P Cordelieres, fabrice.cordelieres at curie.u-psud.fr
    Susanne Bolte, Susanne.bolte@isv.cnrs-gif.fr
 
    Copyright (C) 2006 Susanne Bolte & Fabrice P. Cordelieres
  
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

import ij.*;
import ij.ImagePlus.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.process.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import _JACoP.*;

public class JACoP_ implements PlugIn, ActionListener, ChangeListener, ItemListener, ImageListener, PropertyChangeListener, WindowListener  {
    
    Vector info=new Vector();
    String[]  fitMethList={"Shrink to fit", "Pad with black pixels"};
    int nbImg=0, oldNbImg=0, prevImgA=0, prevImgB=1, thrA, thrB, xyblock, zblock, recomxy, recomz;
    double resxy, resz;
    boolean doChange=false, adaptedZoom=true;
    
    JFrame frame;
    JComboBox imgA, imgB, CostesFit;
    JCheckBox PearsonCh, OverlapCh, MMCh, CostesThrCh, CCFCh, CytoCh, ICACh, CostesRandCh, ObjCh, CostesSlicesIndpCh, CosteszRandCh, CostesShowLastCh, distCentCh, distShowCentCh, centPartCh, showCentPartCh;
    JTabbedPane tabs;
    JPanel superPanel, imgTab, zoomTab, aboutTab, aboutsubTab1,aboutsubTab2, thrTab, CCFTab, CCFsubTab1, CCFsubTab2, CCFsubTab3, CostesRandTab, CostesRand1, CostesRand2, CostesRand3, scopeTab, scope1, scope2, scope3, scope4, scope5, ObjTab, Obj1, Obj2, Obj3;
    JSlider thrImgA, thrImgB, thrSlice;
    JLabel thrImgALab, thrImgBLab, thrSliceLab, warning;
    JFormattedTextField CCFshiftTxt, CostesxyTxt, CosteszTxt, CostesRandRoundTxt, CostesBinTxt, xyCalibTxt, zCalibTxt, waveATxt, waveBTxt, naTxt, RITxt, minSizeTxt, maxSizeTxt;
    ButtonGroup scopeTypeRadio, centreTypeRadio, tableTypeRadio;
    JRadioButton wfRadio, confRadio, cMassRadio, cGeoRadio, fullRadio, colocRadio;
    JButton zoom, jmi, scopeImgA, scopeImgB, scopeSet, analyze;
    
    ImagePlus impA=new ImagePlus(), impB=new ImagePlus();
    Calibration calib=new Calibration();
    
    //Load preferences
    private static boolean PearsonBool=Prefs.get("JACoP_Pearson.boolean", true);
    private static boolean OverlapBool=Prefs.get("JACoP_Overlap.boolean", true);
    private static boolean MMBool=Prefs.get("JACoP_MM.boolean", true);
    private static boolean CostesThrBool=Prefs.get("JACoP_CostesThr.boolean", true);
    private static boolean CCFBool=Prefs.get("JACoP_CCF.boolean", true);
    private static boolean CytoBool=Prefs.get("JACoP_Cyto.boolean", true);
    private static boolean ICABool=Prefs.get("JACoP_ICA.boolean", true);
    private static boolean CostesRandBool=Prefs.get("JACoP_CostesRand.boolean", true);
    private static boolean ObjBool=Prefs.get("JACoP_Obj.boolean", true);
    
    private static int xShift=(int)Prefs.get("JACoP_CCFx.double", 20);
        
    private static boolean scopeType=Prefs.get("JACoP_scopeType.boolean", true);
    private static double xyCalib=(int)Prefs.get("JACoP_xyCalib.double", 67);
    private static double zCalib=(int)Prefs.get("JACoP_zCalib.double", 200);
    private static int waveA=(int)Prefs.get("JACoP_waveA.double", 488);
    private static int waveB=(int)Prefs.get("JACoP_waveB.double", 555);
    private static double na=Prefs.get("JACoP_na.double", 1.4);
    private static double ri=Prefs.get("JACoP_ir.double", 1.518);
    
    private static int nbRand=(int) Prefs.get("JACoP_nbRand.double", 1000);
    private static double binWidth=Prefs.get("JACoP_binWidth.double", 0.001);
    private static int fitMeth=(int) Prefs.get("JACoP_fitMeth.double", 0);
    private static boolean xyRand=Prefs.get("JACoP_xyRand.boolean", false);
    private static boolean zRand=Prefs.get("JACoP_zRand.boolean", true);
    private static boolean showRand=Prefs.get("JACoP_showRand.boolean", true);
    
    private static boolean cMass=Prefs.get("JACoP_cMass.boolean", true);
    private static boolean fullList=Prefs.get("JACoP_fullList.boolean", true);
    private static int minSize=(int) Prefs.get("JACoP_minSize.double", 0);
    private static int maxSize=(int) Prefs.get("JACoP_maxSize.double", 10000);
    private static boolean workDist=Prefs.get("JACoP_workDist.boolean", true);
    private static boolean showCent=Prefs.get("JACoP_showCent.boolean", true);
    private static boolean workCentPart=Prefs.get("JACoP_workCentPart.boolean", true);
    private static boolean showCentPart=Prefs.get("JACoP_showCentPart.boolean", true);
        
    public void run(String arg) {
        if (IJ.versionLessThan("1.39e")) return;
        
        if (Macro.getOptions()==null){
            GUI();
        }else{
            doZeJob(false);
        }
    }
    
    public void GUI(){
        resetArrays();
              
        //-----------------------------------------------------------------------------------------------------------------
        frame = new JFrame("Just Another Colocalisation Plugin");
        frame.setSize(400, 585);
        if (!IJ.isWindows())frame.setSize(430, 610);
        frame.setLocation(0, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-frame.getHeight()/2));
        //>>>>>>>>>>>>>>>>>>>>>>>>>>
        frame.setResizable(true);
        frame.setIconImage(new ImageIcon(getClass().getResource("coloc.png")).getImage()); 
              
        superPanel = new JPanel();
        
        superPanel.add(new JLabel("Images to analyse:"));
        
        imgTab = new JPanel(new GridLayout(2,2));
        imgTab.setPreferredSize(new Dimension(380, 45));
        if (!IJ.isWindows())imgTab.setPreferredSize(new Dimension(410, 45));
        imgA=new JComboBox();
        imgB=new JComboBox();
        imgTab.add(new JLabel("Image A"));
        imgTab.add(imgA);
        imgA.addActionListener(this);
        imgTab.add(new JLabel("Image B"));
        imgTab.add(imgB);
        imgB.addActionListener(this);
        
        superPanel.add(imgTab);
        
        zoomTab=new JPanel();
        zoomTab.setPreferredSize(new Dimension (380, 35));
        if (!IJ.isWindows())zoomTab.setPreferredSize(new Dimension(410, 35));
        zoom=new JButton("Reset zoom");
        zoomTab.add(zoom);
        zoom.addActionListener(this);
        superPanel.add(zoomTab);
        
        superPanel.add(new JLabel("Analysis to perform:"));
        
        JPanel analysisPanel = new JPanel(new GridLayout(5,2));
        analysisPanel.setPreferredSize(new Dimension (380, 100));
        if (!IJ.isWindows())analysisPanel.setPreferredSize(new Dimension(410, 100));
               
        PearsonCh=new JCheckBox("Pearson's coefficient", PearsonBool);
        OverlapCh=new JCheckBox("Overlap coeff., k1 & k2", OverlapBool);
        MMCh=new JCheckBox("M1 & M2 coefficients", MMBool);
        CostesThrCh=new JCheckBox("Costes' automatic threshold", CostesThrBool);
        CCFCh=new JCheckBox("Van Steensel's CCF", CCFBool);
        CytoCh=new JCheckBox("Cytofluorogram", CytoBool);
        ICACh=new JCheckBox("Li's ICA", ICABool);
        CostesRandCh=new JCheckBox("Costes' randomization", CostesRandBool);
        ObjCh=new JCheckBox("Objects based methods", ObjBool);
        
        analysisPanel.add(PearsonCh);
        PearsonCh.addItemListener(this);
        analysisPanel.add(OverlapCh);
        OverlapCh.addItemListener(this);
        analysisPanel.add(MMCh);
        MMCh.addItemListener(this);
        analysisPanel.add(CostesThrCh);
        CostesThrCh.addItemListener(this);
        analysisPanel.add(CCFCh);
        CCFCh.addItemListener(this);
        analysisPanel.add(CytoCh);
        CytoCh.addItemListener(this);
        analysisPanel.add(ICACh);
        ICACh.addItemListener(this);
        analysisPanel.add(CostesRandCh);
        CostesRandCh.addItemListener(this);
        analysisPanel.add(ObjCh);
        ObjCh.addItemListener(this);
        
        superPanel.add(analysisPanel);
        
        superPanel.add(new JLabel("Parameters:"));
        
        tabs = new JTabbedPane(SwingConstants.BOTTOM);

        
        aboutTab = new JPanel();
        aboutTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())aboutTab.setPreferredSize(new Dimension(410, 200));
        
        aboutsubTab1=new JPanel();
        aboutsubTab1.setPreferredSize(new Dimension(380, 145));
        if (!IJ.isWindows())aboutsubTab1.setPreferredSize(new Dimension(410, 145));
        JTextArea aboutTxt=new JTextArea("Please refer to and cite:\nBolte S, Cordelieres FP. A guided tour into subcellular colocalization  analysis in light microscopy. J Microsc. 2006;224:213-32.\n\n\nFreely downloadable from:\nhttp://www.blackwell-synergy.com/doi/pdf/10.1111/j.1365-2818.2006.01706.x");
        aboutTxt.setSize(380,155);
        if (!IJ.isWindows())aboutTxt.setSize(410, 155);
        aboutTxt.setLineWrap(true);
        aboutTxt.setEditable(false);
        aboutTxt.setBackground(frame.getBackground());
        aboutsubTab1.add(aboutTxt);
        aboutTab.add(aboutsubTab1);
        
        aboutsubTab2=new JPanel();
        aboutsubTab2.setPreferredSize(new Dimension(380, 45));
        if (!IJ.isWindows())aboutsubTab2.setPreferredSize(new Dimension(410, 45));
        jmi=new JButton("Click here to download the pdf from Journal of Microscopy");
        jmi.addActionListener(this);
        aboutsubTab2.add(jmi);
        aboutTab.add(aboutsubTab2);
        
        tabs.addTab("About", aboutTab);
        
        thrTab = new JPanel(new GridLayout(3,2));
        thrTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())thrTab.setPreferredSize(new Dimension(410, 200));
        thrImgA=new JSlider();
        thrImgB=new JSlider();
        thrSlice=new JSlider();
        
        thrImgALab=new JLabel();
        thrImgBLab=new JLabel();
        
                
        thrTab.add(new JLabel("Threshold Image A"));
        thrTab.add(thrImgALab);
        thrTab.add(thrImgA);
        thrImgA.addChangeListener(this);
        thrImgA.setMinimum(0);
        thrImgA.setMaximum(0);
        thrImgA.setValue(0);
        
        thrTab.add(new JLabel("Threshold Image B"));
        thrTab.add(thrImgBLab);
        thrTab.add(thrImgB);
        thrImgB.addChangeListener(this);
        thrImgB.setMinimum(0);
        thrImgB.setMaximum(0);
        thrImgB.setValue(0);
        
        thrTab.add(new JLabel("Slice"));
        thrSlice.setMinimum(1);
        thrSliceLab=new JLabel();
        thrTab.add(thrSliceLab);
        thrSlice.addChangeListener(this);
        
        thrTab.add(thrSlice);
        thrSlice.setMaximum(1);
        thrSlice.setValue(1);
        
        tabs.addTab("Threshold", thrTab);
        
        DecimalFormat threeDigits=(DecimalFormat) (DecimalFormat.getInstance(Locale.UK));
        threeDigits.setGroupingSize(0);
        threeDigits.setMaximumFractionDigits(6);
        DecimalFormat noDigit=(DecimalFormat) (DecimalFormat.getInstance(Locale.UK));
        noDigit.setGroupingSize(0);
        noDigit.setMaximumFractionDigits(0);
        
        
        CCFTab = new JPanel();
        CCFTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())CCFTab.setPreferredSize(new Dimension(410, 200));
        
        CCFsubTab1=new JPanel();
        CCFsubTab1.setPreferredSize(new Dimension(380, 75));
        if (!IJ.isWindows())CCFsubTab1.setPreferredSize(new Dimension(410, 75));
        CCFTab.add(CCFsubTab1);
        
        CCFsubTab2=new JPanel(new GridLayout(1,2));
        CCFsubTab2.setPreferredSize(new Dimension(380, 25));
        if (!IJ.isWindows())CCFsubTab2.setPreferredSize(new Dimension(410, 25));
        CCFsubTab2.add(new JLabel("x shift"));
        CCFshiftTxt=new JFormattedTextField(noDigit);
        CCFshiftTxt.setValue(xShift);
        CCFsubTab2.add(CCFshiftTxt);
        
        CCFTab.add(CCFsubTab2);
        
        CCFsubTab3=new JPanel();
        CCFsubTab3.setPreferredSize(new Dimension(380, 75));
        if (!IJ.isWindows())CCFsubTab3.setPreferredSize(new Dimension(410, 75));
        CCFTab.add(CCFsubTab3);
        tabs.addTab("CCF", CCFTab);
        
        scopeTab = new JPanel();
        scopeTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())scopeTab.setPreferredSize(new Dimension(410, 200));
        
        scope1 = new JPanel(new GridLayout(1,2));
        scope1.setPreferredSize(new Dimension(380, 15));
        if (!IJ.isWindows())scope1.setPreferredSize(new Dimension(410, 15));
        
        scopeTypeRadio = new ButtonGroup();
        wfRadio=new JRadioButton("Wide-Field", scopeType);
        wfRadio.addItemListener(this);
        confRadio=new JRadioButton("Confocal", !scopeType);
        scopeTypeRadio.add(wfRadio);
        scopeTypeRadio.add(confRadio);
        scope1.add(wfRadio);
        scope1.add(confRadio);
        scopeTab.add(scope1);
        
        
        scope2 = new JPanel();
        scope2.setPreferredSize(new Dimension(380, 20));
        if (!IJ.isWindows())scope2.setPreferredSize(new Dimension(410, 20));
        scopeImgA=new JButton("Get calib. from ImgA");
        scopeImgA.setPreferredSize(new Dimension (150, 15));
        if (!IJ.isWindows())scopeImgA.setPreferredSize(new Dimension(170, 15));
        scopeImgA.addActionListener(this);
        scope2.add(scopeImgA);
        scopeImgB=new JButton("Get calib. from ImgB");
        scopeImgB.setPreferredSize(new Dimension (150, 15));
        if (!IJ.isWindows())scopeImgB.setPreferredSize(new Dimension(170, 15));
        scopeImgB.addActionListener(this);
        scope2.add(scopeImgB);
        scopeTab.add(scope2);
        
        scope3 = new JPanel(new GridLayout(2,2));
        scope3.setPreferredSize(new Dimension(380, 38));
        if (!IJ.isWindows())scope3.setPreferredSize(new Dimension(410, 38));
        xyCalibTxt=new JFormattedTextField(noDigit);
        xyCalibTxt.setValue(xyCalib);
        xyCalibTxt.addPropertyChangeListener("value", this);
        zCalibTxt=new JFormattedTextField(noDigit);
        zCalibTxt.setValue(zCalib);
        zCalibTxt.addPropertyChangeListener("value", this);
        scope3.add(new JLabel("xy calib (nm)"));
        scope3.add(xyCalibTxt);
        scope3.add(new JLabel("z calib (nm)"));
        scope3.add(zCalibTxt);
        scopeTab.add(scope3);
        
        scope4 = new JPanel();
        scope4.setPreferredSize(new Dimension(380, 20));
        if (!IJ.isWindows())scope4.setPreferredSize(new Dimension(410, 20));
        scopeSet=new JButton("Write calib. to images");
        scopeSet.setPreferredSize(new Dimension (160, 15));
        if (!IJ.isWindows())scopeSet.setPreferredSize(new Dimension(180, 15));
        scopeSet.addActionListener(this);
        scope4.add(scopeSet);
        scopeTab.add(scope4);
        
        
        
        scope5 = new JPanel(new GridLayout(4,2));
        scope5.setPreferredSize(new Dimension(380, 76));
        if (!IJ.isWindows())scope5.setPreferredSize(new Dimension(410, 76));
        waveATxt=new JFormattedTextField(noDigit);
        waveATxt.setValue(waveA);
        waveBTxt=new JFormattedTextField(noDigit);
        waveBTxt.setValue(waveB);
        naTxt=new JFormattedTextField(threeDigits);
        naTxt.setValue(na);
        RITxt=new JFormattedTextField(threeDigits);
        RITxt.setValue(ri);
        scope5.add(new JLabel("Wavelength A (nm)"));
        scope5.add(waveATxt);
        scope5.add(new JLabel("Wavelength B (nm)"));
        scope5.add(waveBTxt);
        scope5.add(new JLabel("NA"));
        scope5.add(naTxt);
        scope5.add(new JLabel("Refractive index"));
        scope5.add(RITxt);
        scopeTab.add(scope5);
        
        
        tabs.addTab("Micro.", scopeTab);
                
        CostesRandTab = new JPanel();
        CostesRandTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())CostesRandTab.setPreferredSize(new Dimension(410, 200));
        CostesxyTxt=new JFormattedTextField(noDigit);
        CostesxyTxt.setValue(2);
        CosteszTxt=new JFormattedTextField(noDigit);
        CosteszTxt.setValue(1);
        CostesRandRoundTxt=new JFormattedTextField(noDigit);
        CostesRandRoundTxt.setValue(nbRand);
        CostesBinTxt=new JFormattedTextField(threeDigits);
        CostesBinTxt.setValue(binWidth);
        
        CostesRand1=new JPanel(new GridLayout(4,2));
        CostesRand1.setPreferredSize(new Dimension(380,95));
        if (!IJ.isWindows())CostesRand1.setPreferredSize(new Dimension(410, 95));
        CostesRand1.add(new JLabel("xy block size (pix.)"));
        CostesRand1.add(CostesxyTxt);
        CostesRand1.add(new JLabel("z block size (pix.)"));
        CostesRand1.add(CosteszTxt);
        CostesRand1.add(new JLabel("Nb of random. rounds"));
        CostesRand1.add(CostesRandRoundTxt);
        CostesRand1.add(new JLabel("Bin width"));
        CostesRand1.add(CostesBinTxt);
        CostesRandTab.add(CostesRand1);
        
        CostesRand2=new JPanel(new GridLayout(1,2));
        CostesRand2.setPreferredSize(new Dimension(380,25));
        if (!IJ.isWindows())CostesRand2.setPreferredSize(new Dimension(410, 25));
        CostesFit=new JComboBox(fitMethList);
        CostesFit.setSelectedIndex(fitMeth);
        CostesRand2.add(new JLabel("Image fitting to block's size"));
        CostesRand2.add(CostesFit);
        CostesRandTab.add(CostesRand2);
        
        CostesRand3=new JPanel(new GridLayout(3,1));
        CostesRand3.setPreferredSize(new Dimension(380,55));
        if (!IJ.isWindows())CostesRand3.setPreferredSize(new Dimension(410, 55));
        CostesSlicesIndpCh=new JCheckBox("Slices to be considered as independent");
        CostesSlicesIndpCh.setSelected(xyRand);
        CostesRand3.add(CostesSlicesIndpCh);
        CostesSlicesIndpCh.addItemListener(this);
        CosteszRandCh=new JCheckBox("z randomization as well");
        CosteszRandCh.setSelected(zRand);
        CostesRand3.add(CosteszRandCh);
        if (CostesSlicesIndpCh.isSelected()) CosteszRandCh.setEnabled(false);
        CostesShowLastCh=new JCheckBox("Show last randomized image");
        CostesShowLastCh.setSelected(showRand);
        CostesRand3.add(CostesShowLastCh);
        CostesRandTab.add(CostesRand3);
        
        tabs.addTab("Costes' rand°", CostesRandTab);
        
        ObjTab = new JPanel();
        ObjTab.setPreferredSize(new Dimension(380, 200));
        if (!IJ.isWindows())ObjTab.setPreferredSize(new Dimension(410, 200));
        
        
        
        Obj1=new JPanel(new GridLayout(2,2));
        Obj1.setPreferredSize(new Dimension(380,40));
        if (!IJ.isWindows())Obj1.setPreferredSize(new Dimension(410, 40));
        centreTypeRadio = new ButtonGroup();
        cMassRadio=new JRadioButton("Centre of mass", cMass);
        cGeoRadio=new JRadioButton("Geometrical centre", !cMass);
        centreTypeRadio.add(cMassRadio);
        centreTypeRadio.add(cGeoRadio);
        Obj1.add(cMassRadio);
        Obj1.add(cGeoRadio);
        
        tableTypeRadio = new ButtonGroup();
        fullRadio=new JRadioButton("Show full table", fullList);
        colocRadio=new JRadioButton("Show coloc° only table", !fullList);
        tableTypeRadio.add(fullRadio);
        tableTypeRadio.add(colocRadio);
        Obj1.add(fullRadio);
        Obj1.add(colocRadio);
        
        ObjTab.add(Obj1);
        
        Obj2=new JPanel(new GridLayout(2,2));
        Obj2.setPreferredSize(new Dimension(380,40));
        if (!IJ.isWindows())Obj2.setPreferredSize(new Dimension(410, 40));
        Obj2=new JPanel(new GridLayout(2,2));
        minSizeTxt=new JFormattedTextField(noDigit);
        minSizeTxt.setText(""+minSize);
        maxSizeTxt=new JFormattedTextField(noDigit);
        maxSizeTxt.setText("1");
        Obj2.add(new JLabel("Min. particle size (pix.)"));
        Obj2.add(minSizeTxt);
        Obj2.add(new JLabel("Max. particle size (pix.)"));
        Obj2.add(maxSizeTxt);
        ObjTab.add(Obj2);
        
        Obj3=new JPanel(new GridLayout(4,1));
        Obj3.setPreferredSize(new Dimension(380,95));
        if (!IJ.isWindows())Obj3.setPreferredSize(new Dimension(410, 95));
        distCentCh=new JCheckBox("Work on distances between centres");
        distCentCh.setSelected(workDist);
        distCentCh.addItemListener(this);
        Obj3.add(distCentCh);
        
        distShowCentCh=new JCheckBox("Show centres map");
        distShowCentCh.setSelected(showCent);
        if (!distCentCh.isSelected()) distShowCentCh.setEnabled(false);
        Obj3.add(distShowCentCh);
        
        centPartCh=new JCheckBox("Work on centres-particles coincidence");
        centPartCh.setSelected(workCentPart);
        centPartCh.addItemListener(this);
        Obj3.add(centPartCh);
        
        showCentPartCh=new JCheckBox("Show centres-particles map");
        showCentPartCh.setSelected(showCentPart);
        if (!centPartCh.isSelected()) showCentPartCh.setEnabled(false);
        Obj3.add(showCentPartCh);
        
        ObjTab.add(Obj3);
        
        tabs.addTab("Obj.", ObjTab);
        
        tabs.setOpaque(true);
        superPanel.add(tabs);
        
        tabs.addChangeListener(this);
        
        warning=new JLabel("Please check red labeled tabs before launching analysis");
        warning.setForeground(Color.RED);
        superPanel.add(warning);
                
        analyze=new JButton("Analyze");
        analyze.addActionListener(this);
        superPanel.add(analyze);
        ImagePlus.addImageListener(this);
        frame.getContentPane().add(superPanel);
        frame.addWindowListener(this);
        updateImgList(null);
        imgA.setSelectedIndex(0);
        imgB.setSelectedIndex(1);
        resetThr();
        if (nbImg!=0) adaptZoom();
        updateTicked();
        frame.setVisible(true);
        doChange=true;

    }
    
    public void doZeJob(boolean logVar){
        if (Macro.getOptions()==null){
            impA=WindowManager.getImage((String) imgA.getSelectedItem());
            impB=WindowManager.getImage((String) imgB.getSelectedItem());
            calib=getFinalCalib();
            retrieveParam(logVar, impA, impB, calib);
        }else{
            macroInterpreter(Macro.getOptions());
            calib=impA.getCalibration();
            if(calib.getUnit().equals("°m")){
                calib.setUnit("nm");
                calib.pixelWidth*=1000;
                calib.pixelHeight*=1000;
                calib.pixelDepth*=1000;
            }
        }
        
        ImageColocalizer ic=new ImageColocalizer(impA, impB, calib);
        if (PearsonBool) ic.Pearson();
        if (OverlapBool) ic.Overlap(thrA, thrB);
        if (MMBool) ic.MM(thrA, thrB);
        if (CostesThrBool) ic.CostesAutoThr();
        if (CCFBool) ic.CCF(xShift);
        if (CytoBool) ic.CytoFluo();
        if (ICABool) ic.ICA();
        if (CostesRandBool) ic.CostesRand(xyblock, zblock, nbRand, binWidth, fitMeth, xyRand, zRand, showRand);
        if (ObjBool){
            if (workDist) ic.distBetweenCentres(thrA, thrB, minSize, maxSize, resxy, resz, cMass, fullList, showCent);
            if (workCentPart) ic.coincidenceCentreParticle(thrA, thrB, minSize, maxSize, cMass, fullList, showCentPart);
        }
     }
    
    
    public void retrieveParam(boolean logVar, ImagePlus ipA, ImagePlus ipB, Calibration cal){
        thrA=thrImgA.getValue();
        thrB=thrImgB.getValue();
        
        xShift=Integer.parseInt(CCFshiftTxt.getText());
        
        xyblock=Integer.parseInt(CostesxyTxt.getText());
        zblock=Integer.parseInt(CosteszTxt.getText());
        nbRand=Integer.parseInt(CostesRandRoundTxt.getText());
        binWidth=Double.parseDouble(CostesBinTxt.getText());
        fitMeth=CostesFit.getSelectedIndex();
        xyRand=CostesSlicesIndpCh.isSelected();
        zRand=CosteszRandCh.isSelected();
        showRand=CostesShowLastCh.isSelected();
        
        //dist------------------
        
        calcResolution();
        
        cMass=cMassRadio.isSelected();
        fullList=fullRadio.isSelected();
        minSize=Integer.parseInt(minSizeTxt.getText());
        maxSize=Integer.parseInt(maxSizeTxt.getText());
        workDist=distCentCh.isSelected();
        showCent=distShowCentCh.isSelected();
        workCentPart=centPartCh.isSelected();
        showCentPart=showCentPartCh.isSelected();
        
        if (logVar){
            IJ.log("imgA: "+ ipA.getTitle());
            IJ.log("imgB: "+ ipB.getTitle());
            
            IJ.log("thrA: "+thrA);
            IJ.log("thrB: "+thrB);
            
            IJ.log("cal: "+cal);
            
            IJ.log("xshift: "+xShift);
            IJ.log("xyblock: "+xyblock);
            IJ.log("zblock: "+zblock);
            IJ.log("nbRand: "+nbRand);
            IJ.log("binWidth: "+binWidth);
            IJ.log("fitMeth: "+fitMeth);
            IJ.log("xyRand: "+xyRand);
            IJ.log("zRand: "+zRand);
            IJ.log("showRand: "+showRand);

            IJ.log("scopeType: "+scopeType);
            IJ.log("xyCalib: "+xyCalib);
            IJ.log("zCalib: "+zCalib);
            IJ.log("waveA: "+waveA);
            IJ.log("waveB: "+waveB);
            IJ.log("na: "+na);
            IJ.log("ri: "+ri);
            
            IJ.log("resxy: "+resxy);
            IJ.log("resz: "+resz);
            
            
            IJ.log("cMass: "+cMass);
            IJ.log("fullList: "+fullList);
            IJ.log("min size: "+minSize);
            IJ.log("max size: "+maxSize);
            IJ.log("workDist: "+workDist);
            IJ.log("showCent: "+showCent);
            IJ.log("workCentPart: "+workCentPart);
            IJ.log("showCentPart: "+showCentPart);
            
            
        }
                
        Prefs.set("JACoP_Pearson.boolean", PearsonBool);
        Prefs.set("JACoP_Overlap.boolean", OverlapBool);
        Prefs.set("JACoP_MM.boolean", MMBool);
        Prefs.set("JACoP_CostesThr.boolean", CostesThrBool);
        Prefs.set("JACoP_CCF.boolean", CCFBool);
        Prefs.set("JACoP_Cyto.boolean", CytoBool);
        Prefs.set("JACoP_ICA.boolean", ICABool);
        Prefs.set("JACoP_CostesRand.boolean", CostesRandBool);
        Prefs.set("JACoP_Obj.boolean", ObjBool);
        
        Prefs.set("JACoP_CCFx.double", xShift);
        
        Prefs.set("JACoP_scopeType.boolean", scopeType);
        Prefs.set("JACoP_xyCalib.double", xyCalib);
        Prefs.set("JACoP_zCalib.double", zCalib);
        Prefs.set("JACoP_waveA.double", waveA);
        Prefs.set("JACoP_waveB.double", waveB);
        Prefs.set("JACoP_na.double", na);
        Prefs.set("JACoP_ri.double", ri);
        
        Prefs.set("JACoP_nbRand.double", nbRand);
        Prefs.set("JACoP_binWidth.double", binWidth);
        Prefs.set("JACoP_fitMeth.double", fitMeth);
        Prefs.set("JACoP_xyRand.boolean", xyRand);
        Prefs.set("JACoP_zRand.boolean", zRand);
        Prefs.set("JACoP_showRand.boolean", showRand);
        
        Prefs.set("JACoP_cMass.boolean", cMass);
        Prefs.set("JACoP_fullList.boolean", fullList);
        Prefs.set("JACoP_minSize.double", minSize);
        Prefs.set("JACoP_maxSize.double", maxSize);
        Prefs.set("JACoP_workDist.boolean", workDist);
        Prefs.set("JACoP_showCent.boolean", showCent);
        Prefs.set("JACoP_workCentPart.boolean", workCentPart);
        Prefs.set("JACoP_showCentPart.boolean", showCentPart);
        if (Recorder.record) macroGenerator(ipA.getTitle(), ipB.getTitle());
        
    }
    
    public void macroInterpreter(String arg){
        int start=0, end=0;
        
        //Check what should be done
        PearsonBool=(arg.indexOf("pearson")!=-1)?true:false;
        OverlapBool=(arg.indexOf("overlap")!=-1)?true:false;
        MMBool=(arg.indexOf("mm")!=-1)?true:false;
        CostesThrBool=(arg.indexOf("costesthr")!=-1)?true:false;
        CCFBool=(arg.indexOf("ccf")!=-1)?true:false;
        CytoBool=(arg.indexOf("cytofluo")!=-1)?true:false;
        ICABool=(arg.indexOf("ica")!=-1)?true:false;
        CostesRandBool=(arg.indexOf("costesrand")!=-1)?true:false;
        workDist=(arg.indexOf("objdist")!=-1)?true:false;
        workCentPart=(arg.indexOf("objcentpart")!=-1)?true:false;
        ObjBool=workDist||workCentPart;
        
        //Find the images' names
        start=arg.indexOf("imga=")+5;
        end=arg.indexOf(" ", start);
        if ((arg.charAt(start)+"").equals("[")){
            start++;
            end=arg.indexOf("]", start);
        }
        String imgATitle=arg.substring(start, end);
        start=end+1;
        impA=WindowManager.getImage(imgATitle);
        
        start=arg.indexOf("imgb=")+5;
        end=arg.indexOf(" ", start);
        if ((arg.charAt(start)+"").equals("[")){
            start++;
            end=arg.indexOf("]", start);
        }
        String imgBTitle=arg.substring(start, end);
        start=end+1;
        impB=WindowManager.getImage(imgBTitle);
        
        if (impA==null || impB==null){
            IJ.error("JACoP error, within a macro", "Image not found while running JACoP from a macro\n1-Use \"open(path)\" in your macro to open images\n2-Make sure you have called the right image !");
            return;
        }
        
        
        
        //Find the threshold values
        if (MMBool || ObjBool){
            start=arg.indexOf("thra=")+5;
            end=arg.indexOf(" ",start);
            thrA=(int) Double.parseDouble(arg.substring(start, end));
            start=arg.indexOf("thrb=")+5;
            end=arg.indexOf(" ",start);
            thrB=(int) Double.parseDouble(arg.substring(start, end));
        }
                
        if (CCFBool){
            start=arg.indexOf("ccf=")+4;
            end=arg.indexOf(" ",start);
            xShift=(int) Double.parseDouble(arg.substring(start, end));
        }
                
        if (CostesRandBool){
            start=arg.indexOf("costesrand=")+11;
            end=arg.indexOf(" ",start);
            String[] tmp=arg.substring(start, end).split("-");
            xyblock=(int) Double.parseDouble(tmp[0]);
            zblock=(int) Double.parseDouble(tmp[1]);
            nbRand=(int) Double.parseDouble(tmp[2]);
            binWidth=Double.parseDouble(tmp[3]);
            fitMeth=(int) Double.parseDouble(tmp[4]);
            xyRand=Boolean.parseBoolean(tmp[5]);
            zRand=Boolean.parseBoolean(tmp[6]);
            showRand=Boolean.parseBoolean(tmp[7]);
        }
              
        if (workDist){
            start=arg.indexOf("objdist=")+8;
            end=arg.indexOf(" ",start);
            String[] tmp=arg.substring(start, end).split("-");
            minSize=(int) Double.parseDouble(tmp[0]);
            maxSize=(int) Double.parseDouble(tmp[1]);
            resxy=(int) Double.parseDouble(tmp[2]);
            resz=(int) Double.parseDouble(tmp[3]);
            cMass=Boolean.parseBoolean(tmp[4]);
            fullList=Boolean.parseBoolean(tmp[5]);
            showCent=Boolean.parseBoolean(tmp[6]);
        }
        
        if (workCentPart){
            start=arg.indexOf("objcentpart=")+12;
            end=arg.indexOf(" ",start);
            String[] tmp=arg.substring(start, end).split("-");
            minSize=(int) Double.parseDouble(tmp[0]);
            maxSize=(int) Double.parseDouble(tmp[1]);
            cMass=Boolean.parseBoolean(tmp[2]);
            fullList=Boolean.parseBoolean(tmp[3]);
            showCentPart=Boolean.parseBoolean(tmp[4]);
        }
    }
    
    public void macroGenerator(String titleA, String titleB){
        Recorder.setCommand("JACoP ");
        Recorder.recordOption("imga", titleA);
        Recorder.recordOption("imgb", titleB);
        if (MMBool || ObjBool){Recorder.recordOption("thra", ""+thrA); Recorder.recordOption("thrb", ""+thrB);}
        if (PearsonBool) Recorder.recordOption("pearson");
        if (OverlapBool) Recorder.recordOption("overlap");
        if (MMBool) Recorder.recordOption("mm");
        if (CostesThrBool) Recorder.recordOption("costesthr");
        if (CCFBool) Recorder.recordOption("ccf", ""+xShift);
        if (CytoBool) Recorder.recordOption("cytofluo");
        if (ICABool) Recorder.recordOption("ica");
        if (CostesRandBool) Recorder.recordOption("costesrand", xyblock+"-"+zblock+"-"+nbRand+"-"+binWidth+"-"+fitMeth+"-"+xyRand+"-"+zRand+"-"+showRand);
        if (ObjBool){
            if (workDist) Recorder.recordOption("objdist", minSize+"-"+maxSize+"-"+resxy+"-"+resz+"-"+cMass+"-"+fullList+"-"+showCent);
            if (workCentPart) Recorder.recordOption("objcentpart", minSize+"-"+maxSize+"-"+cMass+"-"+fullList+"-"+showCentPart);
        }
        Recorder.saveCommand();
    }
    
    public void updateTicked(){
        PearsonBool=PearsonCh.isSelected();
        OverlapBool=OverlapCh.isSelected();
        MMBool=MMCh.isSelected();
        CostesThrBool=CostesThrCh.isSelected();
        CCFBool=CCFCh.isSelected();
        CytoBool=CytoCh.isSelected();
        ICABool=ICACh.isSelected();
        CostesRandBool=CostesRandCh.isSelected();
        ObjBool=ObjCh.isSelected();
        
        
        if (/*PearsonBool ||*/ OverlapBool || MMBool || ObjBool /*|| ICABool*/){
            tabs.setForegroundAt(1, Color.RED);
        }else{
            tabs.setForegroundAt(1, Color.BLACK);
        }
        
        if (CCFBool){
            tabs.setForegroundAt(2, Color.RED);
        }else{
            tabs.setForegroundAt(2, Color.BLACK);
        }
        
        if (CostesRandBool){
            tabs.setForegroundAt(3, Color.RED);
            tabs.setForegroundAt(4, Color.RED);
        }else{
            tabs.setForegroundAt(4, Color.BLACK);
            if (!ObjBool) tabs.setForegroundAt(3, Color.BLACK);
        }
        
        if (ObjBool){
            tabs.setForegroundAt(3, Color.RED);
            tabs.setForegroundAt(5, Color.RED);
        }else{
            if (!CostesRandBool) tabs.setForegroundAt(3, Color.BLACK);
            tabs.setForegroundAt(5, Color.BLACK);
        }
        
    }
    
    public void adaptZoom(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight=(int) screenSize.getHeight();
        if (!((String) imgA.getSelectedItem()).equals("[No image]") && !((String) imgB.getSelectedItem()).equals("[No image]")){
            ImageWindow iwA=WindowManager.getImage((String) imgA.getSelectedItem()).getWindow();
            ImageWindow iwB=WindowManager.getImage((String) imgB.getSelectedItem()).getWindow();

            int width=(int) (screenSize.getWidth()-frame.getWidth()-frame.getX())/2;
            int widthA=width, widthB=width;
            int heightA=iwA.getImagePlus().getHeight()*width/iwA.getImagePlus().getWidth();
            int heightB=iwB.getImagePlus().getHeight()*width/iwB.getImagePlus().getWidth();

            if (heightA>screenHeight){
                heightA=screenHeight;
                widthA=iwA.getWidth()*screenHeight/iwA.getHeight();
            }

            if (heightB>screenHeight){
                heightB=screenHeight;
                widthB=iwB.getWidth()*screenHeight/iwB.getHeight();
            }


            iwA.setLocationAndSize(frame.getWidth()+frame.getX(), screenHeight/2-heightA/2, widthA, heightA);
            iwB.setLocationAndSize(frame.getWidth()+frame.getX()+iwA.getWidth(), screenHeight/2-heightB/2, widthB, heightB);

            iwA.toFront();
            iwB.toFront();
        }
        adaptedZoom=true;
   }
    
    public void calcResolution(){
        scopeType=wfRadio.isSelected();
        xyCalib=Double.parseDouble(xyCalibTxt.getText());
        zCalib=Double.parseDouble(zCalibTxt.getText());
        waveA=Integer.parseInt(waveATxt.getText());
        waveB=Integer.parseInt(waveBTxt.getText());
        na=Double.parseDouble(naTxt.getText());
        ri=Double.parseDouble(RITxt.getText());
        
        if (scopeType){
            resxy=0.61*waveA/na;
            resz=2*waveA/Math.pow(na,2);
        }else{
            resxy=0.4*waveB/na;
            resz=1.4*waveB/Math.pow(na,2);
        }
    }
    
    public void resetZoom(){
        ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
        ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());
        
        ipA.getCanvas().unzoom();
        ipB.getCanvas().unzoom();
        adaptedZoom=false;
    }
    
    public void getCalibFromImg(int origin){
        ImagePlus ip=WindowManager.getImage((String) imgA.getSelectedItem());
        if (origin==1) ip=WindowManager.getImage((String) imgB.getSelectedItem());
        
        Calibration cal=ip.getCalibration();
        String unit=cal.getUnit();
        
        if (unit.equals("nm")){
            xyCalibTxt.setText(""+cal.pixelWidth);
            zCalibTxt.setText(""+cal.pixelDepth);
        }else if (unit.equals("°m") || unit.equals("micron") || unit.equals("um")){
            xyCalibTxt.setText(""+cal.pixelWidth*1000);
            zCalibTxt.setText(""+cal.pixelDepth*1000);
        }else{
            IJ.showMessage("No appropriate calibration found");
        }
        
        updateCostesRandParam();
    }
    
    public Calibration getFinalCalib(){
        Calibration cal=new Calibration();
        cal.setUnit("nm");
        cal.pixelWidth=Double.parseDouble(xyCalibTxt.getText());
        cal.pixelHeight=Double.parseDouble(xyCalibTxt.getText());
        cal.pixelDepth=Double.parseDouble(zCalibTxt.getText());
        return cal;
    }
    
    public void setCalibToImg(){
        Calibration cal=new Calibration();
        cal.setUnit("°m");
        cal.pixelWidth=Double.parseDouble(xyCalibTxt.getText())/1000;
        cal.pixelHeight=Double.parseDouble(xyCalibTxt.getText())/1000;
        cal.pixelDepth=Double.parseDouble(zCalibTxt.getText())/1000;
        ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
        ipA.setCalibration(cal);
        ipA.updateAndRepaintWindow();
        ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());
        ipB.setCalibration(cal);
        ipB.updateAndRepaintWindow();
     }
    
    public void updateCostesRandParam(){
        calcResolution();

        recomxy=(int) (resxy/xyCalib);
        recomz=(int) (resz/zCalib);
        
        CostesxyTxt.setText(""+recomxy);
        CosteszTxt.setText(""+recomz);
    }
    
    public void updateImgList(ImagePlus img){
        nbImg=0;
        
        int selectA=imgA.getSelectedIndex(), selectB=imgB.getSelectedIndex();
        
        if (WindowManager.getImageCount()!=0){
            int[] IDList=WindowManager.getIDList();
            for (int i=0;i<IDList.length;i++){
                ImagePlus currImg=WindowManager.getImage(IDList[i]);
                if (currImg.getBitDepth()!=24 && currImg.getBitDepth()!=32){
                    nbImg++;
                    if (img==null) addImgToList(currImg);
                }
            }
        }
        
        if (nbImg<2){
            tabs.setSelectedIndex(0);
            tabs.setEnabled(false);
            analyze.setEnabled(false);
            warning.setText("At least 2 images should be opened to run colocalisation analysis");
        }else{
            tabs.setEnabled(true);
            analyze.setEnabled(true);
            warning.setText("Please check red labeled tabs before launching analysis");
        }
        
        
        if (((ImgInfo) info.elementAt(1)).title.equals("[No image]")) info.removeElementAt(1);
        if (((ImgInfo) info.elementAt(0)).title.equals("[No image]")) info.removeElementAt(0);
         
        if (nbImg>oldNbImg && img!=null) addImgToList(img);
       
            
        if (nbImg<oldNbImg){
            String title=img.getTitle();
            for (int i=0; i<info.size(); i++){
                if ((((ImgInfo) info.elementAt(i)).title).equals(title)){
                    info.remove(i);
                    i=info.size();
                    if (selectA==i) selectA=selectA-1;
                    if (selectB==i) selectB=selectB-1;
                }
            }
        }
            
        while (info.size()<2){
            info.add(new ImgInfo());
        }
        
        imgA.removeActionListener(this);
        imgB.removeActionListener(this);
        
        imgA.removeAllItems();
        imgB.removeAllItems();
        
        for (int i=0; i<info.size(); i++){
            String item=(((ImgInfo) info.elementAt(i)).title);
            imgA.addItem(item);
            imgB.addItem(item);
        }
        
        imgA.addActionListener(this);
        imgB.addActionListener(this);
        
        selectA=Math.max(selectA, 0);
        selectB=Math.max(selectB, 0);
        if (nbImg<2) selectB=1;
        imgA.setSelectedIndex(selectA);
        imgB.setSelectedIndex(selectB);
        
        oldNbImg=nbImg;
    }
    
    public void addImgToList(ImagePlus img){
        int min=65535, max=0;
        for (int i=1; i<=img.getNSlices(); i++){
            img.setSlice(i);
            min=Math.min(min, (int) img.getStatistics().min);
            max=Math.max(max, (int) img.getStatistics().max);
        }
        info.addElement(new ImgInfo(img.getTitle(), min, max, img.getProcessor().getAutoThreshold()));
    }
    
    
    public void resetArrays(){
        info.clear();
        info.add(new ImgInfo());
        info.add(new ImgInfo());
    }
    
    public void updateThr(){
        int indexA=imgA.getSelectedIndex();
        int indexB=imgB.getSelectedIndex();
        ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
        ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());
        if (tabs.getSelectedIndex()==1 && nbImg>1){
            ipA.getProcessor().setThreshold(((ImgInfo) info.elementAt(indexA)).thr, ((ImgInfo) info.elementAt(indexA)).max, ImageProcessor.RED_LUT);
            ipA.updateAndDraw();

            ipB.getProcessor().setThreshold(((ImgInfo) info.elementAt(indexB)).thr, ((ImgInfo) info.elementAt(indexB)).max, ImageProcessor.RED_LUT);
            ipB.updateAndDraw();
        }
    }
    
    public void updateMaxSize(){
        ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
        ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());
        
        if (ipA!=null && ipB!=null){
            int size=Math.max(ipA.getWidth()*ipA.getHeight()*ipA.getNSlices(), ipB.getWidth()*ipB.getHeight()*ipB.getNSlices());
            maxSizeTxt.setText(""+size);
        }
    }
    
    public void resetThr(){
        if (nbImg!=0){
            ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
            ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());
            
            if (ipA!=null){
                ipA.getProcessor().resetThreshold();
                ipA.updateAndDraw();
            }
            
            if (ipB!=null){
                ipB.getProcessor().resetThreshold();
                ipB.updateAndDraw();
            }
        }
    }
    
    public void updateSlice(){
        ImagePlus ipA=WindowManager.getImage((String) imgA.getSelectedItem());
        ImagePlus ipB=WindowManager.getImage((String) imgB.getSelectedItem());

        if (ipA!=null && ipB!=null){
            int maxSlice=Math.max(ipA.getNSlices(), ipB.getNSlices());
            thrSlice.setMaximum(maxSlice);
            maxSlice=(maxSlice+1)/2;
            thrSlice.setValue(maxSlice);
            thrSliceLab.setText(""+maxSlice);
            ipA.setSlice(maxSlice);
            ipB.setSlice(maxSlice);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        Object origin=e.getSource();
        
        //Modifiers: none=16, shift=17, ctrl=18, alt=24
        if (origin==jmi){
            try {BrowserLauncher.openURL("http://www.blackwell-synergy.com/doi/pdf/10.1111/j.1365-2818.2006.01706.x");}
            catch (IOException ie) {}
        }
        
        
        if (origin==analyze) doZeJob(e.getModifiers()==17?true:false);
        
        if (origin==zoom){
            if (adaptedZoom){
                resetZoom();
                zoom.setText("Adapt zoom");
            }else{
                adaptZoom();
                zoom.setText("Reset zoom");
            }
        }
        
        
        if (origin==imgA){
            int index=imgA.getSelectedIndex();
            
            if (prevImgA!=imgB.getSelectedIndex()){
                ImagePlus ipOld=WindowManager.getImage((String) imgA.getItemAt(prevImgA));
                if (ipOld!=null){
                    ipOld.getProcessor().resetThreshold();
                    ipOld.updateAndDraw();
                }
            }
            
            doChange=false;
            thrImgA.setMinimum(((ImgInfo) info.elementAt(index)).min);
            thrImgA.setMaximum(((ImgInfo) info.elementAt(index)).max);
            thrImgA.setValue(((ImgInfo) info.elementAt(index)).thr);
            doChange=true;
            thrImgALab.setText(""+((ImgInfo) info.elementAt(index)).thr);
            
            updateMaxSize();
            updateThr();
            updateSlice();
            adaptZoom();
            prevImgA=index;
            
        }
        
        if (origin==imgB){
            int index=imgB.getSelectedIndex();
            
            if (prevImgB!=imgA.getSelectedIndex()){
                ImagePlus ipOld=WindowManager.getImage((String) imgB.getItemAt(prevImgB));
                if (ipOld!=null){
                    ipOld.getProcessor().resetThreshold();
                    ipOld.updateAndDraw();
                }
            }
            
            doChange=false;
            thrImgB.setMinimum(((ImgInfo) info.elementAt(index)).min);
            thrImgB.setMaximum(((ImgInfo) info.elementAt(index)).max);
            thrImgB.setValue(((ImgInfo) info.elementAt(index)).thr);
            doChange=true;
            thrImgBLab.setText(""+((ImgInfo) info.elementAt(index)).thr);
             
            updateMaxSize();
            updateThr();
            updateSlice();
            adaptZoom();
            prevImgB=index;
        }
        
        if (origin==scopeImgA) getCalibFromImg(0);
        if (origin==scopeImgB) getCalibFromImg(1);
        
        
        if (origin==scopeSet) setCalibToImg();
    }
    
    public void itemStateChanged(ItemEvent e){
        Object origin=e.getSource();
        boolean checked=e.getStateChange()==1?true:false;
        updateTicked();
        
        if (checked && tabs.isEnabled()){
            int tabSelect=tabs.getSelectedIndex();
            //if (origin==PearsonCh) tabSelect=1;
            if (origin==OverlapCh) tabSelect=1;
            if (origin==MMCh) tabSelect=1;
            if (origin==CostesThrCh) tabSelect=1;
            if (origin==CCFCh) tabSelect=2;
            //if (origin==ICACh) tabSelect=1;
            if (origin==CostesRandCh) tabSelect=4;
            if (origin==ObjCh) tabSelect=5;
            tabs.setSelectedIndex(tabSelect);
        }
        
        if (origin==wfRadio) updateCostesRandParam();
        
        if (origin==CostesSlicesIndpCh){
            CosteszRandCh.setEnabled(false);
            if(!CostesSlicesIndpCh.isSelected()) CosteszRandCh.setEnabled(true);
        }
        
        if (origin==distCentCh){
            distShowCentCh.setEnabled(true);
            if (!distCentCh.isSelected()) distShowCentCh.setEnabled(false);
        }
        
        if (origin==centPartCh){
            showCentPartCh.setEnabled(true);
            if (!centPartCh.isSelected()) showCentPartCh.setEnabled(false);
        }
    }
    
    public void imageOpened(ImagePlus imp){
        updateImgList(imp);
    }
    
    public void imageClosed(ImagePlus imp){
        updateImgList(imp);
    }
    
    public void imageUpdated(ImagePlus imp){}
    
    public void stateChanged(ChangeEvent e) {
        Object origin=e.getSource();
        
        if (origin==tabs && tabs.getSelectedIndex()==1){
            updateThr();
        }else{
            resetThr();
        }
        
        if (origin==thrImgA && doChange){
            int value=thrImgA.getValue();
            int imgID=imgA.getSelectedIndex();
            ((ImgInfo) info.elementAt(imgID)).thr=value;
            thrImgALab.setText(""+value);
            if (imgID==imgB.getSelectedIndex()){
                thrImgB.setValue(value);
                thrImgBLab.setText(""+value);
            }
            updateThr();
        }
        
        if (origin==thrImgB && doChange){
            int value=thrImgB.getValue();
            int imgID=imgB.getSelectedIndex();
            ((ImgInfo) info.elementAt(imgID)).thr=value;
            thrImgBLab.setText(""+value);
            if (imgA.getSelectedIndex()==imgID){
                thrImgA.setValue(value);
                thrImgALab.setText(""+value);
            }
            updateThr();
        }
        
        if (origin==thrSlice){
            if (nbImg!=0){
                int value=thrSlice.getValue();
                WindowManager.getImage((String) imgA.getSelectedItem()).setSlice(value);
                WindowManager.getImage((String) imgB.getSelectedItem()).setSlice(value);
                thrSliceLab.setText(""+value);
                updateThr();
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        Object origin=e.getSource();
        if (origin==xyCalibTxt || origin==zCalibTxt) updateCostesRandParam();
    }
    
    public void windowActivated(WindowEvent e) {
        
    }
    
    public void windowClosed(WindowEvent e) {}
    
    public void windowClosing(WindowEvent e) {
        ImagePlus.removeImageListener(this);
        resetThr();
    }
    
    public void windowDeactivated(WindowEvent e) {}
     
    public void windowDeiconified(WindowEvent e) {}
     
    public void windowIconified(WindowEvent e) {}
     
    public void windowOpened(WindowEvent e) {}
}



