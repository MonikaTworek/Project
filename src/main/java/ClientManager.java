import java.net.*;
import javax.swing.*;

/**
 * Klasa pozwala na sprawne przeprowadzanie meczu. Posiada pola dla serwera i klienta.
 *
 */
class ClientManager {
    ClientManager() {
        makeBoard();
    }
    Game boardGraphic;
    /**
     * Nasłuchuje klienta.
     */
    ServerSocket s;
    /**
     * Umożliwia komunikację między klientem, a serwerem.
     */
    Socket socket;

    /**
     * Metoda wykonuje ostatni ruch.
     * @param x współrzędna x
     * @param y współrzędna y
     */
    void paintLastMove(int x, int y) {
        if (x == 100) {
            String a = "Player passed";
            String b = "Game info";
            JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
        }
        else if (x == 30 && y == 2) {
            double b = boardGraphic.getBlackScore();
            double w = boardGraphic.getWhiteScore();

            if (b > w)
                new ThreadForJOptionPane("Black");
            else
                new ThreadForJOptionPane("White");
        }
        else {
            GameWindow.jPanel2.lastX = y*25 + 20;
            GameWindow.jPanel2.lastY = x*25 + 24;
            GameWindow.jPanel2.repaint();
        }
    }

    //TODO to chyba się nie przyda
    /**
     * Usuwa planszę
     */
    void destroyBoard() {
        boardGraphic = null;
    }

    /**
     * Tworzy nową planszę
     */
    private void makeBoard() {
        boardGraphic = new Game(19);
    }

    /**
     * Zwraca planszę
     * @return plansza
     */
    Game getBoard() {
        return(boardGraphic);
    }
}
