import java.util.UUID;

public class RemoteClient {
    String name;
    UUID id;

    public RemoteClient(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

}
