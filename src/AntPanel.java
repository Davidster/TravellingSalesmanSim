//import sun.org.mozilla.javascript.internal.ast.Yield;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


public class AntPanel extends JPanel implements MouseListener,MouseMotionListener{
    /**
     *
     */
    static final float GRID_SIZE = 40f;
    private final int NUMBER_OF_ANTS = 23;
    private final float DIST_SPEED = 1.0f;
    static final float EVAPORATION_RATE = 0.01f;
    static final float SMELL_DISTANCE = 10.0f;
    private final float TIME_SPEED = 1000.0f;
    private static final long serialVersionUID = 1L;
    private BufferedImage bufferedImage;
    private boolean started;
    private ArrayList<ArrayList<AntSquare>> squareArray;
    private ArrayList<Ant> ants;
    private BufferedImage bf;
    static int chosen = 0;
    private Random choose;
    private Timer animate;
    private Graphics2D g2;
    private float boxHeight;
    private ArrayList<Point> visionPoints;
    int rand;
    double totalAngle;
    private float boxWidth;
    private long counter;
    private ArrayList<AntSquare> foodPoints;

    public AntPanel(){
        totalAngle = 0;
        counter = 0;
        rand = -1;
        choose = new Random();
        bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("C:\\Users\\User\\IdeaProjects\\AntSimulator\\Ant.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        squareArray = new ArrayList<ArrayList<AntSquare>>();
        visionPoints = new ArrayList<Point>();
        ants = new ArrayList<Ant>();
        foodPoints =  new ArrayList<AntSquare>();
        //ants.add(new Ant(choose.nextInt((int) GRID_SIZE-1)+1, choose.nextInt((int) GRID_SIZE-1)+1));
        for(int x = 0; x < NUMBER_OF_ANTS ; x++)
            ants.add(new Ant((int)(GRID_SIZE/2), (int) (GRID_SIZE/2)));
        boolean b = true;
        b = false;
        int x = 0;
        //specX = 10;
        //specY = (int) (GRID_SIZE-10);
        setMinimumSize(new Dimension((int)GRID_SIZE,(int)GRID_SIZE+15));

        animate = new Timer((int) ( TIME_SPEED/AntFrame.speed),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        counter++;
                        for(Ant ant : ants){
                            long time = System.currentTimeMillis();
                            ArrayList<Integer> highests = new ArrayList<Integer>();
                            ArrayList<Point> points = ant.pointsOfVision(squareArray, Math.PI / 5, SMELL_DISTANCE);
                            double pher = 255.0;
                            int highest = 0;
                            ant.lostTrail();
                            for(int a  = 0; a < points.size(); a++){

                                Point point = points.get(a);
                                //System.out.print("x:" +point.x+"y"+point.y);
                                try{
                                    int d1 = (int)Math.round(point.x);
                                    if(d1 < 0)
                                        d1 = 0;
                                    if(d1 > GRID_SIZE-1)
                                        d1 = (int) (GRID_SIZE-1);
                                    int d2 = (int)Math.round(point.y);
                                    if(d2 < 0)
                                        d2 = 0;
                                    if(d2 > GRID_SIZE-1)
                                        d2 = (int) (GRID_SIZE-1);
                                    int tempPher = squareArray.get(d1).get(d2).getPheromoneLevel();

                                    if(pher > tempPher){
                                        ant.caughtTrail();
                                        highest = a;
                                        if(!highests.isEmpty())
                                            highests.clear();
                                        highests.add(a);
                                        pher = tempPher;
                                    }else if(highest == 0 && pher != 255.0){
                                        ant.caughtTrail();
                                    }
                                    if(pher == tempPher && !highests.isEmpty()){
                                        highests.add(a);
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                            //System.out.println("Time taken to find scent: "+(System.currentTimeMillis() - time));
                            time = System.currentTimeMillis();

                            //highest = 1;
                            //if((highest == 0 || highest == 2)){
                            if(highests.size() > 1){
                                highest = randomNumber(highests);
                            }
                            if(ant.hasScent())
                                ant.directToPoint(points.get(highest));
                           // }
                            double dTheta = 0;
                            if(!ant.hasScent()){
                                do
                                    dTheta =  choose.nextGaussian()/6;
                                while(Math.abs(dTheta) > Math.PI/4);
                            }


                           if(Math.abs(dTheta) > 0.15)
                               ant.wiggle(dTheta);


                            chosen = highest;
                            //if(visionPoints.isEmpty())

                            ant.move1Unit();

                            visionPoints = points;
                            //System.out.println("Time taken to direct and move: "+(System.currentTimeMillis() - time));
                            time = System.currentTimeMillis();

                            if(!ant.isHungry()){
                                ant.directoToNest();
                                if(ant.getRoundX() >= 0 && ant.getRoundY() >= 0)
                                    squareArray.get(ant.getRoundX()).get(ant.getRoundY()).addPheromone();
                                if (ant.isNearNest(GRID_SIZE/12)){
                                    ants.get(ants.indexOf(ant)).dropOff();
                                    ants.get(ants.indexOf(ant)).invert();
                                }
                            }

                            for(int a = 0; a < foodPoints.size(); a++){
                                AntSquare s = foodPoints.get(a);
                                if(s.getFoodAmount() != 0 && ant.getRoundX() == s.getPoint().x && ant.getRoundY() == s.getPoint().y){
                                    ant.foundFood();
                                    s.removeFood();
                                }
                            }
                            ant.checkBounds(GRID_SIZE);
                            totalAngle += choose.nextGaussian()/3;
                            //System.out.println("Time taken to do rest: "+(System.currentTimeMillis() - time));
                        }
                        if(counter%50 == 0)
                            System.out.println("Average angle = " + Math.toDegrees(totalAngle) / counter);
                        repaint();
                    }
                 });

        //gives the ability to turn listener focus on or off(mainly for mouse listeners)
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow();
        repaint();
    }



    public boolean redsExist(){
        for(Ant ant : ants){
            if(!ant.isBlue())
                return true;
        }
        return false;
    }
    public int randomNumber(ArrayList<Integer> ints){
        int rand = choose.nextInt(ints.size());
        return ints.get(rand);
    }
    public Ant nextRed(){
        for(Ant ant : ants){
            if(!ant.isBlue())
                return ant;
        }
        return null;
    }
    public double antDistance(Ant a1, Point a2){
        double dx = (a1.getX()-a2.getX());
        double dy =  (a1.getY()-a2.getY());
        double sum = (float) (Math.pow(dx, 2)+Math.pow(dy, 2));
        double distance = (float) Math.pow(sum, 0.5);
        //System.out.println("Distance = "+(float)distance);
        return distance;
    }
    public void paintComponent(Graphics gr){
        //intitialization
        if(getWidth()+15 != getHeight()){
            int big = Math.min(getWidth(), getHeight());
            if(big < GRID_SIZE)
                big = (int)GRID_SIZE;
            setSize(big-15,big);
        }
        if(!started){
            bf = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
            for(int x = 0; x < GRID_SIZE; x++){
                ArrayList<AntSquare> temp = new ArrayList<AntSquare>();
                for(int y = 0; y < GRID_SIZE; y++)
                    temp.add(new AntSquare(x,y));
                squareArray.add(temp);
            }
            for(int a = 0; a < 1; a ++){
                int ex = choose.nextInt((int) GRID_SIZE);
                int wy = choose.nextInt((int) GRID_SIZE);
                AntSquare newSquare = new AntSquare(ex,wy);
                //foodPoints.add(newSquare);
            }

            //set initial conditions here. example: ballx = ((getWidth()/2));
            started = true;
            //animate.start();
        }
        //on resize fix bf NOTE: IMPORTANCE NOT YET CONFIRMED
        if(bf.getHeight() != getHeight() || bf.getWidth() != getWidth()){
             //saving data in a raster
            Raster resize = bf.getData();
            bf = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
            bf.getGraphics().fillRect(0, 0, getWidth(), getHeight());
            //putting data back into bf
            bf.setData(resize);
        }
        draw(bf.getGraphics());
        gr.drawImage(bf,0,0,null);
    }
    public void draw(Graphics g){
    	if(g instanceof Graphics2D){
    		g2 = (Graphics2D)g;
    		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	}
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        boxHeight = getWidth()/GRID_SIZE;
        boxWidth = getWidth()/GRID_SIZE;

        g2.setColor(Color.WHITE);
        for(int x = 0; x < GRID_SIZE; x++){
            ArrayList<AntSquare> col = squareArray.get(x);

            for(int y = 0; y < GRID_SIZE; y++){
                AntSquare temp = col.get(y);
                int pher = temp.getPheromoneLevel();
                Color c = new Color(pher,pher,pher);
                squareArray.get(x).get(y).evaporate(EVAPORATION_RATE);
                g2.setColor(c);
                for(Point p : visionPoints){
                    if(p.x == temp.getPoint().x && p.y == temp.getPoint().y)
                        g2.setColor(Color.CYAN);
                }

                g2.fillRect(Math.round(x * boxWidth),Math.round(y * boxHeight),Math.round(boxWidth),Math.round(boxHeight));
            }
            //visionPoints.clear();
        }

        for(AntSquare p : foodPoints){
            if(p.getFoodAmount() > 0){
                g2.setColor(Color.GREEN);
                g2.fillRect(Math.round(p.getPoint().x * boxWidth),Math.round(p.getPoint().y* boxHeight),Math.round(boxWidth),Math.round(boxHeight));
                float ratio = p.getFoodAmount()/255.0f;
                g2.setColor(Color.RED);
                g2.fillRect(Math.round(p.getPoint().x * boxWidth)+Math.round(boxWidth*(1-ratio)/2),Math.round(p.getPoint().y* boxHeight)+Math.round(boxHeight*(1-ratio)/2),Math.round(boxWidth*ratio),Math.round(boxHeight*ratio));
            }
        }
        g2.setColor(Color.yellow);
        g2.fillOval(Math.round((int)(GRID_SIZE/2) * boxWidth - 4.5f*boxWidth),Math.round((int)(GRID_SIZE/2) * boxHeight - 4.5f*boxHeight),Math.round(10*boxHeight), Math.round (10*boxWidth));
        g2.setColor(Color.BLACK);
        g2.fillRect(Math.round((int)(GRID_SIZE/2) * boxWidth),Math.round((int)(GRID_SIZE/2) * boxHeight),Math.round(boxWidth),Math.round(boxHeight));


        for(Ant ant : ants){

            g2.setColor(ant.getColor());
           // g2.fillRect(Math.round(ant.getRoundX() * boxWidth), Math.round(ant.getRoundY() * boxHeight), Math.round(boxWidth), Math.round(boxHeight));
            //if(ants.indexOf(ant) == 0){

               // for(int x = 0; x  < visionPoints.size(); x++){



                    g2.setColor(Color.BLACK);
                    double radians = ant.getDirection() + Math.PI/4;
                    double sin = Math.abs(Math.sin(radians));
                    double cos = Math.abs(Math.cos(radians));
                    int newWidth = (int)Math.round(bufferedImage.getWidth() * cos +bufferedImage.getHeight() * sin);
                    int newHeight = (int)Math.round(bufferedImage.getWidth() * sin + bufferedImage.getHeight() * cos);

                    BufferedImage rotate = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = rotate.createGraphics();
                    int ex = (newWidth - bufferedImage.getWidth()) / 2;
                    int y = (newHeight - bufferedImage.getHeight()) / 2;
                    AffineTransform at = new AffineTransform();
                    at.setToRotation(radians, ex + (bufferedImage.getWidth() / 2), y + (bufferedImage.getHeight() / 2));
                    at.translate(ex, y);
                    g2d.setTransform(at);
                    g2d.drawImage(bufferedImage, 0, 0, AntPanel.this);
                    g2d.dispose();
                    g2.drawImage(rotate,Math.round(ant.getRoundX() * boxWidth)+Math.round(boxWidth/2 - newWidth/2), Math.round(ant.getRoundY() * boxHeight)+Math.round(boxHeight/2 - newHeight/2),null);



                    //System.out.println(g2.drawImage(bufferedImage,50,50,null)+"");
                    //g2.drawRect((Math.round(Math.round(ant.points[x].x) * boxWidth)), Math.round((Math.round(ant.points[x].y)) * boxHeight), Math.round(boxWidth), Math.round(boxHeight));
               // }

           // }
            g2.setColor(Color.yellow);
            //g2.drawRect((Math.round(Math.round(ant.getRoundX()+Math.cos(ant.roundStraightDirection())) * boxWidth)), Math.round((Math.round(ant.getRoundY()+Math.sin(ant.roundStraightDirection()))) * boxHeight), Math.round(boxWidth), Math.round(boxHeight));
           // g2.drawRect((Math.round(Math.round(ant.getRoundX()+Math.cos(ant.rightDirection())) * boxWidth)), Math.round((Math.round(ant.getRoundY()+Math.sin(ant.rightDirection()))) * boxHeight), Math.round(boxWidth), Math.round(boxHeight));
           // g2.drawRect((Math.round(Math.round(ant.getRoundX()+Math.cos(ant.leftDirection())) * boxWidth)), Math.round((Math.round(ant.getRoundY()+Math.sin(ant.leftDirection()))) * boxHeight), Math.round(boxWidth), Math.round(boxHeight));

//            if(ant.isHungry())
//                g2.setColor(Color.BLACK);
//            else
//                g2.setColor(Color.BLUE);
//            if(ant.hasScent())
//                g2.setColor(Color.YELLOW);
//            g2.setStroke(new BasicStroke(1));
//            g2.drawRect(Math.round(ant.getRoundX() * boxWidth), Math.round(ant.getRoundY() * boxHeight), Math.round(boxWidth), Math.round(boxHeight));
//
//            g2.setStroke(new BasicStroke(2));
//            g2.drawLine(Math.round(ant.getRoundX() * boxWidth) + (int) (boxWidth / 2),
//                    Math.round(ant.getRoundY() * boxWidth) + (int) (boxHeight / 2),
//                    Math.round(ant.getRoundX() * boxWidth) + (int) (boxWidth / 2) + (int) (ant.directionVectorXComponent() * 1.5 * boxWidth),
//                    Math.round(ant.getRoundY() * boxWidth) + (int) (boxHeight / 2) + (int) (ant.directionVectorYComponent() * 1.5 * boxHeight));
//
//            g2.setColor(Color.blue);

//            for(int x = 0; x < ant.threePoints().length; x ++){
//                g2.setStroke(new BasicStroke(x+1));
//            g2.drawLine(Math.round(ant.getRoundX() * boxWidth) + (int)(boxWidth/2),
//                        Math.round(ant.getRoundY() * boxWidth)+(int)(boxHeight/2),
//                        Math.round(Math.round(ant.threePoints()[x].x)*boxWidth),
//                        Math.round(Math.round(ant.threePoints()[x].y)*boxHeight));
            //}
            //System.out.print("Ant "+(ants.indexOf(ant)+1)+": ("+ant.getX() + ", " + ant.getY()+"), ");
        }

        g2.setColor(Color.BLACK);

        g2.setStroke(new BasicStroke(1));
        //"Point: (" + specX + ", " + ((int)GRID_SIZE-specY )+ ")" +
        g2.drawString("Speed: ~"+(int)(1000/(TIME_SPEED/AntFrame.speed))+" Moves Per Second"
                      +". Number of Ants: "+ants.size(), 0, getHeight() - 1);
        g2.setColor(Color.DARK_GRAY);

        g2.drawLine(0, getWidth(), getWidth(), getWidth());



       // System.out.println();
                //g2.setFont(new Font("Arial",Font.BOLD,32));
        //g2.drawString("High Score:"+highscore,(getWidth()-(getWidth()/4)),getHeight()/10);
    }

    //mouselistener mthod
    public void mousePressed(MouseEvent e) {
        //animate.stop();
        int ex = (int) (e.getX()/boxHeight);
        int wy = (int) (e.getY()/boxWidth);
        for(int x = 0; x <1;x++)
            foodPoints.add(new AntSquare(ex,wy));
        repaint();
    }

    //mousemotion lister methods
    public void mouseEntered(MouseEvent e) {
        requestFocusInWindow();
    }
    public void mouseDragged(MouseEvent e) {
        //do something then call repaint :
        repaint();
    }
    public void mouseMoved(MouseEvent e){
        //same as mouseDragged
        repaint();
    }

    //extra abstract methods
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){}

    public void start() {
        animate.start();
    }

    public void reset() {
        animate.stop();
        ants.clear();
        for(int x = 0; x < NUMBER_OF_ANTS; x++)
            ants.add(new Ant((int)(GRID_SIZE/2), (int) (GRID_SIZE/2)));
        repaint();
    }

    public void updateSpeed() {
        animate.setDelay((int) (TIME_SPEED/AntFrame.speed));
        repaint();
    }
}