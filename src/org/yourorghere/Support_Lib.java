package org.yourorghere;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Support_Lib {

    public static void drawFilledCircle(float x, float y, float radius, 
            GLAutoDrawable drawable, float Width, float Height){
        
        GL gl = drawable.getGL();  
        int i;
        int triangleAmount = 40; //# of triangles used to draw circle
        //GLfloat radius = 0.8f; //radius
        float twicePi = (float)(2.0f * Math.PI);
        
        gl.glBegin(GL.GL_TRIANGLE_FAN);
            gl.glVertex2f(x, y); // center of circle
            for(i = 0; i <= triangleAmount;i++) {
                gl.glVertex2f(
                x + (float)(radius * Math.cos((double)i * twicePi / triangleAmount))*((float)(Width/Height)), // WIDTH/HEIGHT
                y + (float)(radius * Math.sin((double)i * twicePi / triangleAmount))*((float)(Width/Height))
                );
            }
        gl.glEnd();
    }

    public static float  clamp(float value, float min, float max){
        return Math.max(min, Math.min(max,value));
    }

    public static void pianoKey(int i) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("audio\\"+i+".wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
    
    
    public static int direction(float x, float y){
        
        //System.out.println(x +""+ y);
        
                              //up right down left
        float [][] compass = {{0,1},{1,0},{0,-1},{-1,0}};
        x=Math.abs(x);
        y=Math.abs(y);
        float max=0.0f;
        int best_di=-1;
        for(int i=0; i<4; i++){
            float dot_product = x*compass[i][0]+y*compass[i][1];
            System.out.println(dot_product);

            if(dot_product>max){
               max=dot_product; 
               best_di=i;
            }
        }
          return best_di;
    }
}
