public interface Host {

    public void sendMessage(Message m);
    public void disconnect();
    public void stop();
}
