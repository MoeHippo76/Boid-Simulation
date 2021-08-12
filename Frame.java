import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFrame;


public class Frame extends JFrame {
    private Image raster;
    private Graphics rGraphics;
    private final int height;
    private final int width;


    Frame(int height, int width){
        this.height = height;
        this.width = width;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width,height);
        setVisible(true);
        setup();
    }

    public void setup(){
        raster = this.createImage(width,height);
        rGraphics = raster.getGraphics();

        for(int i = 0; i<80; i++){
            Boid b = new Boid(height,width,4f,0.4f,100,Color.cyan);
             Boid.allBoids.add(b);
        }
    }


    public void draw(){
        while(true)
        {
            drawBG();  //draws background
            Boids();
            getGraphics().drawImage(raster,0,0,getWidth(),getHeight(),null);
            try{Thread.sleep(15);}catch(Exception e){}
        }
    }

    private void Boids(){
        for(Boid b : Boid.allBoids){
            b.moveBoid();
            b.drawBoid(rGraphics);
        }
    }

    private void drawBG(){
        rGraphics.setColor(new Color(0, 0, 0));
        rGraphics.fillRect(0,0,width,height);
    }


}
