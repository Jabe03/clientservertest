

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class ChatWindow implements KeyListener{

    private Messageable host;
    volatile ArrayList<Message> messages;
    String username;
    UUID userId;
    JFrame frame;
    JPanel panel;
    JTextField textField;

    private HashMap<UUID, String> names;
    private static final int messageSpread = 30;
    private static final int leftBorder = 40;
    private static final Color textColor = new Color(161, 161, 161);
    private static final Color accent1 = new Color(0, 104, 122);
    private static final Color accent2 = new Color(34, 78, 117);
    private static final Color backgroundColor = new Color(25, 28, 38);
    public static void main(String[] args){
        //Scanner tsm = new Scanner(System.in);
        System.out.println("What is your name?");
        ChatWindow cw = new ChatWindow("Josh", null, UUID.randomUUID());


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
    public String getNameById(UUID id){
        return names.get(id);
    }
    public ChatWindow(String username, Messageable m, UUID id){
        this.host = m;
        messages = new ArrayList<Message>();
        names = new HashMap<>();
        this.username = username;
        this.userId = id;
        addUserById(userId,username);
        System.out.println("Username: " + username);
        initFrame();

    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(600, 600));
        this.frame.setSize(new Dimension(600, 600));
        this.frame.setName(this.username.charAt(this.username.length() - 1) == 's' ? this.username + "'" : this.username + "'s");
        System.out.println(this.frame.getName());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.frame.addKeyListener(this);
        this.frame.requestFocus();
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


        this.frame.add(this.panel);
        textField = new JTextField(frame.getWidth()-30);
        //textField.setFocusable(false);
        panel.setLayout(new BorderLayout());
        panel.add(textField, BorderLayout.SOUTH);
        textField.addKeyListener(this);
        this.frame.setVisible(true);

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
        g.fillRoundRect(leftBorder-2, messageSpread*messageNum+35, messageLength + nameLength + 10 + 2 + 5, 20, 10 , 10);
        g.setColor(accent2);
        g.fillRoundRect(leftBorder  + nameLength-5+10, messageSpread*messageNum + 35,  messageLength + 10, 20, 10, 10);
        g.setColor(textColor);
        g.drawString(senderName, leftBorder+3, messageSpread*messageNum + 50);
        g.drawString(messageText, leftBorder + (nameLength)+10, messageSpread * messageNum +50);

    }

    public void addMessage(Message m){
        messages.add(m);
        sendMessageToHost("updateMessages");
        panel.repaint();
    }
    public void setMessages(ArrayList<Message> messages){
        this.messages = messages;
        panel.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed");
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_ENTER){
            String text = textField.getText();
            textField.setText("");
            sendMessageToHost(text);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    public void sendMessageToHost(String m){
        if(host == null){
            if(!m.equals("updateMessages"))
                addMessage(new Message(m, userId));
        } else {
            if(m.equals("updateMessages")){
                host.sendMessage(new Message(null, userId, m));
            } else {
                host.sendMessage(new Message(m, userId));
            }
        }
    }
}
