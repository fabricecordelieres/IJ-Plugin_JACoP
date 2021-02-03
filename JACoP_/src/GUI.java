/*	GUI: "Just Another Colocalization Plugin..." v1, 27/03/20
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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.Macro;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.measure.Calibration;
import ij.plugin.BrowserLauncher;
import ij.plugin.frame.Recorder;
import ij.process.ImageProcessor;

import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import _JACoP.ImageColocalizer;
import _JACoP.ImgInfo;

import javax.swing.JTabbedPane;
import java.awt.GridLayout;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JSlider;
import java.awt.Font;
import javax.swing.border.EtchedBorder;

public class GUI extends JFrame implements ActionListener, ChangeListener, ItemListener, PropertyChangeListener, ImageListener, WindowListener{

	Font font12=new Font("Lucida Grande", Font.PLAIN, 12);
	Font font11=new Font("Lucida Grande", Font.PLAIN, 11);
    
    Vector<ImgInfo> info=new Vector<ImgInfo>();
    String[]  fitMethList={"Shrink to fit", "Pad with black pixels"};
    int nbImg=0, oldNbImg=0, prevImgA=0, prevImgB=1, thrA, thrB, xyblock, zblock, recomxy, recomz;
    double resxy, resz;
    boolean doChange=false, adaptedZoom=true;
    
    ImagePlus impA=new ImagePlus(), impB=new ImagePlus();
    Calibration calib=new Calibration();
    
    
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<String> imgA;
	private JComboBox<String> imgB;
	
	
	

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
    
    
    private JButton jmiBtn;
    private JSlider thrImgA;
    private JSlider thrImgB;
    private JSlider thrSlice;
    private JLabel thrImgALab;
    private JLabel thrSliceLab;
    private JLabel thrBLabel;
    private JLabel thrImgBLab;
    private JFormattedTextField CCFshiftTxt;
    private JRadioButton wfRadio;
    private JRadioButton confRadio;
    private ButtonGroup scopeTypeRadio;
    private JButton scopeImgA;
    private JButton scopeImgB;
    private JFormattedTextField xyCalibTxt;
    private JFormattedTextField zCalibTxt;
    private JButton scopeSet;
    private JFormattedTextField waveATxt;
    private JFormattedTextField waveBTxt;
    private JFormattedTextField naTxt;
    private JFormattedTextField RITxt;
    private JFormattedTextField CostesxyTxt;
    private JFormattedTextField CosteszTxt;
    private JFormattedTextField CostesRandRoundTxt;
    private JFormattedTextField CostesBinTxt;
    private JComboBox CostesFit;
    private JCheckBox CostesSlicesIndpCh;
    private JCheckBox CosteszRandCh;
    private JCheckBox CostesShowLastCh;
    private JRadioButton cMassRadio;
    private JRadioButton cGeoRadio;
    private JRadioButton fullRadio;
    private JRadioButton colocRadio;
    private JFormattedTextField minSizeTxt;
    private JFormattedTextField maxSizeTxt;
    private JCheckBox distCentCh;
    private JCheckBox distShowCentCh;
    private JCheckBox centPartCh;
    private JCheckBox showCentPartCh;
    private JCheckBox PearsonCh;
    private JCheckBox OverlapCh;
    private JCheckBox MMCh;
    private JCheckBox CostesThrCh;
    private JCheckBox CCFCh;
    private JCheckBox CytoCh;
    private JCheckBox ICACh;
    private JCheckBox CostesRandCh;
    private JCheckBox ObjCh;
    private JTabbedPane paramPane;
    private JLabel warningTxt;
    private JButton analyzeBtn;
    private JButton zoomBtn;
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (IJ.versionLessThan("1.39e")) return;
		        
		        if (Macro.getOptions()==null){
		        	GUI frame = new GUI();
		        	frame.setVisible(true);;
		        }else{
		            //doZeJob(false);
		        }
				
		        /*
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		//------------- Number formats -------------
		DecimalFormat threeDigits=(DecimalFormat) (DecimalFormat.getInstance(Locale.UK));
        threeDigits.setGroupingSize(0);
        threeDigits.setMaximumFractionDigits(6);
        DecimalFormat noDigit=(DecimalFormat) (DecimalFormat.getInstance(Locale.UK));
        noDigit.setGroupingSize(0);
        noDigit.setMaximumFractionDigits(0);
		
		//------------- GUI -------------
		setSize(410, 585);//400, 585);
		setTitle("Just Another Colocalisation Plugin v2.1.4 21/02/03");
		setResizable(false);
		setIconImage(new ImageIcon(getClass().getResource("coloc.png")).getImage()); 
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-getHeight()/2));
    
		//------------- Where everything is docked -------------
		contentPane = new JPanel();
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		//------------- Handles images choice and zoom setting -------------
		JPanel imagePanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, imagePanel, 80, SpringLayout.NORTH, contentPane);
		imagePanel.setFont(font12);
		imagePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Images to analyse", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		sl_contentPane.putConstraint(SpringLayout.NORTH, imagePanel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, imagePanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, imagePanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(imagePanel);
		
		
		imgA=new JComboBox<String>();
		imgA.setFont(font12);
		imgB=new JComboBox<String>();
		imgB.setFont(font12);
		SpringLayout sl_imagePanel = new SpringLayout();
		sl_imagePanel.putConstraint(SpringLayout.EAST, imgB, 300, SpringLayout.WEST, imagePanel);
		sl_imagePanel.putConstraint(SpringLayout.EAST, imgA, 300, SpringLayout.WEST, imagePanel);
		imagePanel.setLayout(sl_imagePanel);
		JLabel imgALabel = new JLabel("Image A");
		sl_imagePanel.putConstraint(SpringLayout.NORTH, imgALabel, 5, SpringLayout.NORTH, imagePanel);
		imgALabel.setFont(font12);
		sl_imagePanel.putConstraint(SpringLayout.NORTH, imgA, -4, SpringLayout.NORTH, imgALabel);
		sl_imagePanel.putConstraint(SpringLayout.WEST, imgA, 25, SpringLayout.EAST, imgALabel);
		sl_imagePanel.putConstraint(SpringLayout.WEST, imgALabel, 10, SpringLayout.WEST, imagePanel);
		imagePanel.add(imgALabel);
		imagePanel.add(imgA);
        imgA.addActionListener(this);
        JLabel imgBLabel = new JLabel("Image B");
        sl_imagePanel.putConstraint(SpringLayout.NORTH, imgB, -5, SpringLayout.NORTH, imgBLabel);
        sl_imagePanel.putConstraint(SpringLayout.NORTH, imgBLabel, 30, SpringLayout.NORTH, imgALabel);
        imgBLabel.setFont(font12);
        sl_imagePanel.putConstraint(SpringLayout.WEST, imgB, 27, SpringLayout.EAST, imgBLabel);
        sl_imagePanel.putConstraint(SpringLayout.WEST, imgBLabel, 0, SpringLayout.WEST, imgALabel);
        imagePanel.add(imgBLabel);
        imagePanel.add(imgB);
        imgB.addActionListener(this);
        
        
        zoomBtn=new JButton("Reset zoom");
        sl_imagePanel.putConstraint(SpringLayout.WEST, zoomBtn, 5, SpringLayout.EAST, imgA);
        sl_imagePanel.putConstraint(SpringLayout.EAST, zoomBtn, -5, SpringLayout.EAST, imagePanel);
        zoomBtn.setFont(font12);
        sl_imagePanel.putConstraint(SpringLayout.NORTH, zoomBtn, 0, SpringLayout.NORTH, imgA);
        sl_imagePanel.putConstraint(SpringLayout.SOUTH, zoomBtn, 0, SpringLayout.SOUTH, imgB);
        imagePanel.add(zoomBtn);
        zoomBtn.addActionListener(this);
        
        //------------- Handles analysis choices -------------
        JPanel analysisPanel = new JPanel();
        sl_contentPane.putConstraint(SpringLayout.NORTH, analysisPanel, 0, SpringLayout.SOUTH, imagePanel);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, analysisPanel, 125, SpringLayout.SOUTH, imagePanel);
        analysisPanel.setFont(font12);
        analysisPanel.setLayout(new GridLayout(5, 2, 0, 0));
		analysisPanel.setBorder(new TitledBorder(null, "Analysis to perform", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		sl_contentPane.putConstraint(SpringLayout.WEST, analysisPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, analysisPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(analysisPanel);
		
		PearsonCh=new JCheckBox("Pearson's coefficient", PearsonBool);
		PearsonCh.setFont(font12);
		OverlapCh=new JCheckBox("Overlap coeff., k1 & k2", OverlapBool);
		OverlapCh.setFont(font12);
		MMCh=new JCheckBox("M1 & M2 coefficients", MMBool);
		MMCh.setFont(font12);
		CostesThrCh=new JCheckBox("Costes' automatic threshold", CostesThrBool);
		CostesThrCh.setFont(font12);
		CCFCh=new JCheckBox("Van Steensel's CCF", CCFBool);
		CCFCh.setFont(font12);
		CytoCh=new JCheckBox("Cytofluorogram", CytoBool);
		CytoCh.setFont(font12);
		ICACh=new JCheckBox("Li's ICA", ICABool);
		ICACh.setFont(font12);
		CostesRandCh=new JCheckBox("Costes' randomization", CostesRandBool);
		CostesRandCh.setFont(font12);
		ObjCh=new JCheckBox("Objects based methods", ObjBool);
		ObjCh.setFont(font12);
        
        
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
		
        
      //------------- Parameters panel -------------
		paramPane = new JTabbedPane(JTabbedPane.BOTTOM);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, paramPane, 425, SpringLayout.NORTH, analysisPanel);
		paramPane.setBorder(new TitledBorder(null, "Parameters", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		sl_contentPane.putConstraint(SpringLayout.NORTH, paramPane, 0, SpringLayout.SOUTH, analysisPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, paramPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, paramPane, 0, SpringLayout.EAST, contentPane);
		contentPane.add(paramPane);
		paramPane.setFont(font11);
		
		
		//------------- About panel -------------
		JPanel aboutPanel = new JPanel();
		aboutPanel.setFont(font12);
		paramPane.addTab("About", null, aboutPanel, null);
		SpringLayout sl_aboutPanel = new SpringLayout();
		aboutPanel.setLayout(sl_aboutPanel);
		
		JTextArea aboutTxt = new JTextArea();
		sl_aboutPanel.putConstraint(SpringLayout.SOUTH, aboutTxt, -70, SpringLayout.SOUTH, aboutPanel);
		aboutTxt.setFont(font12);
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, aboutTxt, 5, SpringLayout.NORTH, aboutPanel);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, aboutTxt, 5, SpringLayout.WEST, aboutPanel);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, aboutTxt, -5, SpringLayout.EAST, aboutPanel);
		aboutTxt.setText("Please refer to and cite:\n\nBolte S, Cordelieres FP.\nA guided tour into subcellular colocalization analysis \nin light microscopy. J Microsc. 2006;224:213-32.\n\nDownloadable from:\nhttps://doi.org/10.1111/j.1365-2818.2006.01706.x");
		aboutTxt.setLineWrap(true);
		aboutTxt.setEditable(false);
		aboutTxt.setBackground(getBackground());
		aboutPanel.add(aboutTxt);
		
		jmiBtn = new JButton("Go to Journal of Microscopy");
		sl_aboutPanel.putConstraint(SpringLayout.NORTH, jmiBtn, 5, SpringLayout.SOUTH, aboutTxt);
		sl_aboutPanel.putConstraint(SpringLayout.WEST, jmiBtn, 25, SpringLayout.WEST, aboutPanel);
		sl_aboutPanel.putConstraint(SpringLayout.EAST, jmiBtn, -25, SpringLayout.EAST, aboutPanel);
		jmiBtn.setFont(font12);
		jmiBtn.addActionListener(this);
		aboutPanel.add(jmiBtn);
		
		//------------- Threshold panel -------------
		JPanel thresholdPanel = new JPanel();
		thresholdPanel.setFont(font12);
		paramPane.addTab("Threshold", null, thresholdPanel, null);
		thresholdPanel.setLayout(new GridLayout(3, 3, 0, 0));
		
		JLabel thrALabel = new JLabel("Threshold Image A");
		thrALabel.setFont(font12);
		thresholdPanel.add(thrALabel);
		
		thrImgALab=new JLabel();
		thrImgALab.setFont(font12);
		thresholdPanel.add(thrImgALab);
		
		thrImgA=new JSlider();
		thrImgA.setFont(font12);
		thrImgA.addChangeListener(this);
		thrImgA.setMinimum(0);
		thrImgA.setMaximum(0);
		thrImgA.setValue(0);
		thresholdPanel.add(thrImgA);
		
		thrBLabel = new JLabel("Threshold Image B");
		thrBLabel.setFont(font12);
		thresholdPanel.add(thrBLabel);
		
		thrImgBLab=new JLabel();
		thrImgBLab.setFont(font12);
		thresholdPanel.add(thrImgBLab);
		
		thrImgB=new JSlider();
		thrImgB.addChangeListener(this);
		thrImgB.setMinimum(0);
		thrImgB.setMaximum(0);
		thrImgB.setValue(0);
		thresholdPanel.add(thrImgB);
		
		JLabel sliceLabel = new JLabel("Slice");
		sliceLabel.setFont(font12);
		thresholdPanel.add(sliceLabel);
		
		thrSliceLab=new JLabel();
		thrSliceLab.setFont(font12);
		thresholdPanel.add(thrSliceLab);
		
		thrSlice=new JSlider();
		thrSlice.addChangeListener(this);
		thrSlice.setMinimum(1);
		thrSlice.setMaximum(1);
		thrSlice.setValue(1);
		thresholdPanel.add(thrSlice);
		
		//------------- CCF panel -------------
		JPanel ccfPanel = new JPanel();
		ccfPanel.setFont(font12);
		paramPane.addTab("CCF", null, ccfPanel, null);
		SpringLayout sl_ccfPanel = new SpringLayout();
		ccfPanel.setLayout(sl_ccfPanel);
		JLabel shiftLabel = new JLabel("x shift");
		sl_ccfPanel.putConstraint(SpringLayout.NORTH, shiftLabel, 95, SpringLayout.NORTH, ccfPanel);
		sl_ccfPanel.putConstraint(SpringLayout.WEST, shiftLabel, 130, SpringLayout.WEST, ccfPanel);
		shiftLabel.setFont(font12);
		ccfPanel.add(shiftLabel);
		CCFshiftTxt=new JFormattedTextField(noDigit);
		CCFshiftTxt.setFont(font12);
		sl_ccfPanel.putConstraint(SpringLayout.NORTH, CCFshiftTxt, -5, SpringLayout.NORTH, shiftLabel);
		sl_ccfPanel.putConstraint(SpringLayout.WEST, CCFshiftTxt, 50, SpringLayout.WEST, shiftLabel);
		sl_ccfPanel.putConstraint(SpringLayout.EAST, CCFshiftTxt, 108, SpringLayout.WEST, shiftLabel);
		CCFshiftTxt.setValue(xShift);
		ccfPanel.add(CCFshiftTxt);
		
		//------------- Micro panel -------------
		JPanel microPanel = new JPanel();
		microPanel.setFont(font12);
		paramPane.addTab("Micro.", null, microPanel, null);
		SpringLayout sl_microPanel = new SpringLayout();
		microPanel.setLayout(sl_microPanel);
		
		JPanel scope1 = new JPanel(new GridLayout(1,2));
		sl_microPanel.putConstraint(SpringLayout.SOUTH, scope1, 40, SpringLayout.NORTH, microPanel);
		scope1.setFont(font12);
		sl_microPanel.putConstraint(SpringLayout.NORTH, scope1, 5, SpringLayout.NORTH, microPanel);
		sl_microPanel.putConstraint(SpringLayout.WEST, scope1, 5, SpringLayout.WEST, microPanel);
		sl_microPanel.putConstraint(SpringLayout.EAST, scope1, -5, SpringLayout.EAST, microPanel);
		
		scopeTypeRadio = new ButtonGroup();
		
		wfRadio=new JRadioButton("Wide-Field", scopeType);
		wfRadio.setFont(font12);
		wfRadio.addItemListener(this);
		scopeTypeRadio.add(wfRadio);
		scope1.add(wfRadio);
		confRadio=new JRadioButton("Confocal", !scopeType);
		confRadio.setFont(font12);
		scopeTypeRadio.add(confRadio);
		scope1.add(confRadio);
		
		microPanel.add(scope1);
		
		JPanel scope2 = new JPanel();
		sl_microPanel.putConstraint(SpringLayout.SOUTH, scope2, 25, SpringLayout.SOUTH, scope1);
		scope2.setFont(font12);
		sl_microPanel.putConstraint(SpringLayout.NORTH, scope2, 0, SpringLayout.SOUTH, scope1);
		sl_microPanel.putConstraint(SpringLayout.WEST, scope2, 5, SpringLayout.WEST, microPanel);
		sl_microPanel.putConstraint(SpringLayout.EAST, scope2, -5, SpringLayout.EAST, microPanel);
		
		scopeImgA=new JButton("Get calib. from ImgA");
		scopeImgA.setFont(font12);
		scopeImgA.setPreferredSize(new Dimension (150, 15));
		scopeImgA.addActionListener(this);
		scope2.add(scopeImgA);
		scopeImgB=new JButton("Get calib. from ImgB");
		scopeImgB.setFont(font12);
		scopeImgB.setPreferredSize(new Dimension (150, 15));
		scopeImgB.addActionListener(this);
		scope2.add(scopeImgB);
		
		microPanel.add(scope2);
		
		JPanel scope3 = new JPanel(new GridLayout(2,2));
		sl_microPanel.putConstraint(SpringLayout.SOUTH, scope3, 46, SpringLayout.SOUTH, scope2);
		scope3.setFont(font12);
		sl_microPanel.putConstraint(SpringLayout.NORTH, scope3, 0, SpringLayout.SOUTH, scope2);
		sl_microPanel.putConstraint(SpringLayout.WEST, scope3, 5, SpringLayout.WEST, microPanel);
		sl_microPanel.putConstraint(SpringLayout.EAST, scope3, -5, SpringLayout.EAST, microPanel);
		
		xyCalibTxt=new JFormattedTextField(noDigit);
		xyCalibTxt.setFont(font12);
		xyCalibTxt.setValue(xyCalib);
		xyCalibTxt.addPropertyChangeListener("value", this);
		zCalibTxt=new JFormattedTextField(noDigit);
		zCalibTxt.setFont(font12);
		zCalibTxt.setValue(zCalib);
		zCalibTxt.addPropertyChangeListener("value", this);
		JLabel xyCalibLabel = new JLabel("xy calib (nm)");
		xyCalibLabel.setFont(font12);
		scope3.add(xyCalibLabel);
		scope3.add(xyCalibTxt);
		JLabel zCalibLabel = new JLabel("z calib (nm)");
		zCalibLabel.setFont(font12);
		scope3.add(zCalibLabel);
		scope3.add(zCalibTxt);
		
		microPanel.add(scope3);
		
		JPanel scope4 = new JPanel();
		sl_microPanel.putConstraint(SpringLayout.SOUTH, scope4, 25, SpringLayout.SOUTH, scope3);
		scope4.setFont(font12);
		sl_microPanel.putConstraint(SpringLayout.NORTH, scope4, 0, SpringLayout.SOUTH, scope3);
		sl_microPanel.putConstraint(SpringLayout.WEST, scope4, 5, SpringLayout.WEST, microPanel);
		sl_microPanel.putConstraint(SpringLayout.EAST, scope4, -5, SpringLayout.EAST, microPanel);
		
		scopeSet=new JButton("Write calib. to images");
		scopeSet.setFont(font12);
		scopeSet.setPreferredSize(new Dimension (160, 15));
		scopeSet.addActionListener(this);
		scope4.add(scopeSet);
		
		microPanel.add(scope4);
		
		
		JPanel scope5 = new JPanel(new GridLayout(4,2));
		scope5.setFont(font12);
		sl_microPanel.putConstraint(SpringLayout.NORTH, scope5, 0, SpringLayout.SOUTH, scope4);
		sl_microPanel.putConstraint(SpringLayout.WEST, scope5, 5, SpringLayout.WEST, microPanel);
		sl_microPanel.putConstraint(SpringLayout.SOUTH, scope5, -5, SpringLayout.SOUTH, microPanel);
		sl_microPanel.putConstraint(SpringLayout.EAST, scope5, -5, SpringLayout.EAST, microPanel);
		
		JLabel waveALabel = new JLabel("Wavelength A (nm)");
		waveALabel.setFont(font12);
		scope5.add(waveALabel);
		
		waveATxt=new JFormattedTextField(noDigit);
		waveATxt.setFont(font12);
		waveATxt.setValue(waveA);
		scope5.add(waveATxt);
		
		JLabel waveBLabel = new JLabel("Wavelength B (nm)");
		waveBLabel.setFont(font12);
		scope5.add(waveBLabel);
		
		waveBTxt=new JFormattedTextField(noDigit);
		waveBTxt.setFont(font12);
		waveBTxt.setValue(waveB);
		scope5.add(waveBTxt);
		
		JLabel naLabel = new JLabel("NA");
		naLabel.setFont(font12);
		scope5.add(naLabel);
		
		naTxt=new JFormattedTextField(threeDigits);
		naTxt.setFont(font12);
		naTxt.setValue(na);
		scope5.add(naTxt);
		
		JLabel riLabel = new JLabel("Refractive index");
		riLabel.setFont(font12);
		scope5.add(riLabel);
		
		RITxt=new JFormattedTextField(threeDigits);
		RITxt.setFont(font12);
		RITxt.setValue(ri);
		scope5.add(RITxt);
		
		microPanel.add(scope5);
		
		//------------- Costes' randomization panel -------------
		JPanel costesPanel = new JPanel();
		costesPanel.setFont(font12);
		paramPane.addTab("Costes' rand°", null, costesPanel, null);
		SpringLayout sl_costesPanel = new SpringLayout();
		costesPanel.setLayout(sl_costesPanel);
		
		
		JPanel CostesRand1=new JPanel(new GridLayout(4,2));
		sl_costesPanel.putConstraint(SpringLayout.NORTH, CostesRand1, 5, SpringLayout.NORTH, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.WEST, CostesRand1, 5, SpringLayout.WEST, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.SOUTH, CostesRand1, 95, SpringLayout.NORTH, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.EAST, CostesRand1, -5, SpringLayout.EAST, costesPanel);
		CostesRand1.setFont(font12);
		CostesRand1.setPreferredSize(new Dimension(380,95));
		
		JLabel xyBlockLabel = new JLabel("xy block size (pix.)");
		xyBlockLabel.setFont(font12);
		CostesRand1.add(xyBlockLabel);
		CostesxyTxt=new JFormattedTextField(noDigit);
		CostesxyTxt.setFont(font12);
		CostesxyTxt.setValue(2);
		CostesRand1.add(CostesxyTxt);
		JLabel zBlockLabel = new JLabel("z block size (pix.)");
		zBlockLabel.setFont(font12);
		CostesRand1.add(zBlockLabel);
		CosteszTxt=new JFormattedTextField(noDigit);
		CosteszTxt.setFont(font12);
		CosteszTxt.setValue(1);
		CostesRand1.add(CosteszTxt);
		JLabel nRandLabel = new JLabel("Nb of random. rounds");
		nRandLabel.setFont(font12);
		CostesRand1.add(nRandLabel);
		CostesRandRoundTxt=new JFormattedTextField(noDigit);
		CostesRandRoundTxt.setFont(font12);
		CostesRandRoundTxt.setValue(nbRand);
		CostesBinTxt=new JFormattedTextField(threeDigits);
		CostesBinTxt.setFont(font12);
		CostesBinTxt.setValue(binWidth);
		CostesRand1.add(CostesRandRoundTxt);
		JLabel binWidthLabel = new JLabel("Bin width");
		binWidthLabel.setFont(font12);
		CostesRand1.add(binWidthLabel);
		CostesRand1.add(CostesBinTxt);
		costesPanel.add(CostesRand1);
		
      
		JPanel CostesRand2=new JPanel(new GridLayout(1,2));
		sl_costesPanel.putConstraint(SpringLayout.NORTH, CostesRand2, 0, SpringLayout.SOUTH, CostesRand1);
		sl_costesPanel.putConstraint(SpringLayout.WEST, CostesRand2, 5, SpringLayout.WEST, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.SOUTH, CostesRand2, 50, SpringLayout.SOUTH, CostesRand1);
		sl_costesPanel.putConstraint(SpringLayout.EAST, CostesRand2, -5, SpringLayout.EAST, costesPanel);
		CostesRand2.setFont(font12);
		CostesRand2.setPreferredSize(new Dimension(380,25));
		JLabel fitBlockLabel = new JLabel("Image fitting to block's size");
		fitBlockLabel.setFont(font12);
		CostesRand2.add(fitBlockLabel);
		CostesFit=new JComboBox(fitMethList);
		CostesFit.setFont(font12);
		CostesFit.setSelectedIndex(fitMeth);
		CostesRand2.add(CostesFit);
		costesPanel.add(CostesRand2);
		
		JPanel CostesRand3=new JPanel(new GridLayout(3,1));
		sl_costesPanel.putConstraint(SpringLayout.NORTH, CostesRand3, 0, SpringLayout.SOUTH, CostesRand2);
		sl_costesPanel.putConstraint(SpringLayout.WEST, CostesRand3, 5, SpringLayout.WEST, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.SOUTH, CostesRand3, -5, SpringLayout.SOUTH, costesPanel);
		sl_costesPanel.putConstraint(SpringLayout.EAST, CostesRand3, -5, SpringLayout.EAST, costesPanel);
		CostesRand3.setFont(font12);
		CostesRand3.setPreferredSize(new Dimension(380,55));
		CostesSlicesIndpCh=new JCheckBox("Slices to be considered as independent");
		CostesSlicesIndpCh.setFont(font12);
		CostesSlicesIndpCh.setSelected(xyRand);
		CostesRand3.add(CostesSlicesIndpCh);
		CostesSlicesIndpCh.addItemListener(this);
		CosteszRandCh=new JCheckBox("z randomization as well");
		CosteszRandCh.setFont(font12);
		CosteszRandCh.setSelected(zRand);
		CostesRand3.add(CosteszRandCh);
		CostesShowLastCh=new JCheckBox("Show last randomized image");
		CostesShowLastCh.setFont(font12);
		CostesShowLastCh.setSelected(showRand);
		CostesRand3.add(CostesShowLastCh);
		costesPanel.add(CostesRand3);
		
		if (CostesSlicesIndpCh.isSelected()) CosteszRandCh.setEnabled(false);
		
		//------------- Objects panel -------------
		JPanel objPanel = new JPanel();
		objPanel.setFont(font12);
		paramPane.addTab("Obj.", null, objPanel, null);
        
		JPanel Obj1=new JPanel(new GridLayout(2,2));
        Obj1.setFont(font12);
        ButtonGroup centreTypeRadio = new ButtonGroup();
        cMassRadio=new JRadioButton("Centre of mass", cMass);
        cMassRadio.setFont(font12);
        centreTypeRadio.add(cMassRadio);
        cGeoRadio=new JRadioButton("Geometrical centre", !cMass);
        cGeoRadio.setFont(font12);
        Obj1.add(cMassRadio);
        centreTypeRadio.add(cGeoRadio);
        Obj1.add(cGeoRadio);
        
        ButtonGroup tableTypeRadio = new ButtonGroup();
        SpringLayout sl_objPanel = new SpringLayout();
        sl_objPanel.putConstraint(SpringLayout.NORTH, Obj1, 5, SpringLayout.NORTH, objPanel);
        sl_objPanel.putConstraint(SpringLayout.WEST, Obj1, 5, SpringLayout.WEST, objPanel);
        sl_objPanel.putConstraint(SpringLayout.SOUTH, Obj1, 50, SpringLayout.NORTH, objPanel);
        sl_objPanel.putConstraint(SpringLayout.EAST, Obj1, -5, SpringLayout.EAST, objPanel);
        objPanel.setLayout(sl_objPanel);
        fullRadio=new JRadioButton("Show full table", fullList);
        fullRadio.setFont(font12);
        tableTypeRadio.add(fullRadio);
        Obj1.add(fullRadio);
        colocRadio=new JRadioButton("Show coloc° only table", !fullList);
        colocRadio.setFont(font12);
        tableTypeRadio.add(colocRadio);
        Obj1.add(colocRadio);
        
        objPanel.add(Obj1);
        
        JPanel Obj2=new JPanel(new GridLayout(2,2));
        Obj2.setFont(font12);
        sl_objPanel.putConstraint(SpringLayout.NORTH, Obj2, 5, SpringLayout.SOUTH, Obj1);
        sl_objPanel.putConstraint(SpringLayout.WEST, Obj2, 5, SpringLayout.WEST, objPanel);
        sl_objPanel.putConstraint(SpringLayout.SOUTH, Obj2, 50, SpringLayout.SOUTH, Obj1);
        sl_objPanel.putConstraint(SpringLayout.EAST, Obj2, -5, SpringLayout.EAST, objPanel);
        minSizeTxt=new JFormattedTextField(noDigit);
        minSizeTxt.setFont(font12);
        minSizeTxt.setText(""+minSize);
        maxSizeTxt=new JFormattedTextField(noDigit);
        maxSizeTxt.setFont(font12);
        maxSizeTxt.setText("1");
        JLabel minSizeLabel = new JLabel("Min. particle size (pix.)");
        minSizeLabel.setFont(font12);
        Obj2.add(minSizeLabel);
        Obj2.add(minSizeTxt);
        JLabel maxSizeLabel = new JLabel("Max. particle size (pix.)");
        maxSizeLabel.setFont(font12);
        Obj2.add(maxSizeLabel);
        Obj2.add(maxSizeTxt);
        objPanel.add(Obj2);
        
        JPanel Obj3=new JPanel(new GridLayout(4,1));
        sl_objPanel.putConstraint(SpringLayout.NORTH, Obj3, 5, SpringLayout.SOUTH, Obj2);
        sl_objPanel.putConstraint(SpringLayout.WEST, Obj3, 5, SpringLayout.WEST, objPanel);
        sl_objPanel.putConstraint(SpringLayout.SOUTH, Obj3, -5, SpringLayout.SOUTH, objPanel);
        sl_objPanel.putConstraint(SpringLayout.EAST, Obj3, -5, SpringLayout.EAST, objPanel);
        Obj3.setFont(font12);
        distCentCh=new JCheckBox("Work on distances between centres");
        distCentCh.setFont(font12);
        distCentCh.setSelected(workDist);
        distCentCh.addItemListener(this);
        Obj3.add(distCentCh);
        
        distShowCentCh=new JCheckBox("Show centres map");
        distShowCentCh.setFont(font12);
        distShowCentCh.setSelected(showCent);
        if (!distCentCh.isSelected()) distShowCentCh.setEnabled(false);
        Obj3.add(distShowCentCh);
        
        centPartCh=new JCheckBox("Work on centres-particles coincidence");
        centPartCh.setFont(font12);
        centPartCh.setSelected(workCentPart);
        centPartCh.addItemListener(this);
        Obj3.add(centPartCh);
        
        showCentPartCh=new JCheckBox("Show centres-particles map");
        showCentPartCh.setFont(font12);
        showCentPartCh.setSelected(showCentPart);
        if (!centPartCh.isSelected()) showCentPartCh.setEnabled(false);
        Obj3.add(showCentPartCh);
        
        objPanel.add(Obj3);
		
		
		
		//------------- Launch panel -------------
		JPanel launchPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, launchPanel, 0, SpringLayout.SOUTH, paramPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, launchPanel, 0, SpringLayout.SOUTH, contentPane);
		launchPanel.setFont(font12);
		sl_contentPane.putConstraint(SpringLayout.WEST, launchPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, launchPanel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(launchPanel);
		SpringLayout sl_launchPanel = new SpringLayout();
		launchPanel.setLayout(sl_launchPanel);
		
		warningTxt = new JLabel();
		warningTxt.setFont(font12);
		warningTxt.setHorizontalAlignment(SwingConstants.CENTER);
		sl_launchPanel.putConstraint(SpringLayout.NORTH, warningTxt, 5, SpringLayout.NORTH, launchPanel);
		sl_launchPanel.putConstraint(SpringLayout.WEST, warningTxt, 0, SpringLayout.WEST, launchPanel);
		sl_launchPanel.putConstraint(SpringLayout.EAST, warningTxt, 0, SpringLayout.EAST, launchPanel);
		warningTxt.setText("Please check red labeled tabs before launching analysis");
		warningTxt.setForeground(Color.RED);
		warningTxt.setBackground(getBackground());
		launchPanel.add(warningTxt);
		
		analyzeBtn = new JButton("Analyze");
		sl_launchPanel.putConstraint(SpringLayout.NORTH, analyzeBtn, 5, SpringLayout.SOUTH, warningTxt);
		sl_launchPanel.putConstraint(SpringLayout.WEST, analyzeBtn, 150, SpringLayout.WEST, launchPanel);
		sl_launchPanel.putConstraint(SpringLayout.SOUTH, analyzeBtn, -5, SpringLayout.SOUTH, launchPanel);
		sl_launchPanel.putConstraint(SpringLayout.EAST, analyzeBtn, -150, SpringLayout.EAST, launchPanel);
		analyzeBtn.setFont(font12);
		analyzeBtn.addActionListener(this);
		launchPanel.add(analyzeBtn);
		paramPane.addChangeListener(this);
		
		
		
		
		//------------- Initialize everything -------------
		ImagePlus.addImageListener(this);
        addWindowListener(this);
        resetArrays();
        updateImgList(null);
        imgA.setSelectedIndex(0);
        imgB.setSelectedIndex(1);
        resetThr();
        if (nbImg!=0) adaptZoom();
        updateTicked();
        setVisible(true);
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
            if(calib.getUnit().equals("µm")){
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
            paramPane.setForegroundAt(1, Color.RED);
        }else{
        	paramPane.setForegroundAt(1, Color.BLACK);
        }
        
        if (CCFBool){
        	paramPane.setForegroundAt(2, Color.RED);
        }else{
        	paramPane.setForegroundAt(2, Color.BLACK);
        }
        
        if (CostesRandBool){
        	paramPane.setForegroundAt(3, Color.RED);
        	paramPane.setForegroundAt(4, Color.RED);
        }else{
        	paramPane.setForegroundAt(4, Color.BLACK);
            if (!ObjBool) paramPane.setForegroundAt(3, Color.BLACK);
        }
        
        if (ObjBool){
        	paramPane.setForegroundAt(3, Color.RED);
        	paramPane.setForegroundAt(5, Color.RED);
        }else{
            if (!CostesRandBool) paramPane.setForegroundAt(3, Color.BLACK);
            paramPane.setForegroundAt(5, Color.BLACK);
        }
        
    }
    
    public void adaptZoom(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight=(int) screenSize.getHeight();
        if (!((String) imgA.getSelectedItem()).equals("[No image]") && !((String) imgB.getSelectedItem()).equals("[No image]")){
            ImageWindow iwA=WindowManager.getImage((String) imgA.getSelectedItem()).getWindow();
            ImageWindow iwB=WindowManager.getImage((String) imgB.getSelectedItem()).getWindow();

            int width=(int) (screenSize.getWidth()-getWidth()-getX())/2;
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


            iwA.setLocationAndSize(getWidth()+getX(), screenHeight/2-heightA/2, widthA, heightA);
            iwB.setLocationAndSize(getWidth()+getX()+iwA.getWidth(), screenHeight/2-heightB/2, widthB, heightB);

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
        }else if (unit.equals("µm") || unit.equals("micron") || unit.equals("um")){
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
        cal.setUnit("µm");
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
        
        //If img is null, builds the initial image list, otherwise simply countes the number of images
        if (WindowManager.getImageCount()!=0){
            int[] IDList=WindowManager.getIDList();
            for (int i=0;i<IDList.length;i++){
                ImagePlus currImg=WindowManager.getImage(IDList[i]);
                if (currImg.getBitDepth()!=24 && currImg.getBitDepth()!=32 && !currImg.isComposite()){
                    nbImg++;
                    if(img==null) addImgToList(currImg);
                }
            }
        }
        
        if (nbImg<2){
            paramPane.setSelectedIndex(0);
            paramPane.setEnabled(false);
            analyzeBtn.setEnabled(false);
            warningTxt.setText("JACoP requires 2 img: works on 8/16-bits, not on color/composite");
        }else{
            paramPane.setEnabled(true);
            analyzeBtn.setEnabled(true);
            warningTxt.setText("Please check red labeled tabs before launching analysis");
        }
        
        if (((ImgInfo) info.elementAt(1)).title.equals("[No image]")) info.removeElementAt(1);
        if (((ImgInfo) info.elementAt(0)).title.equals("[No image]")) info.removeElementAt(0);
         
        if(img!=null) if (nbImg>oldNbImg && img.isVisible()) addImgToList(img);
       
            
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
    	if(img.isVisible()) {
	        int min=65535, max=0;
	        for (int i=1; i<=img.getNSlices(); i++){
	            img.setSlice(i);
	            min=Math.min(min, (int) img.getStatistics().min);
	            max=Math.max(max, (int) img.getStatistics().max);
	        }
	        info.addElement(new ImgInfo(img.getTitle(), min, max, img.getProcessor().getAutoThreshold()));
    	}
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
        if (paramPane.getSelectedIndex()==1 && nbImg>1){
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
        if (origin==jmiBtn){
            try {BrowserLauncher.openURL("https://doi.org/10.1111/j.1365-2818.2006.01706.x");}
            catch (IOException ie) {}
        }
        
        
        if (origin==analyzeBtn) doZeJob(e.getModifiers()==17?true:false);
        
        if (origin==zoomBtn){
            if (adaptedZoom){
                resetZoom();
                zoomBtn.setText("Adapt zoom");
            }else{
                adaptZoom();
                zoomBtn.setText("Reset zoom");
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
        
        if (checked && paramPane.isEnabled()){
            int tabSelect=paramPane.getSelectedIndex();
            //if (origin==PearsonCh) tabSelect=1;
            if (origin==OverlapCh) tabSelect=1;
            if (origin==MMCh) tabSelect=1;
            if (origin==CostesThrCh) tabSelect=1;
            if (origin==CCFCh) tabSelect=2;
            //if (origin==ICACh) tabSelect=1;
            if (origin==CostesRandCh) tabSelect=4;
            if (origin==ObjCh) tabSelect=5;
            paramPane.setSelectedIndex(tabSelect);
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
        System.out.println("opened "+imp.getTitle());
    }
    
    public void imageClosed(ImagePlus imp){
    	updateImgList(imp);
    }
    
    public void imageUpdated(ImagePlus imp){}
    
    public void stateChanged(ChangeEvent e) {
        Object origin=e.getSource();
        
        if (origin==paramPane && paramPane.getSelectedIndex()==1){
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
