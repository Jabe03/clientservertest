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
        sendMessage("Josh");
        startRuntimeChat();
    }
    public void startRuntimeChat()throws IOException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Send a message!");
                Scanner tsm = new Scanner(System.in);
                while(true){
                    try {
                        sendMessage(tsm.nextLine());
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setName("Chat listener");
        t.start();
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
        System.out.println("MSg sent!");
    }

}
