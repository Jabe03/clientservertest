import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Closeable, Host{

    private boolean running;

    public static void main(String[] args) throws Exception {
        new Server(4444);
    }

    ServerSocket ss;
    Socket sr;
    OutputStream os;
    //ObjectInputStream ois;
    //InputStream is;
    ChatWindow cw;
    static Server instance;
    public static final UUID serverID = UUID.randomUUID();
    final private ArrayList<RemoteClient> clients;
    volatile ArrayList<Object> packets;

    private Server(int port) throws IOException {

        instance = this;
        clients = new ArrayList<>();
        packets = new ArrayList<>();
        startServer(port);
    }
    /*
    private Server()throws  IOException{
        instance = this;
        Scanner tsm = new Scanner(System.in);
        clients = new ArrayList<>();
        System.out.println("What port?");
        packets = new ArrayList<Object>();

        startServer(tsm.nextInt());
        //startProcessing();


        //Server.getInstance().addPacket("starting 40");
        //System.out.println(this);
    }
    */
    public void startServer(int port) throws IOException{
        cw = new ChatWindow("server", this,serverID);
        startProcessing();
        getConnection(port);
    }
    public void addPacket(Object p){
        packets.add(p);

    }
    public void addClient(RemoteClient c) {
        clients.add(c);
        cw.addUserById(c.id,c.name);
    }
    public static Server getInstance(){
        return instance;
    }
    @Override
    public void close() throws IOException {
        os.close();
        sr.close();
        ss.close();
    }

    public void getConnection(int port) throws IOException {
        //System.out.println("running");
        ss = new ServerSocket(port);
        running = true;
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    sr = ss.accept();
                    System.out.println("accepted");
                    addClient(new RemoteClient(sr));
                    packets.add("Starting char");
                    updateClients();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //System.out.println("Done");
        });
        t.setName("ConnectionSearching");
        t.start();
    }
    public synchronized void startProcessing(){
        Thread t = new Thread(() -> {
            while (true) {
                processPackets();
            }
        });
        t.setName("ServerProcess");
        t.start();
    }

    private void processPackets(){
        //System.out.println("About to check...");
                while (!packets.isEmpty()) {
                    Object packet = packets.get(0);
                    if(packet instanceof Message m){
                        if(m.isTextMessage()){
                            switch(m.getText()){
                                default:
                                    cw.addMessage(m);
                            }

                        } else{
                            switch(m.getText()) {
                                case "updateMessages":
                                    sendOutUpdatedMessageList();
                                    break;
                                case "userLeaving1":
                                    System.out.println("Server sees mID as: " + m.getID());
                                    cw.addMessage(new Message(m.getID(), serverID, m.getText()));

                                    break;
                                default:
                                    cw.addMessage(new Message("serverMessage", serverID, "Unknown command: " + m.getText()));
                            }
                        }
                        //System.out.println("end of process packets " + m);

                    }
                    //System.out.println(packet);
                    packets.remove(0);
                }


    }
    /*
    private ArrayList<Object> getPackets(){
        return packets;
    }
    
     */
    public void updateClients(){
        for(RemoteClient c: clients){
            //System.out.println("Sending message(" + cw.messages + ") to " + cw.getNameById(c.id));
            c.sendObject(new Message(cw.getParticipants(), serverID, "updatedClients"));
        }
    }
    public void sendOutUpdatedMessageList(){

        for(RemoteClient c: clients){
            //System.out.println("Sending message(" + cw.messages + ") to " + cw.getNameById(c.id));
            c.sendObject(new Message(cw.messages, serverID, "updatedMessages"));
        }
    }
    /*
    public static byte[] truncate(byte[] og) {
        int finalIndex = 0;
        for (int i = 0; i < og.length; i++) {
            if (og[i] == 0) {
                finalIndex = i;
                break;
            }
        }



        byte[] res = new byte[finalIndex];
        System.arraycopy(og, 0, res, 0, finalIndex);
        return res;
    }

     */

    @Override
    public void sendMessage(Message m) {
        addPacket(m);
    }
    @Override
    public void disconnect(){

    }

}
