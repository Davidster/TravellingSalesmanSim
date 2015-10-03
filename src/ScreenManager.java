import java.awt.*;
import javax.swing.*;
/**
 * Write a description of class Screen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScreenManager{
    private GraphicsDevice vc;
    //give vc access to monitor
    public ScreenManager(){
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        vc = env.getDefaultScreenDevice();
    }
    //get all compatible dm
    public DisplayMode[] getCompatibleDisplayModes(){
        return vc.getDisplayModes();
    }
    //compares DM passed in to vc DM and see if they match
    public DisplayMode findFirstCompatibleMode(DisplayMode[] modes){
        DisplayMode goodModes[] = vc.getDisplayModes();
        for(int x = 0; x < modes.length; x++)
            for(int y = 0; y < goodModes.length; y++)
                if(displayModesMatch(modes[x],goodModes[y]))
                    return modes[x];      
        return null;
    }
    //get current DM
    public DisplayMode getCurrentDisplayMode(){
        return vc.getDisplayMode();
    }
    //check if two modes match eatch other
    public boolean displayModesMatch(DisplayMode m1, DisplayMode m2){
        if(m1.getWidth() != m2.getWidth() || m1.getHeight() != m2.getHeight())
            return false;
        if(m1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && m2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && m1.getBitDepth() != m2.getBitDepth())
            return false;
        if (m1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && m2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && m1.getRefreshRate() != m2.getRefreshRate())
            return false;
		return true;
    }
    public void setFullScreen(DisplayMode dm, JFrame f){
       // f.setUndecorated(true);
        //f.setResizable(true);
        vc.setFullScreenWindow(f);
        
        if(dm != null && vc.isDisplayChangeSupported()){
            try{
                vc.setDisplayMode(dm);
            }
            catch(Exception ex){}
        }
    }
    public Window getFullScreenWindow(){
        return vc.getFullScreenWindow();
    }
    public void restoreScreen(){
        Window w = vc.getFullScreenWindow();
        if(w != null){
            w.dispose();
        }
        vc.setFullScreenWindow(null);
    }
}
