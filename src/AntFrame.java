import sun.awt.WindowClosingListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AntFrame extends JFrame implements ActionListener, ChangeListener{
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    public static float speed = 1;
    private JPanel difficulties;
    private JSlider slider;
    public CityPanel panel;
    private AntPanel aPanel;
    private ScreenManager s;
    public AntFrame(){
		super("Ant Simulator");
        setLayout(new BorderLayout());


        JButton reset = new JButton("Reset");
        reset.addActionListener(this);
        JButton start = new JButton("Start");
        start.addActionListener(this);
        slider = new JSlider();
        slider.setMinimum(1);
        slider.setMaximum(100);
        slider.setSnapToTicks(false);
        slider.addChangeListener(this);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(20);
        slider.setOrientation(JSlider.VERTICAL);

        difficulties = new JPanel(new GridLayout());
        difficulties.add(reset);
        difficulties.add(start);

        panel = new CityPanel();
        aPanel = new AntPanel();

        slider.setValue(1);

        add("North", difficulties);
        add("East",slider);
        add("Center",panel);
    }
    public AntFrame(ArrayList<ArrayList<Integer>> costMatrix){
        super("Ant Simulator");
        setLayout(new BorderLayout());


        JButton reset = new JButton("Reset");
        reset.addActionListener(this);
        JButton start = new JButton("Start");
        start.addActionListener(this);
        slider = new JSlider();
        slider.setMinimum(1);
        slider.setMaximum(100);
        slider.setSnapToTicks(false);
        slider.addChangeListener(this);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(20);
        slider.setOrientation(JSlider.VERTICAL);

        difficulties = new JPanel(new GridLayout());
        //difficulties.add(reset);
        difficulties.add(start);

        panel = new CityPanel(costMatrix);
        aPanel = new AntPanel();

        slider.setValue(1);

        add("North", difficulties);
        add("East",slider);
        add("Center",panel);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand() == "Start"){
           // ArrayList<Integer> path = panel.start();
            panel.start();
            //pong.setSpeed(4,1);
        }
        if(e.getActionCommand() == "Reset"){
            panel.reset();
           //pong.setSpeed(8,3);
        }

    }

    public void run(DisplayMode dm){
        s = new ScreenManager();
       // s.setFullScreen(dm,this);
        setVisible(true);
        setSize(500,500);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        speed = slider.getValue();
        panel.updateSpeed();
    }

}
