/**
 * Created by Mona on 30.11.2016.
 */
public class Client extends Thread {
    int dim;
    PlayerColor ccolor;

    Client(int dim, PlayerColor ccolor){
        this.ccolor=ccolor;
        this.dim=dim;

    }
}
