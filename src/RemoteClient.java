import java.io.*;
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
    ObjectInputStream ois;
    ObjectOutputStream oos;
    boolean running;

    public RemoteClient(Socket s) {
        this.s = s;
        this.id = UUID.randomUUID();
        try {
            in = s.getInputStream();
            out = s.getOutputStream();
            ois = new ObjectInputStream(in);
            oos = new ObjectOutputStream(out);
        } catch(IOException e){
            e.printStackTrace();
        }

        this.name = getClientName();
        listenForInputs();
    }
    private String getClientName(){
        String name = null;
        try {
            Object o = ois.readObject();
            if (o instanceof Message){
                name = ((Message) o).getText();
            } else {
                oos.writeObject("%error%Name expected, " + o.getClass() + " got... Disconnecting");
                s.close();
            }
        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
            return null;
        }
        return name;
    }

    public void listenForInputs(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while(running){
                    try {
                        Object message = ois.readObject();
                        Server.getInstance().addPacket(message);
//                        byte[] b = new byte[2000];
//                        in.read(b);
//                        String msg =new String(Server.truncate(b));
//                        msg = "%"+id.toString() +"%" + msg;
//                        Server.getInstance().addPacket(msg);
                    } catch(SocketException e){
                        running = false;
                    } catch(IOException e){
                        e.printStackTrace();
                    } catch (ClassNotFoundException e){
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
