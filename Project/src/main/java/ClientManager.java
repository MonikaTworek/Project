import java.net.*;
import javax.swing.*;

/**
 * Klasa pozwala na sprawne przeprowadzanie meczu. Posiada pola dla serwera i klienta.
 *
 */
class ClientManager {
    public GameWindow gameWindow;

    ClientManager(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
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
                new ThreadForJOptionPane("Black", gameWindow.window);
            else
                new ThreadForJOptionPane("White", gameWindow.window);
        }
        else {
            gameWindow.jPanel2.lastX = y*25 + 20;
            gameWindow.jPanel2.lastY = x*25 + 24;
            gameWindow.jPanel2.repaint();
        }
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
