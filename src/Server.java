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
        os = sr.getOutputStream();
        is = sr.getInputStream();

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
        ss = new ServerSocket(5555);
        sr = ss.accept();
    }

    public void sendFile(String fileName) throws Exception {
        FileInputStream fr = new FileInputStream(fileName);
        byte[] b = new byte[(int) new File(fileName).length()];
        fr.read(b);
        os.write(b, 0, b.length);
        fr.close();

    }
}
