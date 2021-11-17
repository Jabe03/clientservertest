

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

public class Client implements Messageable {
    public static void main(String[] args) throws Exception {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("What is the IP?");
//        String addr = sc.nextLine();
//        System.out.println("Whats is the port?");
//        int port = sc.nextInt();
//        Client c = new Client(addr, port);
        Client c = new Client("108.160.228.94", 5656);

    }

    private Socket sr;
    private InputStream is;
    private OutputStream os;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    String clientName;
    UUID clientId;
    ChatWindow cw;

    public Client(String address, int port) throws IOException {
        Scanner sc = new Scanner(System.in);
        clientName = sc.nextLine();
        sc.close();
        establishConnection(address, port);
        sendMessage(new Message(clientName, null));
        startRuntimeChat();
    }
    public void startRuntimeChat()throws IOException{
        clientId = (UUID)readObject();
        cw = new ChatWindow(clientName, this, clientId);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    try {
                        System.out.println("Listening for data...");
                        process(ois.readObject());
                    } catch (IOException | ClassNotFoundException e){
                        System.out.println("Lost connection to server");
                        System.exit(1);
                    }
                }
            }
        });
        t.setName("Chat listener");
        t.start();
    }
    public void process(Object message){
        if(message instanceof Message){
            Message m = (Message)message;

            if(m.isTextMessage()){
                System.out.println("wtf to do with this " + m);
            } else {
                if(m.getText().equals("updatedMessages")){
                    System.out.println("Updated messages: " + m.getObjectMessage());
                    cw.setMessages((ArrayList<Message>)m.getObjectMessage());
                } else if (m.getText().equals("updatedClients")){
                    cw.setParticipants((HashMap<UUID, String>)m.getObjectMessage());
                }
            }
        }
    }
    public void establishConnection(String address, int port) throws IOException {
        sr = new Socket(address, port);
        is = sr.getInputStream();
        os = sr.getOutputStream();
        oos = new ObjectOutputStream(os);
        ois = new ObjectInputStream(is);
    }

//    public void sendMessage(String msg) throws IOException {
//
//        byte[] b = msg.getBytes();
//        os.write(b);
//        System.out.println("Message sent!");
//        System.out.println("MSg sent!");
//
//    }

    public Object readObject(){
        try{
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public void sendObject(Object o)  {
        try {
            oos.writeObject(o);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message m) {
        sendObject(m);
    }


}
