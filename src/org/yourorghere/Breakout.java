package org.yourorghere;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.TextureIO;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class Breakout implements GLEventListener, KeyListener {
    
    //Start Key (SPACE)
    static boolean start,end=false;
    static boolean newGame= true;
    
    //Window Dimensions
    private static final int HEIGHT=1000;
    private static final int WIDTH=720;
    
    //Margin
    private static final float MARGIN_Y = 15;
    private static final float MARGIN_X = 15f;
    
    //Blocks Dimentions
    private static final float Dim_Y = 50;
    private static final float Dim_X = 70;
    private static final float Right_Margin=3f;
    private static final float Bottom_Margin=4f;
    private static final float Block_X= Dim_X+2*Right_Margin;
    private static final float Block_Y = Dim_Y+2*Bottom_Margin;
    
    //Game Structure
    private static final int rows = 4;
    private static final int cols = (int)((WIDTH-2*MARGIN_X)/Block_X);   
    private static  Blocks[][] BLOCKS = new Blocks[rows][cols];
    private static Controller player;
    private static Ball ball;
    private static int direction =0;
    private static float step=3.5f;  
    private static boolean gameOver=false;
    private int texture1; //blocks
    private int texture2;//winner
    private final int  totalBlocks= rows*cols;
    static private int brokenBlocks=0;
    private static boolean firstGame=true;
    
    public static void main(String[] args) throws UnsupportedAudioFileException,
            LineUnavailableException, IOException {
        
        Frame frame = new Frame("Breakout");
        GLCanvas canvas = new GLCanvas();

        canvas.addGLEventListener(new Breakout());
        frame.add(canvas);
        frame.setSize(WIDTH, HEIGHT);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
        
        //Start  a new game
        newGame();
        
          
    }

    public void init(GLAutoDrawable drawable) {
        
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);
        drawable.addKeyListener(this); //FOR KEY LISTENER
        // Setup the drawing area and shading mode
        gl.glClearColor( 0.996f, 0.654f, 0.552f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
        
        
        // image for block texture & winner
		try{
			File img = new File("images/block.jpg");
			Texture t = TextureIO.newTexture(img,true);
			texture1 = t.getTextureObject();
                       
		}
		catch(IOException e){
			e.printStackTrace();
		}
                
                try{
			File img = new File("images/winner.jpg");
			Texture t = TextureIO.newTexture(img,true);
			texture2 = t.getTextureObject();
                       
		}
		catch(IOException e){
			e.printStackTrace();
		}
        
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        
        
        if (height <= 0) { // avoid a divide by zero error!
        
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, WIDTH, HEIGHT,0, -1,1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        

       
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, WIDTH, HEIGHT,0, -1,1);
       
        

        gl.glBegin(GL.GL_QUADS);
                
            //Background
            gl.glColor3f( 0.996f, 0.792f, 0.729f); 
            gl.glVertex3f(1000f,0f,0f); 
            gl.glVertex3f(0f,0f,0f);

            gl.glColor3f( 0.996f, 0.654f, 0.552f); 
            gl.glVertex3f(0f,1800f,0f); 
            gl.glVertex3f(1000f,1800f,0f);
        
        gl.glEnd();
      

        
        //TO ENABLE TEXTURE
        gl.glEnable(GL.GL_TEXTURE_2D); 
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture1);
        
        
        
        gl.glBegin(GL.GL_QUADS);
            //This loop draws the blocks
            for(int i=0; i<rows;i++){  
                for(int j=0; j<cols; j++){

                    if(! BLOCKS[i][j].isBroken()){
                        float[][] current_block = BLOCKS[i][j].getCoord();
                            gl.glTexCoord2f(0.0f, 1.0f);
                        gl.glVertex3f(current_block[0][0], current_block[0][1], 0.0f); 
                            gl.glTexCoord2f(0.0f, 0.0f);
                        gl.glVertex3f(current_block[1][0], current_block[1][1], 0.0f);
                            gl.glTexCoord2f(1.0f, 0.0f);   
                        gl.glVertex3f(current_block[2][0], current_block[2][1], 0.0f); 
                            gl.glTexCoord2f(1.0f, 1.0f);
                        gl.glVertex3f(current_block[3][0], current_block[3][1], 0.0f); 

                    }
                }
            }
        gl.glEnd();
         
        // TO DISABLE TEXTURE   
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        gl.glDisable(GL.GL_TEXTURE_2D);
        
       
        gl.glBegin(GL.GL_QUADS);
            // Draws player stick
            float [][] position = player.getCoord(); 

            gl.glColor3f(0.952f, 0.494f, 0.376f);
            gl.glVertex3f(position[0][0], position[0][1], 0.0f);  
            gl.glVertex3f(position[1][0], position[1][1], 0.0f);  
            gl.glVertex3f(position[2][0], position[2][1], 0.0f);  
            gl.glVertex3f(position[3][0], position[3][1], 0.0f); 
        
        gl.glEnd();
        
        //Draws ball
        gl.glColor3f(0.270f, 0.341f, 0.670f);
        Support_Lib.drawFilledCircle(ball.getCenterX(),ball.getCenterY() , ball.getRadius(), drawable, WIDTH, HEIGHT);
        
        //respond to user input
        player.move(direction*step);
        
        
        //update ball position
        if(start)   ball.update();
        
        //before the ball start to move user must press space
        if(!start && firstGame){
             String text="Press Space to Start";
            drawText(text, text.length(), (WIDTH/2)-60, (HEIGHT/4)+20, gl);
        }
        
         //This loop check if blocks are broken
        for(int i=0; i<rows;i++){ 
            for(int j=0; j<cols; j++){
                try {
                    if( ! BLOCKS[i][j].isBroken() && BLOCKS[i][j].collision(ball)){
                        BLOCKS[i][j].break_it();
                        brokenBlocks++;
                    }
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Breakout.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Breakout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
           
        //Collision Detection for the controller & ball 
        player.collision(ball);
        
                
        //Acceleration of the ball upon breaking half of the bloacks
        if(brokenBlocks/totalBlocks>=0.5 && brokenBlocks/totalBlocks<0.51)
                ball.setVelocity(7);
         //Acceleration of the ball upon breaking 3/4of the bloacks
        else if(brokenBlocks/totalBlocks>=0.75 && brokenBlocks/totalBlocks<0.76)
                ball.setVelocity(9);
 
        
        if(ball.getCenterY()>980){ //LOST THE GAME , ball has fallen
                gameOver=true;
        } 
        if(gameOver){ // GAME OVER
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
  
            gl.glColor3f(0.270f, 0.341f, 0.670f);

            String text="GAME OVER";
            drawText(text, text.length(), (WIDTH/2)-20, (HEIGHT/4)+20, gl);
            String text2="PRESS ENTER TO PLAY AGAIN";
            drawText(text2, text2.length(), (WIDTH/2)-110, (HEIGHT/4), gl);
            start=false;
                                       
        }
        
        
        if(brokenBlocks==totalBlocks){ //WINNER
           gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glOrtho(0, WIDTH, HEIGHT,0, -1,1);
            

            //TO ENABLE TEXTURE
            gl.glEnable(GL.GL_TEXTURE_2D); 
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture2);
            
             gl.glBegin(GL.GL_QUADS);

                        gl.glTexCoord2f(0.0f, 0.0f);
                    gl.glVertex3f(0, 100, 0.0f);
                        gl.glTexCoord2f(0.0f, 1.0f);
                    gl.glVertex3f(0, 500, 0.0f);
                        gl.glTexCoord2f(1.0f, 1.0f); 
                    gl.glVertex3f(720, 500, 0.0f);
                        gl.glTexCoord2f(1.0f, 0.0f);
                    gl.glVertex3f(720, 100, 0.0f);

             gl.glEnd();
                                         
            // TO DISABLE TEXTURE   
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D);
            
            
            gl.glColor3f(0.270f, 0.341f, 0.670f);
            String text="CONGRATULATIONS";
            drawText(text, text.length(), (WIDTH/2)-65, (HEIGHT/4)+20, gl);
            String text2="YOU'RE A WINNER";
            drawText(text2, text2.length(), (WIDTH/2)-53, (HEIGHT/4), gl);
            
            gl.glColor3f( 1f, 1f, 1f); 
            String text3="PRESS ENTER TO PLAY AGAIN";
            drawText(text3, text3.length(), (WIDTH/2)-110, (HEIGHT/4)-20, gl);
            
            
            start=false;   
        
        }
        
        
           
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
      
    }

    @Override
    public void keyPressed(KeyEvent key) {
        int pressed = key.getKeyCode();
        
        if(pressed==KeyEvent.VK_SPACE){ //start game
            start=true; 
            firstGame=false;
        }
        if(pressed==KeyEvent.VK_ENTER && !start){ //Restart game
            try {
                newGame();
      
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Breakout.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Breakout.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Breakout.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(pressed==KeyEvent.VK_ESCAPE){ //exit game
            System.exit(0);
        }
            if(pressed == KeyEvent.VK_RIGHT){ //Move the controller to the right
                direction =1;
            }
            else if (pressed ==KeyEvent.VK_LEFT){ //Move the controller to the left
                direction =-1;

            }
        
    }
    @Override
    public void keyReleased(KeyEvent ke) {
            direction =0;
    }
    
  
    
    public void drawText(String text, int length, int x, int y, GL gl){
    //This method displays text using glutBitmapCharacter 
        GLU glu = new GLU();

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0,800,0,600,-5,5);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity(); 
        gl.glPushMatrix(); 
        gl.glLoadIdentity();
        GLUT glut = new GLUT(); 
        gl.glRasterPos2i(x,y);
        char[] textarr=text.toCharArray();
        for(int i = 0; i<length; i++){
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, textarr[i]);
        }
        gl.glPopMatrix();
        
    }
    
    public static void newGame() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        
        float currentY = MARGIN_Y;
        
        
        //Blocks construction 
        for (int i=0; i< rows; i++){
            float currentX =MARGIN_X;
            for (int j=0; j<cols; j++){ 
            BLOCKS[i][j] = new Blocks(currentX+Right_Margin,currentY+Bottom_Margin,j,Dim_X,Dim_Y);
            currentX+=Block_X;
            }
            currentY+=Block_Y;
        }

        //Palyer controller construction 
        player = new Controller(100,20);
        
        //ball construction
        ball = new Ball(30,WIDTH/2,3*HEIGHT/5);
        
        //number of broken bloack at the start is 0
        brokenBlocks=0; //reset
        
        gameOver = false;  
            
    }
}


