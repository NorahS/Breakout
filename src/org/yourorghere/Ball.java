package org.yourorghere;


public class Ball {
    private float[] center;
    private float r;
    private float V;
    private float[] Vel_com, frontier;
    private int dirction_Y=-1;
    private int dirction_X =1;
    
    public Ball( float r, float centerX,float centerY){
        this.r= r;
        center = new float[2]; center[0]=centerX; center[1]=centerY; 
        this.V= 5.0f;
        Vel_com = new float[2];Vel_com[0]=0;Vel_com[1]=this.V;
        frontier = new float[2]; frontier[0]=centerX; frontier[1]=centerY+this.r;
        
    }
    
    public float[] getCenter(){
        return this.center;
    }
    
    public float getCenterX(){
        return this.center[0];
    }
    
    public float getCenterY(){
        return this.center[1];
    }
    
    public float getVelocity(){
        return this.V;
    }
    
    public void setVelocity(float v){
        this.V=v;
    }
    
    public float getRadius(){
        return this.r;
    }
    
    public void update() {
      center[0]+=Vel_com[0];
      center[1]+=Vel_com[1];
      frontier[0]+=Vel_com[0]; 
      frontier[1]+=Vel_com[1];
      this.withinBoarders();
    }  

    public void updateVelocity(int dir){
        System.out.println(dir);
        if(dir%2==0){
            System.out.println(dir);
            Vel_com[1]*=-1;
        }else{
            Vel_com[0]*=-1;
        }

     }  
    public void accelartion(float r){
         //r = Math.abs(r);
        Vel_com[0]=r*this.V;
        Vel_com[1]=(1-r)*this.V;
     }
    
    public void withinBoarders(){
        float x= frontier[0];
        float y= frontier[1];
        
       if(x+r>=720.0 || x-r<=0.0) Vel_com[0]*=-1;
       else if(y-(r*1.7)<=0 ) Vel_com[1]*=-1;
       
    }


}
