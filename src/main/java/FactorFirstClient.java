import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/**
 * Klasa tworząca handler do gry (serwer)
 */
class FactorFirstClient extends Factor {
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
    private PlayerColor stone;

    /**
     * Tworzenie obiektu
     *
     * @param port port serweru
     */
    FactorFirstClient(int port) {
        stone = PlayerColor.BLACK; // zaczyna pierwszy gracz

        Wait wait = new Wait();
        wait.start();
    }

    /**
     * Metoda uruchamiana, gdy użytkownik wybierze, gdzie położy kamień lub spasuje. Kontroluje kamień.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @throws Exception Gdy wystąpi błąd
     */
    public void start(int x, int y) throws Exception {
        if (socket == null)
            return;

        if (stone == PlayerColor.BLACK) {
            if (game != null) {
                if (/*gdy gra trwa*/) {
                    String coord = x + "-" + y;
                    PlayerColor stone = game.getCurrentPlayer();

                    if ((x == 100) && (y == 100)) {
                        PrintWriter out_txt =  new PrintWriter(socket.getOutputStream(), true);
                        out_txt.println(coord);
                        game.skipMove();
                        this.stone = PlayerColor.WHITE;
                        painLastMove(x, y);
                        new WaitMoveFromClient();

                        return;
                    }

                    Stone p = game.updateBoard(stone, x, y);

                    if (p != null) {
                        PrintWriter out_txt =  new PrintWriter(socket.getOutputStream(), true);
                        out_txt.println(coord);
                        painLastMove(x, y);
                        //TODO:narysuj okno jeszcze raz
                    }
                    else
                        return;
                }
                else
                    socket = null;
            }

            stone = PlayerColor.WHITE;
            new WaitMoveFromClient();
        }
    }

    /**
     * Oczekuje na ruch drugiego gracza
     */
    class WaitMoveFromClient extends Thread {
        /**
         * Buduje obiekt i uruchamia wątek
         */
        public WaitMoveFromClient()
        {
            start();
        }

        /**
         * Wątek czeka na ruch klienta
         */
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String tmp = in.readLine();
                StringTokenizer t = new StringTokenizer(tmp, "-");
                int x = Integer.parseInt(t.nextToken());
                int y = Integer.parseInt(t.nextToken());

                if (game != null) {
                    if (/*jeżeli gra trwa*/) {
                        PlayerColor stone = game.getCurrentPlayer();

                        if ((x == 100) && (y == 100)) {
                            game.skipMove();
                            painLastMove(x, y);
                            //TODO:narysuj okno jeszcze raz
                        }
                        else {
                            Stone p = game.updateBoard(stone, x, y);

                            if (p != null) {
                                stone = game.getCurrentPlayer();
                                painLastMove(x, y);
                            }
                            //TODO:narysuj okno jeszcze raz
                        }
                    }
                    else
                        socket = null;
                }
                else
                    System.out.println("Game inesistente.");

                stone = PlayerColor.WHITE;
            }
            catch (Exception e) {}
        }
    }

    /**
     * Pozwala czekać na połączenie z klientem
     */
    class Wait extends Thread {
        /**
         * Wątek, który czeka na klienta aktywny
         */
        public void run() {
            try {
                s = new ServerSocket(port);
                System.out.println("[GAME] Server listens on port: " + port);
                socket = s.accept();

                System.out.println("[GAME] Client connect.");
            }
            catch (Exception ecc) {}
        }
    }
}
