import java.net.Socket;

/**
 * Klasa tworząca handler do gry (klient)
 */
class FactorSecondClient extends Factor {
    /**
     * Wskazuje czyja kolej
     */
    private boolean stone;

    /**
     * Tworzenie obiektu
     *
     * @param ip   ip
     * @param port port
     */
    FactorSecondClient(String ip, int port) {
        //  try {
        //TODO: grafika + okienko chatu
        //    }
    }
    //TODO: funkcja czekjaca na ruch

/**
 * Metoda uruchamiana, gdy użytkownik położy kamień lub spasuje.
 *
 * @param x współrzędna x
 * @param y współrzędna y
 * @throws Exception Gdy wystąpi błąd
 */
        public void start(int x, int y) throws Exception {
            if (stone) {

            }
            //TODO: grafika

            stone=!stone; //zmiana ruchu
        }
    //TODO: oczekiwanie na tuch pierwszego klienta
}
