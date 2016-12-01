import java.io.*;

/**
 * Klasa tworząca handler do gry (serwer)
 */
public class FactorFirstClient extends Factor {
    /**
     * Przepływ danych wejściowych
     */
    public DataInputStream input_stream;

    /**
     * Przepływ danych wyjściowych
     */
    public DataOutputStream output_stream;

    /**
     * Port serweru
     */
    private int port;

    /**
     * Wskazuje czyja kolej
     */
    private boolean stone;

    /**
     * TODO:uzupelnic run
     * Pozwala czekać na połączenie z klientem
     */
    class Wait extends Thread {

    }

    /**
     *
     * @param port port łączący pierwszego i drugiego klienta
     * @param ccolor kolor zaczynajacego
     */
    FactorFirstClient(int port, PlayerColor ccolor){
        if (ccolor==PlayerColor.BLACK)
            ///ustawic kolor zaczynajacego
        stone=false;
    }
}