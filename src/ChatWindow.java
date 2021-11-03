import javax.swing.*;
import java.util.ArrayList;

public class ChatWindow {
    private JFrame frame;
    private JPanel panel;
    boolean isServerWindow;
    String name;
    private ArrayList<Message> messages;

    public ChatWindow(String clientName, boolean isServerWindow){
        this.name = clientName;
        this.isServerWindow = isServerWindow;
        frame = new JFrame();
        setupFrame();
        panel = new JPanel();
        frame.add(panel);
    }

    public void setupFrame(){

    }



}
