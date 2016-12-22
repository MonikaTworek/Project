package Server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Mona on 10.12.2016.
 */
public class ClientAgent{
    private Server server;
    private int i;
    private int dim;
    private boolean hasPartner =false;


    public ClientAgent(Socket socket, Server server, int i) {
        super();
        this.server = server;
        System.out.println("Creating new client socket...");
        try {
            socket.setSoTimeout(1000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done.");
        //this.start();
    }

    public int getDim() {return dim; }
    public void setDim(int dim){ this.dim=dim; }
    public boolean getHasPartner() { return hasPartner; }
    public void setHasPartner(boolean hasPartner){ this.hasPartner=hasPartner;}

    public void disconnect() {
//        try {
            server.stopServer();
            System.exit(1);
//        } catch (IOException e) {
//            System.err.println("Error while disconnecting client. " + e);
//        }
    }

}