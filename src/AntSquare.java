import java.awt.*;
import java.util.Random;

/**
 * Created by User on 04/03/14.
 */
public class AntSquare {
    private int foodAmount;
    private float pheromoneLevel;
    private Random choose;
    private Point point;
    public AntSquare(int x, int y){
        foodAmount = 0;
        choose = new Random();
        point = new Point(x,y);
        pheromoneLevel = 255;
        putFood();
    }
    public int getFoodAmount(){
        return foodAmount;
    }
    public Point getPoint(){
        return point;
    }
    public void putFood(){
        foodAmount = choose.nextInt(150)+80;
        //foodAmount = 100;
        //foodAmount = 10;
    }
    public void removeFood(){
        foodAmount--;
    }

    public void addPheromone() {
        if(pheromoneLevel > 0.0)
            pheromoneLevel-= 0.5;

    }
    public void evaporate(float rate){

        if(pheromoneLevel < 255.0f)
            pheromoneLevel += rate;
    }

    public int getPheromoneLevel() {
        return (int)pheromoneLevel;
    }
}
