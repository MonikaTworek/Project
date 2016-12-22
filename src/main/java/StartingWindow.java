<<<<<<< HEAD
import javax.imageio.ImageIO;
=======
>>>>>>> master
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
<<<<<<< HEAD
import java.awt.image.BufferedImage;
import java.io.File;
=======
>>>>>>> master
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Wirus on 04.12.2016.
 */
public class StartingWindow extends JFrame implements ActionListener {
<<<<<<< HEAD
    private JButton solo19;
    private JButton solo9;
    private JButton two19;
    private JButton two9;
    private Socket socket;
    private ClientAgent client;

    StartingWindow() {
=======
    private static StartingWindow window;
    private JButton solo19;
    private JButton solo13;
    private JButton solo9;
    private JButton two19;
    private JButton two13;
    private JButton two9;
    private Socket socket1;


    StartingWindow() {
        window = this;
>>>>>>> master
        buildWindow();
    }

    private void buildWindow() {
<<<<<<< HEAD
        BufferedImage myImage;
        try {
            File background = new File("background.png");
            myImage = ImageIO.read(background);
            myImage.getTransparency();
            setContentPane(new ImagePanel(myImage));
            setResizable(false);
            setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        solo19 = new JButton("19x19");
        solo9 = new JButton("9x9");
        two19 = new JButton("19x19");
        two9 = new JButton("9x9");
//
//        JLabel label1 = new JLabel();
//        label1.setText("<html><div style='text-align: center;'><font size=\"12\">" +
//                "Welcome in GO!</font><br><br><br><font size=\"5\">Choose game mode</font></div></html>");
//        label1.setFont(new Font("Monospaced", Font.BOLD, 10));
//        label1.setBounds(0, 0, 400, 100);
//        jPanel1.add(label1);
//
        JLabel label2 = new JLabel("One player:", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Two players:", SwingConstants.CENTER);
        label2.setFont(new Font("Sans Serif", Font.BOLD, 16));
        label3.setFont(new Font("Sans Serif", Font.BOLD, 16));

        label2.setBounds(170, 282, 150, 40);
        label3.setBounds(170, 357, 150, 40);
        add(label2);
        add(label3);

        solo9.setBounds(141, 317, 74, 24);
        solo19.setBounds(266, 317, 74, 24);
        add(solo19);
        add(solo9);
        solo19.addActionListener(this);
        solo9.addActionListener(this);

        two9.setBounds(141, 392, 74, 24);
        two19.setBounds(266, 392, 74, 24);
        add(two19);
        add(two9);
        two19.addActionListener(this);
        two9.addActionListener(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-822)/2, (screenSize.height-561)/2, 480, 520);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GoProject 1.0");
//        try	{
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//            SwingUtilities.updateComponentTreeUI(this);
//        }
//        catch (Exception ignored) {
//        }
=======
        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        jPanel1.setBounds(0, 0, 400, 150);
        jPanel2.setBounds(0, 150, 400, 250);
        getContentPane().setLayout(null);
        jPanel2.setLayout(null);
        getContentPane().add(jPanel1);
        getContentPane().add(jPanel2);

        solo19 = new JButton("19x19");
        solo13 = new JButton("13x13");
        solo9 = new JButton("9x9");
        two19 = new JButton("19x19");
        two13 = new JButton("13x13");
        two9 = new JButton("9x9");

        JLabel label1 = new JLabel();
        label1.setText("<html><div style='text-align: center;'><font size=\"12\">" +
                "Welcome in GO!</font><br><br><br><font size=\"5\">Choose game mode</font></div></html>");
        label1.setFont(new Font("Verdana", Font.BOLD, 10));
        label1.setBounds(0, 0, 400, 100);
        jPanel1.add(label1);

        JLabel label2 = new JLabel("One player:", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Two players:", SwingConstants.CENTER);

        label2.setBounds(150, 20, 100, 40);
        label3.setBounds(150, 100, 100, 40);
        jPanel2.add(label2);
        jPanel2.add(label3);

        solo19.setBounds(40, 60, 80, 25);
        solo13.setBounds(160, 60, 80, 25);
        solo9.setBounds(280, 60, 80, 25);
        jPanel2.add(solo19);
        jPanel2.add(solo13);
        jPanel2.add(solo9);
        solo19.addActionListener(this);
        solo13.addActionListener(this);
        solo9.addActionListener(this);

        two19.setBounds(40, 140, 80, 25);
        two13.setBounds(160, 140, 80, 25);
        two9.setBounds(280, 140, 80, 25);
        jPanel2.add(two19);
        jPanel2.add(two13);
        jPanel2.add(two9);
        two19.addActionListener(this);
        two13.addActionListener(this);
        two9.addActionListener(this);

        try	{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (Exception ignored) {
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-822)/2, (screenSize.height-561)/2, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setBackground(new Color(250, 209, 47));
        jPanel2.setBackground(new Color(250, 209, 47));
        setTitle("GoProject 1.0");
        setResizable(false);
        setVisible(true);
>>>>>>> master
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
<<<<<<< HEAD
        try{
             socket = new Socket("localhost",65333);
=======
        //TODO po kliknięciu następuje
        try{
             socket1 = new Socket("localhost",65333);
>>>>>>> master
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if(source == solo19) {
            System.out.println("Tworze gre 19x19 dla jednego gracza");
        }
<<<<<<< HEAD
=======
        if(source == solo13) {
            System.out.println("Tworze gre 13x13 dla jednego gracza");
        }
>>>>>>> master
        if(source == solo9) {
            System.out.println("Tworze gre 9x9 dla jednego gracza");
        }


//        client=new ClientAgent(socket);
        if(source == two19) {
            System.out.println("Tworze gre 19x19 dla dwóch graczy");
//            client.setDim(19);
<<<<<<< HEAD
            setVisible(false);
        }
        if(source == two9) {
            System.out.println("Tworze gre 9x9 dla dwóch graczy");
            client.setDim(9);
            setVisible(false);
        }
    }
}

class ImagePanel extends JComponent {
    private Image image;
    ImagePanel(Image image) {
        this.image = image;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
=======
        }
        if(source == two13) {
            System.out.println("Tworze gre 13x13 dla dwóch graczy");
//            client.setDim(13);
        }
        if(source == two9) {
            System.out.println("Tworze gre 9x9 dla dwóch graczy");
//            client.setDim(9);
        }
    }
}
>>>>>>> master
