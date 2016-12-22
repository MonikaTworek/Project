import java.io.IOException;
import java.net.Socket;

public class ClientAgent {
    private int dim=19;
    private boolean hasPartner = false;
    final int time = 1000;

    public ClientAgent(Socket socket) {
        super();
        System.out.println("Creating new client socket...");
        try {
            socket.setSoTimeout(time);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done.");
        //this.start();
    }

    public int getDim() {return dim;}

    public void setDim(int dim){this.dim = dim;}

    public boolean getHasPartner() { return hasPartner; }

    public void setHasPartner(boolean hasPartner){
        this.hasPartner = hasPartner;
    }

    public void disconnect() {
//        try {
        //TODO:wywołanie funkcji rezygnuj
        //TODO:przerzucić na serwer
        System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Error while disconnecting client. " + e);
//        }
    }

}