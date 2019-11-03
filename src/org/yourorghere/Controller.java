
package org.yourorghere;


public class Controller {
    private float[] p1,p2,p3,p4;
    private int dimX,dimY;
    
    public Controller(int dimX,int dimY){
         p1 = new float[2];p2 = new float[2];p3 = new float[2];p4 = new float[2];
         /*
         P1************P4
          *             *
         P2************P3
         */
         p1[0]=720/2-dimX/2; p1[1]=930;
         p2[0]=720/2-dimX/2;p2[1]=p1[1]+dimY;
         p3[0]=720/2+dimX/2; p3[1]=p1[1]+dimY;
         p4[0]=720/2+dimX/2; p4[1]= p1[1];
         
         this.dimX=dimX;
         this.dimY= dimY;
    }
    
    public float[][] getCoord(){
       float[][] coor = {p1,p2,p3,p4};
       return coor;
   }
    
    public void move(float delta){
           p1[0]+=delta;
           if(p1[0]<0)
               p1[0]=0;
           else if(p1[0]>720-dimX)
               p1[0]=720-dimX;
           p2[0]=p1[0];
           p3[0]=p1[0]+dimX;
           p4[0]=p3[0];
    }
       
    public void collision(Ball b){
       
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
       
       //
       float p_X =Support_Lib.clamp(d_X,-extent_X,extent_X);
       float p_Y =Support_Lib.clamp(d_Y,-extent_Y,extent_Y);
      

       
       float closest_point_X = rec_center_X +p_X;
       float closest_point_Y = rec_center_Y +p_Y; 
        
       
       d_X = closest_point_X -cir_center_X;
       d_Y = closest_point_Y -cir_center_Y;
       
       
       
       float length = (float)Math.sqrt((d_X*d_X)+(d_Y*d_Y));
       
       if(length+0.001 <=b.getRadius()){

           
           float ratio  =(closest_point_X-rec_center_X)/(extent_X*2);
           b.accelartion(ratio);
           b.updateVelocity(Support_Lib.direction(d_X,d_Y));
         
       }
       
       
   }
}
