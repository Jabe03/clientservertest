

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatWindow implements KeyListener{

    private final Host host;
    volatile ArrayList<Message> messages;
    String username;
    UUID userId;
    JFrame frame;
    JPanel panel;
    JTextField textField;
    private HashMap<UUID, String> names;
    String ip;
    Integer port;


    int state;

    private static final int inputtingUsername = 0;
    private static final int connectingToAServer = 1;
    private static final int runtimeChat = 2;
    private static final int inputtingServerAddress = 3;

    int currentDataInput;

    private static final int gettingUsername = 0;
    private static final int gettingIP = 1;
    private static final int gettingPassword = 2;
    private static final int sendingMessage = 3;
    private static final int gettingPort = 4;

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

    }

//    public String getNameById(UUID id){
//        return names.get(id);
//    }
    public ChatWindow(String username, Host m, UUID id){
        this.host = m;
        messages = new ArrayList<>();
        names = new HashMap<>();
        this.username = username;
        this.userId = id;
        addUserById(userId,username);
        System.out.println("Username: " + username);
        ip = "unused";
        initFrame();
        checkState();
    }
    public ChatWindow(Host m){
        this.host = m;
        messages = new ArrayList<>();
        names = new HashMap<>();


//        addUserById(userId,username);

        initFrame();
        checkState();


    }

    public HashMap<UUID, String> getParticipants() {
        return names;
    }

    private void initFrame() {
        this.frame = new JFrame();
        this.frame.setPreferredSize(new Dimension(600, 600));
        this.frame.setSize(new Dimension(600, 600));
        this.frame.setName("JoshChat");

        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if(host.hasConnection()) {
                    System.out.println("disconnecting");
                    host.disconnect();
                }
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
                if(username != null) {
                    g.setColor(accent3);
                    int usernameLength = g.getFontMetrics().stringWidth(username);
                    g.fillRoundRect(2, this.getHeight() - 32, usernameLength + 10, 20, 5, 5);
                    g.setColor(textColor);
                    g.drawString(username, 7, this.getHeight() - 22);
                }
            }
        };


        this.frame.add(this.panel);
        textField = new JTextField();
        panel.setLayout(new BorderLayout());
        panel.add(textField, BorderLayout.SOUTH);
        textField.addKeyListener(this);
        this.frame.pack();
        this.frame.setVisible(true);

    }

    private void askForUsername(){
        messages.add(new Message("Username? (type and hit enter)", null, "chatWindowMessage"));
        currentDataInput = gettingUsername;
    }
    private void askForIP(){
        messages.add(new Message("IP of server?? (type and hit enter)", null, "chatWindowMessage"));
        currentDataInput = gettingIP;

    }
    private void askForPort(){
        System.out.println("Asking for port....");
        messages.add(new Message("Port of server?? (hit enter for default port(5656))", null, "chatWindowMessage"));
        currentDataInput = gettingPort;
    }

    private void setUsername(String u){
        this.username = u;
        host.setName(u);
        frame.repaint();
        checkState();
    }
    private void setIP(String ip){
        System.out.println("SetIP called!");
        this.ip = ip;
        checkState();
    }
    private void setPort(String port){
        if(port.equals("")){
            this.port = 5656;
            checkState();
            return;
        }
        try{
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e){
            messages.add(new Message("Invalid port, enter again", null, "chatWindowMessage"));
            frame.repaint();
            return;
        }
        checkState();

    }
    private void setPassword(String pw){
        //TODO implement this sometime
    }
    private void checkState(){

        if(username == null){
            state = inputtingUsername;
        } else if(!host.hasConnection()){
            if(port == null || ip == null){
                state = inputtingServerAddress;
            } else{
                state = connectingToAServer;
            }
        }   else{
            state = runtimeChat;
        }
        System.out.println("Current state is: " + state);

        switch(state){
            case inputtingUsername:
                askForUsername();
                break;
            case connectingToAServer:
                messages.clear();
                host.getConnection(ip, port);
                checkState();
                break;
            case inputtingServerAddress:
                System.out.println("Current IP is: " + ip + ", port: " + port);
                if(ip == null) {
                    askForIP();
                }
                else if(port == null) {
                    askForPort();
                }
                break;
            case runtimeChat:
                currentDataInput = sendingMessage;
                break;
        }
        frame.repaint();
        System.out.println("Current data input is" + currentDataInput);

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
                case "chatWindowMessage":
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
//        if(Server.isServerId(m.getID()) && m.isTextMessage()){
//            return "Server: " + m.getText();
//        }
        return switch (m.getText()) {
            case "serverMessage" ->
                    "server: " + m.getObjectMessage();
            case "userJoining" ->
                    //System.out.println(m.getID());
                    names.get(m.getObjectMessage()) + " has joined.";
            case "userLeaving" ->
                    //System.out.println("(146) user leaving has Name and ID: " + names.get(m.getID()) + ", "+ m.getObjectMessage());
                    //System.out.println(names);
                    names.get(m.getObjectMessage()) + " has left.";
            case "chatWindowMessage" ->
                    (String)m.getObjectMessage();
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
            if(!textField.getText().equals("") || currentDataInput==gettingPort) {
                sendAndClearTextField();
            }
        }
    }
    public void sendAndClearTextField(){
        String text = textField.getText();
        textField.setText("");
        sendTextFieldData(text);
    }
    private void sendTextFieldData(String data){
        switch(currentDataInput){
            case sendingMessage -> sendMessageToHost(data);
            case gettingUsername -> setUsername(data);
            case gettingIP -> setIP(data);
            case gettingPort -> setPort(data);
            case gettingPassword -> setPassword(data);
            default -> System.out.println("\u001B[31mWTF!?!?!??!?!?!?");
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
