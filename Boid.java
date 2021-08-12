import java.awt.*;
import java.util.ArrayList;

public class Boid{
    protected Vector2D velocity;
    protected final Vector2D p1 = new Vector2D();
    protected final Vector2D p2 = new Vector2D();
    protected final Vector2D p3 = new Vector2D();
    protected Vector2D position = new Vector2D();
    protected Vector2D acceleration = new Vector2D();
    protected final int PERCEPTION_RADIUS;
    protected final ArrayList<Boid> perceivedBoids = new ArrayList<>();
    protected final float MAX_VELOCITY;
    protected double theta;
    protected final int HEIGHT;
    protected final int WIDTH;
    protected final float TURN_FORCE;
    protected Color color;
    public static ArrayList<Boid> allBoids = new ArrayList<>();

    Boid(int height, int width, float maxVelocity, float turnForce, int perceptionRadius, Color color){
        this.HEIGHT = height;
        this.WIDTH = width;
        this.color = color;
        this.TURN_FORCE = turnForce;
        this.PERCEPTION_RADIUS = perceptionRadius;
        this.MAX_VELOCITY = maxVelocity;
        float v = maxVelocity * (float) Math.random() + 0.1f;
        theta = Math.random()*360-180;
        position.set((float) (Math.random()*(width - 200) -  (width - 200)/2), (float) (Math.random()*(height - 200) - (height - 200)/2));
        velocity = new Vector2D( v * (float) Math.cos(Math.toRadians(theta)), v * (float) Math.sin(Math.toRadians(theta)));
        acceleration.set(0,0);
    }


    public void perception(){
        for(Boid other : allBoids){
            if(other.position.Distance(this.position) <= PERCEPTION_RADIUS && other != this)
                perceivedBoids.add(other);
        }
    }

    public void clearPerception(){
        perceivedBoids.removeAll(perceivedBoids);
    }


    public Vector2D steer(Vector2D desired,Vector2D present,float turn_factor){
        Vector2D steer;
        steer = desired.subtract(present);
        steer = steer.normalize().multiply(turn_factor);
        return steer;
    }


    public Vector2D align(float turn_factor){
        Vector2D desired = new Vector2D();
        int total = perceivedBoids.size();

        for(Boid other : perceivedBoids)
            desired = desired.add(other.velocity);

        if( total != 0){
            desired = desired.normalize().multiply(MAX_VELOCITY);
            return steer(desired,velocity,turn_factor);
        }
        else
            return desired.multiply(0);
    }

    public Vector2D cohesion(float turn_factor){
        Vector2D target = new Vector2D();
        Vector2D desired = new Vector2D();

        int total = perceivedBoids.size();

        for(Boid other : perceivedBoids)
            target = target.add(other.position);

        if(total != 0) {
            target = target.multiply(1.0f / total);
            desired = target.subtract(position);
            desired = desired.normalize().multiply(MAX_VELOCITY);
            desired = steer(desired,velocity,turn_factor);
            return desired;
        }

        else  return desired.multiply(0);
    }

    public Vector2D seperation(float turn_factor,float d, ArrayList<Boid> percievedlist){
        Vector2D target;
        Vector2D desired = new Vector2D();
        float distance;

        int total = 0;

        for(Boid other : percievedlist){
            distance = (float) other.position.Distance(this.position);
            if( distance <= d && distance > 0){
                target = position.subtract(other.position);
                target = target.normalize().multiply(100/(distance));
                desired = desired.add(target);
                total++;
            }
        }

        if(total != 0) {
            desired = desired.multiply(1 / (float) total);
            desired = desired.multiply(MAX_VELOCITY);
            desired = steer(desired,velocity,turn_factor);
            return  desired;
        }
        else return  desired.multiply(0);
    }

    public void velocityLimit(){
        if(velocity.getLength() > MAX_VELOCITY)
            velocity = velocity.normalize().multiply(MAX_VELOCITY);

    }

    public void update(){

        velocity = velocity.add(acceleration);

        velocityLimit();

        position = position.add(velocity);

        theta =  Math.toDegrees(Math.atan2(velocity.getY(),velocity.getX()));

        acceleration = acceleration.multiply(0);
    }


    public void edges(){
        int x = WIDTH /2;
        int y = HEIGHT/2;
        if(position.getX() > x ){
            position.setX(-x);
        }
        if(position.getX() < -x ){
            position.setX(x);
        }
        if(position.getY() > y){
            position.setY(-y);
        }
        if(position.getY() < -y){
            position.setY(y);
        }
    }

    public void moveBoid(){

        p1.set(-5,0);
        p2.set(0,10);
        p3.set(5,0);

        p1.rotate( 90 - theta);
        p2.rotate( 90 - theta);
        p3.rotate( 90 -theta);

        p1.set(position.getX() + p1.getX(), position.getY() + p1.getY());
        p2.set(position.getX() + p2.getX(), position.getY() + p2.getY());
        p3.set(position.getX() + p3.getX(), position.getY() + p3.getY());

        update();
        edges();
        flock();

    }

    public void flock(){
        perception();

        Vector2D align = align(TURN_FORCE);
        Vector2D cohese = cohesion(TURN_FORCE/2);
        Vector2D seperate = seperation(TURN_FORCE,30f, perceivedBoids);


        acceleration = acceleration.add(align);
        acceleration = acceleration.add(cohese);
        acceleration = acceleration.add(seperate);


        clearPerception();
    }


    public void drawBoid(Graphics g) {
        int x = WIDTH / 2;
        int y = HEIGHT / 2;
        int[] X = {x + (int) p1.getX(), x + (int) p2.getX(), x + (int) p3.getX()};
        int[] Y = {y - (int) p1.getY(), y - (int) p2.getY(), y - (int) p3.getY()};

        g.setColor(color);
        g.fillPolygon(X, Y, 3);


    }
}
