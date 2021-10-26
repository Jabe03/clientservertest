import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the IP?");
        String addr = sc.nextLine();
        System.out.println("Whats is the port?");
        int port = sc.nextInt();
        Client c = new Client(addr, port);

    }

    private Socket sr;
    private InputStream is;
    private OutputStream os;

    public Client(String address, int port) throws IOException {
        establishConnection(address, port);
        FileOutputStream fr = new FileOutputStream("lib\\tasks\\test.txt", true);
        byte[] b = new byte[2002];
        int bytesRead = is.read(b, 0, b.length);
        System.out.println(bytesRead);
        fr.write(b, 0, bytesRead);
        // System.out.pritnln("done");
        sr.close();
        fr.close();

    }

    public boolean establishConnection(String address, int port) throws IOException {
        sr = new Socket(address, port);
        is = sr.getInputStream();
        os = sr.getOutputStream();
        return true;
    }

    public void sendMessage(String msg) throws IOException {
        byte[] b = msg.getBytes();
        os.write(b);
    }

}
