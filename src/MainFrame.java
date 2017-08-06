
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class MainFrame extends JFrame implements ActionListener{
    public MainFrame() {
        super();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());

        JButton launchButton = new JButton("Launch TSP Ant Solver");
        launchButton.setActionCommand("launch_tsp_ant_solver");

        /*
         * get greeting text from file
         */
        String greeting_text_html_formatted = "<html><body>Default<br>Greeting</body></html>";
        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/../res/greeting_text.html"));
            String line = br.readLine();
            greeting_text_html_formatted = "";
            while (line != null) {
                greeting_text_html_formatted += line;
                line = br.readLine();
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        JLabel label = new JLabel(greeting_text_html_formatted);
        label.setHorizontalAlignment(JLabel.CENTER);

        launchButton.addActionListener(this);

        add(label, BorderLayout.CENTER);
        add(launchButton, BorderLayout.PAGE_END);

        setFocusable(true);
    }


    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("launch_tsp_ant_solver")){
            dispose();

            GraphFrame frame = new GraphFrame();
            frame.run(new DisplayMode(0, 0, 8, 75));
        }
    }
    public static void main(String []args){
        MainFrame greetingFrame = new MainFrame();
        greetingFrame.pack();
    }
}
