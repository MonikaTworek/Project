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

    private JFrame window;

    /**
     * Tworzenie obiektu oraz ustawienie wiadomościa do wydrukowania
     * @param string - komunikat do wydrukowania: biały(klient) lub czarny(serwer)
     */
    ThreadForJOptionPane(String string, JFrame window) {
        msg = string;
        this.window = window;
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
        window.dispose();
    }
}


