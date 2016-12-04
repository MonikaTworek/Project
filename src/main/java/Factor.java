import java.net.*;
import javax.swing.*;

/**
 * Klasa pozwala na sprawne przeprowadzanie meczu. Posiada podklasy dla pierwszego i drugiego klienta.  Pośrednik.
 *
 */
class Factor {
    Factor() {
        makeGoban();
    }

    /**
     * Tworzy grę
     */
    Game game;

    /**
     * Nasłuchuje drugiego gracza.
     */
    ServerSocket s;

    /**
     * Umożliwia komunikację między klientami.
     */
    Socket socket;

    /**
     * Metoda, która rozpoczyna się, gdy użytkownik wybierze miejsce gdzie położy kamień lub spasuje. Koordynuje kamień.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @throws Exception gdy wystąpi błąd.
     */
    public void start(int x, int y) throws Exception {
        if (game != null) {
            if (/*gd gra trwa*/) {
                PlayerColor stone = game.getCurrentPlayer();

                if ((x == 100) && (y == 100)) {
                    game.skipMove();
                    painLastMove(x, y);
                }
                else {
                    Stone p = game.updateBoard(stone, x, y);

                    if (p != null) {
                        stone = game.getCurrentPlayer();
                        painLastMove(x, y);
                    }
                }
            }
        }
        else
            System.out.println("Game not exist.");
    }

    /**
     * Metoda zwraca ostatni ruch.
     *
     * @param x współrzędna x
     * @param y coordinata y
     */
    void painLastMove(int x, int y) {
        if ((x == 100) && (y == 100)) {
            String a = "Player passed";
            String b = "Game info";
            JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            //TODO: co się zmieni w oknie
        }
    }

    /**
     * Usuwa planszę
     */
    void destroyGoban()
    {
        game = null;
    }

    /**
     * Tworzy nową planszę
     */
    private void makeGoban()
    {
        game = new Game(19);
    }

    /**
     * Zwraca planszę
     *
     * @return plansza
     */
    Game getGoban() { return(game); }

    /**
     * Zwraca socket
     *
     * @return socket socket
     */
    public Socket getSocket()
    {
        return(socket);
    }
}
