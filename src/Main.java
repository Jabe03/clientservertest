import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        Task.createNewTask("shot", "10/25/2021", "5:00PM", "do your shot josh");
        for (Task t : Task.taskList) {
            System.out.println(t);
        }
        // PrintWriter out = new PrintWriter(file);
        // 140.82.210.2
        Socket sr = new Socket("140.82.210.2", 20000);
        InputStream is = sr.getInputStream();
        FileOutputStream fr = new FileOutputStream("lib\\tasks\\test.txt", true);
        byte[] b = new byte[2002];
        int bytesRead = is.read(b, 0, b.length);
        System.out.println(bytesRead);
        fr.write(b, 0, bytesRead);
        // System.out.pritnln("done");
        sr.close();
        fr.close();

    }

}
