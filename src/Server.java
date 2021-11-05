import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Closeable, Messageable{

    private boolean running;

    public static void main(String[] args) throws Exception {
        new Server(5555);
    }

    ServerSocket ss;
    Socket sr;
    OutputStream os;
    ObjectInputStream ois;
    InputStream is;
    ChatWindow cw;
    static Server instance;
    public static final UUID serverID = UUID.randomUUID();
    private ArrayList<RemoteClient> clients;
    volatile ArrayList<Object> packets;

    private Server(int port) throws IOException {

        instance = this;
        clients = new ArrayList<>();
        packets = new ArrayList<Object>();
        startServer(port);
    }
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("RUNning");
                while (running) {
                    try {
                        sr = ss.accept();
                        System.out.println("accepted");
                        addClient(new RemoteClient(sr));
                        System.out.println(clients.get(clients.size() - 1));

                        packets.add("Starting char");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                //System.out.println("Done");
            }

        });
        t.setName("ConnectionSearching");
        t.start();
    }
    public synchronized void startProcessing(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    processPackets();
                }
            }
        });
        t.setName("ServerProcess");
        t.start();
    }

    private void processPackets(){
        //System.out.println("About to check...");
                while (!packets.isEmpty()) {
                    Object packet = packets.get(0);
                    if(packet instanceof Message){
                        Message m = (Message)packet;
                        if(m.isTextMessage()){
                            cw.addMessage(m);
                            //System.out.println(cw.messages);
                        } else{
                            if(m.getText().equals("updateMessages")){
                                sendOutUpdatedMessageList();
                            }
                        }
                        //System.out.println("end of process packets " + m);

                    }
                    //System.out.println(packet);
                    packets.remove(0);
                }


    }
    private ArrayList<Object> getPackets(){
        return packets;
    }

    public void sendOutUpdatedMessageList(){

        for(RemoteClient c: clients){
            System.out.println("Sending message(" + cw.messages + ") to " + cw.getNameById(c.id));
            c.sendObject(new Message(cw.messages, serverID, "updatedMessages"));
        }
    }
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

    @Override
    public void sendMessage(Message m) {
        addPacket(m);
    }


}
