package org.yourorghere;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



class Blocks {
    
    private int key;
    private float[] p1,p2,p3,p4; 
    private boolean broken ;
    
    public Blocks(float x, float y,int key, float dimx, float dimy) {
        p1 = new float[2];p2 = new float[2];p3 = new float[2];p4 = new float[2];
  
        /*
                P1**********P4
                *           *
                *           *
                P2**********P3
        */
        p1[0]=x;p1[1]=y;
        p2[0]=x;p2[1]=y+dimy;
        p3[0]=x+dimx;p3[1]=y+dimy;
        p4[0]=x+dimx;p4[1]=y;
        
        broken=false;
        //consturct the tone
        this.key = key+1;

        

        this.broken = false;
    }
    
    public float[][] getCoord(){
       float[][] coor = {p1,p2,p3,p4};
       return coor;
    }
    
    public boolean isBroken(){
        return this.broken;
    }
    
    public void break_it(){
           this.broken=true;
    }
    
    public boolean collision(Ball b) throws UnsupportedAudioFileException, IOException{
       
       // get ball center points
       float cir_center_X = b.getCenterX();
       float cir_center_Y = b.getCenterY();
       
       // aabb half extent
       float extent_X = (this.p4[0]-this.p1[0])/2; //half width
       float extent_Y = (this.p3[1]-this.p4[1])/2; // half height
       // rec center 
       float rec_center_X = this.p1[0]+extent_X;
       float rec_center_Y = this.p1[1]+extent_Y;
       
       // difference between center : vector D = Vector Cir_center-Rec-center
       float d_X =cir_center_X-rec_center_X;
       float d_Y = cir_center_Y-rec_center_Y;
       
       //clamped points
       float p_X =Support_Lib.clamp(d_X,-extent_X,extent_X);
       float p_Y =Support_Lib.clamp(d_Y,-extent_Y,extent_Y);
      

       //closest point to the center from the rect
       float closest_point_X = rec_center_X +p_X;
       float closest_point_Y = rec_center_Y +p_Y; 
        
       //the difference vector between the two points
       d_X = closest_point_X -cir_center_X;
       d_Y = closest_point_Y -cir_center_Y;
       
       
       
       float length = (float)Math.sqrt((d_X*d_X)+(d_Y*d_Y));
       
       if(length+0.001 <=b.getRadius()){
            try {
               Support_Lib.pianoKey(this.key);
            } catch (LineUnavailableException ex) {
               Logger.getLogger(Blocks.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            b.updateVelocity(Support_Lib.direction(d_X,d_Y));
            return true;
       }
       
       return false;
   }
    
}
