import javax.swing.*;
import java.net.Socket;

class WaitWindow {
    WaitWindow(Socket s) {
        final JOptionPane optionPane = new JOptionPane("Please wait for opponent", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        final JDialog dialog = new JDialog();
        dialog.setTitle("Message");
        dialog.setModal(true);

        dialog.setContentPane(optionPane);

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();

        dialog.setVisible(true);
        boolean toClose = false;

        while(1 > 0) {
            //if client has a partner
            //dialog.dispose(); break;
        }
    }
}
