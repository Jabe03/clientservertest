import java.net.Socket;
import java.util.UUID;

public class RemoteClient {
    String name;
    UUID id;
    Socket s;

    public RemoteClient(String name, Socket s) {
        this.s = s;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    @Override
    public String toString(){
        return "RemoteClient[name: " + this.name + ", id: " + id + "]";
    }


}
