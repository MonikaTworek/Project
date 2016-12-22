package Basics;

import Basics.PlayerColor;

import java.io.*;

/**
 * Klasa jednego kamienia na planszy
 */
public class Stone implements Serializable {
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
    public Stone(int x, int y, PlayerColor color, int group) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.group = group;
    }

    /**
     * Zwraca true, jeżeli kamień jest biały
     * @return oznacza kolor
     */
    public PlayerColor getColor() {
        return(color);
    }


    /**
     * Setuje numer grupy
     */
    public void setGroup(int g) {
        group = g;
    }

    /**
     * Zwraca grupę, do której należy kamień
     * @return oznacza grupę
     */
    public int getGroup() {
        return(group);
    }

    /**
     * Zwraca współrzędną X kamienia
     * @return współrzędna X
     */
    public int getX() {
        return(x);
    }

    /**
     * Zwraca współrzędną Y kamienia
     * @return współrzędna Y
     */
    public int getY() {
        return(y);
    }
}
