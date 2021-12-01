

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class ChatWindow implements KeyListener{

    private Host host;
    volatile ArrayList<Message> messages;
    String username;
    UUID userId;
    JFrame frame;
    JPanel panel;
    JTextField textField;

    private HashMap<UUID, String> names;
    private static final int messageSpread = 10;
    private static final int leftBorder = 40;
    private static final Color textColor = new Color(200, 200, 200);
    private static final Color accent1 = new Color(0, 104, 122);
    private static final Color accent2 = new Color(34, 78, 117);
    private static final Color accent3 = new Color(14, 119, 59);
    private static final Color backgroundColor = new Color(25, 28, 38);
    private static final Color statusMessageColor = new Color(252, 255, 105);
    public static void main(String[] args){
        //Scanner tsm = new Scanner(System.in);
        System.out.println("What is your name?");
        ChatWindow cw = new ChatWindow("Josh", null, UUID.randomUUID());


       openTerminalForInputs(cw);
    }
    @Deprecated
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
    public ChatWindow(String username, Host m, UUID id){
        this.host = m;
        messages = new ArrayList<Message>();
        names = new HashMap<>();
        this.username = username;
        this.userId = id;
        addUserById(userId,username);
        System.out.println("Username: " + username);
        initFrame();

    }

    public HashMap<UUID, String> getParticipants() {
        return names;
    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(600, 600));
        this.frame.setSize(new Dimension(600, 600));
        this.frame.setName(this.username.charAt(this.username.length() - 1) == 's' ? this.username + "'" : this.username + "'s");
        System.out.println(this.frame.getName());
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("disconnecting");
                host.disconnect();
                host.stop();
            }
        });
        //this.frame.addKeyListener(this);
        this.frame.requestFocus();
        this.initPanel();
    }
    private void initPanel() {
        this.panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.setColor(backgroundColor);
                g.fillRect(0,0,frame.getWidth(), frame.getHeight());
                int yCursor = 35; //messages start on y=35
                for(Message m : ChatWindow.this.messages) {
                    yCursor = ChatWindow.this.paintMessage(m, g, yCursor);
                }
                g.setColor(accent3);
                int usernameLength = g.getFontMetrics().stringWidth(username);
                g.fillRoundRect(2, this.getHeight()-32, usernameLength+10 , 20, 5, 5);
                g.setColor(textColor);
                g.drawString(username, 7, this.getHeight()-22);
            }
        };


        this.frame.add(this.panel);
        textField = new JTextField(frame.getWidth()-30);
        //textField.setBounds(100,100,100,100);
        //textField.setFocusable(false);
        panel.setLayout(new BorderLayout());
        panel.add(textField, BorderLayout.SOUTH);
        textField.addKeyListener(this);
        this.frame.setVisible(true);

    }


    public void addUserById(UUID id, String name){
        names.put(id, name);
        System.out.println(names);
    }
    private int paintMessage(Message m, Graphics g, int yCursor){
        if(m.isTextMessage()){
            if(Server.isServerId(m.getID())){
                yCursor = drawStatusMessage(m,g,yCursor);
            } else {
                yCursor = drawTextMessage(m, g, yCursor);
            }
        } else{
            switch(m.getText()){
                case "serverMessage":
                case "userJoining":
                case "userLeaving":
                    yCursor = drawStatusMessage(m,g,yCursor);
                    break;
                default:
                    System.out.println("(123)Unknown command: " + m.getText());
            }
        }
        return yCursor;
    }
    public int drawStatusMessage(Message m, Graphics g, int yCursor ){
        yCursor+=2;
        g.setColor(statusMessageColor);
        String messageText = generateStatusMessage(m);
        g.drawString(messageText, leftBorder, yCursor + 5);
        yCursor+= 12;
        return yCursor;

    }
    public String generateStatusMessage(Message m){
        //System.out.println(m);
        if(Server.isServerId(m.getID()) && m.isTextMessage()){
            return "Server: " + m.getText();
        }
        return switch (m.getText()) {
            case "userJoining" ->
                    //System.out.println(m.getID());
                    names.get(m.getObjectMessage()) + " has joined.";
            case "userLeaving" ->
                    //System.out.println("(146) user leaving has Name and ID: " + names.get(m.getID()) + ", "+ m.getObjectMessage());
                    //System.out.println(names);
                    names.get(m.getObjectMessage()) + " has left.";
            default -> "unknown server command";
        };
    }
    private int drawTextMessage(Message m, Graphics g, int yCursor){
        String senderName = names.get(m.getID());
        if(senderName == null) senderName = "Unknown user";
        String messageText = m.getText();
        int messageLength = g.getFontMetrics().stringWidth(messageText);
        int nameLength = g.getFontMetrics().stringWidth(senderName);
        g.setColor(accent1);
        g.fillRoundRect(leftBorder-2, yCursor, messageLength + nameLength + 10 + 2 + 5, 20, 10 , 10);
        g.setColor(accent2);
        g.fillRoundRect(leftBorder  + nameLength-5+10, yCursor,  messageLength + 10, 20, 10, 10);
        g.setColor(textColor);
        g.drawString(senderName, leftBorder+3, yCursor + 15);
        g.drawString(messageText, leftBorder + (nameLength)+10, yCursor +15);
        yCursor+= 20 + messageSpread;
        return yCursor;
    }

    public void addMessage(Message m){
        //System.out.println("cw recieved massage: " + m);
        messages.add(m);
        sendMessageToHost("updateMessages");
        panel.repaint();
        //System.out.println(messages);
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
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_ENTER){
            if(!textField.getText().equals("")) {
                String text = textField.getText();
                textField.setText("");
                sendMessageToHost(text);
            }
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

    public void setParticipants(HashMap<UUID, String> newNames) {
        names = newNames;
    }

}
