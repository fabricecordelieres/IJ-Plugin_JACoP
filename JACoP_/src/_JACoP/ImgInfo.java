/*
 * ImgInfo.java
 *
 * Created on 14 janvier 2008, 21:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package _JACoP;
import ij.*;
import ij.measure.*;

/**
 *
 * @author Fabrice Cordelières
 */
public class ImgInfo {
    public String title;
    public int min;
    public int max;
    public int thr;
    
    /** Creates a new instance of ImgInfo */
    public ImgInfo() {
        this.title="[No image]";
        this.min=0;
        this.max=0;
        this.thr=0;
    }
    /** Creates a new instance of ImgInfo */
    public ImgInfo(String title, int min, int max, int thr) {
        this.title=title;
        this.min=min;
        this.max=max;
        this.thr=thr;
     }
    
}
