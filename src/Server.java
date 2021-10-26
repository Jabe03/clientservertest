import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Closeable {

    private boolean running;

    public static void main(String[] args) throws Exception {
        Server s = new Server(5555);
    }

    ServerSocket ss;
    Socket sr;
    OutputStream os;
    InputStream is;
    private ArrayList<RemoteClient> clients;

    public Server(int port) throws IOException {
        getConnection(port);

    }

    public void addClient(RemoteClient c) {
        if (clients == null)
            clients = new ArrayList<RemoteClient>();
        clients.add(c);
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
        Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
                System.out.println("RUNning");
                while(running){
                    try{
                    sr = ss.accept();
                    System.out.println("accepted");
                    byte[] b = new byte[1000];
                    sr.getInputStream().read(b, 0 , b.length);
                    String name = new String(b);
                    addClient(new RemoteClient(name, sr));
                    System.out.println(clients.get(clients.size()-1));
                    } catch(IOException e){
                        e.printStackTrace();
                    }

                }
                System.out.println("Done");
            }

        });
        t.setName("ConnectionSearching");
        t.start();
    }

    public void sendFile(String fileName) throws Exception {
        FileInputStream fr = new FileInputStream(fileName);
        byte[] b = new byte[(int) new File(fileName).length()];
        fr.read(b);
        os.write(b, 0, b.length);
        fr.close();

    }
}
