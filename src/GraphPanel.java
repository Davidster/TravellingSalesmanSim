import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


public class GraphPanel extends JPanel implements MouseListener,MouseMotionListener{
    /**
     *
     */
    static final float GRID_SIZE = 200f;
    private static final long serialVersionUID = 1L;
    private boolean started;
    private ArrayList<ArrayList<AntSquare>> squareArray;
    public ArrayList<Point> points,drawPoints;
    private ArrayList<Integer> pathToDraw,currentAntPath;
    private int currentAntPathIndex;
    private BufferedImage bufferedImage;
    public ArrayList<ArrayList<Double>> phersToDraw;
    private BufferedImage bf;
    static int chosen = 0;
    private Random choose;
    private Graphics2D g2;
    private float boxHeight;
    static int ANT_SPEED = 250;
    int rand;
    double totalAngle;
    private float boxWidth;
    private long counter;
    public boolean matrixDisplayed;
    private GraphFrame gFrame;
    public Timer antAnimation;

    public GraphPanel(GraphFrame frame){
        gFrame = frame;
        totalAngle = 0;
        counter = 0;
        rand = -1;
        matrixDisplayed = false;
        choose = new Random();
        squareArray = new ArrayList<ArrayList<AntSquare>>();
        points = new ArrayList<Point>();
        drawPoints = new ArrayList<Point>();

        bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File("C:\\Users\\User\\IdeaProjects\\AntSimulator\\Ant.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        antAnimation = new Timer(ANT_SPEED,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean timeToStop = false;
                    currentAntPathIndex++;
                    if(currentAntPathIndex == currentAntPath.size()){
                        timeToStop = true;
                        currentAntPathIndex = 0;
                    }
                    repaint();
                    if(timeToStop) {
                        refresh();
                        repaint();
                        antAnimation.stop();
                    }

                }
        });
        //specX = 10;
        //specY = (int) (GRID_SIZE-10);
        //setMinimumSize(new Dimension((int)GRID_SIZE,(int)GRID_SIZE+15));


        //gives the ability to turn listener focus on or off(mainly for mouse listeners)
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow();
        repaint();
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


        if(currentAntPath != null && currentAntPathIndex >= 0 && !currentAntPath.isEmpty()) {
            Point currentPoint = drawPoints.get(currentAntPath.get(currentAntPathIndex));
            Point nextPoint;
            if(currentAntPathIndex == currentAntPath.size()-1)
                nextPoint = drawPoints.get(currentAntPath.get(0));
            else
                nextPoint = drawPoints.get(currentAntPath.get(currentAntPathIndex+1));
            double dx = nextPoint.getX()-currentPoint.getX();
            double dy = (nextPoint.getY()-currentPoint.getY());
            //double norm = Math.sqrt(dx*dx + dy*dy);
            //if in the two left quadrants, switch angle
            double radians = Math.atan(dy / dx)+Math.PI/4;
            if(dx < 0)
                radians += Math.PI;
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
            g2d.drawImage(bufferedImage, 0, 0, GraphPanel.this);
            g2d.dispose();
            g2.drawImage(rotate, Math.round(currentPoint.x * boxWidth) + Math.round(boxWidth / 2 - newWidth / 2), Math.round(currentPoint.y * boxHeight) + Math.round(boxHeight / 2 - newHeight / 2), null);
        }



        for(Point p : drawPoints){
            g2.setColor(Color.RED);
            g2.fillRect(Math.round(p.x * boxWidth),Math.round(p.y* boxHeight),Math.round(boxWidth),Math.round(boxHeight));
            int delta = Math.round((boxWidth/2));

            int ex = Math.round(p.x*boxWidth);
            int wy = Math.round(p.y*boxHeight);
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(2));
            g2.drawString(""+(drawPoints.indexOf(p)+1),ex+delta,wy+delta);

        }
        g2.setStroke(new BasicStroke(1));

        g2.setColor(Color.BLACK);

        if(drawPoints.isEmpty()){
            g2.drawString("Click displayMode matrix to read from file: "+GraphFrame.currentFile,GRID_SIZE*boxHeight/2-100,GRID_SIZE*boxWidth/2);
            g2.drawString("(or simply add points by clicking inside this box)",GRID_SIZE*boxHeight/2-115,GRID_SIZE*boxWidth/2 + 20);
        }

//        for(int x = 1; x < GRID_SIZE; x++){
//            int ex = Math.round(x*boxWidth);
//            int wy = Math.round(x*boxHeight);
//
//            g2.drawLine(ex,0,ex,Math.round(GRID_SIZE*boxWidth));
//            g2.drawLine(0,wy,Math.round(GRID_SIZE*boxHeight),wy);
//
//
//        }
        g2.setColor(Color.BLACK);



        if(!points.isEmpty()){
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.BLACK);
            if(pathToDraw != null)
                g2 = drawPath(g2,pathToDraw);
            g2.setStroke(new BasicStroke(2));
            if(phersToDraw != null && !phersToDraw.isEmpty())
                g2 = drawPhers(g2, phersToDraw);
        }

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        //"Point: (" + specX + ", " + ((int)GRID_SIZE-specY )+ ")" +
        g2.drawString("Evaporation: "+(float)gFrame.getEvap()+" Alpha: "+(float)gFrame.getAlpha()
               +" Beta: "+(float)gFrame.getBeta(), 0, getHeight() - 1);
        g2.setColor(Color.DARK_GRAY);

        g2.drawLine(0, getWidth(), getWidth(), getWidth());
    }
    public Graphics2D drawPath(Graphics2D g, ArrayList<Integer> path){
        for(int i = 0; i < path.size(); i++){
            Point p1,p2;
            p1 = drawPoints.get(path.get(i));
            //p1.setLocation(p1.getX()*boxWidth,p1.getY()*boxHeight);
            if(i < path.size()-1) {
                p2 = drawPoints.get(path.get(i+1));
                //p1.setLocation(p2.getX()*boxWidth,p2.getY()*boxHeight);
            }else{
                p2 = drawPoints.get(path.get(0));
                //p1.setLocation(p2.getX()*boxWidth,p2.getY()*boxHeight);
            }
            int x1 = (int) Math.round(p1.getX()*boxWidth);
            int y1 = (int)Math.round(p1.getY()*boxHeight);
            int x2 = (int)Math.round(p2.getX()*boxWidth);
            int y2 = (int)Math.round(p2.getY()*boxHeight);
            g.drawLine(x1,y1,x2,y2);
        }
        return g;
    }
    public Graphics2D drawPhers(Graphics2D g, ArrayList<ArrayList<Double>> pherMap){
        for(int i = 0; i < pherMap.size(); i++){
            ArrayList<Double> tempArray = pherMap.get(i);
            //if(i == 2)
                //System.out.println(tempArray.toString());
            for(int j = 0; j < tempArray.size(); j++) {
                //p1.setLocation(p1.getX()*boxWidth,p1.getY()*boxHeight);
                if (i != j){
                    Point p1, p2;
                    p1 = drawPoints.get(i);
                    p2 = drawPoints.get(j);
                    g2.setColor(new Color(255,0,0,(int)Math.min((pherMap.get(i).get(j)-1)*CityPanel.PHER_DRAW_SCALING,255)));
                    int x1 = (int) Math.round(p1.getX() * boxWidth);
                    int y1 = (int) Math.round(p1.getY() * boxHeight);
                    int x2 = (int) Math.round(p2.getX() * boxWidth);
                    int y2 = (int) Math.round(p2.getY() * boxHeight);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
        return g;
    }

    //mouselistener mthod
    public void mousePressed(MouseEvent e) {
        //animate.stop();
        int ex = (int) (e.getX()/boxHeight);
        int wy = (int) (e.getY()/boxWidth);
        boolean keep = true;
        for(int x = 0; x < points.size() ;x++){
            if(points.get(x).x == ex && points.get(x).y == wy)
                keep = false;
        }
        if(keep && !matrixDisplayed) {
            points.add(new Point(ex, wy));
            drawPoints.add(new Point(ex, wy));
        }
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

    public void reset() {
        antAnimation.stop();
        while(antAnimation.isRunning()){}

        points.clear();
        drawPoints.clear();
        if(pathToDraw != null)
            pathToDraw.clear();
        if(phersToDraw != null)
            phersToDraw.clear();
        if(currentAntPath != null)
            currentAntPath.clear();

        repaint();
    }
    public void randomC(){
        int numberOfPoints = choose.nextInt(15)+10;
        double dTheta = Math.PI*2/numberOfPoints;
        //NORMAL DISTRIBUTION OF CIRCLE SIZE
        double r = Math.min(Math.abs((choose.nextGaussian()*GRID_SIZE/10))+boxHeight*5,GRID_SIZE/2);
        System.out.println(r+"");
        //EVEN DISTRIBUTION OF CIRCLE SIZE
        //double r = choose.nextDouble()*GRID_SIZE/2;
        float dy = choose.nextFloat()*GRID_SIZE;
        float dx = choose.nextFloat()*GRID_SIZE;
        for(int i = 0; i < numberOfPoints; i++){

            double direction = dTheta*i;
            int x = (int) Math.round(r*Math.cos(direction)+dx);
            int y = (int) Math.round(r*Math.sin(direction)+dy);
            Point p = new Point(x,y);
            boolean keep = true;
            if(x < 0 || x > GRID_SIZE || y < 0 || y > GRID_SIZE)
                keep = false;
            for(Point temp : points) {
                if(temp.getX() == p.getX() && temp.getY() == p.getY())
                    keep = false;
            }
            if(keep){
                points.add(p);
                drawPoints.add(p);
            }
        }
        repaint();
    }
    public void randomize() {
        //reset();
        for(int i = 0; i < 10; i++) {
            Point p = randomPoint();
            points.add(p);
            drawPoints.add(p);
        }
        System.out.println(points.toString());
        repaint();
    }
    public Point randomPoint(){
        Point p = new Point(choose.nextInt((int)GRID_SIZE),choose.nextInt((int) GRID_SIZE));
        for(Point temp : points) {
            if(temp.getX() == p.getX() && temp.getY() == p.getY())
                return randomPoint();
        }
        return p;
    }

    public void refresh(){

        if(CityPanel.sharedPath != null) {
            pathToDraw = CityPanel.sharedPath;
            phersToDraw = CityPanel.sharedPhers;
        }
        //System.out.println(pathToDraw);
        requestFocusInWindow();
        repaint();
    }
    public Point highestXAndY(){
        double largestX = 0;
        double largestY = 0;
        for(int i = 0; i < points.size(); i++){
            double x = points.get(i).getX();
            double y = points.get(i).getY();
            if(x > largestX)
                largestX = x;
            if(y > largestY)
                largestY = y;
        }
        System.out.println(largestX+"");
        return new Point((int)largestX,(int)largestY);
    }
    public ArrayList<ArrayList<Integer>> displayMatrix() {
        ArrayList<ArrayList<Integer>> costMatrix = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < points.size(); i++){
            ArrayList<Integer> tempRow = new ArrayList<Integer>();
            for(int j = 0; j < points.size(); j++)
                if(i != j)
                    tempRow.add((int)Math.round(Math.sqrt((Math.pow(points.get(i).getX()-points.get(j).getX(),2)+Math.pow(points.get(i).getY()-points.get(j).getY(),2)))));
                else
                    tempRow.add(-1);
            costMatrix.add(tempRow);
        }
        System.out.println(costMatrix.toString());
        return costMatrix;
    }
    public ArrayList<ArrayList<Integer>> displayMatrix(String filePath, int dimension, String type) {
        ArrayList<ArrayList<Integer>> costMatrix = new ArrayList<ArrayList<Integer>>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        ArrayList<String> everyNumber = new ArrayList<String>();
        try {

            while ((line = reader.readLine()) != null) {
                String parts[] = line.split(" ");
                for (String s : parts) {
                    if (!s.equals(""))
                        everyNumber.add(s);
                }
                //for(int i = 0; i < parts.length )
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(type.equals("MATRIX")) {
            if (everyNumber.size() < dimension * dimension) {
                for (int i = 0; i < dimension; i++) {
                    ArrayList<Integer> tempArray = new ArrayList<Integer>();
                    for (int j = 0; j < dimension; j++) {
                        int numb;
                        if (j < dimension - i)
                            numb = Integer.valueOf(everyNumber.get((dimension * i) + j));
                        else if (j < dimension - i)
                            numb = Integer.valueOf(everyNumber.get((dimension * j) + i));
                        else
                            numb = -1;
                        tempArray.add(numb);
                    }
                    costMatrix.add(tempArray);
                }
            } else {
                for (int i = 0; i < dimension; i++) {
                    ArrayList<Integer> tempArray = new ArrayList<Integer>();
                    for (int j = 0; j < dimension; j++) {
                        int numb = Integer.valueOf(everyNumber.get((dimension * i) + j));
                        if (numb == 100000000)
                            numb = -1;
                        tempArray.add(numb);
                    }
                    costMatrix.add(tempArray);
                }
            }
        }else if(type.equals("EUCLID")){
            for(int i = 0; i < everyNumber.size(); i+=3){
                double numbX = Double.valueOf((everyNumber.get(i+1)));
                double numbY = Double.valueOf((everyNumber.get(i+2)));
                //numbX += 100;
                //numbY += 100;
//                numbX *= 100;
//                numbY *= 100;
                System.out.println(numbX);
                Point p = new Point((int)numbX,(int)numbY);
                points.add(p);
                drawPoints.add(new Point(p.x,p.y));
            }
            Point bounds = highestXAndY();
            for(int i = 0; i < points.size(); i++){
                Point p = points.get(i);
                Point scaledPoint = new Point((p.x*((int)GraphPanel.GRID_SIZE)/bounds.x),p.y*((int)GraphPanel.GRID_SIZE)/bounds.y);
                drawPoints.set(i,scaledPoint);
            }
            costMatrix = displayMatrix();
        }
        System.out.println(costMatrix.toString());
        return costMatrix;
    }

    public void animateAnt() {
        currentAntPath = CityPanel.sharedAnt;
        currentAntPathIndex = -1;
        antAnimation.restart();
    }
}