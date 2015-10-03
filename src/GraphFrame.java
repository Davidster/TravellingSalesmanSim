import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphFrame extends JFrame implements ActionListener, ChangeListener{
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    public static float speed = 1;
    private final JButton randomCircle,randomize;
    private JPanel difficulties;
    private JSlider evapSlider,alphaSlider,betaSlider,pheroSlider;
    private AntPanel aPanel;
    private ScreenManager s;
    DisplayMode display;
    private GraphPanel panel;
    private AntFrame frame;
    static final String currentFile = "berlin52.txt";
    static final String FILE_TYPE = "EUCLID";
    static final int DIMENSION = 43;

    public GraphFrame(){
        super("Graph Creator");
        setLayout(new BorderLayout());

        display = new DisplayMode(0,0,8,75);

        JButton reset = new JButton("Reset");
        reset.addActionListener(this);
        JButton displayMatrix = new JButton("Display Matrix");
        displayMatrix.addActionListener(this);
        JButton start = new JButton("Refresh");
        start.addActionListener(this);
        randomize = new JButton("Add Random Points");
        randomize.addActionListener(this);
        randomize.setActionCommand("Random Graph");
        randomCircle = new JButton("Add Random Circle");
        randomCircle.addActionListener(this);
        randomCircle.setActionCommand("Random Circle");

        evapSlider = new JSlider();
        alphaSlider = new JSlider();
        betaSlider = new JSlider();
        pheroSlider = new JSlider();

        evapSlider.setMinimum(0);
        evapSlider.setMaximum(100);
        alphaSlider.setMinimum(0);
        alphaSlider.setMaximum(8);
        betaSlider.setMinimum(0);
        betaSlider.setMaximum(8);
        pheroSlider.setMaximum(30);
        pheroSlider.setMinimum(1);

        evapSlider.setSnapToTicks(false);
        alphaSlider.setSnapToTicks(false);
        betaSlider.setSnapToTicks(false);
        pheroSlider.setSnapToTicks(false);

        evapSlider.addChangeListener(this);
        alphaSlider.addChangeListener(this);
        betaSlider.addChangeListener(this);
        pheroSlider.addChangeListener(this);

        evapSlider.setPaintTicks(true);
        alphaSlider.setPaintTicks(true);
        betaSlider.setPaintTicks(true);

        evapSlider.setMajorTickSpacing(10);
        evapSlider.setMinorTickSpacing(5);
        alphaSlider.setMajorTickSpacing(2);
        alphaSlider.setMinorTickSpacing(1);
        betaSlider.setMajorTickSpacing(2);
        betaSlider.setMinorTickSpacing(1);



        evapSlider.setPaintLabels(true);
        alphaSlider.setPaintLabels(true);
        betaSlider.setPaintLabels(true);

        evapSlider.setOrientation(JSlider.VERTICAL);
        alphaSlider.setOrientation(JSlider.VERTICAL);
        betaSlider.setOrientation(JSlider.VERTICAL);
        pheroSlider.setOrientation(JSlider.HORIZONTAL);

        difficulties = new JPanel(new GridLayout());
        difficulties.add(reset);
        difficulties.add(start);
        difficulties.add(displayMatrix);

        JPanel right = new JPanel(new GridLayout());
        JPanel bottom = new JPanel(new GridLayout(3,1));
        JPanel randoms = new JPanel(new GridLayout(1,2));
        GridLayout settingsLayout = new GridLayout(3,2);
        JPanel settings = new JPanel(settingsLayout);


        JLabel e = new JLabel("Evap");
        JLabel a = new JLabel("Alpha");
        JLabel b = new JLabel("Beta");
        JLabel p = new JLabel("Pheromone Color Scaling");

        settings.add(e);
        settings.add(evapSlider);
        settings.add(a);
        settings.add(alphaSlider);
        settings.add(b);
        settings.add(betaSlider);

        randoms.add(randomize);
        randoms.add(randomCircle);

        bottom.add(p);
        bottom.add(pheroSlider);
        bottom.add(randoms);

        right.add(settings);
        //right.add(randoms);

        panel = new GraphPanel(this);

        evapSlider.setValue(33);
        alphaSlider.setValue(6);
        betaSlider.setValue(4);
        pheroSlider.setValue(3);

        add("North", difficulties);
        add("East",right);
        add("Center",panel);
        add("South",bottom);

        //divideLabels(evapSlider,100.f);
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0.0") );
        labelTable.put( new Integer( 10 ), new JLabel("0.1") );
        labelTable.put( new Integer( 20 ), new JLabel("0.2") );
        labelTable.put( new Integer( 30 ), new JLabel("0.3") );
        labelTable.put( new Integer( 40 ), new JLabel("0.4") );
        labelTable.put( new Integer( 50 ), new JLabel("0.5") );
        labelTable.put( new Integer( 60 ), new JLabel("0.6") );
        labelTable.put( new Integer( 70 ), new JLabel("0.7") );
        labelTable.put( new Integer( 80 ), new JLabel("0.8") );
        labelTable.put( new Integer( 90 ), new JLabel("0.9") );
        labelTable.put( new Integer( 100 ), new JLabel("1.0") );
        evapSlider.setLabelTable(labelTable);
        divideLabels(alphaSlider,2.f);
        divideLabels(betaSlider,2.f);
    }

    private void divideLabels(JSlider jSlider, float factor) {
        Enumeration e = jSlider.getLabelTable().keys();

        while (e.hasMoreElements()) {
            Integer i = (Integer) e.nextElement();
            JLabel label = (JLabel) jSlider.getLabelTable().get(i);
            int newNumb = Math.round(10*i/factor);
            label.setText(String.valueOf(newNumb / 10));
            //System.out.println(label.getText());
        }
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand() == "Display Matrix"){
            ArrayList<ArrayList<Integer>> costMatrix;
            if(panel.points.isEmpty()) {
                costMatrix = panel.displayMatrix("C:\\Users\\User\\Desktop\\" + currentFile, DIMENSION, FILE_TYPE);
                //costMatrix = panel.displayMatrix(System.getProperty("user.dir") + currentFile, DIMENSION, FILE_TYPE);
                CityPanel.BEST_SOLUTION = CityPanel.FILE_SOLUTION;
            }
            else {
                costMatrix = panel.displayMatrix();
                CityPanel.BEST_SOLUTION = 0;
            }
            //panel.displayMatrix("C:\\Users\\User\\Desktop\\ftv33.txt");
            frame = new AntFrame(costMatrix);
            frame.panel.giveGraphPanel(panel);

            frame.run(display);
            frame.setLocation(500,0);
            panel.matrixDisplayed = true;
            updateSliders();
            //pong.setSpeed(4,1);
        }
        if(e.getActionCommand() == "Reset"){
            panel.reset();
            panel.matrixDisplayed = false;

            if(frame != null) {
                frame.panel.reset();
                frame.dispose();
            }
            //randomize.setEnabled(true);
            //randomCircle.setEnabled(true);
            //pong.setSpeed(8,3);
        }
        if(e.getActionCommand() == "Refresh"){
            panel.refresh();
        }
        if(e.getActionCommand() == "Random Graph"){
            if(!panel.matrixDisplayed)
                panel.randomize();
            //pong.setSpeed(8,3);
        }
        if(e.getActionCommand() == "Random Circle"){
            if(!panel.matrixDisplayed)
                panel.randomC();
            //pong.setSpeed(8,3);
        }

    }

    public void run(DisplayMode dm){
        s = new ScreenManager();
        //s.setFullScreen(dm,this);
        setVisible(true);
        setSize(500,500);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        panel.repaint();
        if(frame != null)
            updateSliders();
       // panel.updateSpeed();
    }

    private void updateSliders() {
        frame.panel.setEvap(evapSlider.getValue()/100.);
        frame.panel.setAlpha(alphaSlider.getValue()/2.);
        frame.panel.setBeta(alphaSlider.getValue()/2.);
        frame.panel.setPherDrawScaling(pheroSlider.getValue());
    }
    public double getAlpha(){
        return alphaSlider.getValue()/2.;
    }
    public double getBeta(){
        return betaSlider.getValue()/2.;
    }
    public double getEvap(){
        return evapSlider.getValue()/100.;
    }

}
