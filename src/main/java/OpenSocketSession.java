import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;


/**
 * Klasa okna pozwalająca wybrać tryb klient/serwer
 *
 */
class OpenSocketSession extends JFrame {
    private JButton jButton2;
    private JButton jButton3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JRadioButton jRadioButton1;
    private JRadioButton jRadioButton2;
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private ButtonGroup group;

    /**
     * Inicjalizacja komponentów graficznych
     */
    OpenSocketSession() {
        initComponents();
        initModel();
    }

    /**
     * Inicjalizuje komponenty po kolei
     */

    private void initComponents() {
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jRadioButton1 = new JRadioButton();
        jRadioButton2 = new JRadioButton();
        jPanel3 = new JPanel();
        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jButton3 = new JButton();
        jPanel4 = new JPanel();
        jLabel2 = new JLabel();
        jTextField2 = new JTextField();
        jLabel3 = new JLabel();
        jTextField3 = new JTextField();
        jButton2 = new JButton();
        group = new ButtonGroup();

        getContentPane().setLayout(null);

        setTitle("Socket połączony");
        jPanel1.setLayout(null);
        jPanel2.setLayout(null);

        jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Choose", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 11)));
        jRadioButton1.setFont(new Font("Dialog", 0, 12));
        jRadioButton1.setText("ClientManager");
        jRadioButton1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setMargin(new Insets(0, 0, 0, 0));
        jPanel2.add(jRadioButton1);
        group.add(jRadioButton1);
        jRadioButton1.setBounds(50, 30, 60, 15);
        jRadioButton1.addActionListener(e -> {
            if (jRadioButton1.isSelected())
                focusClient();
        });

        jRadioButton2.setFont(new Font("Dialog", 0, 12));
        jRadioButton2.setText("Server");
        jRadioButton2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setMargin(new Insets(0, 0, 0, 0));
        group.add(jRadioButton2);
        jPanel2.add(jRadioButton2);
        jRadioButton2.setBounds(130, 30, 60, 15);
        jRadioButton2.addActionListener(e -> {
            if (jRadioButton2.isSelected())
                focusServer();
        });

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 240, 70);

        jPanel3.setLayout(null);

        jPanel3.setBorder(BorderFactory.createTitledBorder(null, "Start server session", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 11)));
        jLabel1.setFont(new Font("Dialog", 0, 12));
        jLabel1.setText("port");
        jPanel3.add(jLabel1);
        jLabel1.setBounds(20, 30, 50, 15);

        jPanel3.add(jTextField1);
        jTextField1.setBounds(80, 30, 140, 19);

        jButton3.setFont(new Font("Dialog", 0, 12));
        jButton3.setText("start");
        jPanel3.add(jButton3);
        jButton3.setBounds(80, 55, 90, 25);
        jButton3.addActionListener(e -> {
            check();

            int port = Integer.parseInt(jTextField1.getText());
            try {
                GameWindow.window.manager = new Client(null, port, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        });

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 70, 240, 100);

        jPanel4.setLayout(null);

        jPanel4.setBorder(BorderFactory.createTitledBorder(null, "Connect with server", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 11)));
        jLabel2.setFont(new Font("Dialog", 0, 12));
        jLabel2.setText("Number IP");
        jPanel4.add(jLabel2);
        jLabel2.setBounds(20, 30, 70, 15);

        jPanel4.add(jTextField2);
        jTextField2.setBounds(100, 30, 120, 19);

        jLabel3.setFont(new Font("Dialog", 0, 12));
        jLabel3.setText("port");
        jLabel3.setVisible(false);
        jPanel4.add(jLabel3);
        jLabel3.setBounds(20, 50, 31, 15);

        jPanel4.add(jTextField3);
        jTextField3.setBounds(100, 50, 120, 19);

        jButton2.setFont(new Font("Dialog", 0, 12));
        jButton2.setText("connect");
        jPanel4.add(jButton2);
        jButton2.setBounds(80, 75, 90, 25);
        jButton2.addActionListener(e -> {
            check();

            String ip = jTextField2.getText();
            int port = Integer.parseInt(jTextField3.getText());
            try {
                GameWindow.window.manager = new Client(ip, port, false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        });

        jPanel1.add(jPanel4);
        jPanel4.setBounds(0, 170, 240, 110);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 240, 280);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-249)/2, (screenSize.height-310)/2, 249, 310);

        setResizable(false);
        setVisible(true);
    }

    /**
     * Niszczy socket lub serversocket do nasłuchania. Wywoływany na końcu każdej partii lub gry.
     */
    public void check() {
        try {
            if (GameWindow.window.manager != null) {
                if (GameWindow.window.manager.s != null)
                    GameWindow.window.manager.s.close();

                if (GameWindow.window.manager.socket != null)
                    GameWindow.window.manager.socket.close();

                GameWindow.window.manager = null;
            }
        }
        catch (Exception ec) {
            ec.printStackTrace();
        }
    }


    /**
     * Inicjalizuje model poprzez ustawienie domyślnego  IP (localhost) i portu (7777)
     */
    private void initModel() {
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jTextField1.setEnabled(false);
        jTextField1.setText("7777");
        jTextField2.setEnabled(false);
        jTextField2.setText("localhost");
        jTextField3.setEnabled(false);
        jTextField3.setText("7777");
    }

    /**
     * Co się dzieje, gdy wybierze się klienta.
     */
    private void focusClient() {
        jButton2.setEnabled(true);
        jTextField2.setEnabled(true);
        jTextField3.setEnabled(true);

        jButton3.setEnabled(false);
        jTextField1.setEnabled(false);
    }

    /**
     * Co się dzieje, gdy wybierze się serwer.
     */
    private void focusServer() {
        jButton3.setEnabled(true);
        jTextField1.setEnabled(true);

        jButton2.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
    }
}
