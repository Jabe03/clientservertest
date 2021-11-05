import java.util.UUID;

public class Message {

    String text;
    UUID id;

    public Message(String text, UUID id){
        this.text = text;
        this.id = id;
    }
    public UUID getID(){ return this.id;}
    public String getText(){
        return this.text;
    }



}
