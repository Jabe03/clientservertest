import java.util.UUID;

public interface Host {

    public void sendMessage(Message m);
    public void disconnect();
    public void stop();
    public boolean hasConnection();
    public UUID getConnection(String ip, int port);
    public void setName(String name);
}
