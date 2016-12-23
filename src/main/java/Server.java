import java.io.*;
import java.net.*;
import java.util.*;


public class Server extends Thread{

    private ServerSocket serverSocket = null;
    private boolean listen = false;
    private List<ClientAgent> clientAgents = Collections.synchronizedList(new ArrayList<ClientAgent>());
    private final int SocketNumber = 65233;
    private final int time = 1000;
    private final int MaxClient = 1000;
    private GameWindow gfirst9;
    private GameWindow gsecond9;
    private GameWindow gfirst19;
    private GameWindow gsecond19;
    private GameWindow gbot;
    private GameWindow gbotbot;

    Socket socket;

    Server(){
        super();
        try {
            serverSocket = new ServerSocket(SocketNumber);
//            serverSocket.setSoTimeout(time);
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
        while (listen) {
            try {
                socket = serverSocket.accept();
                if (socket != null) {
                    if (clientAgents.size() == MaxClient) {
                        (new ObjectOutputStream(socket.getOutputStream())).writeObject(0);
                    } else {
                        ClientAgent clientAgent = new ClientAgent(socket);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String tmp = in.readLine();
                        StringTokenizer t = new StringTokenizer(tmp, "-");
                        int dim = Integer.parseInt(t.nextToken());
                        int withbot = Integer.parseInt(t.nextToken());
                        clientAgent.setDim(dim);
                        if (withbot == 1) {
                            clientAgent.setWithbot(true);
                        }
                        clientAgents.add(clientAgent);
                        toPair();
                        //TODO: różne serwery na różne rodzaje gier? bo wtedy z automatu mają dim podane, bo tak nie wiem jak przekazać
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //TODO:przy iksie w okienku. czyli robota Agaty :P
    public void deleteClient(ClientAgent clientAgent){
        clientAgent.disconnect();
        clientAgents.remove(clientAgent);
    }

    public void toPair() throws IOException {
        boolean flaga19=false;
        boolean flaga9=false;
        int first19 = 0;
        int first9 = 0;

        for (int i = 0; i < clientAgents.size(); i++) {
            if (!clientAgents.get(i).getHasPartner()) {
                if(clientAgents.get(i).getWithBot()){
                    gbot = new GameWindow(clientAgents.get(i).getDim());
                    gbot.manager = new Client(null, SocketNumber - i -1, true);
                    gbotbot = new GameWindow(clientAgents.get(i).getDim());
                    gbotbot.setVisible(true);
                    gbotbot.manager = new Client( SocketNumber - i - 1, clientAgents.get(i).getDim());
                    //TODO: URUCHOMIENIE BOTA
                }
                switch (clientAgents.get(i).getDim()){
                    case 9:
                        if(!flaga9) {
                            first9 = i;
                            flaga9 = true;
                        }
                        else {
                            gfirst9 = new GameWindow(9);
                            gfirst9.window.manager = new Client(null, SocketNumber - first9-1, true);
                            gsecond9 = new GameWindow(9);
                            gsecond9.window.manager = new Client("localhost", SocketNumber - first9-1, false);
                            clientAgents.get(i).setHasPartner(true);
                            clientAgents.get(first9).setHasPartner(true);
                            flaga9=false;
                        }
                        break;
                    case 19:
                        if(!flaga19) {
                            first19 = i;
                            flaga19=true;
                        }
                        else {
                            gfirst19=new GameWindow(19);
                            gfirst19.window.manager = new Client(null, SocketNumber-first19-1, true);
                            gsecond19=new GameWindow(19);
                            gsecond19.window.manager = new Client("localhost", SocketNumber -first19 -1, false);

                            clientAgents.get(i).setHasPartner(true);
                            clientAgents.get(first19).setHasPartner(true);
                            flaga19=false;
                        }
                        break;
                }

            }

        }
    }

    public static void main(String args[]){
        Server server = new Server();
        while (true);
    }

}