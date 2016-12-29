import javax.swing.*;

class Log extends JTextArea {
    Log(int x, int y) {
        super(x, y);
    }

    void sendLogText(String msg) {
        this.append(msg);
        this.setCaretPosition(this.getDocument().getLength());
    }
}