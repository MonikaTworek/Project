import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Wirus on 04.12.2016.
 */
public class StartingWindow extends JFrame implements ActionListener {
    private JButton solo19;
    private JButton solo9;
    private JButton two19;
    private JButton two9;
    private Socket socket;
    private ClientAgent client;

    StartingWindow() {
        buildWindow();
    }

    private void buildWindow() {
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        PrintWriter out_txt;
        try{
             socket = new Socket("localhost",65233);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if(source == solo19) {
            System.out.println("Tworze gre 19x19 dla jednego gracza");
            try {
                out_txt = new PrintWriter(socket.getOutputStream(), true);
                String jol= "19-1";
                out_txt.println(jol);
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
        if(source == solo9) {
            System.out.println("Tworze gre 9x9 dla jednego gracza");
            try {
                out_txt = new PrintWriter(socket.getOutputStream(), true);
                String jol="9-1";
                out_txt.println(jol);
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }

        if(source == two19) {
            System.out.println("Tworze gre 19x19 dla dwóch graczy");
            try {
                String jol="19-0";
                out_txt = new PrintWriter(socket.getOutputStream(), true);
                out_txt.println(jol);
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
        if(source == two9) {
            System.out.println("Tworze gre 9x9 dla dwóch graczy");
            try {
                String jol="9-0";
                out_txt = new PrintWriter(socket.getOutputStream(), true);
                out_txt.println(jol);
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
