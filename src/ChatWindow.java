

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class ChatWindow {

    ArrayList<Message> messages;
    String username;
    JFrame frame;
    JPanel panel;
    JTextField textField;

    private HashMap<UUID, String> names;
    private static int messageSpread = 30;
    private static int leftBorder = 40;
    private static Color textColor = new Color(161, 161, 161);
    private static Color accent1 = new Color(0, 104, 122);
    private static Color accent2 = new Color(34, 78, 117);
    private static Color backgroundColor = new Color(25, 28, 38);
    public static void main(String[] args){
        //Scanner tsm = new Scanner(System.in);
        System.out.println("What is your name?");
        ChatWindow cw = new ChatWindow("Josh");


       openTerminalForInputs(cw);
    }
    private static void openTerminalForInputs(ChatWindow cw){
        UUID id = UUID.randomUUID();
        cw.addUserById(id, "Josh");
        //poopcw.addMessage(new Message("Hello!!!" , id));
        System.out.println("New user" + cw.names.get(id));
        while(true) {
            Scanner tsm = new Scanner(System.in);
            cw.addMessage(new Message(tsm.nextLine(), id));
        }
    }
    public ChatWindow(String username){
        messages = new ArrayList<Message>();
        names = new HashMap<>();
        this.username = username;
        System.out.println("Username: " + username);
        initFrame();

    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(600, 600));
        this.frame.setSize(new Dimension(600, 600));
        this.frame.setName(this.username.charAt(this.username.length() - 1) == 's' ? this.username + "'" : this.username + "'s");
        this.frame.setDefaultCloseOperation(3);

        this.initPanel();
    }

    private void initPanel() {
        this.panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.setColor(backgroundColor);
                g.fillRect(0,0,frame.getWidth(), frame.getHeight());
                for(int i = 0; i < ChatWindow.this.messages.size(); ++i) {
                    ChatWindow.this.paintMessage(g, i);
                }

            }
        };

        this.frame.setVisible(true);
        this.frame.add(this.panel);
        textField = new JTextField(frame.getWidth()-30);
        textField.setBounds(new Rectangle((frame.getWidth()-30)/2, frame.getHeight()-50, frame.getWidth()-30, 30));
        textField.setLocation((30/2), frame.getHeight()-100);
        panel.setLayout(new FlowLayout());
        panel.add(textField);
        panel.repaint();
    }

    public void addUserById(UUID id, String name){
        names.put(id, name);
    }
    private void paintMessage(Graphics g, int messageNum){
        Message m = messages.get(messageNum);
        String senderName = names.get(m.getID());
        if(senderName == null) senderName = "Unknown user";
        String messageText = m.getText();
        int messageLength = g.getFontMetrics().stringWidth(messageText);
        int nameLength = g.getFontMetrics().stringWidth(senderName);
        g.setColor(accent1);
        g.fillRoundRect(leftBorder-2, messageSpread*messageNum+35, messageLength + nameLength + 10 + 2 + 5, 20, 5 , 5);
        g.setColor(accent2);
        g.fillRoundRect(leftBorder  + nameLength-5+10, messageSpread*messageNum + 35,  messageLength + 10, 20, 5, 5);
        g.setColor(textColor);
        g.drawString(senderName, leftBorder, messageSpread*messageNum + 50);
        g.drawString(messageText, leftBorder + (nameLength)+10, messageSpread * messageNum +50);

    }

    public void addMessage(Message m){
        messages.add(m);
        panel.repaint();
    }
}
