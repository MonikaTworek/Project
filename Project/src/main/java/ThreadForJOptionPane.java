import javax.swing.*;

/**
 * Klasa tworząca pop-up kończący grę.
 */
class ThreadForJOptionPane implements Runnable {
    /**
     * Wątek
     */
    private Thread thread;

    /**
     * Wiadomość: 'biały' lub 'czarny'
     */
    private String msg;

    /**
     * Tworzenie obiektu oraz ustawienie wiadomościa do wydrukowania
     * @param string - komunikat do wydrukowania: biały(klient) lub czarny(serwer)
     */
    ThreadForJOptionPane(String string) {
        msg = string;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Metoda watku
     */
    public void run() {
        String a = msg + " won the game !";
        String b = "Game over";
        JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
//        GameWindow.check();
    }
}


