import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/**
 * Klasa tworząca handler do gry (klient)
 */
public class FactorSecondClient extends Factor {

    /**
     * Wskazuje czyja kolej
     */
    private boolean stone;

    /**
     * Tworzenie obiektu
     *
     * @param ip adres IP serwera
     * @param port port serwera
     */
    FactorSecondClient(String ip, int port) {
        stone = false;//zaczyna FirstClient

        try {
            socket = new Socket(ip, port);
            System.out.println("[GAME] Client connect with server " + ip);
        }
        catch (Exception e) {
            System.out.println("-->  Error in the network connection.");
        }

        new WaitMoveFromServer();
    }

    /**
     * Metoda uruchamiana, gdy użytkownik wybierze gdzie chce położyć kamień lub spasuje. Kontroluje kamień.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @throws Exception Gdy wystąpi błąd
     */
    public void start(int x, int y) throws Exception {
        if (stone) {
            if (game_graph != null) {
                if (/*jeżeli gra trwa*/) {
                    String coord = x + "-" + y;
                    PlayerColor stone = game_graph.getMove();

                    if ((x == 100) && (y == 100)) {
                        PrintWriter out_txt =  new PrintWriter(socket.getOutputStream(), true);
                        out_txt.println(coord);
                        //TODO: zmienia czyj ruch i inne rzeczy związane z położeniem kamienia. coś w rodzaju skip
                        this.stone = !(this.stone);
                        painLastMove(x, y);
                        new WaitMoveFromServer();

                        return;
                    }

                    Stone p = game_graph.updateBoard(stone, x, y, "");

                    if (p != null) {
                        PrintWriter out_txt =  new PrintWriter(socket.getOutputStream(), true);
                        out_txt.println(coord);
                        painLastMove(x, y);
                        //TODO:narysuj jeszcze raz
                    }
                    else
                        return;
                }
                else
                    socket = null;
            }

            stone = !stone;

            new WaitMoveFromServer();
        }
    }

    /**
     * Oczekuje na ruch pierwszego klienta
     */
    private class WaitMoveFromServer extends Thread {
        /**
         * Buduje obiekt i uruchamia wątek
         */
        WaitMoveFromServer()
        {
            start();
        }

        /**
         * Wątek czeka na ruch serwera
         */
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String tmp = in.readLine();
                StringTokenizer t = new StringTokenizer(tmp, "-");
                int x = Integer.parseInt(t.nextToken());
                int y = Integer.parseInt(t.nextToken());

                if (game_graph != null) {
                    if (/*jeżeli gra trwa*/) {
                        PlayerColor stone = game_graph.getMove();

                        if ((x == 100) && (y == 100)) {
                            //TODO:zmienia czyj ruch i inne rzeczy związane z położeniem kamienia. coś w rodzaju skip
                            painLastMove(x, y);
                            //TODO:narysuj okno jeszcze raz
                        }
                        else {
                            Stone p = game_graph.updateBoard(stone, x, y, "");

                            if (p != null) {
                                stone = game_graph.getMove();
                                painLastMove(x, y);
                            }
                            //TODO: narysuj okno jeszcze raz
                        }
                    }
                    else
                        socket = null;
                }
                else
                    System.out.println("Game inesistente.");

                stone = !stone;
            }
            catch (Exception e) {}
        }
    }
}
/* TODO co jak klient zamknie okno */