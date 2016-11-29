import java.io.*;

/**
 * Klasa jednego kamienia na planszy
 */
class Stone implements Serializable {
    /**
     * Współrzędna x kamienia
     */
    private int x;

    /**
     * Współrzędna y kamienia
     */
    private int y;

    /**
     * Wskazuje kolor kamienia
     */
    private PlayerColor color;

    /**
     * Wskazuje grupę kamienia
     */
    private int group;


    /**
     * Inicjuje i tworzy kamień
     * @param x oznacza współrzędną X
     * @param y oznacza współrzędną Y
     * @param color oznacza, czy kamień jest biały
     * @param group wskazuje grupę, do której należy kamień
     */
    Stone(int x, int y, PlayerColor color, int group) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.group = group;
    }

    /**
     * Zwraca true, jeżeli kamień jest biały
     * @return oznacza kolor
     */
    PlayerColor getColor() {
        return(color);
    }

    /**
     * Zwraca grupę, do której należy kamień
     * @return oznacza grupę
     */
    int getGroup() {
        return(group);
    }

    /**
     * Zwraca współrzędną X kamienia
     * @return współrzędna X
     */
    int getX() {
        return(x);
    }

    /**
     * Zwraca współrzędną Y kamienia
     * @return współrzędna Y
     */
    int getY() {
        return(y);
    }
}
