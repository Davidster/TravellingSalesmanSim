//import com.google.common.collect.Collections2;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


public class CityPanel extends JPanel implements MouseListener,MouseMotionListener{
    /**
     *
     */
    static float GRID_SIZE;
    static float NUMBER_OF_CITIES;
    static float OPTIMAL_PHEROMONE_SCALING = 0.01f;
    static float PHER_DRAW_SCALING = 3;
    static int PATHS_TO_KEEP = 5;
    private final float TIME_SPEED = 1000.0f;
    private static final long serialVersionUID = 1L;
    private boolean started;
    private ArrayList<ArrayList<Integer>> costMatrix, topCheapest;
    private ArrayList<ArrayList<Double>> pherTrails;
    ArrayList<Integer> cheapestPath,antPath,antPath2;
    static ArrayList<Integer> sharedPath, sharedAnt;
    static ArrayList<ArrayList<Double>> sharedPhers;
    private BufferedImage bf;
    static int chosen = 0;
    private Random choose;
    private Timer animate,antAnimate;
    private Graphics2D g2;
    public double currentPercentDifference;
    public boolean alreadyHave;
    long totalTime;


    private float boxHeight;
    int rand;
    private float boxWidth;
    private long counter;
    public long time;
    public ArrayList<Integer> cheapestPath2;
    static final int MAX_DISTANCE = 40;
    static final int FTV_33_SOL = 1286;
    static final int A_280_SOL = 2579;
    static final int BR_17_SOL = 39;
    static final int BERLIN_52_SOL = 7542;
    static final int BIER_127_SOL = 118282;
    static final int BAY_S_29_SOL =  2020;
    static final int EIL_76_SOL = 538;
    static final int EIL_101_SOL = 629;
    static final int P_43_SOL = 5620;
    static final int GIL_262 = 2378;
    static final int ALI_535 =  202339;
    static final int FILE_SOLUTION = BERLIN_52_SOL;
    static int BEST_SOLUTION = FILE_SOLUTION;

    //GIL262: current solution: 2611, optimal solution: 2378, percent difference: 9.79815, count: 7754, time elapsed : 7.9245167minutes (475seconds)
    //        Smart Method: { 2 170 230 26 148 114 253 101 193 203 57 214 131 238 10 42 197 194 80 113 103 66 168 47 55 175 259 17 24 40 127 147 245 159 134 158 60 110 109 20 227 29 41 210 178 92 255 252 111 236 23 120 22 44 199 251 208 246 25 141 104 74 215 126 116 213 112 8 190 123 118 3 84 135 146 39 102 15 219 75 162 59 181 171 169 52 16 243 6 165 37 222 85 105 132 97 51 67 201 78 32 226 136 176 233 38 242 256 56 122 43 223 225 241 11 235 82 157 249 62 237 177 69 163 224 250 64 125 195 27 261 173 54 204 198 73 96 207 166 19 185 72 21 138 100 145 115 209 4 81 186 172 140 164 7 160 45 128 48 196 174 143 14 98 240 117 107 212 77 91 50 88 94 151 86 149 247 152 216 46 93 187 161 179 200 63 180 144 192 184 218 119 35 260 239 156 28 182 95 34 258 188 87 154 76 155 99 70 108 244 33 254 234 68 142 232 231 202 139 1 229 71 31 205 221 129 130 61 90 211 49 12 133 5 79 262 191 30 18 137 257 124 228 53 65 206 83 217 36 220 150 183 58 189 248 121 106 9 153 13 167 89 } cost = 2611

    //BIER127 : 122000(3.14%), 151695 ITERATIONS, evap = 0.322, pher_drop_scaling -> averageCost(), alpha = 3, beta = 2, 2 ants per iteration
    //BERLIN52 : 7532(OPTIMAL), 12031 ITERATIONS, evap = 0.321,  pher_drop_scaling -> averageCost(), alpha = 3, beta = 2, 2 ants per iteration
    //FTV33 : 1315 (1286), 69294 ITERATIONS, evap = 0.3209, pher_drop_scaling -> averageCost(), alpha = 3, beta = 2, 2 ants per iteration
    static double PHEROMONE_DROP_SCALING = MAX_DISTANCE;
    static double ALPHA = 3;
    static double EVAPORATION_COEFFICIENT = 0.33;
    static double BETA = 2;

    private boolean simplify;
    int currentSolution,bestSolution;
    int count = 0;
    int iterationsSinceLast = 0;
    public GraphPanel gPanel;

    public void giveGraphPanel(GraphPanel gP){
        gPanel = gP;
    }

    public void setup(){
        counter = 0;
        time = 0;
        totalTime = 0;
        rand = -1;
        simplify = true;
        choose = new Random();
        cheapestPath = new ArrayList<Integer>();
        antPath = new ArrayList<Integer>();
        antPath2 = new ArrayList<Integer>();
        cheapestPath2 = new ArrayList<Integer>();
        topCheapest = new ArrayList<ArrayList<Integer>>();
        pherTrails = new ArrayList<ArrayList<Double>>();
        //ants.add(new Ant(choose.nextInt((int) GRID_SIZE-1)+1, choose.nextInt((int) GRID_SIZE-1)+1));
        boolean b = true;
        b = false;
        int x = 0;
        //specX = 10;
        //specY = (int) (GRID_SIZE-10);
        setMinimumSize(new Dimension((int)GRID_SIZE,(int)GRID_SIZE+15));
        animate = new Timer(100,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        //int count = 0;
                        // for(int i = 0; i < 1000; i++) {
                        // while (lowC != realLowC) {
                        antPath = getSmartPath();

                        //antPath2 = getSmartPath();

                        int ant1Cost = costOfPath(antPath);

                        //int ant2Cost = costOfPath(antPath2);
                        int ant2Cost = Integer.MAX_VALUE;
                        //System.out.println(count+"");
                        if(count%2 == 0)
                            evaporate();
                        dropPhers(antPath,ant1Cost);
                        //dropPhers(antPath2,ant2Cost);
                        if (count == 0 || ant1Cost < currentSolution || ant2Cost < currentSolution) {
                            if(Math.min(ant1Cost, ant2Cost) == ant1Cost){
                                cheapestPath = antPath;
                                currentSolution = ant1Cost;
                            }else{
                                cheapestPath = antPath2;
                                currentSolution = ant2Cost;
                            }
                            iterationsSinceLast = 0;
                            sharedPath = cheapestPath;
                            sharedPhers = pherTrails;
                            addCheapest(cheapestPath);

                            if(gPanel != null)
                                gPanel.refresh();
                            totalTime = System.currentTimeMillis()-time;
                            currentPercentDifference = (currentSolution-bestSolution)*100./bestSolution;
                            System.out.println("\ncurrent solution: "+currentSolution+", optimal solution: "+bestSolution+", percent difference: "+(float)(currentPercentDifference)+", count: "+count
                                                +", time elapsed : "+totalTime/60000.0f+"minutes ("+totalTime/1000+"seconds)");
                            System.out.print("Smart Method: { ");
                            for(int i : cheapestPath){
                                System.out.print((i+1)+" ");
                            }
                            System.out.println("} cost = "+costOfPath(cheapestPath));
                           // for(ArrayList<Integer> path : topCheapest)
                                //System.out.print(costOfPath(path)+" ");
                            System.out.println();


                        }
                        count++;
                        iterationsSinceLast++;
                        if(count%1000 == 0) {
                            sharedPhers = pherTrails;
                            if(gPanel != null)
                                gPanel.refresh();
                            repaint();
                        }
                        if(iterationsSinceLast > count){
                            iterationsSinceLast = 0;
                            //ALPHA+=0.08;
                            //BETA+=0.04;
                        }
                        if(currentSolution == bestSolution) {
                            System.out.println("Avergae iterations taken until convergence = "+count);
                            System.out.print("Smart Method: { ");
                            for(int i : cheapestPath){
                                System.out.print((i+1)+" ");
                            }

                            System.out.println("} cost = "+costOfPath(cheapestPath2));
                            sharedPath = cheapestPath;
                            if(gPanel != null)
                                gPanel.refresh();
                           // System.out.println("\n\n\nSmart Method: "+ cheapestPath.toString() +" cost = "+costOfPath(cheapestPath)+", execution time = "+((System.currentTimeMillis()-time)-(count*animate.getDelay())));
                            animate.stop();

                            repaint();
                        }

                        // }
                        // }
                        //System.out.println(animate.getDelay()+"  "+(System.currentTimeMillis()-time));


                    }
                });
        antAnimate = new Timer((int)(NUMBER_OF_CITIES+2)*GraphPanel.ANT_SPEED+250,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        antPath = getSmartPath();
                        //antPath2 = getSmartPath();

                        int ant1Cost = costOfPath(antPath);
                        //int ant2Cost = costOfPath(antPath2);
                        int ant2Cost = Integer.MAX_VALUE;
                        evaporate();
                        dropPhers(antPath,ant1Cost);
                        if (count == 0 || ant1Cost < currentSolution || ant2Cost < currentSolution) {
                            if(Math.min(ant1Cost, ant2Cost) == ant1Cost){
                                cheapestPath = antPath;
                                currentSolution = ant1Cost;
                            }else{
                                cheapestPath = antPath2;
                                currentSolution = ant2Cost;
                            }
                            iterationsSinceLast = 0;
                            sharedPath = cheapestPath;
                            sharedPhers = pherTrails;
                            totalTime = System.currentTimeMillis()-time;
                            System.out.println("\ncurrent solution: "+currentSolution+", optimal solution: "+bestSolution+", percent difference: "+((float)(currentSolution-bestSolution)*100f/bestSolution)+", count: "+count
                                    +", time elapsed : "+totalTime/60000.0f+"minutes ("+totalTime/1000+"seconds)");
                            System.out.print("Smart Method: { ");
                            for(int i : cheapestPath){
                                System.out.print((i+1)+" ");
                            }
                            System.out.println("} cost = "+costOfPath(cheapestPath2));


                        }
                        count++;
                        iterationsSinceLast++;
                        repaint();
                        if(iterationsSinceLast > count){
                            iterationsSinceLast = 0;
                            //ALPHA+=0.08;
                            //BETA+=0.04;
                        }
                        if(currentSolution == bestSolution) {
                            System.out.println("Avergae iterations taken until convergence = "+count);
                            System.out.print("Smart Method: { ");
                            for(int i : cheapestPath){
                                System.out.print((i+1)+" ");
                            }

                            System.out.println("} cost = "+costOfPath(cheapestPath2));
                            sharedPath = cheapestPath;
                            if(gPanel != null)
                                gPanel.refresh();
                            // System.out.println("\n\n\nSmart Method: "+ cheapestPath.toString() +" cost = "+costOfPath(cheapestPath)+", execution time = "+((System.currentTimeMillis()-time)-(count*animate.getDelay())));
                            animate.stop();

                            repaint();
                        }
                        sharedPhers = pherTrails;
                        sharedAnt = antPath;
                        if(gPanel != null)
                            gPanel.animateAnt();


                        // }
                        // }
                        //System.out.println(animate.getDelay()+"  "+(System.currentTimeMillis()-time));


                    }
                });

        //gives the ability to turn listener focus on or off(mainly for mouse listeners)
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow();
        repaint();
    }
    public void addCheapest(ArrayList<Integer> path){
        topCheapest.add(path);
        if(topCheapest.size() > PATHS_TO_KEEP){
            topCheapest.remove(0);
        }
    }
    public CityPanel(){
        GRID_SIZE = 11f;
        NUMBER_OF_CITIES = GRID_SIZE-1;
        setup();
    }
    public CityPanel(ArrayList<ArrayList<Integer>> cMatrix){
        costMatrix = cMatrix;
        GRID_SIZE = cMatrix.size()+1;
        NUMBER_OF_CITIES = cMatrix.size();
        setup();
    }

    public void evaporate() {
        //EVAPORATION_COEFFICIENT = 0.315 + (0.015*Math.sin(count/1000.));
        //if(count%100 == 0)
           // System.out.println(EVAPORATION_COEFFICIENT);
        for(int x = 0; x < pherTrails.size(); x++){
            ArrayList<Double> temp = pherTrails.get(x);
            for(int y = 0; y < temp.size(); y++){
                temp.set(y,Math.max(temp.get(y)*(1-EVAPORATION_COEFFICIENT),1.));
            }
            pherTrails.set(x,temp);
        }
    }

    public void dropPhers(ArrayList<Integer> path, int totalCost){
        for(int i = 0; i < path.size(); i++){
            int start = path.get(i);
            int next;
            if(i < path.size()-1)
                next = path.get(i+1);
            else
                next = path.get(0);
            ArrayList<Double> col = pherTrails.get(next);
            double dPher = (PHEROMONE_DROP_SCALING*NUMBER_OF_CITIES/(totalCost));
            double currentLevel = getPheromoneLevel(start,next);
            col.set(start,currentLevel+dPher);
            pherTrails.set(next,col);
        }
    }
    @Override
    public void paintComponent(Graphics gr){
        //initialization
        if(getWidth()+15 != getHeight()){
            int big = Math.min(getWidth(), getHeight());
            if(big < GRID_SIZE)
                big = (int)GRID_SIZE;
            setSize(big-15,big);
        }
        if(!started) {
            bf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            System.out.println("initial"+costMatrix.toString());
            if(costMatrix == null) {
                alreadyHave = false;
                costMatrix = new ArrayList<ArrayList<Integer>>();
                bestSolution = 0;
            }
            else {
                alreadyHave = true;
                bestSolution = BEST_SOLUTION;
            }
                for (int x = 1; x < GRID_SIZE; x++) {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    ArrayList<Double> temp2 = new ArrayList<Double>();
                    for (int y = 1; y < GRID_SIZE; y++) {
                        //if(y == 2 && x == 3)
                        //  temp2.add(50.);
                        // else
                        temp2.add(1.);
                        if (y != x)
                            temp.add(choose.nextInt(MAX_DISTANCE) + 1);
                        else
                            temp.add(0);
                    }
                    if(!alreadyHave)
                        costMatrix.add(temp);
                    pherTrails.add(temp2);
                }

                //set initial conditions here. example: ballx = ((getWidth()/2));

                //animate.start();
            }
            started = true;



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

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        for(int x = 1; x < GRID_SIZE; x++){
            int ex = Math.round(x*boxWidth);
            int wy = Math.round(x*boxHeight);

            g2.drawLine(ex,0,ex,Math.round(GRID_SIZE*boxWidth));
            g2.drawLine(0,wy,Math.round(GRID_SIZE*boxHeight),wy);
            g2.setStroke(new BasicStroke(1));
            //NOTE: ASSUMES THAT BOXES ARE SQUARES
            int delta = Math.round((boxWidth/2));
            g2.drawString(""+x,ex+delta,delta);
            g2.drawString(""+x,delta,wy+delta);
            for(int y = 0; y < costMatrix.size(); y++){
                wy = Math.round((y+1)*boxHeight);
                if(costMatrix.get(x-1).get(y) >= 0 && NUMBER_OF_CITIES < 40)
                    g2.drawString(""+costMatrix.get(y).get(x-1),ex+delta,wy+delta);

            }
            //draw grid with g2.drawLine() to build matrix

        }
        for(int x = 0; x < costMatrix.size(); x++){
            for(int y = 0; y < costMatrix.size(); y++){

                for(int h = 0; h < antPath.size(); h++){
                    g2.setColor(new Color(50,255,255,100));
                    int second = h+1;
                    if(second == antPath.size())
                        second = 0;
                    if(antPath.get(h) == y && antPath.get(second) == x) {
                        g2.fillRect(Math.round((x+1)*boxWidth),Math.round((y+1)*boxHeight),Math.round(boxWidth),Math.round(boxHeight));
                        g2.setColor(Color.BLACK);
                        g2.drawString(""+(h+1),Math.round((x+1)*boxWidth),Math.round((y+2)*boxHeight));
                    }

                }

                for(int h = 0; h < cheapestPath2.size(); h++){
                    g2.setColor(new Color(50,235,100,100));
                    int second = h+1;
                    if(second == cheapestPath2.size())
                        second = 0;
                    if(cheapestPath2.get(h) == y && cheapestPath2.get(second) == x) {
                        g2.fillRect(Math.round((x+1)*boxWidth),Math.round((y+1)*boxHeight),Math.round(boxWidth)/2,Math.round(boxHeight)/2);
                        g2.setColor(Color.BLACK);
                        g2.drawString(""+(h+1),Math.round((x+1)*boxWidth),Math.round(((y+1)*boxHeight+(boxHeight/2))));
                    }
                }
                g2.setColor(Color.BLACK);
                if(costMatrix.get(x).get(y) == -1){
                    g2.fillRect(Math.round((x+1)*boxWidth),Math.round((y+1)*boxHeight),Math.round(boxWidth),Math.round(boxHeight));
                }
                g2.setColor(new Color(255,0,0,(int)Math.min(getPheromoneLevel(x,y)*3,255)-1));
                g2.fillOval(Math.round((x+1)*boxWidth),Math.round((y+1)*boxHeight),Math.round(boxWidth),Math.round(boxHeight));
            }
        }

        g2.setColor(Color.BLACK);

        g2.setStroke(new BasicStroke(1));
        //"Point: (" + specX + ", " + ((int)GRID_SIZE-specY )+ ")" +
        g2.drawString("Speed: ~"+(int)(1000/(TIME_SPEED/AntFrame.speed))+" Moves Per Second"
                +". Number of Iterations: "+count, 0, getHeight() - 1);
        g2.setColor(Color.DARK_GRAY);

        g2.drawLine(0, getWidth(), getWidth(), getWidth());

    }

    //mouselistener mthod
    public void mousePressed(MouseEvent e) {
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
    public ArrayList<Integer> findLowest(ArrayList<ArrayList<Integer>> allPaths){
        int lowest = -1;
        int highest = 0;
       // System.out.println("allpaths.size: "+allPaths.size());
        ArrayList<Integer> cheapest = new ArrayList<Integer>();
        for(int i = 0; i < allPaths.size(); i++){
            ArrayList<Integer> path = allPaths.get(i);
            int distance = costOfPath(path);
            if(lowest == -1 || lowest > distance) {
                lowest = distance;
                cheapest = path;
            }
            if(highest < distance)
                highest = distance;

//            System.out.print("Path: ");
//            for(int k = 0; k < path.size(); k++)
//                System.out.print((path.get(k)+1)+" -> ");
//            System.out.println(" Total Cost = "+distance);
        }
        System.out.println(costMatrix.toString());
        System.out.println("\nLowest Total Cost: "+lowest+"\nHighest Total Cost: "+highest);
        return cheapest;
    }
    public void start() {
        //animate.start();
        //cheapestPath2 = findLowest(bruteForcePaths((int) (GRID_SIZE - 1)));

       // ArrayList<Integer> cheapestPath3 = findLowest(bruteForcePaths((int) (GRID_SIZE - 1)));


//        System.out.print("Brute Force Method: { ");
//        for(int i : cheapestPath2){
//            System.out.print((i+1)+" ");
//        }
//        System.out.println("} cost = "+costOfPath(cheapestPath2)+", execution time = "+bruteTimeS);
        //System.out.println("Brute Force Method (without simplification): "+ cheapestPath3.toString() +"cost = "+costOfPath(cheapestPath3)+", execution time = "+bruteTime);
        time = System.currentTimeMillis();
        //bestSolution = costOfPath(cheapestPath2);
        PHEROMONE_DROP_SCALING = averageCost()/1;
        System.out.println("Average cost in current TSP: "+averageCost());
        ArrayList<Integer> dummyPath = new ArrayList<Integer>();
        //bays29: {1, 28, 6, 12, 9, 5, 26, 29, 3, 2, 20, 10, 4, 15, 18, 17, 14, 22, 11, 19, 25, 7, 23, 27, 8, 24, 16, 13, 21 }
        //berlin52 : {1,49,32,45,19,41,8,9,10,43,33,51,11,52,14,13,47,26,27,28,12,25,4,6,15,5,24,48,38,37,40,39,36,35,34,44,46,16,29,50,20,23,30,2,7,42,21,17,3,18,31,22}
        int[] a = {1,49,32,45,19,41,8,9,10,43,33,51,11,52,14,13,47,26,27,28,12,25,4,6,15,5,24,48,38,37,40,39,36,35,34,44,46,16,29,50,20,23,30,2,7,42,21,17,3,18,31,22};
        for(int a0 :a){
            dummyPath.add(a0-1);
        }

       // System.out.println("My Lowest: 2032, Lowest From Internet:"+costOfPath(dummyPath));
        if(NUMBER_OF_CITIES > 20)
            animate.start();
        else
            antAnimate.start();


        //repaint();
    }
    public int averageCost(){
        int total = 0;
        for(ArrayList<Integer> tempArray : costMatrix)
            for(int i : tempArray)
                total+=i;
        return total/(costMatrix.size()*costMatrix.size());
    }
    public ArrayList<Integer> getSmartPath() {
        int numberOfCities = (int) (GRID_SIZE - 1);
        ArrayList<Integer> path = new ArrayList<Integer>();
        ArrayList<Integer> alreadyTravelledTo = new ArrayList<Integer>();
        int num = choose.nextInt(numberOfCities);
        path.add(num);
        alreadyTravelledTo.add(num);
        while (alreadyTravelledTo.size() < numberOfCities) {
            int lowestCost = -1;
            int lowestCostIndex = -1;
//            for (int in = 1; in < numberOfCities; in++)
//                if (!path.contains(in)) {
//                    lowestCost = getCost(path.get(path.size() - 1), in);
//                    lowestCostIndex = in;
//                }
//            for (int i = 1; i < numberOfCities; i++) {
//                //System.out.println(path.contains(i) + "(" + i + ")");
//                if (!path.contains(i)) {
//                    int currentCost = getCost(path.get(path.size() - 1), i);
//                    if (currentCost < lowestCost) {
//                        lowestCost = currentCost;
//                        lowestCostIndex = i;
//
//                    }
//                }
//            }
            boolean noPhers = false;
            if(alreadyTravelledTo.size() == 1 && Math.random() <= 0.01)
                    noPhers = true;
            ArrayList<Double> desirability = new ArrayList<Double>();
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for(int i = 0; i < numberOfCities; i++){
                if(!path.contains(i)){
                    //ALPHA = 3 - Math.abs(Math.sin(count/500));
                    double pherLevel = Math.pow(getPheromoneLevel(path.get(path.size() - 1), i),ALPHA);
                    //pherLevel = 1.;
                    //System.out.println(Math.sin(count/100.)/2+"");
                    double cost = Math.pow(getCost(path.get(path.size() - 1), i),BETA);
                    double desireLevel = (pherLevel/cost);
                    if(noPhers)
                        desireLevel = 1 / cost;
                    //if(cheapestContains(path.get(path.size() - 1), i)) {
                      //  desireLevel *= 2;
                       // System.out.println("contains: ("+(path.get(path.size()-1)+1)+" to "+(i+1)+")");
                   // }

                   // if(count >= 10000)

                      //  desireLevel *= topCheapestScale(path.get(path.size() - 1), i);

                    desirability.add(desireLevel);
                    indexes.add(i);
                }
            }
            //System.out.println();
           // System.out.println(desirability.toString());
            lowestCostIndex = indexes.get(weightedRandomIndex(desirability));
            //System.out.print(lowestCost + " ");
            alreadyTravelledTo.add(lowestCostIndex);
            path.add(lowestCostIndex);
        }
        //System.out.println();
       // for (int i : path)
          //  System.out.println(i + 1);
        return path;

    }
    public boolean cheapestContains(int i, int j){
        boolean contains = false;
        for (int a = 0; a < cheapestPath.size(); a++) {
            int next = a + 1;
            if (a == cheapestPath.size() - 1)
                next = 0;
            if (i == a && j == next)
                contains = true;
            if (i == next && j == a)
                contains = true;
        }
        return contains;
    }
    public double topCheapestScale(int i, int j){
        double scale = 1;
        for(int pathIndex = 0; pathIndex < topCheapest.size(); pathIndex++) {
            boolean contains = false;
            ArrayList<Integer> currentPath = topCheapest.get(pathIndex);
            for (int a = 0; a < currentPath.size(); a++) {
                int next = a + 1;
                if (a == cheapestPath.size() - 1)
                    next = 0;
                if (i == a && j == next)
                    contains = true;
                if (i == next && j == a)
                    contains = true;
            }
            if(contains){
                scale += OPTIMAL_PHEROMONE_SCALING*Math.pow(pathIndex,1.5);
            }
        }
       // System.out.print(scale+" ");
        return scale;
    }

    public double getPheromoneLevel(int from, int to) {
        return pherTrails.get(from).get(to);
    }

    public int weightedRandomIndex(ArrayList<Double> desirabilities){
        double total = 0;
        int chosenIndex = -1;
        //System.out.print("Percentages: [");
        for (int i = 0; i < desirabilities.size(); i++) {
            double num = desirabilities.get(i);
            if(num == Double.POSITIVE_INFINITY)
                return i;
            total += num;
        }
        Random choose = new Random();
        double rand = choose.nextDouble()*total;
        //System.out.print("Rand: " + rand+"... ");
        double temp = 0.;
        for (int i = 0; i < desirabilities.size(); i++) {
            double cost = desirabilities.get(i);
            //System.out.print((cost / total) * 100 + ", ");
            if (temp <= rand && rand <= temp + cost) {
                chosenIndex = i;
            }
            temp += cost;
        }
        //System.out.println("]");
            //System.out.println();
            //try{Thread.sleep(100);}catch(InterruptedException ex){}
        return chosenIndex;
    }
    public int costOfPath(ArrayList<Integer> path){
        int cost = 0;
        for(int i = 0; i < path.size(); i++){
            int a = path.get(i);
            int b;
            if(i+1 == path.size())
                b = path.get(0);
            else
                b = path.get(i+1);
            cost += getCost(a,b);
            //System.out.print(getCost(a,b)+" + ");
        }
       // System.out.println();
        return cost;
    }
    public int getCost(int from, int to) {
        return costMatrix.get(from).get(to);
    }
    public void setEvap(double evap){
        EVAPORATION_COEFFICIENT = evap;
    }
    public void setAlpha(double alpha){
        ALPHA = alpha;
    }
    public void setBeta(double beta){
        BETA = beta;
    }
    public void reset() {
        animate.stop();

        while(animate.isRunning()){}
        costMatrix = null;
        pherTrails.clear();
        cheapestPath.clear();
        cheapestPath2.clear();
        count = 0;
        iterationsSinceLast = 0;
        started = false;
        repaint();
    }
    public void updateSpeed() {
        animate.setDelay((int) (TIME_SPEED/(AntFrame.speed*3.5)));

        repaint();
    }

    public void setPherDrawScaling(int pherDrawScaling) {
        PHER_DRAW_SCALING = pherDrawScaling;
    }
}