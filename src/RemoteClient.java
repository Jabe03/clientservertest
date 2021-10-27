import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.PriorityQueue;
import java.util.UUID;

public class RemoteClient {
    String name;
    UUID id;
    Socket s;
    InputStream in;
    OutputStream out;
    boolean running;

    public RemoteClient(String name, Socket s) {
        this.s = s;
        this.name = name;
        this.id = UUID.randomUUID();
        try {
            in = s.getInputStream();
            out = s.getOutputStream();
        } catch(IOException e){
            e.printStackTrace();
        }
        listenForInputs();
    }

    public void listenForInputs(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while(running){
                    try {
//
                        byte[] b = new byte[2000];
                        in.read(b);
                        String msg =new String(Server.truncate(b));
                        msg = "%"+id.toString() +"%" + msg;
                        Server.getInstance().addPacket(msg);
                    } catch(SocketException e){
                        running = false;
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setName("client " + this.name + " listener");
        t.start();
    }

    @Override
    public String toString(){
        return "RemoteClient[name: " + this.name + ", id: " + id + "]";
    }


}
