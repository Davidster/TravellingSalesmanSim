
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener{
    JButton openButton;
    JLabel label;
    DisplayMode displayMode;

    public MainFrame() {
        super();

        setVisible(true);
        setLayout(new GridLayout(2, 0));
        displayMode = new DisplayMode(0, 0, 8, 75);

        openButton = new JButton("Click To Open");
        openButton.setActionCommand("AntOpener");

        label = new JLabel("Travelling Salesman Problem Solver");
        label.setHorizontalAlignment(JLabel.CENTER);

        openButton.addActionListener(this);

        add(label);
        add(openButton);

        setFocusable(true);
    }


    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("AntOpener")){
            dispose();
            GraphFrame frame = new GraphFrame();

            frame.run(displayMode);
           // frame.pack();
        }
    }
    public static void main(String []args){
        MainFrame main = new MainFrame();
        main.pack();
        String s = "\"Instructions: Upon opening the program, you can select points by clicking on the white area" +
                "                 or using the provided buttons in order to construct the problem. Conversely, you can refrain from adding any custom \n" +
                "                points in which case the file that is defined in the constant String named currentFile within the class: GraphFrame will be used. " +
                "                In order for this to work, you must also call the String named FILE_TYPE either 'MATRIX' or 'EUCLID' depending on the format of the file " +
                "               (for the MATRIX type file, the dimension must also be included in the constant int DIMENSION). " +
                "                Euclid means that it is composed of a set of points, corresponding to a 2d map, and Matrix means that is is defined as a matrix of costs. " +
                "                In order to commence the solving of the problem, click on the button that reads 'Display Matrix' and then click start. " +
                "                It is reccomended to set the speed slider on the matrix window to the top in order to maximize the amount of iterations per second computed by the algorithm. " +
                "                It is also important to note that if 20 or less points are contained within the map (AKA matrix = 20x20), " +
                "                a visual demonstration of the ant agent used in each iteration will be displayed, slowing down to algorithm immensely. In order for the percent difference to be meaningful," +
                "                the variable FILE_SOLUTION must be set to the correct known value (as located from TSPLIB website)";
        String html1 = "<html><body style='width: ";
        String html2 = "px'>";

        JOptionPane.showMessageDialog(null, new JLabel(html1+"300"+html2+s));

    }
}
