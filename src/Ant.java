import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by User on 04/03/14.
 */
public class Ant {
    private double x,y;
    private Color color;
    private boolean followingTrail;
    private double directionX;
    private boolean hasFood;
    public Point.Double[] points;

    public Ant(int x0, int y0){
        x = x0;
        y = y0;
        hasFood = false;
        //if(x == 0)
            //color = Color.BLUE;
       // else
            color = Color.RED;
        followingTrail = false;
        directRandomly();
        //directionX = 0;
    }
    public void directRandomly(){
        Random choose = new Random();
        directionX = choose.nextDouble()*2*Math.PI;
        //directionY = (Math.PI/2)-directionX;
        //System.out.println("New Direction: "+Math.toDegrees(directionX)+" or "+Math.toDegrees(directionX)%360);
    }
    public void lostTrail(){
       followingTrail = false;
    }
    public void caughtTrail(){
        followingTrail = true;
    }
    public boolean hasScent(){
        return followingTrail;
    }
    public void wiggle(double d){
        directionX += d;
    }
    public void invert(){
        directionX += Math.PI;
    }
    public double directionVectorXComponent(){
        return Math.cos(directionX);
    }
    public double directionVectorYComponent(){
        return Math.sin(directionX);
    }
    //returns three possible points that the ant can go to
    public double roundStraightDirection(){
        double remainderX = 0.0;
        double remainderY = 0.0;
        if(x%1 < 0.5)
            remainderX = x%1;
        if(y%1 < 0.5)
            remainderY = y%1;
        double distanceX = Math.round(remainderX + directionVectorXComponent());
        double distanceY = Math.round(remainderY + directionVectorYComponent());
        double rootSquareSum = Math.pow(Math.pow(distanceX,2)+Math.pow(distanceY,2),0.5);
        double straightDirection = Math.toRadians(Math.round(Math.toDegrees(Math.asin(distanceY / rootSquareSum))));

        if(distanceX < 0)
            straightDirection = -straightDirection + Math.PI;
        System.out.println(distanceX+"Straight direction: "+Math.toDegrees(straightDirection));
        System.out.println("Right direction: "+Math.toDegrees(straightDirection+Math.PI/4));
        System.out.println("left direction: "+Math.toDegrees(straightDirection-Math.PI/4));

        return straightDirection;
    }
    public double rightDirection(){
        return roundStraightDirection()+Math.PI/4;
    }
    public double leftDirection(){
        return roundStraightDirection()-Math.PI/4;
    }
    public Point.Double[] threePoints(){
        Point.Double straight = new Point.Double((x + directionVectorXComponent()), (y + directionVectorYComponent()));
        int dxInt = (int)Math.round(straight.x)-getRoundX();
        int dyInt = (int)Math.round(straight.y)-getRoundY();
        //System.out.println(dxInt+" "+dyInt+" "+straight.toString() );
        int rightTurnDx = 0;
        int rightTurnDy = 0;
        int leftTurnDx = 0;
        int leftTurnDy = 0;
        if(dxInt == 1 && dyInt == -1){
            rightTurnDy = 0;
            rightTurnDx = 1;
            leftTurnDy = -1;
            leftTurnDx = 0;
        }
        if(dxInt == -1 && dyInt == -1){
            rightTurnDy = -1;
            rightTurnDx = 0;
            leftTurnDy = 0;
            leftTurnDx = -1;
        }
        if(dxInt == 0 && dyInt == 1){
            rightTurnDy = dyInt;
            rightTurnDx = -1;
            leftTurnDy = dyInt;
            leftTurnDx = 1;
        }
        if(dxInt == 1 && dyInt == 0){
            rightTurnDy = 1;
            rightTurnDx = dxInt;
            leftTurnDy = -1;
            leftTurnDx = dxInt;
        }
        if(dxInt == -1 && dyInt == 0){
            rightTurnDy = -1;
            rightTurnDx = dxInt;
            leftTurnDy = 1;
            leftTurnDx = dxInt;
        }
        if(dxInt == 0 && dyInt == -1){
            rightTurnDy = dyInt;
            rightTurnDx = 1;
            leftTurnDy = dyInt;
            leftTurnDx = -1;
        }
        if(dxInt == -1 && dyInt == 1){
            rightTurnDy = 0;
            rightTurnDx = -1;
            leftTurnDy = 1;
            leftTurnDx = 0;
        }
        if(dxInt == 1 && dyInt == 1){
            rightTurnDy = 0;
            rightTurnDx = 1;
            leftTurnDy = 0;
            leftTurnDx = 1;
        }

        Point.Double rightTurn = new Point.Double(x + rightTurnDx,y+rightTurnDy);
        Point.Double leftTurn = new Point.Double(x + leftTurnDx, y+leftTurnDy);
        //Point.Double rightTurn = new Point.Double(getRoundX()+Math.cos(rightDirection()),getRoundY()+Math.sin(rightDirection()));
        //Point.Double leftTurn = new Point.Double(getRoundX()+Math.cos(leftDirection()),getRoundY()+Math.sin(leftDirection()));

        points = new Point.Double[3];
        points[1] = straight;
        points[2] = rightTurn;
        points[0] = leftTurn;

        //System.out.println("Current point: ("+x+", "+y+")");
       // System.out.println(leftTurn.toString()+" "+straight.toString()+" "+rightTurn.toString());
        return points;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public boolean isBlue(){
        return color == color.BLUE;
    }
    public Color getColor(){
        return color;
    }
    public void setX(double n){
        x = n;
        //checkBounds(AntPanel.GRID_SIZE);
    }
    public void setY(double n){
        y = n;
        //checkBounds(AntPanel.GRID_SIZE);
    }
    public void moveX(double d){
        x += d;
    }
    public void moveY(double d){
        y += d;
    }
    public void move1Unit() {
        double dx = directionVectorXComponent();
        double dy = directionVectorYComponent();
        moveX(dx);
        moveY(dy);
    }
    //returns false if out of bounds
    public boolean checkBounds(float gridSize){
        //System.out.println("Checking bounds start, x = "+getRoundX());
        boolean b = true;
        if(getRoundX() > gridSize-1){
            x = 0;
            b = false;
        }
        else if(getRoundX() < 0){
            x = gridSize-1;
            b = false;
        }

        if(getRoundY() > gridSize-1){
            y = 0;
            b = false;
        }
        else if(getRoundY() < 0){
            y  = gridSize-1;
            b = false;
        }
        //System.out.println("Checking bounds end, x = "+getRoundX()+b);
        return b;
    }
    public void foundFood(){
        hasFood = true;
        directoToNest();
//        double distanceToNestX = ((int)(AntPanel.GRID_SIZE/2)) - x;
//        double distanceToNestY = ((int)(AntPanel.GRID_SIZE/2)) - y;
//        double rootSquareSum = Math.pow(Math.pow(distanceToNestX,2)+Math.pow(distanceToNestY,2),0.5);
//        directionX = Math.asin(distanceToNestY/rootSquareSum);
//        //directionY = Math.PI/2 - directionX;
//
//        if(distanceToNestX < 0)
//            directionX = -directionX + Math.PI;

        //System.out.println("New Direction: "+Math.toDegrees(directionX)+" or "+Math.toDegrees(directionX)%360);
    }

    public void directoToNest() {
        directToPoint(new Point(Math.round(AntPanel.GRID_SIZE / 2),Math.round(AntPanel.GRID_SIZE/2)));
    }

    public void directToPoint(Point p){
        double distanceX = (p.x - getRoundX());
        double distanceY = (p.y - getRoundY());
        double rootSquareSum = Math.pow(Math.pow(distanceX,2)+Math.pow(distanceY,2),0.5);
        directionX = Math.asin(distanceY / rootSquareSum);

        if(distanceX < 0)
            directionX = -directionX + Math.PI;
        //if(distanceY < 0)
            //directionX -= Math.PI/2;
        //directionX = -directionX;
        //if(distanceX < 0)
            //directionX = -directionX + Math.PI;
        //System.out.println("New Direction: "+Math.toDegrees(directionX)+" or "+Math.toDegrees(directionX)%360);
    }
    public void dropOff(){
        hasFood = false;
    }
    public boolean isHungry() {
        return !hasFood;
    }
    public int getRoundY(){
        return (int) Math.round(y);
    }
    public int getRoundX() {
        return (int) Math.round(x);
    }

    public boolean isNearNest(double dist) {
        double distance = Math.pow(Math.pow(x-(AntPanel.GRID_SIZE/2),2)+Math.pow(y-(AntPanel.GRID_SIZE/2),2),0.5);
        return (distance <= dist);
    }


    public void setDirection(double d) {
        directionX = d;
    }
    public double getDirection(){
        return directionX;
    }

    public ArrayList<Point> pointsOfVision(ArrayList<ArrayList<AntSquare>> antSquares, double visionAngle, double visionDistance){
        ArrayList<Point> points = new ArrayList<Point>();
        Point[] triangle = new Point[3];
        triangle[0] = new Point(getRoundX(),getRoundY());
        triangle[1] = new Point((int)Math.round(x + Math.cos(directionX+visionAngle) * visionDistance),(int)Math.round(y + Math.sin(directionX+visionAngle) * visionDistance));
        triangle[2] = new Point((int)Math.round(x + Math.cos(directionX-visionAngle) * visionDistance),(int)Math.round(y + Math.sin(directionX-visionAngle) * visionDistance));
        long time = System.currentTimeMillis();
        for(ArrayList<AntSquare> row : antSquares){
            for(AntSquare square : row){
                if(inTriangle(square,triangle)){
                    points.add(square.getPoint());
                }
            }
        }
        System.out.println("Time taken to fill triangle: "+(System.currentTimeMillis() - time)/1000.);
        return points;
    }
    public boolean inTriangle(AntSquare antSquare,  Point[] triangle) {
        Polygon p=new Polygon(new int[]{triangle[0].x,triangle[1].x,triangle[2].x},new int[]{triangle[0].y,triangle[1].y,triangle[2].y},3);

        float dx = antSquare.getPoint().x - (float)x;
        float dy = antSquare.getPoint().y - (float)y;

        boolean tooFar = (Math.pow(Math.pow(dx,2)+Math.pow(dy,2),0.5) > AntPanel.SMELL_DISTANCE);


        if(!(antSquare.getPoint().x == getRoundX() && antSquare.getPoint().y == getRoundY()) && !tooFar)
            if(p.contains(antSquare.getPoint().x,antSquare.getPoint().y)){
                return true;
            }
        return false;
    }

}
