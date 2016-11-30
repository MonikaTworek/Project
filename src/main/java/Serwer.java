import java.io.*;
import java.net.*;

/**
 * Created by Mona on 30.11.2016.
 */
public class Serwer {
    //TODO: do wielu klientów trza poszukać, bo teraz to chyba ino jeden
    void start() throws IOException {
        ServerSocket mainsocket = new ServerSocket(65535); // do 1024
        try {

            Socket client= mainsocket.accept();
            InputStreamReader sInput = new InputStreamReader(client.getInputStream());
            BufferedReader input = new BufferedReader(sInput);
            PrintWriter output = new PrintWriter(client.getOutputStream(), true);
            System.out.println("polaczony");

        }
        catch(IOException ex){
            System.exit(-1);
        }
    }
}
