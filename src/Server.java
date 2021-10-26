import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Server implements Closeable {

    private boolean running;

    public static void main(String[] args) throws Exception {
        Server s = Server.getInstance();
    }

    ServerSocket ss;
    Socket sr;
    OutputStream os;
    InputStream is;
    static Server instance;
    private ArrayList<RemoteClient> clients;
    PriorityQueue<String> packets;

    private Server(int port) throws IOException {
        getConnection(port);
        instance = this;
    }
    private Server()throws  IOException{
        Scanner tsm = new Scanner(System.in);
        System.out.println("What port?");
        getConnection(tsm.nextInt());
    }

    public void addClient(RemoteClient c) {
        if (clients == null)
            clients = new ArrayList<RemoteClient>();
        clients.add(c);
    }
    public static Server getInstance(){
        if(instance == null){
            try {
                instance = new Server();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return instance;
    }
    @Override
    public void close() throws IOException {
        os.close();
        sr.close();
        ss.close();
    }

    public void getConnection(int port) throws IOException {
        System.out.println("running");
        ss = new ServerSocket(port);
        running = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("RUNning");
                while (running) {
                    try {
                        sr = ss.accept();
                        System.out.println("accepted");
                        byte[] b = new byte[1000];
                        sr.getInputStream().read(b, 0, b.length);
                        String name = new String(truncate(b));
                        addClient(new RemoteClient(name, sr));
                        System.out.println(clients.get(clients.size() - 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println("Done");
            }

        });
        t.setName("ConnectionSearching");
        t.start();
    }
    public void startProcessing(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (packets == null) packets = new PriorityQueue<String>();
                while (running) {
                    packets.poll();
                }
            }
        });
        t.setName("ServerProcess");
    }
    public void sendFile(String fileName) throws Exception {
        FileInputStream fr = new FileInputStream(fileName);
        byte[] b = new byte[(int) new File(fileName).length()];
        fr.read(b);
        os.write(b, 0, b.length);
        fr.close();

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
        for (int i = 0; i < finalIndex; i++) {
            res[i] = og[i];
        }
        return res;
    }
}
