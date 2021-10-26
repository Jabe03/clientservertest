import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
        static ServerSocket ss;
        static Socket sr;
    public static void main(String[] args)throws Exception{
        ss = new ServerSocket(5555);
        sr = ss.accept();
        sendFile("test.txt");
    }
    public static void sendFile(String fileName) throws Exception{
        FileInputStream fr = new FileInputStream(fileName);

        byte[] b = new byte[(int)new File(fileName).length()];
        fr.read(b);
        OutputStream os = sr.getOutputStream();
        os.write(b, 0 , b.length);
        fr.close();
        os.close();
    }
}
