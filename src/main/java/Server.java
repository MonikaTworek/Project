import java.io.*;
import java.net.*;
import java.util.*;


public class Server extends Thread{

    private ServerSocket serverSocket = null;
    private boolean listen = false;
    private List<ClientAgent> clientAgents = Collections.synchronizedList(new ArrayList<ClientAgent>());
    private final int SocketNumber = 65535;
    private final int time = 1000;
    private final int MaxClient = 1000;

    Server(){
        super();
        try {
            serverSocket = new ServerSocket(SocketNumber);
            serverSocket.setSoTimeout(time);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        listen = true;
        start();
        System.out.println("Started");
    }


    public void stopServer(){
        listen = false;
    }

    public void run() {
        Socket socket;
        while (listen) {
            try {
                socket = serverSocket.accept();
                System.out.print(socket);
                if (socket != null) {
                    if (clientAgents.size() == MaxClient) {
                        (new ObjectOutputStream(socket.getOutputStream())).writeObject(0);
                    } else {
                        ClientAgent clientAgent = new ClientAgent(socket);
                        clientAgents.add(clientAgent);
                        //TODO: różne serwery na różne rodzaje gier? bo wtedy z automatu mają dim podane, bo tak nie wiem jak przekazać
                    }
                }
                toPair();
            }
            catch (IOException e){
            }
        }
    }

    public void deleteClient(ClientAgent clientAgent){
        clientAgent.disconnect();
        clientAgents.remove(clientAgent);
    }


    public void toPair(){
        System.out.print("wszedło \n");
        int first19 = 0;
        int first13 = 0;
        int first9 = 0;
        boolean flaga19=false;
        boolean flaga13=false;
        boolean flaga9=false;
        for (int i = 0; i < clientAgents.size(); i++) {
            if (!clientAgents.get(i).getHasPartner()) {
                switch (clientAgents.get(i).getDim()){
                    case 9:
                        if(!flaga9) {
                            first9 = i;
                            flaga9 = true;
                        }
                        else {
                            GameWindow.window.manager = new Client(null, SocketNumber - first9, true);
                            GameWindow.window.manager = new Client("localhost", SocketNumber - i, false);
                            clientAgents.get(i).setHasPartner(true);
                            clientAgents.get(first9).setHasPartner(true);
                            flaga9=false;
                        }
                    case 13:
                        if(!flaga13) {
                            first13 = i;
                            flaga13 = true;
                        }
                        else {
                            GameWindow.window.manager = new Client(null, SocketNumber - first13, true);
                            System.out.print("skoncz z liniami glupolu");
                            GameWindow.window.manager = new Client("localhost", SocketNumber - i, false);
                            clientAgents.get(i).setHasPartner(true);
                            clientAgents.get(first13).setHasPartner(true);
                            flaga13=false;
                        }
                    case 19:
                        if(!flaga19) {
                            first19 = i;
                            flaga19=true;
                        }
                        else {
                            System.out.print("Ciag dalszy \n");
                            GameWindow.window.manager = new Client(null, SocketNumber-first19-1, true);
                            GameWindow.window.manager = new Client("localhost", SocketNumber - i -1, false);
                            clientAgents.get(i).setHasPartner(true);
                            clientAgents.get(first19).setHasPartner(true);
                            flaga19=false;
                        }
                }

            }

        }
    }

    public static void main(String args[]){
        Server server = new Server();
        while (true);
    }

}