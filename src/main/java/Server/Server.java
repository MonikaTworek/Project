package Server;

import java.io.*;
import java.net.*;
import java.util.*;



public class Server extends Thread{

    private ServerSocket serverSocket = null;
    private boolean listen = false;
    private List<ClientAgent> clientAgents = Collections.synchronizedList(new ArrayList<ClientAgent>());


    public Server(){
        super();

        try {
            serverSocket = new ServerSocket(65535);
            serverSocket.setSoTimeout(1000);
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
        int i=1;
        while (listen)
        {
            try {
                socket = serverSocket.accept();
                if (socket != null) {
                    if (clientAgents.size() == 1000) {
                        (new ObjectOutputStream(socket.getOutputStream())).writeObject(0);
                    } else {
                        ClientAgent clientAgent = new ClientAgent(socket, this, i);
                        clientAgents.add(clientAgent);
                        i++;
                    }
                }
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
        int j=0;
        for (int i = 0; i < clientAgents.size(); i++) {
            if(clientAgents.get(i).getHasPartner()==false)
                j++;


        }
    }

    public static void main(String args[]){
        Server server = new Server();
        while (true);
    }

}